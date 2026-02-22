@echo off
cd /d D:\Projects\Software_Project\Hospital-Selection-System\backend
echo ========================================
echo Running Test with Full Logs
echo ========================================
echo.

call mvn clean test -Dtest=CollectionControllerTest#testAddCollection_Success -X > debug_output.txt 2>&1

echo.
echo ========================================
echo Searching for errors in output...
echo ========================================
findstr /C:"ERROR" /C:"Exception" /C:"Caused by" /C:"NullPointerException" /C:"SQLException" debug_output.txt
echo.

echo ========================================
echo Last 100 lines of output:
echo ========================================
powershell -Command "Get-Content debug_output.txt | Select-Object -Last 100"

pause
