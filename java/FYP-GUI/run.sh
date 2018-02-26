#!/bin/bash

# Runs the GUI
clear; java -jar target/fyp-gui-0.0.1-SNAPSHOT.war -Xms1024m -Xmx15g -Dorg.bytedeco.javacpp.maxphysicalbytes=15000000000
