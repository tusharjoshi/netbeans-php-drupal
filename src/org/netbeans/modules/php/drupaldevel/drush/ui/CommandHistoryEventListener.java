/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupaldevel.drush.ui;

import java.util.EventListener;

/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
public interface CommandHistoryEventListener  extends EventListener {

    public void commandEnter(CommandHistoryEvent evt);
}

