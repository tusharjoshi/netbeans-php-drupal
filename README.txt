Drupal development module for the Netbeans IDE.

Full documentation available at:
http://www.hollyit.net/documentation/drupal-development-tool-netbeans

2.0-bleeding

This version introduces Drush support. This is an early release for testing. This should now work on all major operating
systems. 

Before using Drush support you must have Drush properly installed and configured on your system. You must 
then configure the Drush path under Tools->Options->PHP->Drupal.

You can access Drush via the Drush command window (Window->Drupal->Drush)

The Drush window accepts Drush commands. The non-interactive -y parameter is automatically passed.

You can specify the URL of your site via the host box.

For Drush to work, Drupal must be located in the root of your project. I am looking into ways of fixing this,
but it is a requirement as of now. 

The Drush command field and host field have a history based upon the active project. The history files are stored
under nbproject/private for each project.

If you find any problems, please copy the output of your Drush output window to the support ticket. Also include the
version of PHP you are running, the Drupal version and Drush version, Netbeans version, operating system and version as well
as output from any error windows that appear in Netbeans. The more information you can provide, the easier and quicker it 
will be for me to track down problems.
