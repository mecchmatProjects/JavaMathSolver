#!/bin/bash
# Тестовий набір для Basic_calc.java — 16 сценаріїв
# Запуск: bash test_suite.sh
J() { java -Dstdout.encoding=UTF-8 Basic_calc; }
echo "### T1 нормальний сценарій";      printf 'add 2 3\nsub 10 4\nmul 4 7\npow 2 10\nabs -5\nexit\n' | J 2>&1
echo "### T2 ALG-04 ділення";            printf 'div 10 2\ndiv 7 2\ndiv 10.0 2\ndiv 1.0 0.0\ndiv -1.0 0.0\ndiv 0.0 0.0\ndiv 5 0\nexit\n' | J 2>&1
echo "### T3 текст замість числа";       printf 'add abc 2\nexit\n' | J 2>&1
echo "### T4 EOF без exit";              printf 'add 1 2\n' | J 2>&1
echo "### T5 невідома операція/регістр"; printf 'hello\nADD\nsqrt\nhelp\nexit\n' | J 2>&1
echo "### T6 текст у div";               printf 'div abc 2\n' | J 2>&1
echo "### T7 завелике ціле";             printf 'div 10000000000 2\n' | J 2>&1
echo "### T8 наукова нотація у div";     printf 'div 1e5 2\n' | J 2>&1
echo "### T9 кома у div";                printf 'div 2,5 2\ndiv 10 2,0\nexit\n' | J 2>&1
echo "### T10 переповнення int";         printf 'div -2147483648 -1\ndiv 2147483647 1\nexit\n' | J 2>&1
echo "### T11 кома в add (POSIX)";       printf 'add 2,5 1\n' | J 2>&1
echo "### T12 локаль uk_UA, крапка";     printf 'add 3.14 1\n' | java -Duser.language=uk -Duser.country=UA -Dstdout.encoding=UTF-8 Basic_calc 2>&1
echo "### T13 локаль uk_UA, кома";       printf 'add 3,14 1\nexit\n' | java -Duser.language=uk -Duser.country=UA -Dstdout.encoding=UTF-8 Basic_calc 2>&1
echo "### T14 математичні межі";         printf 'add 0.1 0.2\nsub 0.3 0.1\nmul 0.1 3\nadd 1e308 1e308\npow 2 0.5\npow 0 0\npow -8 0.3333333333333333\npow -2 2\nabs -0.0\nabs -1e-5\nexit\n' | J 2>&1
echo "### T15 exit замість числа";       printf 'add\nexit\n' | J 2>&1
echo "### T16 зайві числа";              printf 'add 1 2 3 4\nexit\n' | J 2>&1
