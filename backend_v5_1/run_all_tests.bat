@echo off
cd /d D:\Projects\Software_Project\Hospital-Selection-System\backend
echo ========================================
echo Running All Backend Tests
echo ========================================
echo.

call mvn clean test

echo.
echo ========================================
echo Test Summary
echo ========================================
echo.

for %%f in (target\surefire-reports\*.txt) do (
    echo %%f
    type "%%f"
    echo.
)

pause
