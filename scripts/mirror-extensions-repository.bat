@echo off
setlocal EnableExtensions EnableDelayedExpansion

rem Mirror the extensions-export branch into IgnisCore-Extensions.
rem Requires push access to https://github.com/rowan-smith/IgnisCore-Extensions
rem
rem Usage:
rem   scripts\mirror-extensions-repository.bat
rem   scripts\mirror-extensions-repository.bat batch

if /I "%~1"=="/?" goto :usage
if /I "%~1"=="-h" goto :usage
if /I "%~1"=="--help" goto :usage

if not defined IGNIS_CORE_REPO set "IGNIS_CORE_REPO=https://github.com/rowan-smith/IgnisCore.git"
if not defined IGNIS_EXTENSIONS_FALLBACK_BRANCH set "IGNIS_EXTENSIONS_FALLBACK_BRANCH=extensions-export"
if not defined IGNIS_EXTENSIONS_REPO set "IGNIS_EXTENSIONS_REPO=https://github.com/rowan-smith/IgnisCore-Extensions.git"
if not defined IGNIS_EXTENSIONS_TARGET_BRANCH set "IGNIS_EXTENSIONS_TARGET_BRANCH=main"

set "BATCH=0"
if /I "%~1"=="batch" set "BATCH=1"
if /I "%~1"=="--batch" set "BATCH=1"
if /I "%~1"=="/batch" set "BATCH=1"
if /I "%~1"=="-batch" set "BATCH=1"

if "%BATCH%"=="1" set "GIT_TERMINAL_PROMPT=0"

set "WORKDIR=%TEMP%\ignis-ext-mirror-%RANDOM%%RANDOM%"
mkdir "%WORKDIR%" 2>nul
if errorlevel 1 (
  echo Failed to create temporary directory.&
  exit /b 1
)

echo Cloning %IGNIS_CORE_REPO% (%IGNIS_EXTENSIONS_FALLBACK_BRANCH%)...
git clone --branch "%IGNIS_EXTENSIONS_FALLBACK_BRANCH%" --depth 1 "%IGNIS_CORE_REPO%" "%WORKDIR%\export"
if errorlevel 1 goto :fail

pushd "%WORKDIR%\export"
git remote add target "%IGNIS_EXTENSIONS_REPO%"
echo Pushing to %IGNIS_EXTENSIONS_REPO% (%IGNIS_EXTENSIONS_TARGET_BRANCH%)...
git push target "HEAD:%IGNIS_EXTENSIONS_TARGET_BRANCH%"
if errorlevel 1 goto :fail_popd

popd
rmdir /s /q "%WORKDIR%"
echo Mirrored %IGNIS_EXTENSIONS_FALLBACK_BRANCH% from IgnisCore to %IGNIS_EXTENSIONS_REPO% (%IGNIS_EXTENSIONS_TARGET_BRANCH%)
exit /b 0

:usage
echo Usage: %~nx0 [batch]
echo.
echo   batch   Run non-interactively ^(sets GIT_TERMINAL_PROMPT=0^)
exit /b 0

:fail_popd
popd

:fail
if exist "%WORKDIR%" rmdir /s /q "%WORKDIR%"
exit /b 1
