@echo off
cd /d D:\Projects\Software_Project\Hospital-Selection-System\backend
echo ========================================
echo Running Test
echo ========================================
call mvn clean test -Dtest=UserControllerTest#testRegister_Success
echo.
echo Exit Code: %ERRORLEVEL%
pause
