#!/bin/bash

# Adds the backup data back to the database from `fyp_backup.sql` (the database should be cleared first)
mysql -u tom -p fyp < fyp_backup.sql

