# План реализации бэкпорта 1.21.1

Этот план следует выполнять последовательно. Каждый крупный участок меняется отдельным reviewable шагом; совместимость подтверждается командами, oracle или ручным сценарием.

## Зависимости

```text
A baseline/audit → B remap/import → C version pin/generation → D registries/data
                                                        ├→ E protocol 767
                                                        ├→ F assets/rendering
                                                        └→ G saves/worldgen
E + F + G → H gameplay parity → I native mods → J stabilization/release
```

## Рабочие этапы

| Этап | Результат / критерий выхода | Статус |
|---|---|---|
| A. Baseline и аудит | Точный checkout, toolchain/workspace snapshot, baseline logs, аудит-документы | COMPLETE; исходные failures записаны |
| B. Старые obfuscated JAR | Проверяемый remap client/server, mapping SHAs, единый pipeline для `horizon-prepare`, `lab.py`, `version_port.py`, повторяемые exporters/datagen | COMPLETE; ART 2.0.18, real 1.21.1 datagen и repeatability подтверждены |
| C. Version pin | Lock на 1.21.1, генератор констант и data tables для 767/3955, guard от активных 26.3 таблиц | NOT STARTED |
| D. Registries/data | Полные и детерминированные 1.21.1 registries, integrity checks, без молчаливых alias неизвестных ID | NOT STARTED |
| E. Protocol 767 | Полная native wire реализация status/login/config/play и malformed packet behavior; двусторонние vanilla сценарии | NOT STARTED |
| F. Assets/render | Legacy models/overrides и полный аудит representative ресурсов/UI/audio без системных missing assets | NOT STARTED |
| G. Saves/worldgen | DataVersion 3955, новые миры 5023 отклоняются безопасно, save/reload/vanilla roundtrip в трёх измерениях | NOT STARTED |
| H. Gameplay | Поведенческие области по порядку зависимостей, каждая с vanilla reference + oracle/probe | NOT STARTED |
| I. Native mods | SDK/manifest/prebuilt для 1.21.1 и минимальный native mod, подтверждающий content+hook | NOT STARTED |
| J. Release | fmt, clippy, полный workspace suite, parity, clean prepare, soak, saves, OS builds, benchmark и gaps | NOT STARTED |

## Очередность реализации

1. Закрыть Stage A и сохранить воспроизводимый baseline.
2. Реализовать и проверить JAR remap; не коммитить исходные или remapped игровые файлы.
3. Переключить lock/version generation на 1.21.1 и пересобрать все generated tables.
4. Обновить registry/data совместно с protocol registry sync.
5. Реализовать protocol 767, сначала handshake/status/login/configuration, затем play packet families.
6. Параллельно подтверждать assets, saves/worldgen и gameplay с независимыми fixtures; не маскировать gaps заглушками.
7. Подтвердить native mod API и только затем стабилизировать/release.

Для каждого изменения в итоговой записи указывать Implemented, Verified (команда и результат), Not verified, Risks/blockers и ровно один следующий actionable task.

