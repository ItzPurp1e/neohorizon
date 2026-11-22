package dev.horizonlab.parity;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.Vec3;

/**
 * Original exporter of per-state gameplay facts read through vanilla's own accessors:
 * light, hardness, sounds and tool requirements. Item components come from the
 * vanilla data generator's component reports.
 */
public final class VanillaGameplayFactsExporter {
    private static JsonObject sound(SoundEvent event) {
        var result = new JsonObject();
        result.addProperty("id", BuiltInRegistries.SOUND_EVENT.getKey(event).toString());
        return result;
    }

    /** Calls the old contextual accessor or its context-free successor without compiling against both APIs. */
    private static Object call(Object target, String name, Object... args) {
        for (var method : target.getClass().getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == args.length) {
                try {
                    return method.invoke(target, args);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new IllegalStateException("Missing " + name + "/" + args.length + " on " + target.getClass());
    }

    private static int dampening(Object state, EmptyBlockGetter level) {
        try {
            return (int) call(state, "getLightDampening");
        } catch (IllegalStateException oldApi) {
            return (int) call(state, "getLightBlock", level, BlockPos.ZERO);
        }
    }

    private static boolean contextualBoolean(Object state, String name, EmptyBlockGetter level) {
        try {
            return (boolean) call(state, name);
        } catch (IllegalStateException oldApi) {
            return (boolean) call(state, name, level, BlockPos.ZERO);
        }
    }

    private static Vec3 offset(Object state, EmptyBlockGetter level) {
        try {
            return (Vec3) call(state, "getOffset", BlockPos.ZERO);
        } catch (IllegalStateException oldApi) {
            return (Vec3) call(state, "getOffset", level, BlockPos.ZERO);
        }
    }

    /** Writes each state as it is read: the whole file (56 MB) never sits in memory. */
    private static void states(Gson gson, com.google.gson.stream.JsonWriter out) throws java.io.IOException {
        var level = EmptyBlockGetter.INSTANCE;
        for (var state : Block.BLOCK_STATE_REGISTRY) {
            var o = new JsonObject();
            var block = state.getBlock();
            o.addProperty("id", Block.getId(state));
            o.addProperty("emission", state.getLightEmission());
            o.addProperty("dampening", dampening(state, level));
            o.addProperty("skylight_down", contextualBoolean(state, "propagatesSkylightDown", level));
            o.addProperty("shape_occlusion", state.useShapeForLightOcclusion());
            o.addProperty("can_occlude", state.canOcclude());
            o.addProperty("solid_render", contextualBoolean(state, "isSolidRender", level));
            o.addProperty("hardness", state.getDestroySpeed(level, BlockPos.ZERO));
            o.addProperty("requires_tool", state.requiresCorrectToolForDrops());
            o.addProperty("replaceable", state.canBeReplaced());
            o.addProperty("random_ticks", state.isRandomlyTicking());
            o.addProperty("block_entity", state.hasBlockEntity());
            o.addProperty("render_shape", state.getRenderShape().name());
            o.addProperty("liquid", state.liquid());
            o.addProperty("ignited_by_lava", state.ignitedByLava());
            o.addProperty("push", state.getPistonPushReaction().name());
            o.addProperty("offset", state.hasOffsetFunction());
            o.addProperty("full_collision", state.isCollisionShapeFullBlock(level, BlockPos.ZERO));
            o.addProperty("map_color", state.getMapColor(level, BlockPos.ZERO).col);
            // The outline (selection) shape without the random offset, as boxes
            // for picking and as the edges LevelRenderer draws.
            var offset = offset(state, level);
            var shape = state.getShape(level, BlockPos.ZERO);
            // Some offset models (e.g. tall flowers) have an unshifted outline.
            boolean outlineOffset = state.hasOffsetFunction() && Shapes.joinIsNotEmpty(
                shape, state.getShape(level, new BlockPos(17, 0, 29)), BooleanOp.NOT_SAME);
            o.addProperty("outline_offset", outlineOffset);
            if (outlineOffset) shape = shape.move(-offset.x, -offset.y, -offset.z);
            var boxes = new JsonArray();
            for (var box : shape.toAabbs()) {
                var b = new JsonArray();
                for (double v : new double[] {box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ}) b.add(v);
                boxes.add(b);
            }
            o.add("outline", boxes);
            var edges = new JsonArray();
            shape.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
                var e = new JsonArray();
                for (double v : new double[] {x0, y0, z0, x1, y1, z1}) e.add(v);
                edges.add(e);
            });
            o.add("outline_edges", edges);
            o.addProperty("friction", block.getFriction());
            o.addProperty("speed_factor", block.getSpeedFactor());
            o.addProperty("jump_factor", block.getJumpFactor());
            o.addProperty("explosion_resistance", block.getExplosionResistance());
            o.addProperty("item", BuiltInRegistries.ITEM.getKey(block.asItem()).toString());
            var sound = state.getSoundType();
            var sounds = new JsonObject();
            sounds.addProperty("volume", sound.getVolume());
            sounds.addProperty("pitch", sound.getPitch());
            sounds.add("break", sound(sound.getBreakSound()));
            sounds.add("step", sound(sound.getStepSound()));
            sounds.add("place", sound(sound.getPlaceSound()));
            sounds.add("hit", sound(sound.getHitSound()));
            sounds.add("fall", sound(sound.getFallSound()));
            o.add("sound", sounds);
            gson.toJson(o, out);
        }
    }

    public static void main(String[] args) throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        // The same bytes Gson.toJson(JsonElement) gives for {"states": [...]}.
        var gson = new Gson();
        // Streamed beside the target and moved over it when whole: a run stopped halfway leaves
        // no short file for the next preparation to keep.
        var target = Path.of(args[0]);
        var partial = target.resolveSibling(target.getFileName() + ".partial");
        try (var out = gson.newJsonWriter(Files.newBufferedWriter(partial, StandardCharsets.UTF_8))) {
            out.beginObject().name("states").beginArray();
            states(gson, out);
            out.endArray().endObject();
        }
        Files.move(partial, target, StandardCopyOption.REPLACE_EXISTING);
    }
}
