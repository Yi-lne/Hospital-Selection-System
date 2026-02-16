@echo off
set LOG_FILE=test_output.log
cd /d D:\Projects\Software_Project\Hospital-Selection-System\backend
echo Running test with detailed output...
call mvn clean test -Dtest=CollectionControllerTest#testAddCollection_AlreadyExists > %LOG_FILE% 2>&1

echo ========================================
echo Full Output:
echo ========================================
type %LOG_FILE%

echo.
echo ========================================
echo Searching for ERROR/Exception:
echo ========================================
findstr /C:"ERROR" /C:"Exception" /C:"Caused by" %LOG_FILE%

pause
