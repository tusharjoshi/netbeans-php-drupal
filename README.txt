Drupal development module for the Netbeans IDE.

Full documentation available now available under Help in Netbeans

2.0 - Final

The templating system has been reworked. If you are currently using custom template libraries, then please check out Help in Netbeans for the changes as your old libraries will no longer work.

This version introduces Drush support. This is an early release for testing. This should now work on all major operating
systems. 

Before using Drush support you must have Drush properly installed and configured on your system. You must 
then configure the Drush path under Tools->Options->PHP->Drupal.

You can access Drush via the Drush command window (Window->Drupal->Drush)

The Drush window accepts Drush commands. The non-interactive -y parameter is automatically passed.

You can specify the URL of your site via the host box.

If Drupal is not in your project root directory or you store your Netbeans project meta in a different directory then you need to set the location of Drupal under the project properties.

The Drush command field and host field have a history based upon the active project. The history files are stored under nbproject/private for each project.

If you find any problems, please copy the output of your Drush output window to the support ticket. Also include the version of PHP you are running, the Drupal version and Drush version, Netbeans version, operating system and version as well as output from any error windows that appear in Netbeans. The more information you can provide, the easier and quicker it will be for me to track down problems.
