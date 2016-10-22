README - 2.0 - Final
====================

[![Join the chat at https://gitter.im/netbeans-php-drupal/Lobby](https://badges.gitter.im/netbeans-php-drupal/Lobby.svg)](https://gitter.im/netbeans-php-drupal/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

**Netbeans Drupal Plugin**

**10/15/2016: NOTE for old FORKS**
The complete commit history has been changed to remove the NBM release binary files from the commits, hence all old forks prior to 10/15/2016 will need a force update to their repositories.  The current development will now happen on the develop branch and the master branch is protected from force commits.

**NOTE ON DRUSH (Update for 2.0.11):** When setting up Drush, you must specify the absolute path to the folder
that drush exists in. This is the location of drush for *NIX/Mac or drush.bat for Windows.

Drush path and configuration for this plugin is found under NetBeans: Preferences/Options > PHP > Drupal after installing this plugin.  For path to drush on mac, you might enter /Users/youruser/.drush


What is NDDT
------------

NDDT is a module for the [NetBeans IDE][1] that aides in the development of [Drupal][2] module and
theme development. It provides rapid access to all the core Drupal hooks and theme override functions.

NDDT is powered by a custom templating system that allows for easy customization and addition
of new Drupal hooks and code snippets. View the Help section inside of NDDT for examples of
how you can quickly expand the functionality of this tool.

Drush Integration
-----------------

Starting with version 2 [Drush][3] is now a part of NDDT. Execute numerous Drush related commands
without ever having to leave your IDE. A custom Drush plugin is also utilized allowing you
to find all places where a certain hook is invoked.

The version 2 release of NDDT now has integration with [Drush][3]. 

Requirements
------------

* Netbeans 8.2 or greater
* Drush installed and configured on your local machine (for Drush related features only)

Installation
------------
(UPDATE: earlier the NBM binary was committed in the repository, which has been removed now.  The releases will be done through the release mechanism in github)

You can download the latest NBM build here:

https://github.com/tusharvjoshi/NBDrupalDevel/releases/download/v2.0.11/NBDrupalDevelTool.nbm

Open up Netbeans and go to
Tools->Plugins. Select the Downloaded tab in the Plugins window then click the 
"Add Plugins" button. Navigate to the file you saved and select it. Simply follow the
on-screen instructions and you are ready to go.

Documentation
-------------
Documentation is included in the tool under the NetBeans help menu.

Contributing
------------
NDDT is open source, licensed under the Apache v2. If you are interested in contributing to the
development of the tool or even the documentation then please raise a PR or enhancement 
request via issues in Github.

Building
--------
For building NBM from source a keystore is needed which should be placed in the ../keystore folder.  The command used to generate this keystore is as follows

  keytool -genkey -alias nddt2 -keystore ../keystore/nddt2 -validity 3650

[1]: http://www.netbeans.org
[2]: http://www.drupal.org
[3]: http://www.drupal.org/project/drush

