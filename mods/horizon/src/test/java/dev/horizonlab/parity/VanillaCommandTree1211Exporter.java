package dev.horizonlab.parity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Lifecycle;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;

/** Exports the 1.21.1 Brigadier registration order and named suggestion
 * providers without depending on the post-1.21 registry/server APIs. */
public final class VanillaCommandTree1211Exporter {
    @SuppressWarnings("unchecked")
    private static <T> HolderLookup.RegistryLookup<T> emptyLookup(
            ResourceKey<? extends Registry<? extends T>> key) {
        return (HolderLookup.RegistryLookup<T>) java.lang.reflect.Proxy.newProxyInstance(
            VanillaCommandTree1211Exporter.class.getClassLoader(),
            new Class<?>[] { HolderLookup.RegistryLookup.class },
            (proxy, method, args) -> switch (method.getName()) {
                case "key" -> key;
                case "registryLifecycle" -> Lifecycle.stable();
                case "listElements", "listTags" -> Stream.empty();
                case "get" -> Optional.empty();
                case "filterFeatures", "filterElements" -> proxy;
                case "toString" -> "empty lookup for " + key;
                default -> throw new UnsupportedOperationException(method.toString());
            });
    }

    private static JsonObject tree(CommandNode<CommandSourceStack> node, String path,
                                   List<String> suggestions) {
        JsonObject out = new JsonObject();
        JsonArray order = new JsonArray();
        JsonObject children = new JsonObject();
        for (CommandNode<CommandSourceStack> child : node.getChildren()) {
            String childPath = path.isEmpty() ? child.getName() : path + " " + child.getName();
            order.add(child.getName());
            children.add(child.getName(), tree(child, childPath, suggestions));
            if (child instanceof ArgumentCommandNode<?, ?> argument
                    && argument.getCustomSuggestions() != null) {
                @SuppressWarnings("unchecked")
                var provider = (com.mojang.brigadier.suggestion.SuggestionProvider<SharedSuggestionProvider>)
                    (Object) argument.getCustomSuggestions();
                suggestions.add(childPath + "\t" + SuggestionProviders.getName(provider));
            }
        }
        out.add("order", order);
        out.add("children", children);
        return out;
    }

    public static void main(String[] args) throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        RegistryAccess.Frozen access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        CommandBuildContext context = new CommandBuildContext() {
            @Override public Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
                return access.listRegistries();
            }
            @Override public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(
                    ResourceKey<? extends Registry<? extends T>> key) {
                return access.lookup(key).or(() -> Optional.of(emptyLookup(key)));
            }
        };
        Commands commands = new Commands(Commands.CommandSelection.ALL, context);
        List<String> suggestions = new ArrayList<>();
        JsonObject tree = tree(commands.getDispatcher().getRoot(), "", suggestions);
        Path out = Path.of(args[0]);
        Files.createDirectories(out);
        Files.writeString(out.resolve("tree.json"), tree.toString());
        Files.write(out.resolve("suggestions.tsv"), suggestions);
    }
}
