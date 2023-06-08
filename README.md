# AMRIT - Admin Service
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)  ![branch parameter](https://github.com/PSMRI/Admin-API/actions/workflows/sast-and-package.yml/badge.svg)

The Admin Module is a collection of tools and scripts that allow users to manage the project. It includes tools for managing users, permissions, and settings.It is the master of all branches.
Admin module provides a user-friendly interface for managing your application. It includes features such as:

* User management
* Role management
* Permission management

## Building From Source
This microservice is built on Java, Spring boot framework and MySQL DB.

Prerequisites 
* JDK 1.8
* Maven 
* Springboot V2
* MySQL

$ ./mvn clean install

## Installation
This service has been tested on Wildfly as the application server. To install the admin module, follow these steps:

* Clone the repository to your local machine.
* Install the dependencies.
  - npm install
* Run the development server.
  - npm start

## Configuration
The admin module can be configured by editing the config.js file. This file contains all of the settings for the module, such as the database connection string, the user authentication mechanism, and the role hierarchy.


## Usage
All the features have been exposed as REST endpoints.
Refer to the SWAGGER API specification for details.

The admin module can be used to manage all aspects of your application.
To access the admin module, navigate to http://localhost:3000/admin in your browser.
You will be prompted to login with a valid user account. Once you have logged in, you will be able to view and manage all of the resources in your application.
