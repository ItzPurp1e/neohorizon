# Матрица проверки бэкпорта

Статус `VERIFIED` разрешён только при наличии наблюдаемого результата. Неизвестный baseline отмечается `BLOCKED`, а не `FAIL`.

| ID | Категория / проверка | Target | Expected | Actual | Статус | Команда / воспроизведение |
|---|---|---|---|---|---|---|
| A-01 | `pr_ready` baseline | source SHA `aaa14aa` | exit 0 либо зафиксированный исходный failure | Не запускалось: checkout отсутствует | BLOCKED | `python3 tools/pr_ready.py` |
| A-02 | Protocol crate baseline | source SHA `aaa14aa` | тесты проходят либо известные upstream failures записаны | Не запускалось: checkout и cargo отсутствуют | BLOCKED | `cargo test --locked -p horizon-protocol` |
| A-03 | Core + world baseline | source SHA `aaa14aa` | тесты проходят либо failures записаны | Не запускалось: checkout и cargo отсутствуют | BLOCKED | `cargo test --locked -p horizon-core -p horizon-world` |
| A-04 | Client baseline | source SHA `aaa14aa` | тесты проходят либо failures записаны | Не запускалось: checkout и cargo отсутствуют | BLOCKED | `cargo test --locked -p horizon-client --lib --bins --tests` |
| A-05 | Python unit baseline | source SHA `aaa14aa` | suite проходит либо failures записаны | Не запускалось: каталог tests отсутствует в workspace | BLOCKED | `python3 -m unittest discover -s tests` |
| B-01 | Remap determinism | 1.21.1 | одинаковые input SHAs дают одинаковые remapped outputs | Не измерено | NOT STARTED | Повторить pipeline на чистом checkout |
| C-01 | Generated version constants | 1.21.1 | protocol 767 / DataVersion 3955 / pack 34/48 / Java 21 | Не измерено | NOT STARTED | Generator + generated-file diff |
| E-01 | Vanilla 1.21.1 client → NeoHorizon server | 1.21.1 | status→login→configuration→play | Не измерено | NOT STARTED | E2E harness/manual client |
| E-02 | NeoHorizon client → vanilla 1.21.1 server | 1.21.1 | connect, chunk, movement, interaction | Не измерено | NOT STARTED | E2E harness/manual server |
| E-03 | Packet robustness | 1.21.1 | malformed/truncated packets fail safely | Не измерено | NOT STARTED | Protocol corpus/fuzz tests |
| F-01 | Representative visual/audio assets | 1.21.1 | no systemic missing asset; fixture parity | Не измерено | NOT STARTED | Resource fixtures + screenshot/audio review |
| G-01 | Save/reload/vanilla roundtrip | DataVersion 3955 | world survives both directions; newer version rejected | Не измерено | NOT STARTED | Save fixture + vanilla 1.21.1 oracle |
| H-01 | Common gameplay acceptance | 1.21.1 | matrix behavior matches vanilla oracle | Не измерено | NOT STARTED | Per-feature probes in repository tools |
| I-01 | Minimal Horizon-native mod | 1.21.1 | registers content and executes hook | Не измерено | NOT STARTED | Native mod integration scenario |
| J-01 | Release quality gates | 1.21.1 | fmt, clippy, workspace tests, parity and clean prepare pass | Не измерено | NOT STARTED | See Stage J in plan |

Для каждой новой строки добавлять точный vanilla class/method или спецификацию packet/data, command, expected/actual и лог/fixture location.

