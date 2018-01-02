#!/bin/bash

# Installs the FYP-NLP project to the maven repository
clear; mvn clean install -Dmaven.test.skip=true
