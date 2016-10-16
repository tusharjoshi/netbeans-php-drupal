/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupal.drush.ui;

import java.util.EventListener;

/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
public interface CommandProcessorEventListener extends EventListener {

    public void commandDone(CommandProcessorEvent evt);
}
