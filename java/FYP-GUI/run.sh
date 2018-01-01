#!/bin/bash

# Runs the GUI
clear; mvn clean package -Dmaven.test.skip=true

java -jar target/fyp-gui-0.0.1-SNAPSHOT.war

