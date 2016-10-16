/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupal.ui.apitree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
public class ApiTreeItem extends DefaultMutableTreeNode {

    private String itemFile;
    private String type;
    private String name;
    private String search;

    public ApiTreeItem(String name) {
        super(name);
    }

    public void setFile(String file) {
        this.itemFile = file;
    }

    public String getFile() {
        return this.itemFile;
    }

    public void setItemType(String type) {
        this.type = type;
    }

    public String getItemType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSearch(String name) {
        this.search = name;
    }

    public String getSearch() {
        return search;
    }
}
