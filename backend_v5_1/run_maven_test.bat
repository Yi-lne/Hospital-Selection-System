@echo off
call mvn clean test > test_results.txt 2>&1
echo Done!
