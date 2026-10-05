#!/usr/bin/env bash
# Golden-master регресія CLI.
# usage: bash scripts/regression.sh <classpath> <main-class> [cases-file]
#   до C0:    bash scripts/regression.sh src MathSolver > before.txt
#   після C0: bash scripts/regression.sh target/classes solver.app.MathSolver > after.txt
#   порівняння: diff before.txt after.txt
set -u
set -f   # без glob: 2*x не має розгортатися в імена файлів
CP="$1"
MAIN="$2"
CASES="${3:-scripts/cases.txt}"

while IFS= read -r line || [ -n "$line" ]; do
  line="${line%$'\r'}"                       # Windows CRLF
  [ -z "$line" ] && continue
  case "$line" in \#*) continue ;; esac      # коментарі
  echo "\$ $line"
  # shellcheck disable=SC2086  # розбиття на аргументи навмисне
  java -cp "$CP" "$MAIN" $line 2>&1
done < "$CASES"

# Окремо: запуск без аргументів
echo "\$ (no arguments)"
java -cp "$CP" "$MAIN" 2>&1
