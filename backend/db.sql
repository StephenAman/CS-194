-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`users` (
  `id` VARCHAR(45) NOT NULL,
  `name` VARCHAR(255) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`mics`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`mics` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `created_by` VARCHAR(45) NOT NULL,
  `mic_name` VARCHAR(255) NOT NULL,
  `venue_name` VARCHAR(255) NOT NULL,
  `venue_address` VARCHAR(255) NOT NULL,
  `venue_lat` FLOAT NOT NULL,
  `venue_lng` FLOAT NOT NULL,
  `start_time` VARCHAR(45) NOT NULL COMMENT 'Formatted string following ISO 8601. \"hh:mm:ss\"\n',
  `duration` INT NOT NULL COMMENT 'In seconds',
  `meeting_basis` VARCHAR(45) NOT NULL,
  `set_time` INT NOT NULL,
  `num_slots` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_mics_users_idx` (`created_by` ASC),
  CONSTRAINT `fk_mics_users`
    FOREIGN KEY (`created_by`)
    REFERENCES `mydb`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`instances`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`instances` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `mic_id` INT NOT NULL,
  `date` DATETIME NOT NULL,
  `status` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_instances_mics1_idx` (`mic_id` ASC),
  CONSTRAINT `fk_instances_mics1`
    FOREIGN KEY (`mic_id`)
    REFERENCES `mydb`.`mics` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`signups`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`signups` (
  `user_id` VARCHAR(45) NOT NULL,
  `instance_id` INT NOT NULL,
  `slot_number` INT NOT NULL,
  `time` DATETIME NOT NULL,
  PRIMARY KEY (`instance_id`, `slot_number`),
  INDEX `fk_signups_users1_idx` (`user_id` ASC),
  INDEX `fk_signups_instances1_idx` (`instance_id` ASC),
  CONSTRAINT `fk_signups_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `mydb`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_signups_instances1`
    FOREIGN KEY (`instance_id`)
    REFERENCES `mydb`.`instances` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
