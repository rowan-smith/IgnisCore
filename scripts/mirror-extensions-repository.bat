@echo off
setlocal

rem Mirror the extensions-export branch into IgnisCore-Extensions.
rem Requires push access to https://github.com/rowan-smith/IgnisCore-Extensions
rem
rem Usage:
rem   scripts\mirror-extensions-repository.bat
rem   scripts\mirror-extensions-repository.bat batch

if /I "%~1"=="/?" goto :usage
if /I "%~1"=="-h" goto :usage
if /I "%~1"=="--help" goto :usage

set "SCRIPT_DIR=%~dp0"
set "PS1=%SCRIPT_DIR%mirror-extensions-repository.ps1"

if not exist "%PS1%" (
  echo Missing %PS1%>&2
  exit /b 1
)

set "BATCH_FLAG="
if /I "%~1"=="batch" set "BATCH_FLAG=-Batch"
if /I "%~1"=="--batch" set "BATCH_FLAG=-Batch"
if /I "%~1"=="/batch" set "BATCH_FLAG=-Batch"
if /I "%~1"=="-batch" set "BATCH_FLAG=-Batch"

where pwsh >nul 2>&1
if %ERRORLEVEL%==0 (
  pwsh -NoProfile -ExecutionPolicy Bypass -File "%PS1%" %BATCH_FLAG%
  exit /b %ERRORLEVEL%
)

where powershell >nul 2>&1
if %ERRORLEVEL%==0 (
  powershell -NoProfile -ExecutionPolicy Bypass -File "%PS1%" %BATCH_FLAG%
  exit /b %ERRORLEVEL%
)

echo PowerShell is required. Install PowerShell 7+ or use Windows PowerShell 5.1.>&2
exit /b 1

:usage
echo Usage: %~nx0 [batch]
echo.
echo   batch   Run non-interactively ^(sets GIT_TERMINAL_PROMPT=0^)
exit /b 0
