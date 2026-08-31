# Наскрізний проєкт дисципліни Java
# **MathSolver — математичний програмний розв'язувач**

## Загальна концепція

Протягом семестру студенти розробляють один спільний програмний продукт — **MathSolver**, програму, яка приймає математичну задачу у текстовому вигляді, розпізнає її структуру, будує внутрішнє представлення та виконує відповідний алгоритм розв'язання.

Фінальна ідея системи:

```text
Користувач
    │
    │ "solve x^2 - 5*x + 6 = 0"
    ▼
┌──────────────────┐
│ Text processing  │
│ Tokenizer/Parser │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ Mathematical AST │
└────────┬─────────┘
         │
         ├───────────────┐
         ▼               ▼
     Algebra          Geometry
         │               │
         └───────┬───────┘
                 ▼
              Solver
                 │
                 ▼
              Result
```

На перших лабораторних ця архітектура існує лише як **цільова модель**. На кожній наступній лабораторній одна її частина стає реальною.

---

# Організація команди

20 студентів працюють в одному Git-репозиторії.

Пропонується чотири робочі групи:

| Група | Кількість | Напрям |
|---|---:|---|
| Algebra | 6 | алгебраїчні обчислення та рівняння |
| Geometry | 6 | точки, вектори, прямі, геометричні об'єкти |
| Text / Expression | 5 | текст, токенізація, синтаксис математичних виразів |
| Core / Integration | 3 | спільна інфраструктура, запуск, інтеграція |

На перших лабораторних групи ще не повинні бути жорстко ізольованими. Студентам важливо пройти шлях від простого процедурного коду до архітектури, у якій ці компоненти взаємодіють.

---

# Правило кожної лабораторної

Кожна лабораторна має чотири частини:

1. **Java-навичка**
2. **Математична задача**
3. **Зміна MathSolver**
4. **Pull Request**

Тобто студент не просто здає `.java`-файл.

Він проходить цикл:

```text
Issue
 ↓
Branch
 ↓
Implementation
 ↓
Test / manual verification
 ↓
Pull Request
 ↓
Code Review
 ↓
Merge
```

---

# Лабораторна робота №1
# «Перший модуль MathSolver»

### Тема

Java 21: структура програми, JVM/JDK, компіляція та запуск, `main`, змінні, примітивні типи, `String`, форматований вивід, `Math`, аргументи командного рядка.

Це безпосередньо відповідає першому розділу курсу: Java-платформа, перший клас, прості типи, `String`, форматований вивід, `Math` та command-line arguments.

---

## Мета

Створити першу працездатну версію MathSolver, яка:

- запускається з командного рядка;
- приймає математичну операцію;
- приймає числові аргументи;
- виконує обчислення;
- виводить результат у зрозумілому форматі.

На цьому етапі **ще немає складного parser-а**.

Наприклад:

```text
java MathSolver add 2 3
```

має дати:

```text
Result: 5.000000
```

А:

```text
java MathSolver sqrt 25
```

дає:

```text
Result: 5.000000
```

---

## Що вивчаємо з Java

Студент повинен використати:

- `public class`;
- `main`;
- `String[] args`;
- `int`;
- `double`;
- `boolean`;
- перетворення `String → double`;
- `System.out.println`;
- `System.out.printf`;
- `Math.sqrt`;
- `Math.pow`;
- `Math.abs`;
- компіляцію через `javac`;
- запуск через `java`.

Перший розділ спеціально використовує command-line arguments і показує `Math`, форматований вивід та різницю між примітивами й типами-посиланнями.

---

# Завдання

## Обов'язкова частина

Реалізувати:

```text
add
sub
mul
div
pow
sqrt
abs
```

Приклади:

```text
java MathSolver add 2 3
java MathSolver mul 4 5
java MathSolver pow 2 10
java MathSolver sqrt 144
```

---

## Algebra group

Реалізувати базові арифметичні операції:

```text
add
sub
mul
div
pow
```

---

