# JavaMathSolver 0.4

## Опис проєкту
Консольна математична утиліта (CLI) мовою Java, розроблена в межах Лабораторної роботи №1. Програма зчитує команди та аргументи через аргументи командного рядка (`String[] args`), виконує алгебраїчні та геометричні обчислення, а також містить вбудовану систему довідки.

## Вимоги
* Встановлений **Java Development Kit (JDK) 17** або вище.
* Доступ до командного рядка (консолі/терміналу).

## Структура проєкту (з версії 0.3)
```text
src/main/java/solver/
├── app/       MathSolver (main), CommandDispatcher, CommandCatalog, HelpPrinter
├── core/      Messages, Output, NumberParser, ArgumentValidator, CharClassifier, TokenType, Token, Tokenizer
├── algebra/   Arithmetic, Equations, NumberTheory, TaylorSeries
└── geometry/  Distances, Shapes, Triangles, Solids, Intersections, Polygon, MonteCarlo
src/test/java/solver/   JUnit 5 тести
scripts/                регресійні кейси CLI
```
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
Класи з'являться в `target/classes/`. Тести: `mvn test`.

Варіант 2 — без Maven (лише JDK):
```bash
javac -encoding UTF-8 -d out $(find src/main/java -name "*.java")
```
У Windows PowerShell:
```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src/main/java -Filter *.java).FullName
```

## Спосіб запуску
Запуск програми виконується з явним зазначенням шляху до класів (`-cp target/classes`):
```bash
java -cp target/classes solver.app.MathSolver <command> <arguments>
```

Отримання списку доступних команд:
```bash
java -cp target/classes solver.app.MathSolver help
```

## Приклади
Нижче наведено приклади виконання основних математичних команд та реакції на помилкові виклики:

### 1. Додавання (Addition)
```bash
java -cp target/classes solver.app.MathSolver add 10 25
# Result: 35.000000
```

### 2. Множення (Multiplication)
```bash
java -cp target/classes solver.app.MathSolver mul 4 7
# Result: 28.000000
```

### 3. Піднесення до степеня (Power)
```bash
java -cp target/classes solver.app.MathSolver pow 2 10
# Result: 1024.000000
```

### 4. Квадратний корінь (Square root)
```bash
java -cp target/classes solver.app.MathSolver sqrt 144
# Result: 12.000000
```

### 5. Обчислення відстані між точками (Distance)
```bash
java -cp target/classes solver.app.MathSolver distance 1 2 4 6
# Result: 5.000000
```

### 6. Перевірка виразу (validate-expr, C8)
```bash
java -cp target/classes solver.app.MathSolver validate-expr "2*x + 3"
# Result: true
java -cp target/classes solver.app.MathSolver validate-expr "2..5"
# Error: Invalid token: 2..5
```

### 7. Розбиття виразу на токени (tokenize, C10 / C15)
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
Нерозпізнані лексеми показуються як `UNKNOWN` (напр. `2: UNKNOWN 2..5`); керівні символи — у видимій формі (`\n`).

### 8. Статистика токенів (token-stats, C9)
```bash
java -cp target/classes solver.app.MathSolver token-stats "2*x + 3*x - sin(x)"
# Numbers: 2
# Identifiers: 4
# Operators: 4
# Parentheses: 2
```
Лексеми `UNKNOWN` не враховуються. Операторів тут 4 (`*`, `+`, `*`, `-`); у методичці Lab 3 для цього прикладу помилково вказано 3.

> Вираз краще брати в лапки. Без лапок аргументи склеюються через пробіл (`tokenize 2*x + 3` = `tokenize "2*x + 3"`), але shell може розгорнути `*` чи `(` сам.

### 9. Невідома команда (Unknown command)
```bash
java -cp target/classes solver.app.MathSolver hello 1 2
# Unknown command: hello
# Use 'help' to see available commands.
```

---

## Таблиця команд

| Команда | Аргументи | Опис операції |
| :--- | :--- | :--- |
| `help` | — | Показати перелік усіх доступних команд |
| `add` | `a b` | Додавання двох чисел ($a + b$) |
| `sub` | `a b` | Віднімання чисел ($a - b$) |
| `mul` | `a b` | Множення чисел ($a \cdot b$) |
| `div` | `a b` | Ділення чисел ($a / b$) |
| `pow` | `a b` | Піднесення до степеня ($a^b$) |
| `sqrt` | `x` | Квадратний корінь ($\sqrt{x}$) |
| `abs` | `x` | Абсолютне значення ($\|x\|$) |
| `distance` | `x1 y1 x2 y2` | Евклідова відстань між точками |
| `origin-distance` | `x y` | Відстань точки від початку координат |
| `circle-area` | `r` | Площа круга ($\pi r^2$) |
| `circle-circumference` | `r` | Довжина кола ($2\pi r$) |
| `rectangle-area` | `a b` | Площа прямокутника ($a \cdot b$) |
| `rectangle-perimeter` | `a b` | Периметр прямокутника ($2(a+b)$) |
| `solve-linear` | `a b` | Розв'язання лінійного рівняння ($ax+b=0$) |
| `solve-quadratic` | `a b c` | Розв'язання квадратного рівняння ($ax^2+bx+c=0$) |
| `max3` | `a b c` | Максимальне з трьох чисел |
| `gcd` | `a b` | Найбільший спільний дільник (алгоритм Евкліда) |
| `factorial` | `n` | Факторіал числа ($n!$) |
| `fibonacci` | `n` | N-те число Фібоначчі |
| `triangle-area` | `a b c` | Площа трикутника за трьома сторонами (формула Герона) |
| `triangle-valid` | `a b c` | Перевірка, чи існує трикутник із заданими сторонами |
| `quadrant` | `x y` | Визначення чверті координатної площини для точки |
| `manhattan-distance` | `x1 y1 x2 y2` | Манхеттенська відстань між точками |
| `midpoint` | `x1 y1 x2 y2` | Середина відрізка |
| `collinear` | `x1 y1 x2 y2 x3 y3` | Перевірка, чи лежать три точки на одній прямій |
| `tokenize` | `"expr"` | Список токенів `Token[]`: індекс, тип, лексема (C10, C15) |
| `token-stats` | `"expr"` | Кількість чисел, ідентифікаторів, операторів, дужок (C9) |
| `validate-expr` | `"expr"` | Перевірка, що всі лексеми виразу розпізнані (C8); синтаксис не перевіряється |
