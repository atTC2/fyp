#!/bin/bash

# MySQL
echo 'Setting up MySQL...'
sudo apt-get -qq install mysql-server -y
echo 'done'
echo 'If `mysql_secure_installation` is not automatically run, please run this to finish MySQL setup.'
