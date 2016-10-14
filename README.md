README - 2.0 - Final
====================

No Longer Maintained!!!
-----------------------

Since releasing this tools I have stopped using Netbeans and moved on to PHPStorm. Due to that and lack of time, I can no longer maintain this repository. If anyone is interested in taking over, please open up an issue so we can talk about transfering ownership.

**Netbeans Drupal Development Tool (NDDT)**

**NOTE ON DRUSH (Update for 2.0.11):** When setting up Drush, you must specify the absolute path to the folder
that drush exists in. This is the location of drush for *NIX/Mac or drush.bat for Windows.

Drush path and configuration for this plugin is found under NetBeans: Preferences/Options > PHP > Drupal after installing this plugin.  For path to drush on mac, you might enter /Users/youruser/.drush

Co-Maintainer Wanted!!!
-----------------------
If you are interested in helping out with maintaining this project, then please open an issue or email
jamie [a t] hollyit [d o t] com.


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

* Netbeans 7.0.1 or greater
* Drush installed and configured on your local machine (for Drush related features only)

Installation
------------

You can download the latest NBM build here:

https://github.com/HollyIT/NBDrupalDevel/raw/master/NBDrupalDevelTool.nbm

Open up Netbeans and go to
Tools->Plugins. Select the Downloaded tab in the Plugins window then click the 
"Add Plugins" button. Navigate to the file you saved and select it. Simply follow the
on-screen instructions and you are ready to go.

Documentation
-------------
Documentation is included in the tool under the NetBeans help menu.

Contributing
------------
NDDT is open source, licensed under the GPL. If you are interested in contributing to the
development of the tool or even the documentation then please contact me here on GitHub.

[1]: http://www.netbeans.org
[2]: http://www.drupal.org
[3]: http://www.drupal.org/project/drush

