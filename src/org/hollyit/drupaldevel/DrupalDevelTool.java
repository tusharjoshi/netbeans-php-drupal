package org.hollyit.drupaldevel;

/**
 *
 * @author Jamie Holly/HollyIT <jamie@hollyit.org>
 */
import java.awt.event.ActionEvent;
import org.openide.util.NbBundle;
import org.netbeans.editor.BaseAction;
import org.openide.util.actions.Presenter;
import javax.swing.text.JTextComponent;
import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.text.BadLocationException;
import org.hollyit.drupaldevel.api.DrupalDevelDialog;
import org.hollyit.drupaldevel.api.libraryParser;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager.Result;
import org.openide.util.ImageUtilities;

public final class DrupalDevelTool {

    public static final String drupalDevelAction = "drupal-devel-action"; // NOI18N
    private static final long serialVersionUID = 1773902972727935215L;
    private Result lkpInfo;

    @NbBundle.Messages(drupalDevelAction + "=File Context")
    static class DrupalDevelAction extends BaseAction implements Presenter.Toolbar {

        private DrupalDevelDialog dialog;
        private Boolean dialogInit = false;
        private Project proj;

        public DrupalDevelAction() {
        }

        @Override
        public Component getToolbarPresenter() {

            // Create our toolbar button and assign the Drop icon to it.
            String iconBase = (String) "org/hollyit/drupaldevel/drupal.png";
            Icon icon = (Icon) ImageUtilities.loadImageIcon(iconBase, true);
            JButton tbButton = new JButton(icon);
            //toolbarButton.setAction(this);
            tbButton.addActionListener(this);
            tbButton.setToolTipText((String) getValue(NAME));
            return tbButton;
        }

        @Override
        public void actionPerformed(ActionEvent evt, final JTextComponent target) {
            // Action selectInProjectsAction = Util.lookupActionInLayer("Actions/Window/SelectDocumentNode/org-netbeans-modules-project-ui-SelectInProjects.instance");
            // selectInProjectsAction.actionPerformed(evt);
            if (!dialogInit) {
                dialogInit = true;
                dialog = new DrupalDevelDialog(null, true);
                dialog.setLibrary(Util.getLibraryPath() + "/code");
                dialog.btnInsert.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        try {
                            String text = libraryParser.getTemplate(dialog.libraryPath + "/" + dialog.getItem() + ".tpl");
                            text = libraryParser.parseVariables(text, target);
                            int cursorPos = text.indexOf("${set_cursor}");
                            int offset = -1;
                            if (cursorPos>=0){
                                text = libraryParser.getReplacement(text, "${set_cursor}", "");
                                offset = target.getCaretPosition() + cursorPos;
                            }
                            DrupalEditorUtilities.insert(text, target);
                            if (offset>=0){
                                target.setCaretPosition(offset);
                            }
                            
                            
                        } catch (BadLocationException ble) {
                            target.getToolkit().beep();
                        } finally {
                            dialog.setVisible(false);
                        }

                    }
                });
            }
            dialog.setVisible(true);
            //JOptionPane.showMessageDialog(null, Util.getLibraryPath());

        }
    }
}
