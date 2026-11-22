# Этап A — аудит исходного состояния

## Статус

**PARTIAL**. Source commit и рабочее окружение установлены. Rust baseline запущен на точном checkout; клиент проходит, protocol и world имеют наблюдаемые тестовые сбои. `pr_ready.py` ещё выполняется, поэтому этап нельзя считать завершённым.

## Исходная точка

- Репозиторий: `ItzPurp1e/neohorizon`, default branch `main`.
- Source SHA: `aaa14aa26e4597ee8f18715552c5fe5d6078ac21`.
- Цель: backport Minecraft 1.21.1 (protocol 767, DataVersion 3955, Java 21).
- Аудитная ветка: `backport/1.21.1-stage-a`; изменения документов не направляются в `main`.
- Локальный точный checkout: `D:\Projects\NeoHorizon\_checkout`.

## Среда

- Windows 11, build `10.0.26300.0`; PowerShell 5.1.
- Git checkout проверен на указанном SHA; Cargo workspace: 73 members.
- Edition 2024; минимальный Rust `1.95`; CI pin из `tools/ci/rust-version`: `1.99.1`.
- Для прогона подготовлены локальные portable Rust 1.99.1/1.95.0, Python 3.12.10, MinGit, MSVC 14.51 и Windows SDK headers/libs. Глобальный PATH и системные настройки не менялись.
- Текущие исходники описывают NeoHorizon 26.3 (protocol 777, DataVersion 5023, Java 25), а не runtime 1.21.1.

## Подтверждённые findings

- В `tools/lab.py` подготовка прекращается, если manifest содержит mappings; комментарий указывает, что старым версиям нужен remapping stage.
- `tools/version_port.py` отклоняет obfuscated older versions.
- В `step_1_21_1.rs` явно оставлены неполные/несовместимые места: serverbound packet coverage, explosion layout, legacy recipe book, inventory/cursor sync, entity position sync и serializer IDs.
- Эти findings показывают наличие работ, но не являются полным packet-by-packet diff.

## Baseline

Подробные команды, фактические результаты и ограничения находятся в `TEST_MATRIX.md`. Кратко: client PASS; core PASS; world имеет 3 Windows `PermissionDenied` failures; protocol test process завершается `STATUS_STACK_OVERFLOW`; `pr_ready.py` ещё в процессе Python tests.

## Завершение этапа

Нужны финальные exit codes и сводка Python suite, расследование Windows-specific failures, сохранение логов и завершённая синхронизация всех audit-файлов на ветке Stage A. До этого Stage A остаётся PARTIAL.

