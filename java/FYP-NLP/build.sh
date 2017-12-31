#!/bin/bash

# Cleans old compiled information and compiles all code
clear; mvn clean package -Dmaven.test.skip=true
