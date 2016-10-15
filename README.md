README - 2.0 - Final
====================

**Netbeans Drupal Development Tool (NDDT)**

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

* Netbeans 7.0.1 or greater
* Drush installed and configured on your local machine (for Drush related features only)

Installation
------------
(UPDATE: earlier the NBM binary was committed in the repository, which has been removed now.  The releases will be done through the release mechanism in github)

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

