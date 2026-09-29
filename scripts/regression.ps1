# Golden-master регресія CLI (PowerShell 5.1+ / 7+). Аналог regression.sh.
#
# Запуск із кореня репозиторію:
#   до C0:    powershell -ExecutionPolicy Bypass -File scripts\regression.ps1 -Cp src -Main MathSolver -Out ..\before.txt
#   після C0: powershell -ExecutionPolicy Bypass -File scripts\regression.ps1 -Cp out -Main solver.app.MathSolver -Out ..\after.txt
#   порівняння: git diff --no-index ..\before.txt ..\after.txt
param(
    [Parameter(Mandatory = $true)][string]$Cp,
    [Parameter(Mandatory = $true)][string]$Main,
    [Parameter(Mandatory = $true)][string]$Out,
    [string]$Cases = "scripts\cases.txt"
)

$ErrorActionPreference = "Stop"
$result = New-Object System.Collections.Generic.List[string]

foreach ($raw in [System.IO.File]::ReadAllLines((Resolve-Path $Cases))) {
    $line = $raw.Trim()
    if ($line -eq "" -or $line.StartsWith("#")) { continue }
    $result.Add('$ ' + $line)
    # аргументи розбиваються за пробілами, як у bash; символи * ^ ( ) передаються як є
    $argv = @($line -split '\s+')
    $output = & java -cp $Cp $Main @argv
    foreach ($o in @($output)) { if ($null -ne $o) { $result.Add([string]$o) } }
}

$result.Add('$ (no arguments)')
$output = & java -cp $Cp $Main
foreach ($o in @($output)) { if ($null -ne $o) { $result.Add([string]$o) } }

# UTF-8 без BOM і LF, щоб before/after порівнювалися байт у байт
$outPath = [System.IO.Path]::GetFullPath((Join-Path (Get-Location) $Out))
$utf8 = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText($outPath, ([string]::Join("`n", $result) + "`n"), $utf8)
Write-Host ("Saved {0} lines to {1}" -f $result.Count, $outPath)