## Geometry group

Реалізувати перші геометричні обчислення:

```text
distance x1 y1 x2 y2
```

Наприклад:

```text
java MathSolver distance 1 2 4 6
```

Результат:

```text
Distance = 5.000000
```

---

## Text group

Відповідає за розбір аргументів:

```text
distance
1
2
4
6
```

Студенти повинні визначити:

- яка операція задана;
- скільки аргументів потрібно;
- як аргументи перетворити з `String` у число.

---

## Core group

Створює базову структуру проєкту:

```text
mathsolver/
└── src/
    └── MathSolver.java
```

та README із:

- описом програми;
- командами компіляції;
- прикладами запуску.

---

# Pull Requests

Кожен студент отримує окремий Issue.

Наприклад:

```text
#12 Implement square root operation
```

Студент створює:

```text
feature/sqrt-operation
```

пише код і відкриває PR.

PR повинен містити:

- реалізацію;
- приклад запуску;
- короткий опис рішення.

---

# Результат лабораторної

Наприкінці першої лабораторної:

```text
MathSolver 0.1
```

уміє виконувати прості математичні операції.

Це **дуже маленька програма**, але це вже справжній продукт, який надалі буде розширюватися.

---

# Лабораторна робота №2
# «Вирази, оператори та перший алгоритм Solver»

### Тема

Арифметичні та логічні вирази, оператори, `if`, `switch`, цикли, методи.

Це відповідає другому розділу плану курсу: арифметика, ділення, логічні вирази, `if`, `switch`, цикли та статичні методи.

---

# Нова проблема

Перша версія працює:

```text
java MathSolver add 2 3
```

Але це ще не Solver.

Ми хочемо:

```text
java MathSolver 2 + 3
```

або хоча б:

```text
java MathSolver add 2 3
```

з нормальною системою команд.

---

# Завдання

Переробити програму так, щоб логіка операцій була розділена на методи:

```java
add(...)
subtract(...)
multiply(...)
divide(...)
power(...)
```

Головний метод повинен лише організовувати виконання.

Поступово рухаємося від:

```java
public static void main(...)
```

із великою кількістю коду до:

```text
main
 │
 ├── parse command
 ├── validate arguments
 └── execute operation
```

---

# Algebra

Додати:

```text
linear equation
```

Наприклад:

```text
solve-linear 2 5 13
```

що означає:

\[
2x+5=13.
\]

Результат:

```text
x = 4
```

---

# Geometry

Додати:

```text
circle-area r
rectangle-area a b
triangle-area a h
```

Наприклад:

```text
java MathSolver circle-area 5
```

---

# Text

Створити перший простий command dispatcher:

```text
add
sub
mul
sqrt
distance
circle-area
```

Замість великого набору незалежних `if` поступово перейти до структурованої обробки команд.

---

# Додаткове завдання

Реалізувати команду:

```text
help
```

Результат:

```text
MathSolver commands:

add a b
sub a b
mul a b
div a b
sqrt x
pow x y
distance x1 y1 x2 y2
circle-area r
```

---

# Pull Request

PR повинен демонструвати, що студент:

- створив окремий метод;
- використав параметри;
- повертає значення через `return`;
- не дублює код без необхідності.

---

# Результат

```text
MathSolver 0.2
```

Тепер програма вже має:

```text
CLI
 ↓
Command
 ↓
Method
 ↓
Calculation
 ↓
Result
```

---

# Лабораторна робота №3
# «Дані Solver-а: масиви, посилання та множини математичних об'єктів»

### Тема

Посилання, `null`, `new`, масиви, передавання аргументів у методи.

Це відповідає третьому розділу плану: primitive/reference distinction, `null`, створення об'єктів, масиви примітивів і посилань та передавання аргументів.

---

# Нова проблема

Solver вже працює з одним виразом.

А тепер потрібно обробляти:

```text
5
10
15
20
25
```

або набір точок:

```text
(1,2)
(4,6)
(3,7)
(0,0)
```

