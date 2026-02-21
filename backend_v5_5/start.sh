#!/bin/bash
cd "D:/Projects/Software_Project/Hospital-Selection-System/backend"
mvn spring-boot:run 2>&1 | tee "D:/hospital-startup.log"
