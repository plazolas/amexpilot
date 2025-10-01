#!/bin/bash

mvn clean install
docker build -t amexpilot:1.0.0 .
docker run -p 8888:8888 amexpilot:1.0.0

# Test 1 check app is active
OUT=$(curl -s http://localhost:8888)
if [[ "$OUT" == *"active"*  ]]; then
    echo "server is active"
  else
    echo "server is not running"
    exit
fi

# Test 2 insert two users
OUT1=$(curl -s -X POST -H "Content-Type: application/json" -d '{"name": "don", "email": "don@mail.com"}' http://localhost:8888/users)
OUT2=$(curl -s -X POST -H "Content-Type: application/json" -d '{"name": "tim", "email": "tim@mail.com"}' http://localhost:8888/users)

OUT="$OUT1$OUT2"
if [[ "$OUT" == *"don@mail.com"* && "$OUT" == *"tim@mail.com"* ]]; then
    echo "users inserted"
  else
    echo "users could not be inserted"
    exit
fi

# Test 3 check all users inserted
OUT=$(curl -s http://localhost:8888/users)
if [[ "$OUT" == *"don@mail.com"* && "$OUT" == *"tim@mail.com"* ]]; then
    echo "all users found"
  else
    echo "error fetching all users"
    exit
fi

# Test 4 check delete user
OUT=$(curl -v -X DELETE http://localhost:8888/users/1)
if [[ "$OUT" == *"200 OK"* ]]; then
    echo "user deleted"
  else
    echo "error deleting user"
    exit
fi

# Test 5 check update user
OUT=$(curl -s -X PUT -H "Content-Type: application/json" -d '{"id":"2","name": "timmy", "email": "timothy@mail.com"}' http://localhost:8888/users)
if [[ "$OUT" == *"timothy@mail.com"* ]]; then
    echo "user updated"
  else
    echo "error updating user"
    exit
fi
echo "all tests passed"

docker stop amexpilot:1.0.0
docker rmi amexpilot:1.0.0
echo "bye!"