---

# Algebra

Реалізувати роботу з масивом чисел:

```text
sum
average
min
max
```

Наприклад:

```text
java MathSolver average 2 4 6 8 10
```

---

# Geometry

Створити масив точок.

Поки що можна використати просту структуру:

```java
double[] x;
double[] y;
```

або, якщо курс уже дозволяє це, підготувати перехід до `Point`.

Завдання:

```text
distance-all
```

обчислює відстані між послідовними точками.

---

# Text

Розібрати довільну кількість аргументів командного рядка:

```text
java MathSolver average 1 2 3 4 5 6 7 8
```

Студенти повинні навчитися:

```text
args.length
args[i]
Double.parseDouble(args[i])
```

---

# Додаткове завдання

Реалізувати:

```text
help <command>
```

Наприклад:

```text
java MathSolver help average
```

---

# Важливе обговорення

На цій лабораторній вперше ставимо питання:

> Чому Solver не повинен представляти все як `double`?

Наприклад:

```text
Point
Equation
Polynomial
Vector
Expression
```

— це різні математичні сутності.

Це створює мотивацію для наступної лабораторної — **власних класів**.

---

# Результат

```text
MathSolver 0.3
```

У Solver з'являються перші колекції математичних даних.

---

# Лабораторна робота №4
# «Об'єктна модель математичного Solver-а»

### Тема

Власні класи, поля, методи, конструктори, інкапсуляція, `private/public`, `equals`, `hashCode`, `toString`, пакети та Javadoc.

Цей блок прямо відповідає четвертому розділу курсу, де як доменні приклади запропоновані, зокрема, точки, дроби та комплексні числа.

---

# Це ключова лабораторна

До цього моменту ми писали:

```text
процедури
```

Тепер починаємо будувати:

```text
МАТЕМАТИЧНУ МОДЕЛЬ
```

---

# Geometry

Створити:

```java
Point2D
Vector2D
```

Наприклад:

```java
Point2D p = new Point2D(1, 2);
Point2D q = new Point2D(4, 6);
```

Методи:

```text
distanceTo()
translate()
toString()
```

---

# Algebra

Створити:

```text
Fraction
ComplexNumber
```

Наприклад:

\[
\frac{2}{3}
\]

і

\[
3+4i.
\]

---

# Text

Створити:

```text
Command
CommandParser
```

Поки що вони можуть бути дуже простими.

Наприклад:

```text
"distance 1 2 4 6"
```

перетворюється на:

```text
Command
operation = "distance"
arguments = [1, 2, 4, 6]
```

---

# Архітектура

Після цієї лабораторної структура приблизно така:

```text
src/
├── algebra/
│   ├── Fraction.java
│   └── ComplexNumber.java
│
├── geometry/
│   ├── Point2D.java
│   └── Vector2D.java
│
├── parser/
│   └── Command.java
│
└── MathSolver.java
```

---

# Обов'язкова вимога

Кожен доменний клас повинен мати:

- приватні поля;
- конструктор;
- методи;
- `toString()`;
- осмислену назву;
- Javadoc для публічних методів.

---

# Pull Request

Тепер PR повинен мати:

```text
Implementation
Tests / examples
Javadoc
Description
```

І мінімум один студент повинен зробити **code review**.

---

# Результат

```text
MathSolver 0.4
```

Тепер Solver має справжню предметну модель.

---

# Лабораторна робота №5
# «Різні типи математичних сутностей»

### Тема

Наслідування, поліморфізм, абстрактні класи, композиція, `record`, `sealed`-типи та pattern matching.

Це відповідає п'ятому розділу курсу.

---

# Нова проблема

У Solver з'являються різні математичні об'єкти:

```text
Point
Vector
Line
Circle
Polynomial
Equation
```

Виникає питання:

> Чи можна представити їх єдиною абстракцією?

---

# Text / Expression team

Починає будувати майбутню ієрархію:

