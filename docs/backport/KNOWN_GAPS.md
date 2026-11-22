# Известные gaps и блокеры

## Исходные failures Stage A

- **CONFIRMED — protocol stack overflow:** `item::tests::deeply_nested_bundles_still_decode` аварийно завершает test binary с `STATUS_STACK_OVERFLOW`; повтор с `RUST_MIN_STACK=16777216` падает так же. Это воспроизводимое состояние исходного commit.
- **CONFIRMED ON WINDOWS — MCC permission errors:** три `horizon-world` stranded MCC upgrade tests завершаются `PermissionDenied` (Windows error 5), изолированный повтор воспроизводит их. Требуется Linux baseline для выяснения платформенной области.
- **CONFIRMED ON WINDOWS — Python suite failures:** standalone suite — 53 failures/16 errors/175 skipped; из `pr_ready` — 53/15/175. Ошибка в числе errors между запусками — один случай нестабильности. В выводе есть POSIX/Unix runner assumptions (`tee` отсутствует в MinGit, ожидаются slash paths, некоторые скрипты используют Unix-команды), но все оставшиеся падения ещё не классифицированы по одному.
- **PASS — shared pr_ready gates:** public tree, repository hygiene, layers, addresses, export lists и test stand-ins. `pr_ready` exit 1 только из-за Python suite; scope — 0 changed files.
- **PASS — client baseline:** 1,955 library и 16 integration tests.
- **VERIFIED — checkout/toolchain:** точный source SHA и 73 workspace members; Rust CI pin 1.99.1, minimum 1.95; переносимое локальное окружение.

## Технические gaps бэкпорта (предварительные)

- **OPEN — old JAR remap:** `horizon-prepare`/tools должны поддержать 1.21.1 obfuscated client/server вместе с hash-проверкой mappings и outputs.
- **OPEN — protocol adapter completeness:** source comments указывают на legacy recipe book, explosion layout, cursor/inventory packets, entity position sync и serializer IDs. Проверить также relative teleport/velocity, vehicle/player input и направления mappings по полным packet tables и oracle.
- **OPEN — runtime generation:** 26.3 constants/data tables ещё не переключены на 1.21.1.
- **OPEN — registries/data:** полный versioned diff и генерация 1.21.1 не проведены.
- **OPEN — assets:** совместимость legacy item model JSON/overrides и весь ресурсный набор не проверены.
- **OPEN — saves/worldgen/gameplay/native mods:** parity не подтверждена.

Неизвестные gaps не трактуются как поддерживаемые функции. Обновлять документ только с evidence и reproduction steps.

