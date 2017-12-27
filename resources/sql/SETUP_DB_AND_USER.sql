-- Create the FYP database
DROP SCHEMA IF EXISTS `fyp` ;
CREATE SCHEMA IF NOT EXISTS `fyp` DEFAULT CHARACTER SET utf8 ;

-- Create the FYP user and give permissions
CREATE USER 'fyp_user'@'%' IDENTIFIED BY 'SPaZdZcjyNwpjMAgpBfExQNvDdx6p4cRmW4ZzuUPHF73eHhCKtXRsqCrQsAdkytN';
GRANT ALL ON fyp . * TO 'fyp_user'@'%';
