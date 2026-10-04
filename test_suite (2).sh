#!/bin/bash
# =============================================================================
#  Ручний тестовий набір для Basic_calc.java  (Lab 1, команда Algebra)
#  Перевіряє ALG-01..ALG-06 + sqrt у трьох категоріях за п. 2.6 методички:
#  нормальний випадок / граничний випадок / некоректний випадок.
#
#  Запуск:  javac -encoding UTF-8 ../src/Basic_calc.java -d .
#           bash test_suite.sh > test_log.txt 2>&1
#  Потрібно: JDK 19+ (через прапорець -Dstdout.encoding), bash
#            (на Windows — Git Bash або WSL)
# =============================================================================

CLASS=Basic_calc
J()    { java -Dstdout.encoding=UTF-8 $CLASS; }
J_UK() { java -Duser.language=uk -Duser.country=UA -Dstdout.encoding=UTF-8 $CLASS; }

hdr() { echo; echo "============================================================"; \
        echo "$1"; echo "============================================================"; }

echo "Тестовий прогін Basic_calc"
echo "Дата:    $(date '+%Y-%m-%d %H:%M:%S')"
echo "Java:    $(java -version 2>&1 | head -1)"
echo "Локаль:  ${LANG:-POSIX}"

# ---------- НОРМАЛЬНІ ВИПАДКИ -------------------------------------------
hdr "T1  ALG-01..06 — базові операції (очікувано: 5, 6, 28, 1024, 5)"
printf 'add 2 3\nsub 10 4\nmul 4 7\npow 2 10\nabs -5\nexit\n' | J 2>&1

hdr "T2  ALG-04 — ділення, дослідження з методички"
echo "# очікувано: 5 | 3 (відкидання) | 5.0 | Infinity | -Infinity | NaN | повідомлення"
printf 'div 10 2\ndiv 7 2\ndiv 10.0 2\ndiv 1.0 0.0\ndiv -1.0 0.0\ndiv 0.0 0.0\ndiv 5 0\nexit\n' | J 2>&1

hdr "T3  sqrt — коректні значення та захист від від'ємного"
printf 'sqrt 144\nsqrt 2\nsqrt 0\nsqrt -1\nsqrt -0.0\nexit\n' | J 2>&1

hdr "T4  Невідома операція, чутливість до регістру, вихід з циклу"
printf 'hello\nADD\nhelp\nexit\n' | J 2>&1

hdr "T5  Числа на окремих рядках (типовий інтерактивний сценарій)"
printf 'add\n2\n3\nexit\n' | J 2>&1

# ---------- ГРАНИЧНІ ВИПАДКИ --------------------------------------------
hdr "T6  Похибка double та переповнення"
printf 'add 0.1 0.2\nsub 0.3 0.1\nmul 0.1 3\nadd 1e308 1e308\nexit\n' | J 2>&1

hdr "T7  Math.pow та Math.abs — граничні значення"
printf 'pow 2 0.5\npow 0 0\npow -8 0.3333333333333333\npow -2 2\nabs -0.0\nabs -1e-5\nexit\n' | J 2>&1

hdr "T8  Межі int у divInt (переповнення MIN_VALUE / -1)"
printf 'div -2147483648 -1\ndiv 2147483647 1\nexit\n' | J 2>&1

hdr "T9  Кома як роздільник у гілці div"
printf 'div 2,5 2\ndiv 10 2,0\nexit\n' | J 2>&1

# ---------- НЕКОРЕКТНИЙ ВВІД (має оброблятися без падіння) --------------
hdr "T10 Текст замість числа"
printf 'add abc 2\nexit\n' | J 2>&1

hdr "T11 Слово 'exit' замість числа, робота продовжується"
printf 'add\nexit\nadd 2 3\nexit\n' | J 2>&1

hdr "T12 Текст у гілці div"
printf 'div abc 2\nexit\n' | J 2>&1

hdr "T13 Ціле поза межами int"
printf 'div 10000000000 2\nexit\n' | J 2>&1

hdr "T14 Наукова нотація у div (немає крапки -> гілка int)"
printf 'div 1e5 2\nexit\n' | J 2>&1

hdr "T15 Кома в add за поточної локалі"
printf 'add 2,5 1\nexit\n' | J 2>&1

hdr "T16 Контроль кількості чисел (зайвий ввід)"
printf 'add 1 2 3 4\nabs 5 6\nsqrt 4 4\nexit\n' | J 2>&1

# ---------- ВІДОМІ ДЕФЕКТИ ----------------------------------------------
hdr "T17 [ВІДОМИЙ ДЕФЕКТ] Ctrl+D замість операції -> NoSuchElementException"
printf 'add 1 2\n' | J 2>&1

hdr "T18 [ВІДОМИЙ ДЕФЕКТ] Ctrl+D посеред введення чисел у div"
printf 'div 10' | J 2>&1

hdr "T19 [ВІДОМИЙ ДЕФЕКТ] Залежність від локалі ОС (uk_UA)"
echo "--- 'add 3.14 1' (крапка) ---"
printf 'add 3.14 1\nexit\n' | J_UK 2>&1
echo "--- 'add 3,14 1' (кома) ---"
printf 'add 3,14 1\nexit\n' | J_UK 2>&1
echo "--- 'div 3.14 2' (крапка, власний парсинг) ---"
printf 'div 3.14 2\nexit\n' | J_UK 2>&1

hdr "КІНЕЦЬ ПРОГОНУ"
