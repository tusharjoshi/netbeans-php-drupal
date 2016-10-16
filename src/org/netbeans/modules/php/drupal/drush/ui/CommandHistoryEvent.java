/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupal.drush.ui;

import java.util.EventObject;

/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
public class CommandHistoryEvent extends EventObject {
    public String command = "";
    public CommandHistoryEvent (Object source, String command) {
        super(source);
        this.command = command;
    }
}

