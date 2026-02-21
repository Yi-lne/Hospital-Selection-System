# Backend Test Runner Script
# Run backend tests and capture results

Write-Host "Running Backend Tests..."
Write-Host "================================"

# Change to backend directory
Set-Location backend

Write-Host "Cleaning and compiling..."
mvn clean compile test-compile

Write-Host "================================"
Write-Host "Running tests..."
Write-Host "================================"

# Run tests and capture output
$testResult = mvn test 2>&1
Write-Host $testResult

# Save output to file
$testResult | Out-File -FilePath "test_results.txt" -Encoding UTF8

Write-Host "================================"
Write-Host "Test run completed!"
Write-Host "Results saved to: test_results.txt"
Write-Host "================================"

# Display results
Get-Content test_results.txt | Select-Object -Last 50
