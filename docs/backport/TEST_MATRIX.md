# Матрица проверки бэкпорта

Baseline source SHA: `aaa14aa26e4597ee8f18715552c5fe5d6078ac21`. Host: Windows 11 build `10.0.26300.0`, PowerShell 5.1. Rust: CI-pinned `1.99.1` (MSVC target); Python `3.12.10`; MSVC `14.51.36231`; Windows SDK headers/libs `10.0.28000.0`. Временные средства находятся в `_bootstrap`; системные настройки не менялись. Полные stdout/stderr логи сохранены в `D:\Projects\NeoHorizon\_baseline_*.log`.

| ID | Команда | Фактический результат | Статус |
|---|---|---|---|
| A-01 | `python3 tools/pr_ready.py` | Exit 1; duration 25m25s; scope 0 files. Public tree, repository hygiene, layers, addresses, export lists, test stand-ins прошли. Python tests: 2,459 run, 53 failures, 15 errors, 175 skipped. Rust/clippy/crate gates были корректно исключены, так как код не менялся. | FAIL (Python suite) |
| A-02 | `cargo test --locked -p horizon-protocol` | Exit `0xc00000fd` / `STATUS_STACK_OVERFLOW`. Воспроизведено изолированно на `item::tests::deeply_nested_bundles_still_decode`; увеличение `RUST_MIN_STACK` до 16 MiB не устраняет падение. | FAIL (reproducible test process crash) |
| A-03 | `cargo test --locked -p horizon-core -p horizon-world` | Core: 214 passed, 0 failed, 8 ignored; дополнительный бинарь: 9 passed. World: 793 passed, 3 failed, 22 ignored. Три `PermissionDenied` (Windows error 5) в stranded MCC upgrade tests. Изолированный повтор трёх тестов воспроизводит все 3; два соседних MCC сценария проходят. | FAIL (Windows file operation) |
| A-04 | `cargo test --locked -p horizon-client --lib --bins --tests` | 1,955 library tests и 16 integration tests passed; 0 failures. | PASS |
| A-05 | `python3 -m unittest discover -s tests -v` | Exit 1; 2,459 run in 1,224.263s; 53 failures, 16 errors, 175 skipped. Отдельный повтор внутри `pr_ready` сообщил 53 failures, 15 errors, 175 skipped (25m25s). Разница в один error показывает нестабильность одного из тестов. | FAIL (Windows host baseline) |
| B-01 | Remap determinism | Не запускалось. | NOT STARTED |
| C-01 | Generated version constants | Не запускалось. | NOT STARTED |
| E–J | Interop, assets, save roundtrip, gameplay, native mod, release gates | Не запускалось; эти проверки относятся к последующим этапам. | NOT STARTED |

### Классификация ограничений

- `item::tests::deeply_nested_bundles_still_decode` переполняет stack на закреплённом исходном commit и на Windows; повтор с увеличенным `RUST_MIN_STACK` не помогает. Считать воспроизводимым исходным failure, а не нехваткой штатного размера стека.
- Все три world failures — операции обновления stranded `.mcc` файлов, воспроизведённые на Windows. Требуется Linux-проверка, чтобы определить, является ли проблема платформенной.
- Python suite на Windows имеет 53 failures и 15–16 errors. В tracebacks присутствуют host-dependent предположения (POSIX пути, Unix shell tools вроде `tee`, Linux-команды в CI runner tests, Windows `cmd` обработка аргументов). Остаток без отдельного анализа не классифицировать как исключительно environment или product bug. SDK linking был исправлен до финального прогона; итоговый лог не содержит `LNK1181`.
- Предварительные неполные прогоны с отсутствующим Git или неверными SDK путями не являются baseline и в результаты выше не включены.

Этап A считается завершённым как фиксация исходного состояния. Наблюдаемые failures остаются открытыми upstream findings и должны быть учтены до интерпретации последующих результатов.

