# blexrtask ![CI status](https://img.shields.io/badge/build-passing-brightgreen.svg)

This is a Java [Spring Boot](http://projects.spring.io/spring-boot/) application for crawling a webpage.

[HtmlUnit](http://htmlunit.sourceforge.net/) GUI-Less browser is used for crawling the webpage.

## Installation

### Requirements

For building and running the application you need:
* [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven 3.3.9](https://maven.apache.org)


## Running the application locally

Run as *Spring Boot App*


In order to make a **.war** file set the goals to *clean package* in maven build.

## Usage

There are two types of crawling the page. The first one is the initial which crawls all the games and it is called only once after starting the application.
The second crawl is for the new games and it is scheduled by a cron parameter (scheduler.cron) set in the application.properties file. Initialy it is set to start every day at 00:00.
 

## Database
The create statement for the database is provided in a separate SQL txt file *CreateDatabaseStructure*.
The database parameters are stored in the application.properties file. 

The **default parameters** are:

connection.name=blexr

connection.url=jdbc:mysql://127.0.0.1:3306/blexr

connection.username=root

connection.password=admin01


The images of the games are saved in the database as a Blob file. Because there are more games with the same image, in order not to duplicate the images in database a MD5 is generated for each image. The MD5 field in the database is unique, so if there is DuplicateKeyException while inserting new image, the id of the existing image with the same MD5 value is returned.

## Mail
The parameters for setting up the email client are located in the application.properties file. This parameters should be changed according to the email server that will be used for sending mails.

Also the parameters as sendto, sendbcc and the default email subject are in the same properies file.

