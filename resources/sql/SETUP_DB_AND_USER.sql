-- Create the FYP database
CREATE DATABASE fyp;

-- Create the FYP user and give permissions
CREATE USER 'fyp_user'@'localhost' IDENTIFIED BY 'SPaZdZcjyNwpjMAgpBfExQNvDdx6p4cRmW4ZzuUPHF73eHhCKtXRsqCrQsAdkytN';
GRANT ALL ON fyp . * TO 'fyp_user'@'localhost';
