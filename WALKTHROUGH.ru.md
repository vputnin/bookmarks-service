# Как это устроено и что попробовать

## 0. Что нужно установить
- JDK 21, Maven 3.9+, Git, Python 3 (на нём написаны хуки)
- Claude Code: `claude --version`; если версия старая — `claude update`
  (скиллы из `.claude/skills/` вызываются как `/имя` только в свежих версиях)

**Windows:** если команды `python3` нет, откройте `.claude/settings.json` руками и замените
`python3` на `python` в двух местах. Сам Claude этот файл изменить не сможет — его
блокирует хук, и это так и задумано.

## 1. Убедиться, что проект живой (без Claude)
```bash
cd bookmarks-service
mvn -q test            # 10 тестов, должно быть зелёно
mvn spring-boot:run    # открыть http://localhost:8080 и добавить пару ссылок
git log --oneline      # один стартовый коммит в main
```

## 2. Как это работает — по слоям

**CLAUDE.md** читается автоматически в начале каждой сессии. Там команды, конвенции и
порядок работы. Строка `@docs/architecture.md` — это импорт: файл подгружается как часть
CLAUDE.md. Так основной файл остаётся коротким.

**TASKS.md** — бэклог и журнал. Каждая сессия Claude начинается с пустой памятью, и
этот файл служит мостом между сессиями: откуда продолжать и что уже сделано.

**.claude/settings.json** — общие для команды настройки (коммитятся в git):
- `permissions.allow` — что Claude делает без вопросов (тесты, git status/diff/commit);
- `ask` — что требует подтверждения (merge, rebase);
- `deny` — что запрещено (push, reset --hard, rm -rf, чтение .env).
- `hooks` — скрипты, которые Claude Code запускает сам, вне модели:
  - `PreToolUse → protect_files.py` срабатывает *до* правки файла. Если файл защищён,
    скрипт возвращает код 2 и правка блокируется.
  - `PostToolUse → check_java_conventions.py` срабатывает *после* правки Java-файла.
    Найдёт `System.out`, field-`@Autowired` или `Instant.now()` без Clock — вернёт
    ошибку в Claude, и тот сам исправит код. Правило из CLAUDE.md превращается в
    автоматическую проверку, а не надежду, что модель «запомнит».

Личные настройки кладутся в `.claude/settings.local.json` — он в `.gitignore`.

**Скиллы (`.claude/skills/<имя>/SKILL.md`)**:
- `/commit`, `/merge-to-main` имеют `disable-model-invocation: true` — Claude никогда не
  запустит их сам, только вы. Это для действий с побочными эффектами.
- `/review`, `/wrap` можно вызвать командой, но Claude может предложить их и сам.
- `add-endpoint` — пример «фонового» скилла: вы его не вызываете, Claude подгружает его
  сам, когда задача касается API (решает по полю `description`). Его правила не лежат в
  CLAUDE.md и не тратят контекст в каждой сессии.

**Сабагент `code-reviewer`** работает в отдельном контексте и возвращает только итог.
Поле `memory: project` даёт ему постоянную папку `.claude/agent-memory/code-reviewer/`.
Первые 200 строк его `MEMORY.md` автоматически попадают ему в промпт при каждом запуске.
Это и есть «extend agent memory capacity while adhering to design limits» из вакансии:
MEMORY.md держим коротким индексом, подробности — в отдельных файлах рядом
(как `decisions.md`), а `/wrap` следит за лимитом.

## 3. Упражнения

### Упражнение 1. Посмотреть, что Claude видит
Запустите `claude` в папке проекта и выполните:
- `/memory` — какие файлы памяти загружены (CLAUDE.md и импорт архитектуры);
- `/agents` — должен быть `code-reviewer`;
- `/hooks` — два хука;
- `/permissions` — правила allow/ask/deny;
- спросите: «Какие конвенции кода в этом проекте и в каком порядке мы работаем?»

### Упражнение 2. Увидеть хуки в действии
- Попросите: «Добавь в BookmarkService.create логирование через System.out.println».
  Claude внесёт правку, хук вернёт ошибку, и Claude сам переделает на SLF4J.
  Именно это стоит показать на собеседовании.
- Попросите: «Добавь в .claude/settings.json разрешение на git push». Правка будет
  заблокирована.
- После этого откатите эксперимент: `git checkout .`

### Упражнение 3. Полный цикл на задаче T-1
1. «Возьми следующую задачу из TASKS.md». Claude переведёт T-1 в In progress, создаст
   ветку `feat/T-1-...` и, скорее всего, подгрузит скилл `add-endpoint` (это видно в выводе).
2. Дождитесь зелёных тестов и проверьте фильтр в браузере (клик по тегу).
3. `/commit` — сообщение в формате Conventional Commits.
4. `/review` — отработает сабагент. Отклоните одно из замечаний, например:
   «Это не наша конвенция, запомни». Затем загляните в
   `.claude/agent-memory/code-reviewer/MEMORY.md`: решение должно там появиться,
   и в следующий раз ревьюер его не повторит.
5. `/merge-to-main` — проверки, merge с подтверждением, тесты на main, обновление TASKS.md.
6. `/wrap` — журнал сессии, предложения по CLAUDE.md, проверка памяти.
7. Выйдите из Claude, зайдите снова и скажите «продолжаем». Claude поймёт по TASKS.md,
   что дальше T-2. Это и есть «iterative workflow».

### Упражнение 4. Ваша собственная доработка (главное для портфолио)
Пункт вакансии «enhance project-level AI skills» — сделайте одно из:
- новый скилл `/task-new <описание>`, который добавляет задачу в TASKS.md в том же
  формате (ID по порядку, чек-лист);
- научите `/commit` отказываться коммитить, если в diff есть `TODO` без номера задачи;
- добавьте третий хук `Stop`, который запускает `mvn -q test` в конце ответа Claude
  и сообщает ему, если тесты красные;
- добавьте второго сабагента `test-writer` с `tools: Read, Write, Edit, Bash` и
  используйте его в скилле `add-endpoint`.

Делайте это тоже через Claude и через тот же цикл (ветка → /commit → /review → merge).
В истории git будет видно, что вы не только пользуетесь средой, но и развиваете её.

## 4. Соответствие вакансии

| Пункт вакансии | Где в проекте |
|---|---|
| shared .claude/ environment: settings, skills, agents, memory | `.claude/` целиком |
| CLAUDE.md as central reference | `CLAUDE.md` + импорт `docs/architecture.md` |
| TASKS.md for iterative workflows | `TASKS.md`, `/wrap`, `/merge-to-main` |
| /commit, /wrap, /merge-to-main, /review | `.claude/skills/` |
| extend agent memory within design limits | `memory: project`, индекс MEMORY.md ≤ 150 строк, топик-файлы |
| production-ready code with AI review tools | сабагент `code-reviewer` + хуки |
| vertical product slices | задачи T-1…T-4: API + сервис + тесты + UI |
| Git branching strategy | ветки `feat/T-n-*`, merge `--no-ff`, push только человеком |

## 5. Перед собеседованием
Создайте публичный репозиторий на GitHub, выполните 2–3 задачи по циклу выше и
запушьте. Хорошо, если в истории коммитов видны и фичи, и изменения в `.claude/`.
Ссылку добавьте в резюме рядом с пунктом «AI-assisted development workflow».
