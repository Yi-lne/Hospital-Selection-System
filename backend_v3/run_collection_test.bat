@echo off
cd /d D:\Projects\Software_Project\Hospital-Selection-System\backend
echo ========================================
echo Running CollectionControllerTest
echo ========================================
echo.

call mvn clean test -Dtest=CollectionControllerTest

echo.
echo ========================================
echo Test Results
echo ========================================
if %ERRORLEVEL% EQU 0 (
    echo ✅ ALL TESTS PASSED!
) else (
    echo ❌ TESTS FAILED!
    echo Exit Code: %ERRORLEVEL%
)
echo.
pause
