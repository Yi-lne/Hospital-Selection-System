$ErrorActionPreference = "Continue"
cd "D:\Projects\Software_Project\Hospital-Selection-System\backend"
& "D:\maven\apache-maven-3.9.9\bin\mvn.cmd" test -Dtest=RoleServiceTest,FilterServiceTest | Out-File -FilePath "test_results_ps.txt" -Encoding UTF8
Get-Content "test_results_ps.txt"
