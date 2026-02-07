#!/bin/bash
cd "D:/Projects/Software_Project/Hospital-Selection-System/backend"

# Set Java home
export JAVA_HOME="/c/Program Files/Java/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"

echo "JAVA_HOME: $JAVA_HOME"
echo "Java version:"
java -version
echo ""

echo "Running Maven clean test..."
echo "=================================="

mvn clean test 2>&1 | tee test_results_full.log

echo ""
echo "=================================="
echo "Test run completed. Checking for results..."

if [ -d "target/surefire-reports" ]; then
    echo "Surefire reports found!"
    ls -la target/surefire-reports/
else
    echo "No surefire-reports directory found."
fi

