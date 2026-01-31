$ErrorActionPreference = "Continue"
Set-Location "D:\Projects\Software_Project\Hospital-Selection-System\backend"
& mvn clean test-compile 2>&1 | Tee-Object -FilePath "maven_output.txt"
Get-Content "maven_output.txt" | Select-Object -Last 50
