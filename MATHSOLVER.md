# JavaMathSolver 0.3

## Опис проєкту
Консольна математична утиліта (CLI) мовою Java, розроблена в межах Лабораторної роботи №1. Програма зчитує команди та аргументи через аргументи командного рядка (`String[] args`), виконує алгебраїчні та геометричні обчислення, а також містить вбудовану систему довідки.

## Вимоги
* Встановлений **Java Development Kit (JDK) 17** або вище.
* Доступ до командного рядка (консолі/терміналу).

## Структура проєкту (з версії 0.3)
```text
src/main/java/solver/
├── app/       MathSolver (main), CommandDispatcher, CommandCatalog, HelpPrinter
├── core/      Messages, Output, NumberParser, ArgumentValidator (+ Tokenizer у Lab 3)
├── algebra/   Arithmetic, Equations, NumberTheory, TaylorSeries
└── geometry/  Distances, Shapes, Triangles, Solids, Intersections, Polygon, MonteCarlo
src/test/java/solver/   JUnit 5 тести
scripts/                регресійні кейси CLI
```
Правило залежностей: `core` не імпортує інші пакети; `algebra` і `geometry` залежать тільки від `core`; `app` бачить усе.

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

### 7. Невідома команда (Unknown command)
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
| `validate-expr` | `"expr"` | Перевірка, що всі лексеми виразу розпізнані (C8); синтаксис не перевіряється |
