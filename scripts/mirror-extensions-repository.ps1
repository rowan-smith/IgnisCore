# Mirror the extensions-export branch into IgnisCore-Extensions.
# Requires push access to https://github.com/rowan-smith/IgnisCore-Extensions
#
# Usage:
#   pwsh ./scripts/mirror-extensions-repository.ps1
#   pwsh ./scripts/mirror-extensions-repository.ps1 -Batch
#   powershell -File .\scripts\mirror-extensions-repository.ps1 -Batch
#
# Environment:
#   IGNIS_CORE_REPO
#   IGNIS_EXTENSIONS_FALLBACK_BRANCH
#   IGNIS_EXTENSIONS_REPO
#   IGNIS_EXTENSIONS_TARGET_BRANCH

[CmdletBinding()]
param(
    [switch]$Batch
)

$ErrorActionPreference = 'Stop'

$sourceRepo = if ($env:IGNIS_CORE_REPO) { $env:IGNIS_CORE_REPO } else { 'https://github.com/rowan-smith/IgnisCore.git' }
$sourceBranch = if ($env:IGNIS_EXTENSIONS_FALLBACK_BRANCH) { $env:IGNIS_EXTENSIONS_FALLBACK_BRANCH } else { 'extensions-export' }
$targetRepo = if ($env:IGNIS_EXTENSIONS_REPO) { $env:IGNIS_EXTENSIONS_REPO } else { 'https://github.com/rowan-smith/IgnisCore-Extensions.git' }
$targetBranch = if ($env:IGNIS_EXTENSIONS_TARGET_BRANCH) { $env:IGNIS_EXTENSIONS_TARGET_BRANCH } else { 'main' }

if ($Batch) {
    $env:GIT_TERMINAL_PROMPT = '0'
}

$workDir = Join-Path ([System.IO.Path]::GetTempPath()) ("ignis-ext-mirror-" + [guid]::NewGuid().ToString('N'))
$exportDir = Join-Path $workDir 'export'

try {
    New-Item -ItemType Directory -Path $exportDir -Force | Out-Null

    Write-Host "Cloning $sourceRepo ($sourceBranch)..."
    & git clone --branch $sourceBranch --depth 1 $sourceRepo $exportDir
    if ($LASTEXITCODE -ne 0) {
        throw "git clone failed with exit code $LASTEXITCODE"
    }

    Push-Location $exportDir
    try {
        & git remote add target $targetRepo
        if ($LASTEXITCODE -ne 0) {
            throw "git remote add failed with exit code $LASTEXITCODE"
        }

        Write-Host "Pushing to $targetRepo ($targetBranch)..."
        & git push target "HEAD:$targetBranch"
        if ($LASTEXITCODE -ne 0) {
            throw "git push failed with exit code $LASTEXITCODE"
        }
    }
    finally {
        Pop-Location
    }

    Write-Host "Mirrored $sourceBranch from IgnisCore to $targetRepo ($targetBranch)"
}
finally {
    if (Test-Path $workDir) {
        Remove-Item -LiteralPath $workDir -Recurse -Force -ErrorAction SilentlyContinue
    }
}
