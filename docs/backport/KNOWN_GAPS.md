# Известные gaps и блокеры

## Окружение Stage A

- **BLOCKED — local checkout/toolchain:** workspace содержит только план, нет `.git`, исходников, Cargo workspace, Git, Cargo и Rust compiler. Установленные команды baseline не выполнялись.
- **BLOCKED — baseline evidence:** неизвестно, какие failures уже существуют upstream на зафиксированном commit. Нельзя корректно отделить их от регрессий до запуска исходной матрицы.
- **PARTIAL — workspace inventory:** архитектурные crates предварительно перечислены, но точный вывод workspace members не получен командой Cargo.

## Технические gaps бэкпорта (предварительные)

- **OPEN — old JAR remap:** `horizon-prepare`/tools должны поддержать 1.21.1 obfuscated client/server вместе с hash-проверкой mappings и outputs.
- **OPEN — protocol adapter completeness:** предварительно замечены legacy recipe book, explosion layout, cursor/inventory packets, entity position sync, relative teleport/velocity, vehicle/player input и направления packet mappings. Подтвердить по исходным файлам и oracle.
- **OPEN — runtime generation:** 26.3 constants/data tables ещё не переключены на 1.21.1.
- **OPEN — registries/data:** полный versioned diff и генерация 1.21.1 не проведены.
- **OPEN — assets:** совместимость legacy item model JSON/overrides и весь ресурсный набор не проверены.
- **OPEN — saves/worldgen/gameplay/native mods:** parity не подтверждена.

Неизвестные gaps не трактуются как поддерживаемые функции. Обновлять документ только с evidence и reproduction steps.

