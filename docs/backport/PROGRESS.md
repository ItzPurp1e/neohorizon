# Прогресс бэкпорта на Minecraft 1.21.1

Дата записи: 2026-11-22.

## Текущий статус

- Этап A: **COMPLETE**; точный source commit, Windows toolchain и результаты всех пяти обязательных baseline-команд зафиксированы в `TEST_MATRIX.md`.
- Baseline выявил исходные тестовые сбои: protocol stack overflow, три Windows MCC `PermissionDenied`, Python suite failures; клиентский пакет проходит.
- `tools/pr_ready.py`: подготовительные gates проходят; итог команды exit 1 из-за Python suite, результаты записаны.
- Этапы B–J: **NOT STARTED**.

## Выполнено

- Подготовлен точный checkout в `_checkout` на зафиксированном source SHA; файлы исходного проекта находятся в `_upstream_source`.
- Подготовлены локальные Python 3.12.10, Rust 1.99.1 (CI pin), Rust 1.95.0, MinGit, MSVC и SDK компоненты. Системная конфигурация не менялась.
- Зафиксированы Windows 11 build `10.0.26300.0`, PowerShell 5.1, Cargo workspace из 73 members; edition 2024 и минимальный Rust 1.95.
- `cargo test --locked -p horizon-client --lib --bins --tests`: PASS; 1,955 library tests и 16 integration tests passed.
- `cargo test --locked -p horizon-core -p horizon-world`: core 214 passed и дополнительный бинарь 9 passed; world 793 passed, 3 failed, 22 ignored. Точечный повтор воспроизвёл все три `PermissionDenied` stranded MCC failures.
- `cargo test --locked -p horizon-protocol`: падает на `item::tests::deeply_nested_bundles_still_decode` с `STATUS_STACK_OVERFLOW`; повтор с `RUST_MIN_STACK=16777216` также падает.
- `cargo test --locked -p horizon-client --lib --bins --tests`: 1,955 library и 16 integration tests passed.
- `python3 -m unittest discover -s tests -v`: 2,459 tests; 53 failures, 16 errors, 175 skipped; exit 1.
- `python3 tools/pr_ready.py`: 0 source files changed, все семь общих gates до Python passed; Python tests — 2,459 run, 53 failures, 15 errors, 175 skipped; exit 1.
- Обновлённые документы Stage A находятся в `docs/backport/` и на ветке `backport/1.21.1-stage-a`, не в `main`.

## Следующий шаг

Начать этап B после review baseline gaps: исправить/обойти глубоко вложенное bundle decoding; подтвердить MCC upgrade на Linux и Windows; классифицировать Python test failures, часть которых использует POSIX shell/пути, и устранить реальные product regressions отдельно от host-specific harness failures.

