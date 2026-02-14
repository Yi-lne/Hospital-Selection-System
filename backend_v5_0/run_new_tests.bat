@echo off
chcp 65001 > nul
echo ========================================
echo Running New Service Tests
echo ========================================
echo.

cd /d "%~dp0"

echo Cleaning previous test results...
call mvn clean

echo.
echo Running RoleServiceTest...
call mvn test -Dtest=RoleServiceTest -Dfile.encoding=UTF-8

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ RoleServiceTest failed!
    echo.
)

echo.
echo Running FilterServiceTest...
call mvn test -Dtest=FilterServiceTest -Dfile.encoding=UTF-8

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ FilterServiceTest failed!
    echo.
)

echo.
echo ========================================
echo All new tests completed!
echo ========================================
echo.

pause
