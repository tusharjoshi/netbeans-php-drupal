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
public class ApiTreeEvent extends EventObject{
    private String itemPath;
    public ApiTreeEvent(Object source, String itemPath){
        super(source);
        this.itemPath = itemPath;
    }
    
    public String getItemPath(){
        return this.itemPath;
    }
}
