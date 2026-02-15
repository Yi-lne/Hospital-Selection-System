#!/bin/bash
cd "$(dirname "$0")"
export JAVA_HOME=/d/Java/jdk-1.8
export M2_HOME=/d/maven/apache-maven-3.9.9
export PATH=$JAVA_HOME/bin:$M2_HOME/bin:$PATH

mvn clean test 2>&1 | tee test_run.log
