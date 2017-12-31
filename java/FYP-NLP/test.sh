#!/bin/bash

# Tests the system with a specified test class
if [ -z "$1" ]
  then
    echo "No test supplied"
fi

clear; mvn test -Dorg.bytedeco.javacpp.maxphysicalbytes=12000000000 -Dtest=$1
