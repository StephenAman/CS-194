-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `mydb` ;

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
  `lastLocationLat` FLOAT NULL,
  `lastLocationLng` FLOAT NULL,
  `lastLocationTime` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`mics`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`mics` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `createdBy` VARCHAR(45) NOT NULL,
  `micName` VARCHAR(255) NOT NULL,
  `venueName` VARCHAR(255) NOT NULL,
  `venueAddress` VARCHAR(255) NOT NULL,
  `venueLat` FLOAT NOT NULL,
  `venueLng` FLOAT NOT NULL,
  `startDate` DATETIME NOT NULL COMMENT 'Formatted string following ISO 8601. \"hh:mm:ss\"\n',
  `duration` INT NOT NULL COMMENT 'In seconds',
  `meetingBasis` VARCHAR(45) NOT NULL,
  `setTime` INT NOT NULL,
  `numSlots` INT NOT NULL,
  `signupsOpenTimeOfDay` DATETIME NULL,
  `signupsOpenNumDaysBefore` INT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_mics_users_idx` (`createdBy` ASC),
  CONSTRAINT `fk_mics_users`
    FOREIGN KEY (`createdBy`)
    REFERENCES `mydb`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`instances`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`instances` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `micId` INT NOT NULL,
  `startDate` DATETIME NOT NULL,
  `endDate` DATETIME NOT NULL,
  `cancelled` TINYINT(1) NOT NULL,
  `numSlots` INT NOT NULL,
  `setTime` INT NOT NULL,
  `signupsOpenDate` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_instances_mics1_idx` (`micId` ASC),
  CONSTRAINT `fk_instances_mics1`
    FOREIGN KEY (`micId`)
    REFERENCES `mydb`.`mics` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`signups`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`signups` (
  `userId` VARCHAR(45) NOT NULL,
  `instanceId` INT NOT NULL,
  `slotNumber` INT NOT NULL,
  `time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`instanceId`, `slotNumber`),
  INDEX `fk_signups_users1_idx` (`userId` ASC),
  INDEX `fk_signups_instances1_idx` (`instanceId` ASC),
  CONSTRAINT `fk_signups_users1`
    FOREIGN KEY (`userId`)
    REFERENCES `mydb`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_signups_instances1`
    FOREIGN KEY (`instanceId`)
    REFERENCES `mydb`.`instances` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`reviews`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`reviews` (
  `userId` VARCHAR(45) NOT NULL,
  `micId` INT NOT NULL,
  `reviewText` TEXT NOT NULL,
  `time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `fk_reviews_users1_idx` (`userId` ASC),
  INDEX `fk_reviews_mics1_idx` (`micId` ASC),
  PRIMARY KEY (`userId`, `micId`),
  CONSTRAINT `fk_reviews_users1`
    FOREIGN KEY (`userId`)
    REFERENCES `mydb`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_reviews_mics1`
    FOREIGN KEY (`micId`)
    REFERENCES `mydb`.`mics` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
