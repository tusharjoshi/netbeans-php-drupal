/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupaldevel.ui.apitree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
    
public class ApiTreeItem extends DefaultMutableTreeNode{
    private String itemFile;
    
    public ApiTreeItem(String name){
        super(name);
    }
    
    public void setFile(String file){
        this.itemFile = file;
    }
    
    public String getFile(){
        return this.itemFile;
    }
}
