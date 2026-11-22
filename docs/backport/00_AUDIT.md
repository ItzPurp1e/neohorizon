# Этап A — аудит исходного состояния

## Статус

**PARTIAL**. Источник зафиксирован и проверен через GitHub; локальный checkout и инструменты сборки отсутствуют, поэтому исполняемый baseline заблокирован. Численные результаты тестов не предполагаются.

## Исходная точка

- Репозиторий: `ItzPurp1e/neohorizon` (публичный; GitHub-доступ подтверждён).
- Default branch: `main`.
- Базовый commit для аудита: `aaa14aa26e4597ee8f18715552c5fe5d6078ac21`.
- Целевая версия: Minecraft 1.21.1, protocol 767, DataVersion 3955, Java 21.
- Исходная версия по аудиту плана: Minecraft 26.3, protocol 777, DataVersion 5023, Java 25.
- Текущая локальная папка: `D:\Projects\NeoHorizon`; в ней на момент старта имеется план бэкпорта, но нет `.git`, `Cargo.toml` и исходников.
- ОС: Windows (PowerShell; путь workspace Windows).
- Доступные локальные инструменты: Python launcher (`python.exe`, `python3.exe`). Не найдены `git`, `cargo`, `rustc`, `java`, `javac` в PATH и типовых установочных путях.
- Cargo manifest на исходном SHA требует `edition = 2024`, `rust-version = 1.95`. Это заявленный minimum, не фактически установленный toolchain.
- Workspace members прочитаны непосредственно из `Cargo.toml` исходного SHA (73 entries): `crates/horizon-guest-stack`, `crates/horizon-mod-vocab`, `crates/horizon-game-discovery`, `crates/horizon-cosmetics`, `crates/horizon-telemetry`, `crates/horizon-hooks`, `crates/horizon-mod-host`, `crates/horizon-game`, `crates/horizon-official`, `crates/horizon-heartbeat`, `crates/horizon-core`, `crates/horizon-platform`, `crates/horizon-addresses`, `crates/horizon-mod-runtime`, `crates/horizon-mod-render`, `docs/modding/examples`, `crates/horizon-dualsense`, `crates/horizon-narrator`, `crates/horizon-threads`, `crates/horizon-hdr`, `crates/horizon-world`, `crates/horizon-client`, `crates/horizon-engine-slots`, `crates/horizon-mod-points`, `crates/horizon-internals`, `crates/horizon-client-lib`, `crates/horizon-gameplay`, `crates/horizon-client-gameplay`, `crates/horizon-client-core`, `crates/horizon-client-input`, `crates/horizon-client-render`, `crates/horizon-command`, `crates/horizon-items`, `crates/horizon-server-files`, `crates/horizon-java-properties`, `crates/horizon-anticheat`, `crates/horizon-chat-filter`, `crates/horizon-mcp`, `crates/horizon-server`, `crates/horizon-server-launch`, `crates/horizon-bots`, `crates/horizon-assets`, `crates/horizon-protocol`, `crates/horizon-theme`, `crates/horizon-prepare`, `crates/horizon-system`, `crates/horizon-bench`, `crates/horizon-perf-format`, `crates/horizon-perf`, `crates/horizon-perf-share`, `crates/horizon-friends`, `crates/horizon-relay`, `crates/horizon-mod-package`, `crates/horizon-mod-wasm`, `crates/horizon-mod-api-gen`, `crates/horizon-mod-api`, `crates/horizon-mod-dimension-game`, `crates/horizon-hot-paths-game`, `crates/horizon-mod-api-macros`, `crates/horizon-mod-source`, `crates/horizon-mod-surface`, `crates/horizon-mod-api-gen/dispatch-tests`, `crates/horizon-mod-wasm/wire-loopback`, `crates/cargo-horizon-mod`, `crates/horizon-port-pack`, `crates/horizon-port-pack-cli`, `crates/horizon-shaders`, `crates/horizon-optifine`, `crates/horizon-polytone`, `crates/horizon-shader-api`, `crates/horizon-release-sign`, `crates/horizon-mod-deps`, `xtask`.
- В дереве исходного SHA 12,904 entries, включая 117 `Cargo.toml`; GitHub tree API сообщил `truncated = false`. Эта статистика получена по удалённому Git tree, не локальным checkout.

## Архитектурная карта из предварительного анализа

Основные слои: `horizon-core` (версии/RNG/математика), `horizon-protocol` (кодеки, framing, состояния соединения и пакеты), `horizon-world` (NBT, chunks, saves, DataFixer, worldgen), `horizon-client` (клиент и integrated server), gameplay/client-gameplay, render/assets, items/commands/client-core, `horizon-prepare`, Horizon-native mod crates, `cargo-horizon-mod`, а также `tools/` и Java oracle/probe fixtures.

Выявлены адаптерные компоненты 1.21.1↔26.3:

- `crates/horizon-protocol/src/adapter/v1_21_1.rs`
- `crates/horizon-protocol/src/adapter/v1_21_1_ids.rs`
- `crates/horizon-protocol/src/adapter/translate/v1_21_1.rs`
- `crates/horizon-protocol/src/adapter/client_session/step_1_21_1.rs`
- `crates/horizon-protocol/src/item/v1_21_1.rs`

По предварительной проверке адаптер содержит незакрытые случаи recipe book, explosion, inventory/entity sync и relative movement. Это предварительные findings, их необходимо подтвердить на checkout указанного commit и привязать к точным строкам.

## Baseline

| Команда | Результат |
|---|---|
| `python3 tools/pr_ready.py` | Попытка запуска через Windows command shim завершилась до запуска программы: `The system cannot execute the specified program`. Нет checkout, `tools/` и рабочего Python executable. |
| `cargo test --locked -p horizon-protocol` | Попытка запуска; `cargo is not recognized as an internal or external command`. |
| `cargo test --locked -p horizon-core -p horizon-world` | Попытка запуска; `cargo is not recognized as an internal or external command`. |
| `cargo test --locked -p horizon-client --lib --bins --tests` | Попытка запуска; `cargo is not recognized as an internal or external command`. |
| `python3 -m unittest discover -s tests` | Попытка запуска завершилась до запуска программы: `The system cannot execute the specified program`; также отсутствует локальный каталог `tests/`. |

Все 5 команд были вызваны, но ни один тестовый процесс не стартовал. Статус каждой проверки — **BLOCKED (environment/setup)**, а не PASS/FAIL. Существующие upstream failures пока неизвестны.

## Блокеры и следующий шаг

1. Подключить/создать локальный Git checkout `ItzPurp1e/neohorizon` на точном SHA выше в workspace.
2. Обеспечить Git, Rust toolchain (`cargo`, `rustc`) и соответствующие Python-зависимости; Java 21 нужен для oracle этапов.
3. Снять workspace members через `cargo metadata --locked --no-deps` и повторить baseline-команды без изменений.
4. Обновить этот документ фактическими версиями инструментов, exit codes, логами и разделением upstream failures.

До выполнения пунктов 1–3 этап A не считается завершённым.

