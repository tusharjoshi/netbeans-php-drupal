/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupaldevel.ui.searchbar.autocomplete;

import java.util.EventListener;
/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
public interface AutoCompleteEventListener extends EventListener {
    public void enterPressedEvent(AutoCompleteEvent evt);

}
