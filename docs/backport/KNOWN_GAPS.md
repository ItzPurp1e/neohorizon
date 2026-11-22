# Известные gaps и блокеры

## Окружение Stage A

- **OPEN — protocol suite stack overflow:** `cargo test --locked -p horizon-protocol` завершает тестовый процесс с Windows `STATUS_STACK_OVERFLOW`. Требуется изолировать тест на Linux/другой конфигурации и проверить размер тестового стека.
- **OPEN — Windows filesystem permissions:** 3 world tests падают `PermissionDenied` в сценариях stranded MCC upgrade; вероятно связано с Windows file replacement/permissions. Повторить на Linux и выяснить ожидаемое поведение Windows.
- **OPEN — Python/pr_ready result:** `pr_ready.py` прошёл предварительные gates, но Python suite на текущем этапе не выдал финальную сводку. Предыдущие неполные прогоны имеют ошибки/ограничения harness и не считаются baseline.
- **VERIFIED — checkout/toolchain:** точный source SHA и 73 workspace members; Rust CI pin 1.99.1, minimum 1.95; локальные portable toolchain-компоненты подготовлены.
- **VERIFIED — client baseline:** клиентские тесты прошли (1,955 library + 16 integration).

## Технические gaps бэкпорта (предварительные)

- **OPEN — old JAR remap:** `horizon-prepare`/tools должны поддержать 1.21.1 obfuscated client/server вместе с hash-проверкой mappings и outputs.
- **OPEN — protocol adapter completeness:** source comments указывают на legacy recipe book, explosion layout, cursor/inventory packets, entity position sync и serializer IDs. Проверить также relative teleport/velocity, vehicle/player input и направления mappings по полным packet tables и oracle.
- **OPEN — runtime generation:** 26.3 constants/data tables ещё не переключены на 1.21.1.
- **OPEN — registries/data:** полный versioned diff и генерация 1.21.1 не проведены.
- **OPEN — assets:** совместимость legacy item model JSON/overrides и весь ресурсный набор не проверены.
- **OPEN — saves/worldgen/gameplay/native mods:** parity не подтверждена.

Неизвестные gaps не трактуются как поддерживаемые функции. Обновлять документ только с evidence и reproduction steps.

