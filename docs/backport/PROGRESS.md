# Прогресс бэкпорта на Minecraft 1.21.1

Дата записи: 2026-11-22.

## Текущий статус

- Этап A: **PARTIAL**; аудит зафиксирован на source commit `aaa14aa26e4597ee8f18715552c5fe5d6078ac21`, точный checkout и переносимое окружение подготовлены.
- Rust baseline: клиентские тесты прошли; protocol и world имеют выявленные падения, см. `TEST_MATRIX.md`.
- Python suite и `tools/pr_ready.py`: прогон `pr_ready` выполняется; предыдущие попытки Python suite завершались ошибками окружения, полный прогон пока не имеет итогового сводного отчёта.
- Этапы B–J: **NOT STARTED**.

## Выполнено

- Подготовлен точный checkout в `_checkout` на зафиксированном source SHA; файлы исходного проекта находятся в `_upstream_source`.
- Подготовлены локальные Python 3.12.10, Rust 1.99.1 (CI pin), Rust 1.95.0, MinGit, MSVC и SDK компоненты. Системная конфигурация не менялась.
- Зафиксированы Windows 11 build `10.0.26300.0`, PowerShell 5.1, Cargo workspace из 73 members; edition 2024 и минимальный Rust 1.95.
- `cargo test --locked -p horizon-client --lib --bins --tests`: PASS; 1,955 library tests и 16 integration tests passed.
- `cargo test --locked -p horizon-core -p horizon-world`: core 214 passed и дополнительный бинарь 9 passed; world 793 passed, 3 failed, 22 ignored. Все три ошибки `PermissionDenied` в stranded MCC upgrade tests на Windows.
- `cargo test --locked -p horizon-protocol`: процесс падает с Windows `STATUS_STACK_OVERFLOW` при запуске тестов; точный тест и окружение нужно исследовать отдельно.
- Запущен `python3 tools/pr_ready.py`: прошли public tree, repository hygiene, layers, addresses, export lists и test stand-ins; Python tests выполняются.
- Обновлённые документы Stage A находятся в `docs/backport/` и на ветке `backport/1.21.1-stage-a`, не в `main`.

## Следующий шаг

Получить итог `pr_ready.py`/Python suite, сохранить фактические логи и exit codes, затем отделить подтверждённые исходные ошибки от ограничений Windows/окружения. После этого завершить аудит Stage A перед началом изменений для 1.21.1.

