/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupaldevel.ui.apitree;

import java.util.EventObject;

/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
public class ApiTreeEvent extends EventObject {

    private ApiTreeItem treeItem;

    private String action;
    public static String EVENT_INSERT = "insert";
    public static String EVENT_LOOKUP = "lookup";
    public static String EVENT_HELP = "help";

    public ApiTreeEvent(Object source, ApiTreeItem item, String action) {
        
        super(source);
        this.treeItem = item;

        this.action = action;
    }

    public String getAction() {
        return this.action;
    }

    public ApiTreeItem getItem() {
        return this.treeItem;
    }

}
