# Матрица проверки бэкпорта

Baseline source SHA: `aaa14aa26e4597ee8f18715552c5fe5d6078ac21`. Host: Windows 11 build `10.0.26300.0`, PowerShell 5.1. Rust: CI-pinned `1.99.1` (MSVC target); Python `3.12.10`; MSVC `14.51.36231`; Windows SDK headers/libs `10.0.28000.0`. Временные средства находятся в `_bootstrap`; системные настройки не менялись. Полные stdout/stderr логи сохранены в `D:\Projects\NeoHorizon\_baseline_*.log`.

| ID | Команда | Фактический результат | Статус |
|---|---|---|---|
| A-01 | `python3 tools/pr_ready.py` | Exit 1; duration 25m25s; scope 0 files. Public tree, repository hygiene, layers, addresses, export lists, test stand-ins прошли. Python tests: 2,459 run, 53 failures, 15 errors, 175 skipped. Rust/clippy/crate gates были корректно исключены, так как код не менялся. | FAIL (Python suite) |
| A-02 | `cargo test --locked -p horizon-protocol` | Exit `0xc00000fd` / `STATUS_STACK_OVERFLOW`. Воспроизведено изолированно на `item::tests::deeply_nested_bundles_still_decode`; увеличение `RUST_MIN_STACK` до 16 MiB не устраняет падение. | FAIL (reproducible test process crash) |
| A-03 | `cargo test --locked -p horizon-core -p horizon-world` | Core: 214 passed, 0 failed, 8 ignored; дополнительный бинарь: 9 passed. World: 793 passed, 3 failed, 22 ignored. Три `PermissionDenied` (Windows error 5) в stranded MCC upgrade tests. Изолированный повтор трёх тестов воспроизводит все 3; два соседних MCC сценария проходят. | FAIL (Windows file operation) |
| A-04 | `cargo test --locked -p horizon-client --lib --bins --tests` | 1,955 library tests и 16 integration tests passed; 0 failures. | PASS |
| A-05 | `python3 -m unittest discover -s tests -v` | Exit 1; 2,459 run in 1,224.263s; 53 failures, 16 errors, 175 skipped. Отдельный повтор внутри `pr_ready` сообщил 53 failures, 15 errors, 175 skipped (25m25s). Разница в один error показывает нестабильность одного из тестов. | FAIL (Windows host baseline) |
| B-01 | `tools/version_port.py prepare 1.21.1 -j 4`, затем повтор той же команды | Официальные mappings/JAR прошли checksum verification; ART 2.0.18 remapped client/server с `--reverse --strip-sigs`; vanilla datagen создал 5 reports; 8,269 классов и 67,713 методов проиндексированы. Два настоящих legacy exporters скомпилированы/запущены. Повтор совпал с кэшем: client JAR SHA-256 `8446d377…a503`, server JAR `d81946eb…b9d5`, gameplay exporter `7e013ec2…5010`, entity exporter `bed842d5…c9d2`. | PASS |
| B-02 | `python3 -m unittest discover -s tests -p test_lab.py` с pinned ART/JRE 21 | 28 passed; реальный fixture дважды remapped obfuscated class `a` в `demo/Named`, результаты byte-identical. | PASS |
| B-03 | `cargo test --locked --offline -p horizon-prepare` | Library: 56 passed, 14 ignored daily; CLI: 2 passed; 0 failed. | PASS |
| C-01 | `python3 -m unittest tests.test_gen_version_consts tests.test_version_port tests.test_port_regen tests.test_backport_version_tables` | 32 passed; lock/version constants, pin workflow и markers всех пяти generated tables согласованы с 1.21.1; активных Rust resource paths `horizon-client/26.3` нет. | PASS |
| C-02 | 1.21.1 generator chain: version, packets report, decompiled EntityType, Java command oracle, Rust command-tree comparison | Сгенерированы protocol 767 IDs, 130 entity rows, 208 command-order nodes, 145 suggestion nodes и 28 vanilla/Horizon order rows. | PASS |
| C-03 | `cargo build -p horizon-client-lib --example client_probe` после финальной 1.21.1 packet table | 121 compile error: 26.3 packet structs/adapters ссылаются на пакеты, отсутствующие в protocol 767. Зафиксировано как вход Stage E, не маскируется старой active table. | EXPECTED FAIL (Stage E boundary) |
| E–J | Interop, assets, save roundtrip, gameplay, native mod, release gates | Не запускалось; эти проверки относятся к последующим этапам. | NOT STARTED |

### Классификация ограничений

- `item::tests::deeply_nested_bundles_still_decode` переполняет stack на закреплённом исходном commit и на Windows; повтор с увеличенным `RUST_MIN_STACK` не помогает. Считать воспроизводимым исходным failure, а не нехваткой штатного размера стека.
- Все три world failures — операции обновления stranded `.mcc` файлов, воспроизведённые на Windows. Требуется Linux-проверка, чтобы определить, является ли проблема платформенной.
- Python suite на Windows имеет 53 failures и 15–16 errors. В tracebacks присутствуют host-dependent предположения (POSIX пути, Unix shell tools вроде `tee`, Linux-команды в CI runner tests, Windows `cmd` обработка аргументов). Остаток без отдельного анализа не классифицировать как исключительно environment или product bug. SDK linking был исправлен до финального прогона; итоговый лог не содержит `LNK1181`.
- Предварительные неполные прогоны с отсутствующим Git или неверными SDK путями не являются baseline и в результаты выше не включены.

Этап A считается завершённым как фиксация исходного состояния. Наблюдаемые failures остаются открытыми upstream findings и должны быть учтены до интерпретации последующих результатов.

