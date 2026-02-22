@echo off
cd /d D:\Projects\Software_Project\Hospital-Selection-System\backend
echo ========================================
echo Running Single Test
echo ========================================
echo.
call mvn clean test -Dtest=UserControllerTest#testRegister_Success
echo.
echo ========================================
echo Exit Code: %ERRORLEVEL%
echo ========================================
if %ERRORLEVEL% EQU 0 (
    echo TEST PASSED!
) else (
    echo TEST FAILED!
)
echo.
pause