```text
Expression
│
├── Constant
├── Variable
│
└── BinaryExpression
    ├── Addition
    ├── Subtraction
    ├── Multiplication
    └── Division
```

Поки що можна реалізувати тільки частину.

---

# Algebra

Наприклад:

```text
Polynomial
```

і операції:

```text
evaluate(x)
degree()
```

---

# Geometry

Побудувати:

```text
Shape
│
├── Circle
├── Rectangle
└── Triangle
```

Кожна фігура повинна вміти:

```text
area()
perimeter()
```

---

# Навчальна мета

Студент повинен побачити різницю між:

```text
Circle
```

та

```text
Shape
```

і зрозуміти, навіщо потрібні:

- inheritance;
- overriding;
- polymorphism;
- abstract classes.

---

# Приклад

Можна мати:

```java
Shape[] shapes = {
    new Circle(...),
    new Rectangle(...),
    new Triangle(...)
};
```

і:

```java
for (Shape shape : shapes) {
    System.out.println(shape.area());
}
```

Це вже наближає нас до реального Solver Engine.

---

# Результат

```text
MathSolver 0.5
```

В системі з'являється поліморфна математична модель.

---

# Лабораторна робота №6
# «Контракти Solver-а: interfaces та generics»

### Тема

Інтерфейси, `Comparable`, `Comparator`, generics, bounded type parameters, вкладені та анонімні класи.

Це відповідає шостому розділу курсу.

---

# Головна ідея

Тепер три команди починають реально взаємодіяти.

Нам потрібен контракт:

```java
public interface Solver<I, O> {
    O solve(I input);
}
```

Тепер можна мати:

```text
LinearEquationSolver
QuadraticEquationSolver
GeometrySolver
PolynomialSolver
```

і всі вони реалізують спільний контракт.

---

# Algebra

Створити:

```text
LinearEquationSolver
QuadraticEquationSolver
```

Наприклад:

```text
solve 2*x + 5 = 13
```

→

```text
x = 4
```

---

# Geometry

Створити:

```text
DistanceSolver
IntersectionSolver
AreaSolver
```

---

# Text

Створити:

```text
ExpressionParser
```

який поки що може підтримувати обмежений синтаксис:

```text
2 + 3
2 * 5
x + 2
```

---

# Core

Створює:

```text
SolverRegistry
```

Наприклад:

```text
"linear"      → LinearEquationSolver
"quadratic"   → QuadraticEquationSolver
"distance"    → DistanceSolver
```

---

# Generics

На цій лабораторній студенти вперше побачать, чому:

```java
Solver<I, O>
```

кращий за:

```java
Object solve(Object input)
```

Можна мати:

```java
Solver<LinearEquation, Solution>
```

та:

```java
Solver<Circle, Point[]>
```

---

# Результат

```text
MathSolver 0.6
```

Тепер система має перші справжні **solver-компоненти**, які працюють через спільні контракти.

---

# Єдина історія шести лабораторних

Таким чином студент бачить не шість незалежних лабораторних:

```text
Lab 1     Lab 2     Lab 3     Lab 4     Lab 5     Lab 6
  │         │         │         │         │         │
  ▼         ▼         ▼         ▼         ▼         ▼
 CLI  →  Methods → Arrays → Classes → OOP → Interfaces
  │         │         │         │         │         │
  └─────────┴─────────┴─────────┴─────────┴─────────┘
                         │
                         ▼
                    MathSolver
```

А один поступовий процес:

```text
0.1
"Я можу виконати математичну операцію"

        ↓

0.2
"Я можу обробляти математичні команди"

        ↓

0.3
"Я можу працювати з набором даних"

        ↓

0.4
"Я можу представити математичні сутності об'єктами"

        ↓

0.5
"Я можу працювати з різними типами математичних об'єктів"

        ↓

0.6
"Я можу створювати різні Solver-и через спільний контракт"
```

---

# Система Pull Request для перших лабораторних

## Lab 1

Типові PR:

