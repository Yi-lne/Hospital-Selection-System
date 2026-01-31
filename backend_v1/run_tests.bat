@echo off
cd /d "D:\Projects\Software_Project\Hospital-Selection-System\backend"
call mvn test -Dtest=RoleServiceTest,FilterServiceTest > test_results_batch.log 2>&1
type test_results_batch.log
