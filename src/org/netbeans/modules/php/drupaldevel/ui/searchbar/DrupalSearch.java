/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupaldevel.ui.searchbar;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(category = "Build",
id = "org.netbeans.modules.php.drupaldevel.ui.searchbar.DrupalSearch")
@ActionRegistration(iconBase = "org/netbeans/modules/php/drupaldevel/drupal.png",
displayName = "#CTL_DrupalSearch")
@ActionReferences({
    @ActionReference(path = "Toolbars/File", position = 500)
})
@Messages("CTL_DrupalSearch=Drupal Search")
public final class DrupalSearch extends AbstractAction implements Presenter.Toolbar {
    
    @Override
    public Component getToolbarPresenter() {
        return new DrupalSearchUI();
    }
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
    }
}
