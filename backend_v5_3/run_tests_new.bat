@echo off
chcp 65001
cd /d D:\Projects\Software_Project\Hospital-Selection-System\backend
echo Starting Maven test...
mvn clean test -Dtest=CollectionServiceTest,MessageServiceTest,UserServiceTest > full_output.txt 2>&1
echo.
echo ===== RESULTS =====
type full_output.txt | findstr /C:"Tests run" /C:"FAILURE" /C:"ERROR" /C:"BUILD"
echo.
echo ===== DONE =====
