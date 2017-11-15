#!/bin/bash

# Java
echo 'Setting up Java...'
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get -qq update
sudo apt-get -qq install oracle-java8-installer -y
echo 'done'