```text
Implement addition
Implement multiplication
Implement power
Implement sqrt
Implement distance
Implement CLI help
```

---

## Lab 2

```text
Extract arithmetic methods
Implement linear equation
Add circle area
Refactor command dispatcher
```

---

## Lab 3

```text
Implement average
Implement point array processing
Implement argument parser
```

---

## Lab 4

```text
Create Point2D
Create Vector2D
Create Fraction
Create ComplexNumber
Create Command
```

---

## Lab 5

```text
Create Shape hierarchy
Implement Circle
Implement Rectangle
Create Expression hierarchy
Implement Polynomial
```

---

## Lab 6

```text
Create Solver interface
Implement LinearEquationSolver
Implement QuadraticEquationSolver
Implement DistanceSolver
Implement SolverRegistry
Implement ExpressionParser
```

---

# Що оцінюється на кожній лабораторній

Я б не оцінював тільки «чи працює програма».

Пропоную:

| Компонент | Бали |
|---|---:|
| Java concepts | 20% |
| Математична коректність | 20% |
| Архітектура / якість коду | 20% |
| Unit/manual tests | 15% |
| Pull Request | 15% |
| Code review | 10% |

Особливо важливо: **код, який працює, але порушує контракт команди або ламає `main`, не повинен автоматично отримувати максимальну оцінку.**

---

# Definition of Done

Завдання вважається виконаним лише тоді, коли:

```text
[ ] Issue створено
[ ] Branch створено
[ ] Код реалізовано
[ ] Код компілюється
[ ] Принаймні базові тести/перевірки виконані
[ ] PR створено
[ ] Інший студент зробив review
[ ] Зауваження review опрацьовані
[ ] PR merged
```

---

# Роль викладача

Викладач не повинен писати код за студентів.

Його основна роль:

```text
                   TEACHER
                      │
        ┌─────────────┼─────────────┐
        ▼             ▼             ▼
    Architecture   Mathematics    Java
        │             │             │
        └─────────────┼─────────────┘
                      ▼
                    Review
```

Особливо важливо не давати готову архітектуру на кожному етапі.

Наприклад, перед Lab 4 можна поставити питання:

> У нас є Point, Vector, Fraction і ComplexNumber. Чому зберігати всі математичні дані як `double[]` вже незручно?

Перед Lab 5:

> У нас є Circle, Rectangle і Triangle. Що спільного між ними?

Перед Lab 6:

> Якщо завтра з'явиться `CubicEquationSolver`, як додати його, не переписуючи весь MathSolver?

Тоді **архітектура стає відповіддю на проблему**, а не набором визначень із лекції.

---

# Після шести лабораторних

На цьому етапі MathSolver ще далеко не завершений.

Але фундамент уже є:

```text
                   MathSolver 0.6
                         │
        ┌────────────────┼────────────────┐
        │                │                │
      Algebra         Geometry          Text
        │                │                │
   Polynomial          Point           Command
   Equation            Vector          Parser
   Solver              Shape
        │                │                │
        └────────────────┼────────────────┘
                         │
                    Solver<I,O>
                         │
                         ▼
                    Core Engine
```

І саме **після цього** має сенс переходити до наступних частин курсу: винятків, рядків і regex, файлів, колекцій, Stream API, concurrency, Maven/JUnit/CI, а пізніше — REST API.

Фінальна мета залишається такою:

```text
"solve x^2 - 5*x + 6 = 0"
              │
              ▼
          Tokenizer
              │
              ▼
            Parser
              │
              ▼
             AST
              │
              ▼
       Equation Solver
              │
              ▼
          x₁ = 2
          x₂ = 3
```

А для геометрії:

```text
"intersection of
 circle(x²+y²=25)
 line(y=2x+1)"
              │
              ▼
          Geometry
              │
              ▼
         Algebra System
              │
              ▼
          Solutions
```

Саме ця **наскрізна залежність між Text → Algebra → Geometry → Solver** повинна стати центральною сюжетною лінією всього курсу.
