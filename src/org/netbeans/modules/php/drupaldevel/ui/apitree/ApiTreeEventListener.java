/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupaldevel.ui.apitree;

import java.util.EventListener;

/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
public interface ApiTreeEventListener extends EventListener{
    public void itemSelected(ApiTreeEvent evt);
}
