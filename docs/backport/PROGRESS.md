# Прогресс бэкпорта на Minecraft 1.21.1

Дата записи: 2026-11-22.

## Текущий статус

- Этап A: **COMPLETE**; точный source commit, Windows toolchain и результаты всех пяти обязательных baseline-команд зафиксированы в `TEST_MATRIX.md`.
- Baseline выявил исходные тестовые сбои: protocol stack overflow, три Windows MCC `PermissionDenied`, Python suite failures; клиентский пакет проходит.
- `tools/pr_ready.py`: подготовительные gates проходят; итог команды exit 1 из-за Python suite, результаты записаны.
- Этап B: **COMPLETE**; единый pinned AutoRenamingTool 2.0.18 подключён к `horizon-prepare`, `lab.py` и `version_port.py`. Официальные mappings, исходные JAR и remapped outputs проверяются по SHA, подписи исходных JAR удаляются штатным `--strip-sigs`, а повторный remap обязан дать тот же SHA-256.
- Этапы C–J: **NOT STARTED**.

## Выполнено

- Подготовлен точный checkout в `_checkout` на зафиксированном source SHA; файлы исходного проекта находятся в `_upstream_source`.
- Подготовлены локальные Python 3.12.10, Rust 1.99.1 (CI pin), Rust 1.95.0, MinGit, MSVC и SDK компоненты. Системная конфигурация не менялась.
- Зафиксированы Windows 11 build `10.0.26300.0`, PowerShell 5.1, Cargo workspace из 73 members; edition 2024 и минимальный Rust 1.95.
- `cargo test --locked -p horizon-client --lib --bins --tests`: PASS; 1,955 library tests и 16 integration tests passed.
- `cargo test --locked -p horizon-core -p horizon-world`: core 214 passed и дополнительный бинарь 9 passed; world 793 passed, 3 failed, 22 ignored. Точечный повтор воспроизвёл все три `PermissionDenied` stranded MCC failures.
- `cargo test --locked -p horizon-protocol`: падает на `item::tests::deeply_nested_bundles_still_decode` с `STATUS_STACK_OVERFLOW`; повтор с `RUST_MIN_STACK=16777216` также падает.
- `python3 -m unittest discover -s tests -v`: 2,459 tests; 53 failures, 16 errors, 175 skipped; exit 1.
- `python3 tools/pr_ready.py`: 0 source files changed, все семь общих gates до Python passed; Python tests — 2,459 run, 53 failures, 15 errors, 175 skipped; exit 1.
- Обновлённые документы Stage A находятся в `docs/backport/` и на ветке `backport/1.21.1-stage-a`, не в `main`.
- Реальный pipeline 1.21.1 скачал и проверил официальный manifest, обе mappings, client/server JAR и 62 client libraries, remapped client/server, успешно запустил vanilla datagen и создал `blocks.json`, `commands.json`, `items.json`, `packets.json`, `registries.json`.
- На named server JAR реально скомпилированы и дважды запущены содержательные vanilla exporters: `VanillaGameplayFactsExporter` и `VanillaEntityTypesExporter`. Повторные SHA-256 совпали: `7e013ec2…5010` и `bed842d5…c9d2`.
- Повторный полный remap дал те же SHA-256: client `8446d3777e7a524b425e9b22a2e3fd3878dd2ef61cff62f1ef8817da8632a503`, server `d81946eb2b6f01f93ecba298ac54b0ac8bcf21dfa61601a94d0d39005e36b9d5`.
- Проверки Stage B: 28/28 `test_lab.py`, 56/56 активных unit tests `horizon-prepare` и 2/2 CLI tests прошли; 14 заранее помеченных daily Rust tests не запускались.

## Следующий шаг

Начать этап C: закрепить Minecraft 1.21.1 и Java 21 в version lock, затем регенерировать version-dependent constants/tables поверх уже проверенного Stage B pipeline. Исходные baseline failures остаются в `KNOWN_GAPS.md`.

