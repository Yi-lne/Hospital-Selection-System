@echo off
chcp 65001 > nul
echo ========================================
echo Running Controller Tests
echo ========================================
echo.

cd /d "%~dp0"

echo Cleaning previous test results...
call mvn clean

echo.
echo Running HospitalControllerTest...
call mvn test -Dtest=HospitalControllerTest -Dfile.encoding=UTF-8

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ HospitalControllerTest failed!
    echo.
)

echo.
echo ========================================
echo Test run completed!
echo ========================================
echo.

pause
