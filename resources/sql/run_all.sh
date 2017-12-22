#!/bin/bash

# Run all SQL scripts beginning with 3 numbers
DIRECTORY=.

for i in $DIRECTORY/[0-9][0-9][0-9]_*.sql; do    
    mysql -u fyp_user -pSPaZdZcjyNwpjMAgpBfExQNvDdx6p4cRmW4ZzuUPHF73eHhCKtXRsqCrQsAdkytN < "$i"
done

