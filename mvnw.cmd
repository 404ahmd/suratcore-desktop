@REM ----------------------------------------------------------------------------
@REM Maven Startup Script for Windows
@REM ----------------------------------------------------------------------------

@echo off
setlocal

where mvn >nul 2>&1
if %ERRORLEVEL% equ 0 (
    mvn %*
    goto end
)

echo [ERROR] Maven not found in PATH. Please ensure Maven is installed.
exit /b 1

:end
endlocal

