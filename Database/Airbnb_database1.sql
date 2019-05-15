-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema airbnbdatabase
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema airbnbdatabase
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `airbnbdatabase` DEFAULT CHARACTER SET utf8 ;
USE `airbnbdatabase` ;

-- -----------------------------------------------------
-- Table `airbnbdatabase`.`amenities`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `airbnbdatabase`.`amenities` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 25
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `airbnbdatabase`.`cities`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `airbnbdatabase`.`cities` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `airbnbdatabase`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `airbnbdatabase`.`users` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `birth_date` DATE NOT NULL,
  `phone` VARCHAR(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `airbnbdatabase`.`rooms`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `airbnbdatabase`.`rooms` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `address` VARCHAR(45) NOT NULL,
  `guests` INT(11) UNSIGNED ZEROFILL NOT NULL,
  `bedrooms` INT(11) UNSIGNED ZEROFILL NOT NULL,
  `beds` INT(11) UNSIGNED ZEROFILL NOT NULL,
  `baths` INT(11) UNSIGNED ZEROFILL NOT NULL,
  `price` VARCHAR(45) NOT NULL,
  `details` VARCHAR(100) NOT NULL,
  `city_id` INT(11) NOT NULL,
  `user_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Rooms_Cities1_idx` (`city_id` ASC) VISIBLE,
  INDEX `fk_Rooms_Users1_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_Rooms_Cities1`
    FOREIGN KEY (`city_id`)
    REFERENCES `airbnbdatabase`.`cities` (`id`),
  CONSTRAINT `fk_Rooms_Users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `airbnbdatabase`.`users` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `airbnbdatabase`.`bookings`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `airbnbdatabase`.`bookings` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `end_date` DATE NOT NULL,
  `start_date` DATE NOT NULL,
  `room_id` INT(11) NOT NULL,
  `user_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_bookings_rooms1_idx` (`room_id` ASC) VISIBLE,
  INDEX `fk_bookings_users1_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_bookings_rooms1`
    FOREIGN KEY (`room_id`)
    REFERENCES `airbnbdatabase`.`rooms` (`id`),
  CONSTRAINT `fk_bookings_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `airbnbdatabase`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `airbnbdatabase`.`favourites`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `airbnbdatabase`.`favourites` (
  `room_id` INT(11) NOT NULL,
  `user_id` INT(11) NOT NULL,
  INDEX `fk_table1_rooms1_idx` (`room_id` ASC) VISIBLE,
  INDEX `fk_table1_users1_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_table1_rooms1`
    FOREIGN KEY (`room_id`)
    REFERENCES `airbnbdatabase`.`rooms` (`id`),
  CONSTRAINT `fk_table1_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `airbnbdatabase`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `airbnbdatabase`.`hibernate_sequence`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `airbnbdatabase`.`hibernate_sequence` (
  `next_val` BIGINT(20) NULL DEFAULT NULL)
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `airbnbdatabase`.`messages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `airbnbdatabase`.`messages` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `sender_id` INT(11) NOT NULL,
  `receiver_id` INT(11) NOT NULL,
  `text` TEXT NOT NULL,
  `date_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_message_users1_idx` (`sender_id` ASC) VISIBLE,
  INDEX `fk_message_users2_idx` (`receiver_id` ASC) VISIBLE,
  CONSTRAINT `fk_message_users1`
    FOREIGN KEY (`sender_id`)
    REFERENCES `airbnbdatabase`.`users` (`id`),
  CONSTRAINT `fk_message_users2`
    FOREIGN KEY (`receiver_id`)
    REFERENCES `airbnbdatabase`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `airbnbdatabase`.`photos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `airbnbdatabase`.`photos` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `url` VARCHAR(255) NOT NULL,
  `room_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_photos_rooms1_idx` (`room_id` ASC) VISIBLE,
  CONSTRAINT `fk_photos_rooms1`
    FOREIGN KEY (`room_id`)
    REFERENCES `airbnbdatabase`.`rooms` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `airbnbdatabase`.`reviews`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `airbnbdatabase`.`reviews` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `date` DATETIME NOT NULL,
  `text` VARCHAR(100) NOT NULL,
  `user_id` INT(11) NOT NULL,
  `room_id` INT(11) NOT NULL,
  `stars` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Review_Users1_idx` (`user_id` ASC) VISIBLE,
  INDEX `fk_Review_Rooms1_idx` (`room_id` ASC) VISIBLE,
  CONSTRAINT `fk_Review_Rooms1`
    FOREIGN KEY (`room_id`)
    REFERENCES `airbnbdatabase`.`rooms` (`id`),
  CONSTRAINT `fk_Review_Users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `airbnbdatabase`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `airbnbdatabase`.`rooms_amenities`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `airbnbdatabase`.`rooms_amenities` (
  `amenity_id` INT(11) NOT NULL,
  `room_id` INT(11) NOT NULL,
  PRIMARY KEY (`amenity_id`, `room_id`),
  INDEX `fk_Rooms_Amenities_Amenities1_idx` (`amenity_id` ASC) VISIBLE,
  INDEX `fk_Rooms_Amenities_Rooms1_idx` (`room_id` ASC) VISIBLE,
  CONSTRAINT `fk_Rooms_Amenities_Amenities1`
    FOREIGN KEY (`amenity_id`)
    REFERENCES `airbnbdatabase`.`amenities` (`id`),
  CONSTRAINT `fk_Rooms_Amenities_Rooms1`
    FOREIGN KEY (`room_id`)
    REFERENCES `airbnbdatabase`.`rooms` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
