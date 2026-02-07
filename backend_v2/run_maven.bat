@echo off
setlocal
chcp 65001 >nul
cd /d D:\Projects\Software_Project\Hospital-Selection-System\backend
echo Running Maven clean test...
call mvn clean test -Dtest=UserServiceTest > maven_test_output.txt 2>&1
echo.
echo === Maven Test Results ===
type maven_test_output.txt
echo.
echo === Done ===
endlocal
