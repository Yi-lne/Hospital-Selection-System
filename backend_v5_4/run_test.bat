@echo off
cd /d D:\Projects\Software_Project\Hospital-Selection-System\backend
call mvn clean test -Dtest=UserServiceTest > test_result.txt 2>&1
type test_result.txt
