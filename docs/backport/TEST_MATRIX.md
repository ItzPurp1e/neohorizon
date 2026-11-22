# Матрица проверки бэкпорта

Базовые команды выполнены на source SHA `aaa14aa26e4597ee8f18715552c5fe5d6078ac21` в Windows 11 build `10.0.26300.0`, portable Rust 1.99.1 (CI pin), если не указано иное. Логи в рабочей папке `_baseline_*.log`.

| ID | Команда | Фактический результат | Статус |
|---|---|---|---|
| A-01 | `python3 tools/pr_ready.py` | Public tree, repository hygiene, layers, addresses, export lists и test stand-ins прошли. Python tests не завершились за ~12 минут; прогон остановлен, итогового exit code проверки нет. | INCOMPLETE (interrupted) |
| A-02 | `cargo test --locked -p horizon-protocol` | Тестовый бинарь завершился Windows `STATUS_STACK_OVERFLOW` (`0xc00000fd`); suite не завершила полный проход. | FAIL (process) |
| A-03 | `cargo test --locked -p horizon-core -p horizon-world` | Core: 214 passed, 0 failed, 8 ignored; дополнительный бинарь: 9 passed. World: 793 passed, 3 failed, 22 ignored. Все 3 падения — `PermissionDenied` в stranded MCC upgrade tests. | FAIL (Windows filesystem) |
| A-04 | `cargo test --locked -p horizon-client --lib --bins --tests` | 1,955 library tests passed; integration: 16 passed; 0 failures. | PASS |
| A-05 | `python3 -m unittest discover -s tests` | Полный прогон, выполняемый внутри `pr_ready.py`, не завершился за ~12 минут и был прерван. Несколько прежних попыток имели неполное окружение; пригодного полного unittest summary нет. | INCOMPLETE (interrupted) |
| B-01 | Remap determinism | Не запускалось. | NOT STARTED |
| C-01 | Generated version constants | Не запускалось. | NOT STARTED |
| E-01–E-03 | Client/server interoperability and packet robustness | Не запускалось. | NOT STARTED |
| F-01 | Assets parity | Не запускалось. | NOT STARTED |
| G-01 | Save/reload/vanilla roundtrip | Не запускалось. | NOT STARTED |
| H-01 | Gameplay oracle matrix | Не запускалось. | NOT STARTED |
| I-01 | Native mod acceptance | Не запускалось. | NOT STARTED |
| J-01 | Release gates | Не запускалось. | NOT STARTED |

`RUNNING / INCOMPLETE` означает, что команда была запущена, но финальный результат пока не наблюдался. Windows failures не классифицируются как upstream logic bugs без повторного запуска в поддерживаемом окружении.

