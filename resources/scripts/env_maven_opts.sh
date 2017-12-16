#!/bin/bash

# Set the Maven environment variables

echo '
# Set memory allowances for running the project
export MAVEN_OPTS="-Xms1024m -Xmx40g"
export JAVA_OPTS="-Xms1024m -Xmx40g"' >> ~/.bashrc
