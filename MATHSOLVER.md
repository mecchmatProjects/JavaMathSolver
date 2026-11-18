# JavaMathSolver 0.5

Консольний математичний розв'язувач на Java 17. Програма отримує команду та аргументи з командного рядка (`String[] args`), розв'язує алгебраїчні й геометричні задачі, обробляє набори чисел і точок, розбиває математичні вирази на токени та проходить їх потоком `TokenStream`, з якого наступна лабораторна будуватиме Parser.

Проєкт командний: три підгрупи (Core, Algebra, Geometry) розвивають його від лабораторної до лабораторної в одному репозиторії.

| | |
| :--- | :--- |
| **Версія** | 0.5 (Lab 5) |
| **Мова / збирання** | Java 17+, Maven, JUnit 5 |
| **CLI-команд** | 82 (68 основних + 14 аліасів рядів Тейлора) |
| **Тестів** | ≈1560 перевірок у 53 тестових класах + 257 регресійних CLI-кейсів |
| **Точка входу** | `solver.app.MathSolver` |

## Зміст

1. [Швидкий старт](#1-швидкий-старт)
2. [Що вміє програма: огляд по лабораторних](#2-що-вміє-програма-огляд-по-лабораторних)
3. [Архітектура](#3-архітектура)
4. [Формат вводу та виводу](#4-формат-вводу-та-виводу)
5. [Повідомлення про помилки](#5-повідомлення-про-помилки)
6. [Довідник CLI-команд](#6-довідник-cli-команд)
7. [Програмний API (класи Lab 3–5)](#7-програмний-api-класи-lab-35)
8. [Tokenizer, TokenStream і Parser](#8-tokenizer-tokenstream-і-parser)
9. [Тестування](#9-тестування)
10. [Відомі обмеження](#10-відомі-обмеження)

---

## 1. Швидкий старт

### Вимоги

| Інструмент | Версія | Навіщо |
| :--- | :--- | :--- |
| JDK | 17 або новіша | компіляція і запуск (`maven.compiler.release = 17`) |
| Maven | 3.6+ | збирання і тести; без нього можна зібрати лише `javac` |
| Термінал | bash, zsh, PowerShell, cmd | запуск команд |

### Збирання

Maven (рекомендовано), класи з'являться в `target/classes/`:

```bash
mvn -q compile
```

Лише JDK, класи в `out/`:

```bash
javac -encoding UTF-8 -d out $(find src/main/java -name "*.java")
```

Windows PowerShell:

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src/main/java -Filter *.java).FullName
```

### Запуск

```bash
java -cp target/classes solver.app.MathSolver <command> <arguments>
```

Якщо збирали через `javac`, замініть `target/classes` на `out`.

```bash
java -cp target/classes solver.app.MathSolver help
java -cp target/classes solver.app.MathSolver add 10 25
# Result: 35.000000
```

Без аргументів програма друкує версію і підказку:

```text
JavaMathSolver v0.5
Use 'help' to see available commands.
```

Назва команди нечутлива до регістру: `HELP`, `Help` і `help` однакові.

---

## 2. Що вміє програма: огляд по лабораторних

Кожна лабораторна додає Java-навичку, математичні задачі та нову частину MathSolver.

```text
Lab 1          Lab 2              Lab 3               Lab 4             Lab 5
CLI-каркас ──► методи, switch ──► масиви, набори ──► власні класи ──► об'єктна модель
13 команд      +42 команди        +11 команд          Token, Fraction   TokenStream, Expression
                                  tokenizer           Polynomial ...    Parser API
```

### Lab 1 — MathSolver 0.1: перший модуль

**Java:** структура програми, `main`, `String[] args`, примітивні типи, `Math`, `System.out.printf`.

| Підгрупа | Що додано |
| :--- | :--- |
| Algebra | `add`, `sub`, `mul`, `div`, `pow`, `sqrt`, `abs` |
| Geometry | `distance`, `origin-distance`, `circle-area`, `circle-circumference`, `rectangle-area`, `rectangle-perimeter` |
| Core | розпізнавання команди (`args[0]`), перевірка кількості аргументів, `String → double`, `help`, `Unknown command` |

### Lab 2 — MathSolver 0.2: алгоритми та методи

**Java:** вирази, `if`, `switch`, цикли, статичні методи. Головна зміна: монолітний `main` розбито на «команда → перевірка аргументів → виклик методу».

| Підгрупа | Що додано |
| :--- | :--- |
| Algebra | `solve-linear`, `solve-quadratic`, `max3`, `gcd`, `factorial`, `fibonacci`; 14 рядів Тейлора з аліасами |
| Geometry | `manhattan-distance`, `midpoint`, `quadrant`, `collinear`, `ellipse-area`; трикутник: `triangle-valid`, `triangle-area`, `triangle-medians`, `triangle-bisectors`, `triangle-heights`, `triangle-angles`, `triangle-area-inradius`; тіла: `cylinder-volume`, `cone-volume`, `torus-volume`; перетини: `circle-segment`, `circle-line`, `circles-intersect`, `squares-intersect`, `rect-bounding-box`; `polygon`, `monte-carlo-triangle` |
| Core | CORE-11 диспетчер `switch`, CORE-12 таблиця арності, CORE-13 парсинг `1e-5`, CORE-14 єдиний формат `Result: %f`, CORE-15 стандартні помилки, CORE-16 документація |

### Lab 3 — MathSolver 0.3: масиви та набори даних

**Java:** одновимірні масиви, масиви об'єктів, передавання масивів у методи. Команди тепер приймають довільну кількість чисел.

| Підгрупа | Що додано |
| :--- | :--- |
| Algebra | клас `NumericArray` (A1–A4): сума, середнє, мін/макс, дисперсія, СКВ, пошук, нормалізація; клас `Matrix` (A6); команди `statistics`, `normalize` |
| Geometry | `Point`, `Point[]`, клас `Points` (G1–G5), `Polygon2D` (G6); команди `centroid`, `bounding-box`, `nearest`, `polygon-area`, `convex-hull`, `point-in-polygon` |
| Core | tokenizer C1–C10: `char[]` → лексеми `String[]` → `TokenType[]`; команди `tokenize`, `token-stats`, `validate-expr` |

### Lab 4 — MathSolver 0.4: власні класи

**Java:** класи, поля, конструктори, інкапсуляція, `equals` / `hashCode` / `toString`.

| Підгрупа | Що додано |
| :--- | :--- |
| Algebra | `ComplexNumber`, `Fraction`, `Polynomial` (обчислення, похідна, арифметика, ділення з остачею, корені) |
| Geometry | `Vector2D`, `Line`, `Segment`, `Circle` |
| Core | клас `Token` (C11–C14), tokenizer повертає `Token[]` (C15) |

### Lab 5 — MathSolver 0.5: об'єктна модель розв'язувача

**Java:** композиція, інтерфейси, абстрактні класи, винятки.

| Підгрупа | Що додано |
| :--- | :--- |
| Algebra | дерево виразів `Expression` → `ConstantExpression`, `VariableExpression`, `BinaryExpression` з `evaluate` (A11–A15) |
| Geometry | `Triangle`, `Polygon`, інтерфейси `Figure` / `AreaFigure`, перетворення (зсув, поворот, масштаб), `FigureComposition` |
| Core | `TokenStream` (C16–C19): `current`, `peek(k)`, `next`, `consume`; `ParseException`; контракт `Parser` (C20); команда `walk-expr` |

---

## 3. Архітектура

### Структура репозиторію

```text
JavaMathSolver/
├── pom.xml
├── MATHSOLVER.md              ← цей файл
├── README.md                  ← опис курсу (не редагується)
├── scripts/
│   ├── cases.txt              ← регресійні кейси CLI
│   ├── regression.sh
│   └── regression.ps1
└── src/
    ├── main/java/solver/
    │   ├── app/               точка входу, каталог команд, диспетчер, довідка
    │   ├── core/              парсинг, валідація, вивід, tokenizer, TokenStream
    │   ├── parser/            Parser (TokenStream → Expression)
    │   ├── algebra/           арифметика, рівняння, ряди, масиви, матриці, класи, вирази
    │   └── geometry/          відстані, фігури, трикутники, тіла, перетини, точки, багатокутники
    └── test/java/solver/      JUnit 5 тести (та сама структура пакетів)
```

### Пакети та класи

| Пакет | Класи | Відповідальність |
| :--- | :--- | :--- |
| `app` | `MathSolver`, `CommandCatalog`, `CommandDispatcher`, `HelpPrinter` | `main`, арність і тип аргументів кожної команди, `switch(command)`, довідка |
| `core` | `Messages`, `Output`, `NumberParser`, `ArgumentValidator`, `CharClassifier`, `TokenType`, `Token`, `Tokenizer`, `TokenStream`, `ParseException` | тексти помилок, єдиний формат виводу, парсинг чисел, перевірка арності, лексичний аналіз |
| `algebra` | `Arithmetic`, `Equations`, `NumberTheory`, `TaylorSeries`, `NumericArray`, `BasicStatic`, `Matrix`, `ComplexNumber`, `Fraction`, `Polynomial`, `Expression`, `ConstantExpression`, `VariableExpression`, `BinaryExpression` | алгебраїчні обчислення і моделі |
| `geometry` | `Distances`, `Shapes`, `Triangles`, `Solids`, `Intersections`, `MonteCarlo`, `GeoMath`, `Point`, `Vector2D`, `Line`, `Segment`, `Circle`, `Triangle`, `Polygon`, `Polygon2D`, `Points`, `Figure`, `AreaFigure`, `FigureComposition`, `FigureDistance` | геометричні обчислення і моделі |
| `parser` | `Parser` | побудова дерева `Expression` з потоку токенів |

### Правило залежностей

```text
            app
          /  |  \
   algebra  parser  geometry
        \    |     /
           core
```

* `core` нічого не імпортує з інших пакетів.
* `algebra` і `geometry` залежать тільки від `core`.
* `parser` залежить від `core` і `algebra`.
* `app` бачить усе.

### Як обробляється команда

```text
args = ["solve-quadratic", "1", "-5", "6"]
  │
  ▼  MathSolver.main
1. command = args[0].trim().toLowerCase()
  │
  ▼  CommandCatalog.getExpectedArgsCount        → 3        (CORE-12)
2. відома команда?            ні → "Unknown command: ..."
  │
  ▼  CommandCatalog.getArgKind                  → NUMERIC
3. текстова (tokenize, ...)?  так → вираз склеюється в рядок → dispatchText
  │
  ▼  ArgumentValidator.validateArgs(3, 3)                   (CORE-12)
4. кількість збігається?      ні → "Error: Not enough / Too many arguments"
  │
  ▼  NumberParser.parseArguments                → [1, -5, 6]  (CORE-13)
5. усі числа коректні?        ні → "Invalid number: ..."
  │
  ▼  CommandDispatcher.dispatch → switch(command)            (CORE-11)
6. Equations.solveQuadratic(1, -5, 6)
  │
  ▼  Output.printResult                                      (CORE-14)
Result: x1=2.000000, x2=3.000000
```

---

## 4. Формат вводу та виводу

### Числа на вході

| Формат | Приклади | Примітка |
| :--- | :--- | :--- |
| ціле | `10`, `-5`, `+7` | у межах `long` зберігається як ціле |
| дробове | `3.14`, `.5`, `5.` | роздільник лише крапка: `1,5` → `Invalid number: 1,5` |
| науковий запис | `1e-5`, `2.5E3`, `1e3` | `1e3` приймається і там, де потрібне ціле (`gcd 1e3 30`) |
| не приймається | `abc`, `0x10`, `NaN`, `Infinity`, `1e400` | `Invalid number: <token>` |

### Результат на виході

| Тип результату | Формат | Приклад |
| :--- | :--- | :--- |
| дійсне число | `Result: %f` (6 знаків) | `Result: 5.000000` |
| від'ємний нуль | друкується як нуль | `Result: 0.000000` |
| ціле (`gcd`, `factorial`, `fibonacci`, `circle-segment`) | `Result: %d` | `Result: 120` |
| логічне | `Result: true` / `Result: false` | `Result: true` |
| текст або кілька значень в одному рядку | `Result: ...` | `Result: x1=2.000000, x2=3.000000` |
| кілька рядків (`statistics`, `tokenize`, `token-stats`, `walk-expr`) | кожне значення окремим рядком, без `Result:` | `Count: 5` |
| переповнення (`Infinity`, `NaN`) | не друкується | `Error: Invalid mathematical input` |

### Текстові аргументи

Команди `tokenize`, `token-stats`, `validate-expr`, `walk-expr` отримують вираз, а не числа. Вираз краще брати в лапки: без лапок shell може сам розгорнути `*` або `(`.

```bash
java -cp target/classes solver.app.MathSolver tokenize "2*x + 3"
```

Без лапок аргументи склеюються через пробіл: `tokenize 2*x + 3` = `tokenize "2*x + 3"`.

---

## 5. Повідомлення про помилки

Усі тексти зібрані в `solver.core.Messages` (CORE-15) і друкуються через `Output.printError`.

| Ситуація | Повідомлення | Приклад виклику |
| :--- | :--- | :--- |
| невідома команда | `Unknown command: <name>`, далі `Use 'help' to see available commands.` | `hello 1 2` |
| нечислове значення | `Invalid number: <token>` | `add abc 2` |
| замало аргументів | `Error: Not enough arguments` | `add 5` |
| забагато аргументів | `Error: Too many arguments` | `add 1 2 3` |
| непарна кількість координат | `Error: Coordinates must come in pairs` | `centroid 0 0 1` |
| ділення на нуль | `Error: Division by zero` | `div 1 0` |
| некоректні математичні дані | `Error: Invalid mathematical input` | `sqrt -4`, `triangle-area 1 2 3`, `factorial 21` |
| нерозпізнана лексема | `Error: Invalid token: <lexeme>` | `validate-expr "2..5"` |
| самоперетинний багатокутник | `Error: Polygon must be simple (no self-intersections)` | `polygon-area 0 0 1 1 1 0 0 1` |
| вироджена опукла оболонка | `Error: Convex hull is degenerate` | `convex-hull 0 0 1 1 2 2` |

Програма не падає з винятком на жодному вводі: 257 регресійних кейсів (`scripts/cases.txt`) проходять без stack trace.

---

## 6. Довідник CLI-команд

Скорочення в прикладах: `ms` = `java -cp target/classes solver.app.MathSolver`. Усі приклади нижче — реальний вивід програми.

### 6.1. Службові

| Команда | Аргументи | Що робить |
| :--- | :--- | :--- |
| `help` | — | друкує всі команди, згруповані за розділами |

### 6.2. Алгебра: арифметика (Lab 1)

| Команда | Аргументи | Формула | Обмеження | Приклад → результат |
| :--- | :--- | :--- | :--- | :--- |
| `add` | `a b` | a + b | — | `add 10 25` → `35.000000` |
| `sub` | `a b` | a − b | — | `sub 10 4` → `6.000000` |
| `mul` | `a b` | a · b | — | `mul 2.5 4` → `10.000000` |
| `div` | `a b` | a / b | b ≠ 0 | `div 10 4` → `2.500000` |
| `pow` | `a b` | aᵇ | результат скінченний | `pow 2 10` → `1024.000000` |
| `sqrt` | `x` | √x | x ≥ 0 | `sqrt 144` → `12.000000` |
| `abs` | `x` | \|x\| | — | `abs -7.25` → `7.250000` |

```bash
ms div 1 0
# Error: Division by zero
ms sqrt -4
# Error: Invalid mathematical input
```

### 6.3. Алгебра: рівняння і теорія чисел (Lab 2)

| Команда | Аргументи | Що робить | Обмеження |
| :--- | :--- | :--- | :--- |
| `solve-linear` | `a b` | розв'язує a·x + b = 0 | — |
| `solve-quadratic` | `a b c` | дійсні корені a·x² + b·x + c = 0 | при a = 0 зводиться до лінійного |
| `max3` | `a b c` | найбільше з трьох чисел | — |
| `gcd` | `a b` | НСД, алгоритм Евкліда | цілі числа; `gcd 0 0` = 0 |
| `factorial` | `n` | n! | ціле, 0 ≤ n ≤ 20 (більше не вміщується в `long`) |
| `fibonacci` | `n` | F(n), ітеративно | ціле, 0 ≤ n ≤ 92 |

Усі випадки `solve-quadratic` і `solve-linear`:

| Виклик | Випадок | Результат |
| :--- | :--- | :--- |
| `solve-quadratic 1 -5 6` | D > 0 | `Result: x1=2.000000, x2=3.000000` |
| `solve-quadratic 1 2 1` | D = 0 | `Result: -1.000000` |
| `solve-quadratic 1 1 1` | D < 0 | `Result: No real roots` |
| `solve-quadratic 0 2 4` | a = 0 → 2x + 4 = 0 | `Result: -2.000000` |
| `solve-linear 2 -6` | a ≠ 0 | `Result: 3.000000` |
| `solve-linear 0 0` | 0 = 0 | `Result: Infinite solutions` |
| `solve-linear 0 5` | 5 = 0 | `Result: No solution` |

Корені квадратного рівняння рахуються стійкою формулою (без втрати точності, коли b² ≫ 4ac) і друкуються за зростанням.

```bash
ms gcd 48 18
# Result: 6
ms factorial 5
# Result: 120
ms fibonacci 92
# Result: 7540113804746346429
ms gcd 2.5 5
# Error: Invalid mathematical input
```

### 6.4. Алгебра: ряди Тейлора (Lab 2)

Аргументи всіх команд: `x eps`. Сума накопичується, доки модуль чергового члена ряду не менший за `eps`. Вимоги: `eps > 0`, `x` скінченне. Аліас працює так само, як канонічна назва.

| Команда | Аліас | Функція | Область x | Приклад → результат |
| :--- | :--- | :--- | :--- | :--- |
| `taylor-sin` | `sin-taylor` | sin x | усі x | `1 1e-10` → `0.841471` |
| `taylor-cos` | `series-b` | cos x | усі x | `1 1e-10` → `0.540302` |
| `taylor-sinh` | `series-c` | sh x | усі x | `1 1e-10` → `1.175201` |
| `taylor-cosh` | `series-h` | ch x | усі x | `1 1e-10` → `1.543081` |
| `taylor-exp` | `series-e` | eˣ | усі x | `1 1e-10` → `2.718282` |
| `taylor-ln-one-plus-x` | `series-ln` | ln(1 + x) | \|x\| < 1 | `0.5 1e-12` → `0.405465` |
| `taylor-geom-series` | `series-ye` | 1 / (1 + x) | \|x\| < 1 | `0.5 1e-12` → `0.666667` |
| `taylor-artanh` | `series-j` | arth x | \|x\| < 1 | `0.5 1e-12` → `0.549306` |
| `taylor-sqrt-one-plus-x` | `series-k` | √(1 + x) | \|x\| < 1 | `0.5 1e-12` → `1.224745` |
| `taylor-inv-sqrt-one-plus-x` | `series-l` | 1 / √(1 + x) | \|x\| < 1 | `0.5 1e-12` → `0.816497` |
| `taylor-asin` | `series-m` | arcsin x | \|x\| < 1 | `0.5 1e-12` → `0.523599` |
| `taylor-inv-sq` | `series-z` | 1 / (1 + x)² | \|x\| < 1 | `0.5 1e-12` → `0.444444` |
| `taylor-inv-cube` | `series-i` | 1 / (1 + x)³ | \|x\| < 1 | `0.5 1e-12` → `0.296296` |
| `taylor-inv-one-plus-x2` | `series-y` | 1 / (1 + x²) | \|x\| < 1 | `0.5 1e-12` → `0.800000` |

Особливості реалізації:

* для `sin` і `cos` аргумент спершу зводиться до [−π, π], тому ряд збігається швидко і для великих x;
* для `exp` при x < 0 рахується 1 / e^|x|, щоб уникнути знакозмінного ряду з великими членами;
* кількість ітерацій обмежена 10 000 000 — навіть `eps = 1e-300` не зависає.

```bash
ms taylor-ln-one-plus-x 1 1e-6
# Error: Invalid mathematical input      (x поза областю збіжності)
ms series-e 1 1e-10
# Result: 2.718282
```

### 6.5. Алгебра: набори чисел (Lab 3)

| Команда | Аргументи | Що робить |
| :--- | :--- | :--- |
| `statistics` | `x1 x2 ...` (≥ 1) | кількість, сума, мінімум, максимум, середнє, дисперсія, СКВ |
| `normalize` | `x1 x2 ...` (≥ 1) | min-max нормалізація x′ = (x − min) / (max − min) |

Дисперсія і СКВ генеральні (ділення на n). Якщо всі значення однакові, `normalize` повертає нулі.

```bash
ms statistics 1 4 2 8 5
# Count: 5
# Sum: 20.000000
# Min: 1.000000
# Max: 8.000000
# Mean: 4.000000
# Variance: 6.000000
# StdDev: 2.449490
ms normalize 10 20 30
# Result: [0.000000, 0.500000, 1.000000]
ms normalize 5 5 5
# Result: [0.000000, 0.000000, 0.000000]
```

### 6.6. Геометрія: базові обчислення (Lab 1)

| Команда | Аргументи | Формула | Обмеження | Приклад → результат |
| :--- | :--- | :--- | :--- | :--- |
| `distance` | `x1 y1 x2 y2` | √((x₂−x₁)² + (y₂−y₁)²) | — | `1 2 4 6` → `5.000000` |
| `origin-distance` | `x y` | √(x² + y²) | — | `3 4` → `5.000000` |
| `circle-area` | `r` | π r² | r ≥ 0 | `5` → `78.539816` |
| `circle-circumference` | `r` | 2π r | r ≥ 0 | `5` → `31.415927` |
| `rectangle-area` | `a b` | a · b | a, b ≥ 0 | `3 4` → `12.000000` |
| `rectangle-perimeter` | `a b` | 2(a + b) | a, b ≥ 0 | `3 4` → `14.000000` |

### 6.7. Геометрія: точки (Lab 2)

| Команда | Аргументи | Що робить | Приклад → результат |
| :--- | :--- | :--- | :--- |
| `manhattan-distance` | `x1 y1 x2 y2` | \|x₂ − x₁\| + \|y₂ − y₁\| | `1 2 4 6` → `7.000000` |
| `midpoint` | `x1 y1 x2 y2` | середина відрізка | `1 2 4 6` → `(2.500000, 4.000000)` |
| `quadrant` | `x y` | чверть: `I`, `II`, `III`, `IV`, `AXIS` або `ORIGIN` | `-3 2` → `II` |
| `collinear` | `x1 y1 x2 y2 x3 y3` | чи лежать три точки на одній прямій | `0 0 1 1 2 2` → `true` |
| `ellipse-area` | `a b` | π a b, a, b ≥ 0 | `2 3` → `18.849556` |

`collinear` порівнює векторний добуток з допуском, відносним до довжин відрізків, тому працює і для великих координат. Збіжні точки вважаються колінеарними.

### 6.8. Геометрія: трикутник за сторонами (Lab 2)

Усі команди, крім `triangle-valid`, спершу перевіряють нерівність трикутника: для `1 2 3` (вироджений) або від'ємних сторін друкується `Error: Invalid mathematical input`.

| Команда | Аргументи | Формула | Приклад `3 4 5` → результат |
| :--- | :--- | :--- | :--- |
| `triangle-valid` | `a b c` | a + b > c, a + c > b, b + c > a, усі > 0 | `true` (`1 2 3` → `false`) |
| `triangle-area` | `a b c` | Герон: S = √(p(p−a)(p−b)(p−c)) | `6.000000` |
| `triangle-medians` | `a b c` | mₐ = ½ √(2b² + 2c² − a²) | `m_a=4.272002, m_b=3.605551, m_c=2.500000` |
| `triangle-bisectors` | `a b c` | lₐ = √(bc((b+c)² − a²)) / (b + c) | `l_a=4.216370, l_b=3.354102, l_c=2.424366` |
| `triangle-heights` | `a b c` | hₐ = 2S / a | `h_a=4.000000, h_b=3.000000, h_c=2.400000` |
| `triangle-angles` | `a b c` | теорема косинусів | `A=0.643501 rad (36.869898 deg), B=0.927295 rad (53.130102 deg), C=1.570796 rad (90.000000 deg)` |
| `triangle-area-inradius` | `A B C r` | S = r² (ctg(A/2) + ctg(B/2) + ctg(C/2)) | див. нижче |

`triangle-area-inradius` приймає кути в радіанах. Вимоги: кожен кут у (0, π), A + B + C = π з точністю 10⁻⁴, r > 0.

```bash
ms triangle-area-inradius 1.0471975512 1.0471975512 1.0471975512 1
# Result: 5.196152        (рівносторонній, r = 1 → 3√3)
```

Формула Герона рахується з нормуванням сторін на найбільшу, тому проміжні добутки не переповнюються.

### 6.9. Геометрія: тіла (Lab 2)

| Команда | Аргументи | Формула | Обмеження | Приклад → результат |
| :--- | :--- | :--- | :--- | :--- |
| `cylinder-volume` | `r h` | π r² h | r, h ≥ 0 | `2 5` → `62.831853` |
| `cone-volume` | `r h` | π r² h / 3 | r, h ≥ 0 | `3 1` → `9.424778` |
| `torus-volume` | `r_in r_out` | 2π² R ρ², де R = (r_out + r_in)/2, ρ = (r_out − r_in)/2 | 0 ≤ r_in ≤ r_out | `1 3` → `39.478418` |

### 6.10. Геометрія: перетини (Lab 2)

| Команда | Аргументи | Що робить |
| :--- | :--- | :--- |
| `circle-segment` | `r x y_min len` | кількість точок перетину кола x² + y² = r² з вертикальним відрізком на прямій `x`, що починається в `y_min` (див. [обмеження](#10-відомі-обмеження)) |
| `circle-line` | `cx cy r a b c` | взаємне розташування кола і прямої a·x + b·y + c = 0 |
| `circles-intersect` | `x1 y1 r1 x2 y2 r2` | чи мають два кола спільні точки (дотик включно) |
| `squares-intersect` | `x1 y1 s1 x2 y2 s2` | прямокутник перетину двох квадратів (лівий нижній кут і сторона) |
| `rect-bounding-box` | `x1 y1 x2 y2 x3 y3 x4 y4` | мінімальний прямокутник, що містить два прямокутники (кути: лівий нижній, правий верхній) |

| Виклик | Результат |
| :--- | :--- |
| `circle-segment 5 3 -4 1` | `Result: 1` |
| `circle-line 0 0 1 0 1 0` | `Result: Two points of intersection` |
| `circle-line 0 0 1 0 1 -1` | `Result: One point of tangency` |
| `circle-line 0 0 1 0 1 -2` | `Result: No common points` |
| `circles-intersect 0 0 1 2 0 1` | `Result: true` |
| `squares-intersect 0 0 2 1 1 2` | `Result: Intersect rect: Bottom-Left (1.000000, 1.000000), Top-Right (2.000000, 2.000000)` |
| `squares-intersect 0 0 1 5 5 1` | `Result: Squares do not intersect` |
| `rect-bounding-box 0 0 1 1 2 2 3 3` | `Result: Bounding box: Bottom-Left (0.000000, 0.000000), Top-Right (3.000000, 3.000000)` |

Помилки: від'ємний радіус чи сторона, `a = b = 0` у `circle-line`, кути прямокутника в неправильному порядку у `rect-bounding-box`.

### 6.11. Геометрія: багатокутник і Монте-Карло (Lab 2)

| Команда | Аргументи | Що робить |
| :--- | :--- | :--- |
| `polygon` | `x1 y1 x2 y2 x3 y3 ...` (≥ 3 вершини) | периметр і опуклість |
| `monte-carlo-triangle` | `n` | частка з `n` спроб, у яких три випадкові відрізки з (0, 1) утворюють трикутник |

```bash
ms polygon 0 0 4 0 4 4 0 4
# Result: perimeter=16.000000, convex=true
ms polygon 0 0 2 0 1 1 2 2 0 2
# Result: perimeter=8.828427, convex=false
ms monte-carlo-triangle 100000
# Result: 0.499650
```

Точна ймовірність для `monte-carlo-triangle` дорівнює 1/2. Генератор має фіксований seed 42, тому результат відтворюваний. `n` — ціле з діапазону [1, 2³¹ − 1].

### 6.12. Геометрія: набори точок і багатокутники (Lab 3)

Аргументи — пари координат. Непарна кількість чисел → `Error: Coordinates must come in pairs`.

| Команда | Аргументи | Що робить | Приклад → результат |
| :--- | :--- | :--- | :--- |
| `centroid` | `x1 y1 ...` (≥ 1 точка) | центр мас: (Σxᵢ/n, Σyᵢ/n) | `0 0 4 0 4 4 0 4` → `(2.000000, 2.000000)` |
| `bounding-box` | `x1 y1 ...` (≥ 1 точка) | мінімальні й максимальні x та y | `1 5 -2 3 4 -1` → `minX=-2.000000, maxX=4.000000, minY=-1.000000, maxY=5.000000` |
| `nearest` | `px py x1 y1 ...` | точка набору, найближча до P | `0 0 3 4 1 1 -2 2` → `(1.000000, 1.000000)` |
| `polygon-area` | `x1 y1 x2 y2 x3 y3 ...` | площа простого багатокутника (формула шнурування) | `0 0 4 0 4 3` → `6.000000` |
| `convex-hull` | `x1 y1 x2 y2 x3 y3 ...` | вершини опуклої оболонки проти годинникової стрілки | див. нижче |
| `point-in-polygon` | `px py x1 y1 x2 y2 x3 y3 ...` | чи лежить P усередині (межа включно) | `1 1 0 0 4 0 4 4 0 4` → `true` |

У `nearest` і `point-in-polygon` перша пара — це точка P.

```bash
ms convex-hull 0 0 2 0 1 1 2 2 0 2
# Result: (0.000000, 0.000000) (2.000000, 0.000000) (2.000000, 2.000000) (0.000000, 2.000000)
ms convex-hull 0 0 1 1 2 2
# Error: Convex hull is degenerate
ms polygon-area 0 0 1 1 1 0 0 1
# Error: Polygon must be simple (no self-intersections)
```

### 6.13. Математичні вирази (Lab 3–5)

| Команда | Аргументи | Що робить | Задача |
| :--- | :--- | :--- | :--- |
| `tokenize` | `"expr"` | список токенів: індекс, тип, лексема | C10, C15 |
| `token-stats` | `"expr"` | кількість чисел, ідентифікаторів, операторів, дужок | C9 |
| `validate-expr` | `"expr"` | чи всі лексеми розпізнані (синтаксис не перевіряється) | C8 |
| `walk-expr` | `"expr"` | прохід `TokenStream`: позиція, токен, наступний токен аж до `EOF` | C19 |

```bash
ms tokenize "2*x^2 + 3*x - 5"
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

ms tokenize "sin(x) + sqrt(y)"
# 0: IDENTIFIER sin
# 1: LEFT_PARENTHESIS (
# 2: IDENTIFIER x
# 3: RIGHT_PARENTHESIS )
# 4: PLUS +
# 5: IDENTIFIER sqrt
# 6: LEFT_PARENTHESIS (
# 7: IDENTIFIER y
# 8: RIGHT_PARENTHESIS )

ms token-stats "2*x + 3*x - sin(x)"
# Numbers: 2
# Identifiers: 4
# Operators: 4
# Parentheses: 2

ms validate-expr "2*x + 3"
# Result: true
ms validate-expr "2..5"
# Error: Invalid token: 2..5

ms walk-expr "2 + x"
# 0: NUMBER 2   (next: PLUS +)
# 1: PLUS +   (next: IDENTIFIER x)
# 2: IDENTIFIER x   (next: EOF <EOF>)
```

* `token-stats` для `2*x + 3*x - sin(x)` дає 4 оператори (`*`, `+`, `*`, `-`); у методичці Lab 3 для цього прикладу помилково вказано 3.
* Нерозпізнані лексеми показуються як `UNKNOWN` (`tokenize "1 + 2..5 @"` → `2: UNKNOWN 2..5`, `3: UNKNOWN @`), у статистиці не враховуються.
* Керівні та невидимі символи виводяться у видимій формі (`\n`, `​`).
* Порожній вираз (`walk-expr "   "`) → `Error: Not enough arguments`.

---

## 7. Програмний API (класи Lab 3–5)

Ці класи доступні з Java-коду і покриті тестами. Частина з них поки не має CLI-команди: їх підключатиме Parser і наступні лабораторні.

### 7.1. Algebra

#### `NumericArray` — масив чисел зі статистикою (Lab 3, A1–A5)

| Група | Методи |
| :--- | :--- |
| створення і доступ | `NumericArray(double[])`, `size()`, `get(i)`, `set(i, v)`, `toArray()` |
| агрегати | `sum()`, `mean()`, `min()`, `max()` |
| розсіювання | `variance()`, `standardDeviation()` — генеральні; `sampleVariance()`, `sampleStandardDeviation()` — вибіркові (n − 1) |
| пошук | `contains(v)`, `indexOf(v)`, `count(v)` |
| перетворення | `normalize()` → новий `NumericArray` у [0, 1] |

Масив копіюється при створенні. `BasicStatic` дає ті самі формули дисперсії і СКВ як статичні методи для `double[]`.

#### `Matrix` — матриці (Lab 3, A6)

| Метод | Що робить |
| :--- | :--- |
| `Matrix(rows, cols)`, `Matrix(double[][])` | створення (дані копіюються) |
| `getRows()`, `getCols()`, `get(i, j)`, `getData()` | доступ |
| `multiplyByScalar(k)`, `multiplyMatrix(B)` | множення |
| `determinant()` | визначник (метод Гауса з перестановкою рядків) |
| `trace()`, `trace(k)`, `tracePower(k)` | слід, слід k-ї діагоналі, слід Aᵏ |
| `norm2()`, `norm(p)` | норма Фробеніуса і p-норма (p ≥ 1, також ∞) |
| `inverse()` | обернена матриця |
| `solve(b)` | розв'язок СЛАР A·x = b |
| `realEigenvals()` | дійсні власні числа (зведення до форми Хессенберга й ітерації) |

#### `ComplexNumber` — комплексні числа (Lab 4, A6)

`ComplexNumber(re, im)`, `getReal()`, `getImaginary()`, `add`, `subtract`, `multiply`, `divide`, `abs()` (модуль), `equals` / `hashCode` / `toString`. Незмінний об'єкт.

#### `Fraction` — звичайні дроби (Lab 4, A7)

`Fraction(numerator, denominator)` одразу скорочує дріб і переносить знак у чисельник (`2/-4` → `-1/2`); знаменник 0 → `IllegalArgumentException`. Методи: `add`, `subtract`, `multiply`, `divide`, `compareTo`, `getNumerator()`, `getDenominator()`.

```java
new Fraction(2, 4).add(new Fraction(1, 3));   // 5/6
```

#### `Polynomial` — многочлени (Lab 4, A8)

Коефіцієнти зберігаються від молодшого степеня: 3x² + 2x − 5 → `[-5, 2, 3]`.

| Група | Методи |
| :--- | :--- |
| структура | `degree()`, `isZero()`, `getCoefficient(i)`, `getCoefficients()` |
| обчислення | `evaluate(x)` — схема Горнера |
| аналіз | `derivative()`, `roots()` — дійсні корені |
| арифметика | `add`, `subtract`, `multiply` |
| ділення | `divideAndRemainder(d)` → `[частка, остача]`, `divide(d)`, `remainder(d)` |
| порівняння | `equals(Object)`, `equals(other, eps)` |

```java
Polynomial p = new Polynomial(new double[]{-5, 2, 3});   // 3x² + 2x − 5
p.evaluate(1);       // 0.0
p.derivative();      // 6x + 2
p.roots();           // [-1.666..., 1.0]
```

#### `Expression` — дерево виразу (Lab 5, A11–A15)

```text
Expression (abstract)
├── ConstantExpression   5, 3.14, -7
├── VariableExpression   x, y, z
└── BinaryExpression     left  operator  right
                         Operator: ADD (+), SUBTRACT (-), MULTIPLY (*), DIVIDE (/)
```

| Метод | Що робить |
| :--- | :--- |
| `evaluate(Map<String, Double> variables)` | обчислює вираз для заданих значень змінних |
| `evaluate()` | обчислює вираз без змінних |
| `BinaryExpression(left, "+", right)` | оператор можна задати символом (`Operator.fromSymbol`) |

```java
Expression e = new BinaryExpression(
        new VariableExpression("x"), "+", new ConstantExpression(5));
e.evaluate(Map.of("x", 10.0));   // 15.0
```

### 7.2. Geometry

#### Ієрархія фігур (Lab 4–5)

```text
Figure                       contains(P), distanceTo(P), distanceTo(Figure),
│                            translate(v), rotate(angle, center), scale(k, center)
├── Point
├── Line
├── Segment
└── AreaFigure               + area(), perimeter()
    ├── Circle
    ├── Triangle
    └── Polygon
Vector2D                     (не фігура: напрямок і зсув)
FigureComposition            набір AreaFigure: сумарна площа, площа об'єднання і перетину
```

Усі класи незмінні (`final`, поля `final`): перетворення повертають новий об'єкт.

| Клас | Створення | Основні методи |
| :--- | :--- | :--- |
| `Point` | `Point.of(x, y)` | `distanceTo`, `vectorTo`, `midpoint`, `approxEquals` |
| `Vector2D` | `Vector2D.of(x, y)`, `between(A, B)`, `ZERO` | `add`, `subtract`, `scale`, `negate`, `dot`, `cross`, `length`, `normalize`, `perpendicular`, `rotate`, `angleTo`, `isParallelTo` |
| `Line` | `through(A, B)`, `of(P, v)`, `fromEquation(a, b, c)` | `coefficients`, `slope`, `projection`, `isParallelTo`, `isPerpendicularTo`, `coincidesWith`, `intersection`, `distanceTo` |
| `Segment` | `of(A, B)`, `of(x1, y1, x2, y2)` | `length`, `midpoint`, `toLine`, `closestPointTo`, `intersects`, `intersection`, `isCollinearWith`, `isParallelTo` |
| `Circle` | `of(center, r)`, `of(x, y, r)` | `area`, `circumference`, `diameter`, `onBoundary`, `distanceToBoundary`, `intersects`, `intersection(Circle)`, `intersection(Line)`, `intersectionArea` |
| `Triangle` | `of(A, B, C)` | `vertices`, `sides`, `area`, `perimeter`, `centroid`, `isDegenerate`, `toPolygon` |
| `Polygon` | `of(P1, P2, ...)` | `vertices`, `edges`, `area`, `perimeter`, `isConvex`, `triangulate` |
| `FigureComposition` | `of(F1, F2, ...)` | `sumOfAreas`, `unionArea`, `intersectionArea`, `hasOverlaps` |

#### Статичні утиліти (Lab 3)

| Клас | Методи |
| :--- | :--- |
| `Points` | `distance(A, B)`, `boundingBox(Point[])`, `centroid(Point[])`, `nearest(P, Point[])` |
| `Polygon2D` | `perimeter`, `area`, `isConvex`, `isSimple`, `convexHull`, `triangulate`, `contains`, `intersection`, `translate`, `scale`, `rotate` — над масивом `Point[]` |

---

## 8. Tokenizer, TokenStream і Parser

Конвеєр обробки виразу. Tokenizer відповідає на питання «які елементи містить текст?», Parser — «яка математична структура цих елементів?».

```text
"2 + x * 5"
    │  Tokenizer.tokenize              core, C15
    ▼
Token[]       NUMBER(2) PLUS(+) IDENTIFIER(x) MULTIPLY(*) NUMBER(5)
    │  new TokenStream(tokens)         core, C16–C19
    ▼
TokenStream   position → 0  1  2  3  4 │ EOF
    │  parser.parse(stream)            parser, C20
    ▼
Expression                             algebra, A11–A15
```

### 8.1. Як текст стає токенами (Lab 3 → Lab 4)

```text
"2*x + sin(x)"
 ↓ toCharacters        ['2','*','x','+','s','i','n','(','x',')']      C1
 ↓ lexemes             ["2","*","x","+","sin","(","x",")"]            C7
 ↓ tokenizeTypes       [NUMBER, MULTIPLY, IDENTIFIER, PLUS, ...]      C4
 ↓ tokenize            [NUMBER(2), MULTIPLY(*), IDENTIFIER(x), ...]   C15
```

### 8.2. `TokenType`

| Група | Значення |
| :--- | :--- |
| операнди | `NUMBER`, `IDENTIFIER` |
| оператори | `PLUS`, `MINUS`, `MULTIPLY`, `DIVIDE`, `POWER` |
| дужки | `LEFT_PARENTHESIS`, `RIGHT_PARENTHESIS` |
| службові | `UNKNOWN` — нерозпізнана лексема; `EOF` — кінець потоку (створює лише `TokenStream`) |

### 8.3. Класи `CharClassifier` і `Tokenizer`

| Метод | Повертає | Задача |
| :--- | :--- | :--- |
| `CharClassifier.isDigit / isLetter / isOperator / isDelimiter / isWhitespace / isDecimalPoint` | `boolean` | C2 |
| `Tokenizer.toCharacters(String)` | `char[]` | C1 |
| `Tokenizer.lexemes(String)` | `String[]` | C7 |
| `Tokenizer.tokenizeTypes(String)` | `TokenType[]` | C4–C6 |
| `Tokenizer.tokenize(String)` | `Token[]` | C15 |
| `Tokenizer.isValid(String)` | `boolean` — непорожній і без `UNKNOWN` | C8 |
| `Tokenizer.tokenStatistics(String)` | `int[4]` — числа, ідентифікатори, оператори, дужки | C9 |

Правила розбиття:

| Вираз | Токени |
| :--- | :--- |
| `-5 * (x - 3.14)` | `NUMBER(-5) MULTIPLY(*) LEFT_PARENTHESIS(() IDENTIFIER(x) MINUS(-) NUMBER(3.14) RIGHT_PARENTHESIS())` |
| `3 - -5` | `NUMBER(3) MINUS(-) NUMBER(-5)` |
| `x-1` | `IDENTIFIER(x) MINUS(-) NUMBER(1)` |
| `2x` | `NUMBER(2) IDENTIFIER(x)` |
| `2..5 + x@` | `UNKNOWN(2..5) PLUS(+) IDENTIFIER(x) UNKNOWN(@)` |
| `""`, `"   "` | порожній масив |

Мінус входить у лексему числа лише на початку виразу або після оператора чи `(`.

### 8.4. Клас `Token` (Lab 4, C11–C14)

| Член | Опис |
| :--- | :--- |
| `Token(TokenType type, String lexeme)` | єдиний конструктор |
| `getType()`, `getLexeme()` | гетери; сетерів немає, об'єкт незмінний |
| `toString()` | `Token{type=NUMBER, lexeme='3.14'}` — для логів і тестів |
| `toCompactString()` | `NUMBER(3.14)` — для документації |
| `equals`, `hashCode` | рівність за типом і текстом лексеми |

| Виклик | Результат |
| :--- | :--- |
| `new Token(NUMBER, "3.14")` | ✅ |
| `new Token(null, "3.14")` | `NullPointerException: type must not be null` |
| `new Token(NUMBER, null)` | `NullPointerException: lexeme must not be null` |
| `new Token(NUMBER, "")` | `IllegalArgumentException: lexeme must not be empty` |

Логічна рівність:

```java
Token a = new Token(TokenType.NUMBER, "2");
Token b = new Token(TokenType.NUMBER, "2");
a == b          // false — різні об'єкти
a.equals(b)     // true  — однаковий тип і лексема
```

Лексеми порівнюються як текст: `NUMBER(2)` ≠ `NUMBER(2.0)`. Позиція у виразі не зберігається. `equals(null)` повертає `false`.

### 8.5. Клас `TokenStream` (Lab 5, C16–C19)

Курсор, що рухається лише вперед. Масив токенів копіюється в конструкторі.

| Метод | Повертає | Рухає курсор | Примітка |
| :--- | :--- | :--- | :--- |
| `TokenStream(Token[])` | — | — | `null` або `null`-елемент → `IllegalArgumentException` |
| `current()` | `Token` | ні | за межами → `EOF` |
| `peek()` | `Token` | ні | те саме, що `peek(1)` |
| `peek(int k)` | `Token` | ні | `peek(0) == current()`; `k < 0` → `IllegalArgumentException` (C17) |
| `next()` | `Token` | так | повертає **попередній** поточний токен, як `Iterator.next()` |
| `hasNext()` | `boolean` | ні | `true`, поки лишився реальний токен |
| `consume(TokenType)` | `Token` | так, якщо тип збігся | інакше `ParseException`, курсор стоїть (C18) |
| `reset()` | — | на 0 | повторний прохід |
| `position()` | `int` | ні | від 0; у кінці дорівнює кількості токенів |

```text
2 + x * 5
^
current() → NUMBER(2)     peek(1) → PLUS(+)        peek(2) → IDENTIFIER(x)
peek(3)   → MULTIPLY(*)   peek(4) → NUMBER(5)      peek(5) → EOF(<EOF>)
```

За межами потоку `current()`, `peek(k)`, `next()` повертають `EOF`, а не `null` чи `ArrayIndexOutOfBoundsException`. `peek(Integer.MAX_VALUE)` теж дає `EOF`.

### 8.6. `ParseException` і `Parser` (C20)

`ParseException` — unchecked-виняток з `getPosition()`.

| Джерело | Повідомлення |
| :--- | :--- |
| `consume` | `Error: Expected NUMBER but found IDENTIFIER('x') at position 2` |
| `Parser`, порожній вираз | `Error: Empty expression` |
| `Parser`, невідомий токен | `Error: Unknown token '@' at position 3` |

`solver.parser.Parser` фіксує контракт: `parse(TokenStream)` і обгортка `parse(String)`. Поточна поведінка:

| Вхід | Результат |
| :--- | :--- |
| `null` | `IllegalArgumentException` |
| `""`, `"   "` | `ParseException: Error: Empty expression` |
| `2 + x@` | `ParseException: Error: Unknown token '@' at position 3` |
| `2*x^2 + 3*x - 5` | `UnsupportedOperationException: Parser is not implemented yet` |

Цільова граматика (EBNF, також у Javadoc класу `Parser`):

```text
expression = term   { ("+" | "-") term } ;
term       = power  { ("*" | "/") power } ;
power      = unary  [ "^" power ] ;
unary      = [ "-" ] unary | primary ;
primary    = NUMBER | IDENTIFIER [ "(" expression ")" ] | "(" expression ")" ;
```

---

## 9. Тестування

### Модульні тести (JUnit 5)

```bash
mvn test
```

| Пакет | Тестових класів | Перевірок* | Що покрито |
| :--- | ---: | ---: | :--- |
| `algebra` | 14 | 856 | арифметика, рівняння, НСД / факторіал / Фібоначчі, 14 рядів Тейлора проти `Math.*`, `NumericArray`, `Matrix`, `ComplexNumber`, `Fraction`, `Polynomial`, дерево `Expression` |
| `geometry` | 17 | 297 | усі CLI-методи Lab 1–2 (`Distances`, `Shapes`, `Solids`, `Triangles`, `Intersections`, `MonteCarlo`, `Polygon.processPolygon`), примітиви `GeoMath`, класи Lab 3–5 |
| `core` | 14 | 246 | парсинг чисел, валідація арності, формат виводу, `CharClassifier`, `Tokenizer`, `Token`, `TokenStream`, `consume` |
| `app` | 6 | 147 | `main` наскрізно, каталог команд, маршрутизація кожної команди і всіх 14 аліасів, довідка, `walk-expr` |
| `parser` | 2 | 14 | контракт `Parser`, конвеєр Tokenizer → TokenStream → Parser |
| **Разом** | **53** | **≈1560** | |

\* з урахуванням кожного набору параметрів у `@ParameterizedTest`.

### Регресія CLI (golden master)

`scripts/cases.txt` містить 257 викликів: нормальні, граничні та некоректні випадки для кожної команди. Скрипт друкує фактичний вивід, а `diff` показує, що змінилося після рефакторингу.

```bash
bash scripts/regression.sh target/classes solver.app.MathSolver > after.txt
diff before.txt after.txt
```

Windows PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\regression.ps1 -Cp target\classes -Main solver.app.MathSolver -Out after.txt
git diff --no-index before.txt after.txt
```

---
