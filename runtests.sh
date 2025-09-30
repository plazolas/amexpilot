#!/bin/bash
mvn clean install
# Test 1 check app is active
curl -s -o test1.txt http://localhost:8888 &

# Wait for all background processes to complete
wait
