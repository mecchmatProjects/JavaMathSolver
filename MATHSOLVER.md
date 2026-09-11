# JavaMathSolver 0.1

## Опис проєкту
Консольна математична утиліта (CLI) мовою Java, розроблена в межах Лабораторної роботи №1. Програма зчитує команди та аргументи через аргументи командного рядка (`String[] args`), виконує алгебраїчні та геометричні обчислення, а також містить вбудовану систему довідки.

## Вимоги
* Встановлений **Java Development Kit (JDK) 17** або вище.
* Доступ до командного рядка (консолі/терміналу).

## Спосіб компіляції
Для компіляції вихідного коду виконайте команду з кореневої директорії проєкту:
```bash
javac src/MathSolver.java
```
У результаті у директорії `src/` буде створено скомпільований файл `MathSolver.class`.

## Спосіб запуску
Запуск програми виконується з явним зазначенням шляху до класів (`-cp src`):
```bash
java -cp src MathSolver <command> <arguments>
```

Отримання списку доступних команд:
```bash
java -cp src MathSolver help
```

## Приклади
Нижче наведено приклади виконання основних математичних команд та реакції на помилкові виклики:

### 1. Додавання (Addition)
```bash
java -cp src MathSolver add 10 25
# 10 + 25 = 35
```

### 2. Множення (Multiplication)
```bash
java -cp src MathSolver mul 4 7
# 4 * 7 = 28
```

### 3. Піднесення до степеня (Power)
```bash
java -cp src MathSolver pow 2 10
# 2^10 = 1024
```

### 4. Квадратний корінь (Square root)
```bash
java -cp src MathSolver sqrt 144
# sqrt(144) = 12
```

### 5. Обчислення відстані між точками (Distance)
```bash
java -cp src MathSolver distance 1 2 4 6
# distance = 5
```

### 6. Невідома команда (Unknown command)
```bash
java -cp src MathSolver hello 1 2
# Unknown command: hello
# Use 'help' to see available commands.
```

---

## Таблиця команд (Milestone 0.1)

| Команда | Аргументи | Опис операції | Приклад виклику |
| :--- | :--- | :--- | :--- |
| `help` | — | Показати перелік усіх доступних команд | `java -cp src MathSolver help` |
| `add` | `a b` | Додавання двох чисел ($a + b$) | `java -cp src MathSolver add 10 25` |
| `sub` | `a b` | Віднімання чисел ($a - b$) | `java -cp src MathSolver sub 10 4` |
| `mul` | `a b` | Множення чисел ($a \cdot b$) | `java -cp src MathSolver mul 4 7` |
| `div` | `a b` | Ділення чисел ($a / b$) | `java -cp src MathSolver div 10 2` |
| `pow` | `a b` | Піднесення до степеня ($a^b$) | `java -cp src MathSolver pow 2 10` |
| `sqrt` | `x` | Квадратний корінь ($\sqrt{x}$) | `java -cp src MathSolver sqrt 144` |
| `abs` | `x` | Абсолютне значення ($\|x\|$) | `java -cp src MathSolver abs -5` |
| `distance` | `x1 y1 x2 y2` | Евклідова відстань між точками | `java -cp src MathSolver distance 1 2 4 6` |
| `origin-distance` | `x y` | Відстань точки від початку координат | `java -cp src MathSolver origin-distance 3 4` |
| `circle-area` | `r` | Площа круга ($\pi r^2$) | `java -cp src MathSolver circle-area 5` |
| `circle-circumference` | `r` | Довжина кола ($2\pi r$) | `java -cp src MathSolver circle-circumference 5` |
| `rectangle-area` | `a b` | Площа прямокутника ($a \cdot b$) | `java -cp src MathSolver rectangle-area 3 4` |
| `rectangle-perimeter` | `a b` | Периметр прямокутника ($2(a+b)$) | `java -cp src MathSolver rectangle-perimeter 3 4` |