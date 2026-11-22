# Прогресс бэкпорта на Minecraft 1.21.1

Дата записи: 2026-11-22.

## Текущий статус

- Этап A: **PARTIAL**; GitHub repo и source SHA известны, аудитная документация заведена.
- Локальный baseline: **BLOCKED** из-за отсутствия checkout, Git и Rust toolchain.
- Этапы B–J: **NOT STARTED**.

## Выполнено в этой итерации

- Зафиксирован исходный commit `aaa14aa26e4597ee8f18715552c5fe5d6078ac21` и целевые параметры из инструкции.
- Проверено локальное окружение: `D:\Projects\NeoHorizon` содержит план; команды Git/Cargo/Rust/Java не обнаружены.
- Подготовлены `00_AUDIT.md`, `01_VERSION_DIFF.md`, `02_IMPLEMENTATION.md`, `TEST_MATRIX.md`, `KNOWN_GAPS.md`.
- Документы Stage A сохранены в удалённой ветке `backport/1.21.1-stage-a` от исходного SHA (последний commit `26ee87a564df95ee029b5c2f9d57e80e8ca6145f`). Проверено повторным чтением всех шести файлов через GitHub.
- По удалённым исходникам уточнены 73 workspace member entries, edition 2024/minimum Rust 1.95, version constants 26.3/777/5023/Java 25 и блокировки remap в `tools/lab.py` и `tools/version_port.py`.
- Все пять обязательных baseline-команд предприняты; окружение не позволило запустить тестовые процессы. Результаты записаны как BLOCKED, не PASS/FAIL.

## Не выполнено

- Checkout точного SHA в локальную папку.
- Снятие точных workspace members, toolchain versions и OS build metadata.
- Пять команд baseline и фиксация stdout/stderr/exit codes.
- Подтверждение предварительных технических findings на локальных исходниках.
- Локальная синхронизация audit-документов с GitHub branch (workspace пока не является checkout).

## Следующий ограниченный шаг

Подготовить доступный локальный checkout точного source SHA и Rust/Python окружение, затем завершить Stage A повторным запуском исходной baseline матрицы без изменений кода.

