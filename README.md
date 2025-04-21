# AMRIT - Admin Service
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)  ![branch parameter](https://github.com/PSMRI/Admin-API/actions/workflows/sast-and-package.yml/badge.svg)

The Admin Module is a collection of tools and scripts that allow users to manage the project. It includes tools for managing users, permissions, and settings.It is the master of all branches.
Admin module provides a user-friendly interface for managing your application. It includes features such as:

* User management
* Role management
* Permission management

## Building From Source
This microservice is built on Java, Spring boot framework and MySQL DB.
For steps to clone and set up this Repository, refer to the [Developer Guide](https://piramal-swasthya.gitbook.io/amrit/developer-guide/development-environment-setup)
Prerequisites 
* JDK 17
* Maven 
* Springboot V2
* MySQL

## Environment and setup

1. Install dependencies `mvn clean install`
2. You can copy `admin_example.properties` to `admin_local.properties` and edit the file accordingly. The file is under `src/main/environment` folder.
3. Run the spring server with local configuration `mvn spring-boot:run -DENV_VAR=local`

## Installation
This service has been tested on Wildfly as the application server. To install the admin module, kindly refer to Installation Guide for [API Repository](https://piramal-swasthya.gitbook.io/amrit/developer-guide/development-environment-setup/installation-instructions/for-api-repositories) for guidance.

## Usage
All the features have been exposed as REST endpoints.
Refer to the SWAGGER API specification for details.

The admin module can be used to manage all aspects of your application.
To access the admin module, navigate to http://localhost:3000/admin in your browser.
You will be prompted to login with a valid user account. Once you have logged in, you will be able to view and manage all of the resources in your application.

## Filing Issues

If you encounter any issues, bugs, or have feature requests, please file them in the [main AMRIT repository](https://github.com/PSMRI/AMRIT/issues). Centralizing all feedback helps us streamline improvements and address concerns efficiently.  

## Join Our Community

We’d love to have you join our community discussions and get real-time support!  
Join our [Discord server](https://discord.gg/FVQWsf5ENS) to connect with contributors, ask questions, and stay updated.  
