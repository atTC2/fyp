#!/bin/bash

# Backup the SQL database to `fyp_backup.sql`
mysqldump -u fyp_user -p fyp > fyp_backup.sql

