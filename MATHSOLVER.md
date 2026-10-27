# JavaMathSolver 0.4

## Опис проєкту
Консольна математична утиліта (CLI) мовою Java, що розробляється в межах лабораторних робіт №1–4. Програма зчитує команду та аргументи з командного рядка (`String[] args`), виконує алгебраїчні та геометричні обчислення, обробляє набори чисел і точок, розбиває математичні вирази на токени та містить вбудовану довідку.

## Вимоги
* **Java Development Kit (JDK) 17** або вище (`maven.compiler.release` у `pom.xml` — 17).
* **Apache Maven 3.6+** — для збирання та запуску тестів (необов'язково, можна зібрати лише `javac`).
* Доступ до командного рядка (консолі/терміналу).

## Структура проєкту
```text
src/main/java/solver/
├── app/       MathSolver (main), CommandDispatcher, CommandCatalog, HelpPrinter
├── core/      Messages, Output, NumberParser, ArgumentValidator, CharClassifier, TokenType, Token, Tokenizer
├── algebra/   Arithmetic, Equations, NumberTheory, TaylorSeries, NumericArray, BasicStatic, Matrix
└── geometry/  Distances, Shapes, Triangles, Solids, Intersections, Polygon, MonteCarlo, Point, Points, Polygon2D
src/test/java/solver/   JUnit 5 тести
scripts/                регресійні кейси CLI (cases.txt, regression.sh, regression.ps1)
```

| Пакет | Відповідальність |
| :--- | :--- |
| `app` | Точка входу, каталог команд і їх арність, диспетчеризація, довідка |
| `core` | Парсинг чисел, валідація кількості аргументів, вивід результатів і помилок, tokenizer |
| `algebra` | Арифметика, рівняння, теорія чисел, ряди Тейлора, статистика масивів, матриці |
| `geometry` | Відстані, фігури, трикутники, тіла обертання, перетини, точки та багатокутники |

Правило залежностей: `core` не імпортує інші пакети; `algebra` і `geometry` залежать тільки від `core`; `app` бачить усе.

## Token model (з версії 0.4)
Tokenizer повертає масив об'єктів `Token[]` замість двох паралельних масивів `String[]` + `TokenType[]` (Lab 3). Тип і текст лексеми тепер живуть в одному об'єкті й не можуть розсинхронізуватися.

```text
"2*x + 3" ──► Tokenizer.tokenize ──► Token[]
                                     ├── NUMBER(2)
                                     ├── MULTIPLY(*)
                                     ├── IDENTIFIER(x)
                                     ├── PLUS(+)
                                     └── NUMBER(3)
```

### Клас `Token` (`solver.core.Token`)
| Член | Опис |
|---|---|
| `private final TokenType type` | тип токена (`NUMBER`, `IDENTIFIER`, `PLUS`, `MINUS`, `MULTIPLY`, `DIVIDE`, `POWER`, `LEFT_PARENTHESIS`, `RIGHT_PARENTHESIS`, `UNKNOWN`) |
| `private final String lexeme` | текст лексеми точно так, як він записаний у виразі |
| `Token(TokenType type, String lexeme)` | єдиний конструктор, з валідацією (див. нижче) |
| `getType()`, `getLexeme()` | лише гетери, сетерів немає |
| `toString()` | повний формат для логів і налагодження |
| `toCompactString()` | компактний формат |
| `equals(Object)`, `hashCode()` | логічна рівність |

Клас `final`, поля `final`: токен **незмінний** (immutable) після створення.

### Валідація конструктора
| Виклик | Результат |
|---|---|
| `new Token(NUMBER, "3.14")` | ✅ |
| `new Token(null, "3.14")` | `NullPointerException: type must not be null` |
| `new Token(NUMBER, null)` | `NullPointerException: lexeme must not be null` |
| `new Token(NUMBER, "")` | `IllegalArgumentException: lexeme must not be empty` |

`null` дає `NullPointerException` (через `Objects.requireNonNull`), а некоректне значення дає `IllegalArgumentException`. Це навмисне розділення, а не помилка. Tokenizer ніколи не створює токен із порожньою лексемою.

### Формати виводу
| Метод | Приклад | Де використовується |
|---|---|---|
| `toString()` | `Token{type=NUMBER, lexeme='3.14'}` | логи, налагодження, повідомлення тестів |
| `toCompactString()` | `NUMBER(3.14)` | короткий запис у документації та тестах |
| CLI `tokenize` | `0: NUMBER 3.14` | формат C10 без змін з версії 0.3 (`<index>: <TYPE> <lexeme>`) |

### Логічна рівність (`equals` / `hashCode`)
Два токени рівні, якщо в них **однаковий тип і однакова лексема**:
```java
Token a = new Token(TokenType.NUMBER, "2");
Token b = new Token(TokenType.NUMBER, "2");

a == b        // false — це два різні об'єкти в пам'яті
a.equals(b)   // true  — логічно це один і той самий токен
a.hashCode() == b.hashCode()   // true — контракт equals/hashCode
```
* `==` порівнює посилання, `equals` порівнює вміст.
* Лексеми порівнюються як **текст**, а не як числа: `NUMBER(2)` ≠ `NUMBER(2.0)`, `NUMBER(007)` ≠ `NUMBER(7)`.
* Позиція токена у виразі не зберігається й на рівність не впливає: усі `x` у `x + x` рівні.
* `equals(null)` повертає `false`, а не кидає виняток. Тому токени коректно працюють у `HashSet` / `HashMap` і з `Arrays.equals`.

### API `Tokenizer`
| Метод | Повертає | Примітка |
|---|---|---|
| `tokenize(String)` | `Token[]` | основний метод (C15) |
| `lexemes(String)` | `String[]` | обгортка: лексеми з `tokenize` (сумісність з Lab 3) |
| `tokenizeTypes(String)` | `TokenType[]` | обгортка: типи з `tokenize` (сумісність з Lab 3) |
| `isValid(String)` | `boolean` | `true`, якщо вираз непорожній і без `UNKNOWN` (C8) |
| `tokenStatistics(String)` | `int[4]` | числа, ідентифікатори, оператори, дужки (C9) |

Правила розбиття не змінилися порівняно з 0.3:
| Вираз | `Token[]` |
|---|---|
| `-5 * (x - 3.14)` | `NUMBER(-5) MULTIPLY(*) LEFT_PARENTHESIS(() IDENTIFIER(x) MINUS(-) NUMBER(3.14) RIGHT_PARENTHESIS())` |
| `3 - -5` | `NUMBER(3) MINUS(-) NUMBER(-5)` |
| `x-1` | `IDENTIFIER(x) MINUS(-) NUMBER(1)` |
| `2x` | `NUMBER(2) IDENTIFIER(x)` |
| `2..5 + x@` | `UNKNOWN(2..5) PLUS(+) IDENTIFIER(x) UNKNOWN(@)` |
| `""`, `"   "` | `[]` (порожній масив) |

Знак `-` входить у лексему числа, лише якщо це початок виразу або перед ним стоїть оператор чи `(`.

## Спосіб компіляції
Варіант 1 — Maven (рекомендовано):
```bash
mvn -q compile
```
Класи з'являться в `target/classes/`.

Варіант 2 — без Maven (лише JDK):
```bash
javac -encoding UTF-8 -d out $(find src/main/java -name "*.java")
```
У Windows PowerShell:
```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src/main/java -Filter *.java).FullName
```
У цьому випадку замість `target/classes` у командах запуску використовуйте `out`.

## Спосіб запуску
```bash
java -cp target/classes solver.app.MathSolver <command> <arguments>
```

Список доступних команд:
```bash
java -cp target/classes solver.app.MathSolver help
```

Запуск без аргументів виводить версію та підказку:
```bash
java -cp target/classes solver.app.MathSolver
# JavaMathSolver v0.3
# Use 'help' to see available commands.
```

Назва команди нечутлива до регістру (`HELP` = `help`).

## Тестування
Модульні тести (JUnit 5):
```bash
mvn test
```

Регресійний прогін CLI (golden master) — усі кейси з `scripts/cases.txt`:
```bash
bash scripts/regression.sh target/classes solver.app.MathSolver > after.txt
```
У Windows PowerShell:
```powershell
powershell -ExecutionPolicy Bypass -File scripts\regression.ps1 -Cp target\classes -Main solver.app.MathSolver -Out after.txt
```
Порівняння з попереднім прогоном: `diff before.txt after.txt` (у PowerShell — `git diff --no-index before.txt after.txt`).

## Формат вводу та виводу
* Числа: цілі (`10`, `-5`), дробові (`3.14`, `.5`, `5.`) та науковий запис (`1e-5`, `2.5E3`). Десятковий роздільник — лише крапка (`1,5` — помилка).
* Числовий результат завжди друкується як `Result: <число>` з 6 знаками після крапки: `Result: 5.000000`. Від'ємний нуль виводиться як `0.000000`.
* Цілочисельні результати (`gcd`, `factorial`, `fibonacci`, `circle-segment`) друкуються без дробової частини: `Result: 6`.
* Логічні результати: `Result: true` / `Result: false`.
* Команди з кількома значеннями (`statistics`, `tokenize`, `token-stats`) друкують кожне значення окремим рядком без префікса `Result:`.
* Вираз для `tokenize`, `token-stats`, `validate-expr` краще брати в лапки. Без лапок аргументи склеюються через пробіл (`tokenize 2*x + 3` = `tokenize "2*x + 3"`), але shell може сам розгорнути `*` чи `(`.

## Повідомлення про помилки

| Ситуація | Повідомлення | Приклад виклику |
| :--- | :--- | :--- |
| Невідома команда | `Unknown command: <name>` + `Use 'help' to see available commands.` | `hello 1 2` |
| Нечислове значення | `Invalid number: <token>` | `add abc 2` |
| Замало аргументів | `Error: Not enough arguments` | `add 5` |
| Забагато аргументів | `Error: Too many arguments` | `add 1 2 3` |
| Непарна кількість координат | `Error: Coordinates must come in pairs` | `centroid 0 0 1` |
| Ділення на нуль | `Error: Division by zero` | `div 1 0` |
| Некоректні математичні дані | `Error: Invalid mathematical input` | `sqrt -4`, `triangle-area 1 2 3` |
| Нерозпізнана лексема у виразі | `Error: Invalid token: <lexeme>` | `validate-expr "2..5"` |
| Самоперетинний багатокутник | `Error: Polygon must be simple (no self-intersections)` | `polygon-area 0 0 1 1 1 0 0 1` |
| Вироджена опукла оболонка | `Error: Convex hull is degenerate` | `convex-hull 0 0 1 1 2 2` |

## Приклади

### 1. Арифметика
```bash
java -cp target/classes solver.app.MathSolver add 10 25
# Result: 35.000000
java -cp target/classes solver.app.MathSolver pow 2 10
# Result: 1024.000000
java -cp target/classes solver.app.MathSolver sqrt 144
# Result: 12.000000
```

### 2. Рівняння
```bash
java -cp target/classes solver.app.MathSolver solve-linear 2 -6
# Result: 3.000000
java -cp target/classes solver.app.MathSolver solve-quadratic 1 -5 6
# Result: x1=2.000000, x2=3.000000
java -cp target/classes solver.app.MathSolver solve-quadratic 1 2 1
# Result: -1.000000
java -cp target/classes solver.app.MathSolver solve-quadratic 1 1 1
# Result: No real roots
```
Якщо `a = 0`, `solve-quadratic` розв'язує лінійне рівняння `b*x + c = 0`; `solve-linear 0 0` дає `Infinite solutions`, `solve-linear 0 5` — `No solution`.

### 3. Теорія чисел
```bash
java -cp target/classes solver.app.MathSolver gcd 48 18
# Result: 6
java -cp target/classes solver.app.MathSolver factorial 5
# Result: 120
```

### 4. Ряди Тейлора
Аргументи: точка `x` і точність `eps > 0` (сумування триває, доки модуль члена ряду не менший за `eps`).
```bash
java -cp target/classes solver.app.MathSolver taylor-sin 1 1e-10
# Result: 0.841471
java -cp target/classes solver.app.MathSolver taylor-ln-one-plus-x 0.5 1e-12
# Result: 0.405465
```

### 5. Статистика набору чисел
```bash
java -cp target/classes solver.app.MathSolver statistics 1 4 2 8 5
# Count: 5
# Sum: 20.000000
# Min: 1.000000
# Max: 8.000000
# Mean: 4.000000
# Variance: 6.000000
# StdDev: 2.449490
java -cp target/classes solver.app.MathSolver normalize 10 20 30
# Result: [0.000000, 0.500000, 1.000000]
```
`Variance` та `StdDev` — генеральні (ділення на `n`).

### 6. Геометрія
```bash
java -cp target/classes solver.app.MathSolver distance 1 2 4 6
# Result: 5.000000
java -cp target/classes solver.app.MathSolver triangle-area 3 4 5
# Result: 6.000000
java -cp target/classes solver.app.MathSolver quadrant -3 2
# Result: II
java -cp target/classes solver.app.MathSolver collinear 0 0 1 1 2 2
# Result: true
```

### 7. Набори точок і багатокутники
```bash
java -cp target/classes solver.app.MathSolver centroid 0 0 4 0 4 4 0 4
# Result: (2.000000, 2.000000)
java -cp target/classes solver.app.MathSolver nearest 0 0 3 4 1 1 -2 2
# Result: (1.000000, 1.000000)
java -cp target/classes solver.app.MathSolver polygon 0 0 4 0 4 4 0 4
# Result: perimeter=16.000000, convex=true
java -cp target/classes solver.app.MathSolver convex-hull 0 0 2 0 1 1 2 2 0 2
# Result: (0.000000, 0.000000) (2.000000, 0.000000) (2.000000, 2.000000) (0.000000, 2.000000)
java -cp target/classes solver.app.MathSolver point-in-polygon 1 1 0 0 4 0 4 4 0 4
# Result: true
```
У `nearest` і `point-in-polygon` перша пара чисел — точка P, решта — набір точок / вершини багатокутника. Точка на межі багатокутника вважається всередині.

### 8. Перевірка виразу (validate-expr, C8)
```bash
java -cp target/classes solver.app.MathSolver validate-expr "2*x + 3"
# Result: true
java -cp target/classes solver.app.MathSolver validate-expr "2..5"
# Error: Invalid token: 2..5
```
Перевіряються лише лексеми; синтаксис (`sin(x`, `2 + + 3`) — задача майбутнього Parser.

### 9. Розбиття виразу на токени (tokenize, C10 / C15)
Кожен рядок — це один `Token` з масиву `Tokenizer.tokenize(...)`: індекс, `getType()`, `getLexeme()`.
```bash
java -cp target/classes solver.app.MathSolver tokenize "2*x^2 + 3*x - 5"
# 0: NUMBER 2
# 1: MULTIPLY *
# 2: IDENTIFIER x
# 3: POWER ^
# 4: NUMBER 2
# 5: PLUS +
# 6: NUMBER 3
# 7: MULTIPLY *
# 8: IDENTIFIER x
# 9: MINUS -
# 10: NUMBER 5
```
Нерозпізнані лексеми показуються як `UNKNOWN` (`tokenize "1 + 2..5 @"` → `2: UNKNOWN 2..5`, `3: UNKNOWN @`); керівні символи — у видимій формі (`\n`).

### 10. Статистика токенів (token-stats, C9)
```bash
java -cp target/classes solver.app.MathSolver token-stats "2*x + 3*x - sin(x)"
# Numbers: 2
# Identifiers: 4
# Operators: 4
# Parentheses: 2
```
Лексеми `UNKNOWN` не враховуються. Операторів тут 4 (`*`, `+`, `*`, `-`); у методичці Lab 3 для цього прикладу помилково вказано 3.

### 11. Помилкові виклики
```bash
java -cp target/classes solver.app.MathSolver hello 1 2
# Unknown command: hello
# Use 'help' to see available commands.
java -cp target/classes solver.app.MathSolver div 1 0
# Error: Division by zero
java -cp target/classes solver.app.MathSolver add 5
# Error: Not enough arguments
```

---

## Таблиця команд

### Службові

| Команда | Аргументи | Опис операції |
| :--- | :--- | :--- |
| `help` | — | Показати перелік усіх доступних команд |

### Алгебра: арифметика (Lab 1)

| Команда | Аргументи | Опис операції |
| :--- | :--- | :--- |
| `add` | `a b` | Додавання ($a + b$) |
| `sub` | `a b` | Віднімання ($a - b$) |
| `mul` | `a b` | Множення ($a \cdot b$) |
| `div` | `a b` | Ділення ($a / b$), `b ≠ 0` |
| `pow` | `a b` | Піднесення до степеня ($a^b$) |
| `sqrt` | `x` | Квадратний корінь ($\sqrt{x}$), `x ≥ 0` |
| `abs` | `x` | Абсолютне значення ($\|x\|$) |

### Алгебра: рівняння та теорія чисел (Lab 2)

| Команда | Аргументи | Опис операції |
| :--- | :--- | :--- |
| `solve-linear` | `a b` | Розв'язання рівняння $ax + b = 0$ |
| `solve-quadratic` | `a b c` | Дійсні корені рівняння $ax^2 + bx + c = 0$ |
| `max3` | `a b c` | Максимальне з трьох чисел |
| `gcd` | `a b` | Найбільший спільний дільник цілих чисел |
| `factorial` | `n` | Факторіал $n!$, ціле $0 \le n \le 20$ |
| `fibonacci` | `n` | Число Фібоначчі $F(n)$, ціле $0 \le n \le 92$ |

### Алгебра: ряди Тейлора (Lab 2)
Аргументи всіх команд: `x eps`, де `eps > 0` — точність. Аліас працює так само, як канонічна назва.

| Команда | Аліас | Функція | Обмеження на `x` |
| :--- | :--- | :--- | :--- |
| `taylor-sin` | `sin-taylor` | $\sin x$ | — |
| `taylor-cos` | `series-b` | $\cos x$ | — |
| `taylor-sinh` | `series-c` | $\sinh x$ | — |
| `taylor-cosh` | `series-h` | $\cosh x$ | — |
| `taylor-exp` | `series-e` | $e^x$ | — |
| `taylor-ln-one-plus-x` | `series-ln` | $\ln(1 + x)$ | $\|x\| < 1$ |
| `taylor-geom-series` | `series-ye` | $1 / (1 + x)$ | $\|x\| < 1$ |
| `taylor-artanh` | `series-j` | $\operatorname{artanh} x$ | $\|x\| < 1$ |
| `taylor-sqrt-one-plus-x` | `series-k` | $\sqrt{1 + x}$ | $\|x\| < 1$ |
| `taylor-inv-sqrt-one-plus-x` | `series-l` | $1 / \sqrt{1 + x}$ | $\|x\| < 1$ |
| `taylor-asin` | `series-m` | $\arcsin x$ | $\|x\| < 1$ |
| `taylor-inv-sq` | `series-z` | $1 / (1 + x)^2$ | $\|x\| < 1$ |
| `taylor-inv-cube` | `series-i` | $1 / (1 + x)^3$ | $\|x\| < 1$ |
| `taylor-inv-one-plus-x2` | `series-y` | $1 / (1 + x^2)$ | $\|x\| < 1$ |

### Алгебра: набори чисел (Lab 3)

| Команда | Аргументи | Опис операції |
| :--- | :--- | :--- |
| `statistics` | `x1 x2 ...` | Кількість, сума, мінімум, максимум, середнє, дисперсія, СКВ (A1–A2) |
| `normalize` | `x1 x2 ...` | Min-max нормалізація $x' = (x - \min) / (\max - \min)$; однакові значення дають нулі (A4) |

### Геометрія: базові обчислення (Lab 1)

| Команда | Аргументи | Опис операції |
| :--- | :--- | :--- |
| `distance` | `x1 y1 x2 y2` | Евклідова відстань між точками |
| `origin-distance` | `x y` | Відстань точки від початку координат |
| `circle-area` | `r` | Площа круга ($\pi r^2$), `r ≥ 0` |
| `circle-circumference` | `r` | Довжина кола ($2\pi r$), `r ≥ 0` |
| `rectangle-area` | `a b` | Площа прямокутника ($a \cdot b$) |
| `rectangle-perimeter` | `a b` | Периметр прямокутника ($2(a+b)$) |

### Геометрія: точки, трикутники, тіла, перетини (Lab 2)

| Команда | Аргументи | Опис операції |
| :--- | :--- | :--- |
| `manhattan-distance` | `x1 y1 x2 y2` | Манхеттенська відстань $\|x_2 - x_1\| + \|y_2 - y_1\|$ |
| `midpoint` | `x1 y1 x2 y2` | Середина відрізка |
| `quadrant` | `x y` | Чверть точки: `I`, `II`, `III`, `IV`, `AXIS` або `ORIGIN` |
| `collinear` | `x1 y1 x2 y2 x3 y3` | Чи лежать три точки на одній прямій |
| `ellipse-area` | `a b` | Площа еліпса ($\pi a b$) |
| `triangle-valid` | `a b c` | Чи існує трикутник зі сторонами `a`, `b`, `c` |
| `triangle-area` | `a b c` | Площа трикутника за формулою Герона |
| `triangle-medians` | `a b c` | Медіани $m_a, m_b, m_c$ |
| `triangle-bisectors` | `a b c` | Бісектриси $l_a, l_b, l_c$ |
| `triangle-heights` | `a b c` | Висоти $h_a, h_b, h_c$ |
| `triangle-angles` | `a b c` | Кути трикутника в радіанах і градусах |
| `triangle-area-inradius` | `A B C r` | Площа за кутами (у радіанах, $A + B + C = \pi$) і радіусом вписаного кола |
| `cylinder-volume` | `r h` | Об'єм циліндра ($\pi r^2 h$) |
| `cone-volume` | `r h` | Об'єм конуса ($\pi r^2 h / 3$) |
| `torus-volume` | `r_in r_out` | Об'єм тора за внутрішнім і зовнішнім радіусами, `r_in ≤ r_out` |
| `circle-segment` | `r x y_min len` | Кількість точок перетину кола $x^2 + y^2 = r^2$ з вертикальним відрізком на прямій `x`, що починається в `y_min` |
| `circle-line` | `cx cy r a b c` | Взаємне розташування кола і прямої $ax + by + c = 0$ |
| `circles-intersect` | `x1 y1 r1 x2 y2 r2` | Чи перетинаються два кола |
| `squares-intersect` | `x1 y1 s1 x2 y2 s2` | Перетин двох квадратів (лівий нижній кут і сторона) |
| `rect-bounding-box` | `x1 y1 x2 y2 x3 y3 x4 y4` | Мінімальний прямокутник, що містить два прямокутники |
| `polygon` | `x1 y1 x2 y2 x3 y3 ...` | Периметр і опуклість багатокутника (≥ 3 вершини) |
| `monte-carlo-triangle` | `n` | Ймовірність (метод Монте-Карло, `n` спроб), що три випадкові відрізки утворюють трикутник |

### Геометрія: набори точок і багатокутники (Lab 3)

| Команда | Аргументи | Опис операції |
| :--- | :--- | :--- |
| `centroid` | `x1 y1 x2 y2 ...` | Центр мас набору точок (G4) |
| `bounding-box` | `x1 y1 x2 y2 ...` | Мінімальні та максимальні x, y набору точок (G3) |
| `nearest` | `px py x1 y1 ...` | Найближча до P точка набору (G5) |
| `polygon-area` | `x1 y1 x2 y2 x3 y3 ...` | Площа простого багатокутника (G6) |
| `convex-hull` | `x1 y1 x2 y2 x3 y3 ...` | Вершини опуклої оболонки проти годинникової стрілки (G6) |
| `point-in-polygon` | `px py x1 y1 x2 y2 x3 y3 ...` | Чи лежить точка P у багатокутнику, межа включно (G6) |

### Математичні вирази: tokenizer (Lab 3)

| Команда | Аргументи | Опис операції |
| :--- | :--- | :--- |
| `tokenize` | `"expr"` | Список токенів `Token[]`: індекс, тип, лексема (C10, C15) |
| `token-stats` | `"expr"` | Кількість чисел, ідентифікаторів, операторів, дужок (C9) |
| `validate-expr` | `"expr"` | Перевірка, що всі лексеми виразу розпізнані (C8); синтаксис не перевіряється |
