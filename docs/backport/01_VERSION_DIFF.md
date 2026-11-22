# 1.21.1 — версия и области расхождений

## Зафиксированные целевые параметры

| Поле | Цель |
|---|---:|
| Minecraft | `1.21.1` |
| Protocol | `767` |
| DataVersion | `3955` |
| Resource pack format | `34` |
| Data pack format | `48` |
| Java | `21` |

Официальные артефакты, которые нужно закрепить в `research-lock.json` и проверять до подготовки:

| Артефакт | SHA-1 |
|---|---|
| Version manifest | `ff7ef54488da4a711f3c8a781a9f79105bbe801a` |
| Client JAR | `30c73b1c5da787909b2f73340419fdf13b9def88` |
| Server JAR | `59353fb40c36d304f2035d51e7d6e6baa98dc05c` |
| Client mappings | `2244b6f072256667bcd9a73df124d6c58de77992` |
| Server mappings | `03f8985492bda0afc0898465341eb0acef35f570` |

## Исходная версия и крупные классы несовместимости

Исходная база согласно предварительному аудиту: Minecraft `26.3`, protocol `777`, DataVersion `5023`, Java `25`. Переход нельзя ограничить сменой констант: он затрагивает wire protocol, registries и сериализованные игровые данные.

| Область | Требуемое сравнение и действие | Статус |
|---|---|---|
| Версия/генерация | Constants, packet IDs, entity tracking, command tables, datapack/resource layout | NOT STARTED |
| Подготовка JAR | 1.21.1 использует obfuscated JAR + mappings; проверить hashing/remap pipeline | BLOCKED: точный baseline не снят |
| Protocol | login/config/play, IDs, packet layouts, registries, chunk/light/entity/inventory | NOT STARTED |
| Data | block states, items/components, entities, biomes, tags, recipes, loot | NOT STARTED |
| Assets | legacy item models/overrides, blockstates, atlases, fonts, audio, GUI | NOT STARTED |
| Saves/worldgen | DataVersion 3955, DataFixer range, dimension codecs, worldgen registries | NOT STARTED |
| Gameplay | movement, interactions, menus, entities/AI, redstone, commands, dimensions | NOT STARTED |
| Native mods | target manifest, SDK/prebuilt, registry/hook/WASM behavior | NOT STARTED |

## Предварительно известные protocol gaps

Адаптер в исходниках указывает незавершённые recipe-book, explosion, inventory/entity-sync и relative movement случаи. Перед реализацией требуется сверить packet mapping и семантику с 1.21.1 oracle и зафиксировать классы/методы источника в `TEST_MATRIX.md`.

## Ограничения достоверности

Данный diff — карта областей, а не полный машинный diff registry/packet/data. Полный список должен быть сгенерирован из точного checkout и локальных законно полученных артефактов 1.21.1. Не добавлять JAR/assets/decompiled source/prepared resources в репозиторий.

