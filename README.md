# desktop-gmail-client
A materialized Gmail Desktop client with Oauth 2.0 removing low security apps problem ,that happens with email clients such as Evolution.
JavaFx framework is used in addition to Gmail Api to acess email resources.

This is developed using some open source JavaFx libraries such as,
[JFoenix](https://github.com/jfoenixadmin/JFoenix)
[ControlsFx](https://bitbucket.org/controlsfx/controlsfx)

## Instructions
I have used intellij idea and recommend using that for the project.
### Requirements
* Java 1.8 or higher
* an ide such as Intellij idea

After downloading the project, make sure maven plugin is installed in your IDE, since it is a maven based project. Let the IDE resolve the dependencies or manually do it by right clicking the pom.xml -> maven -> download sources(in case of intellij idea).

There are my dummy client credentials are provided, you can still use them or replace them by creating your own from the Google developer console.


## Features
* Access to mails
* Navigation through the links in email
* Background syncing of emails
* Reply to current email
* Compose and Forward mails
* Send multipart email
* Send attachments upto 35mb of size
* Drag and drop to add attachments
* Notifications for various warning and information messages
