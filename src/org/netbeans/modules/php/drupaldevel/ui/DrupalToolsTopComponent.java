/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupaldevel.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.net.URLEncoder;
import javax.swing.text.JTextComponent;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.*;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.php.drupaldevel.DrupalDevelPreferences;
import org.netbeans.modules.php.drupaldevel.Util;
import org.netbeans.modules.php.drupaldevel.ui.apitree.*;
import javax.swing.text.BadLocationException;
import org.netbeans.api.project.Project;
import org.netbeans.modules.php.drupaldevel.DrupalEditorUtilities;
import org.netbeans.modules.php.drupaldevel.drush.ui.DrushTopComponent;
import org.netbeans.modules.php.drupaldevel.libraryParser;
import org.openide.awt.HtmlBrowser;
import org.netbeans.modules.php.drupaldevel.ui.searchbar.autocomplete.*;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//org.netbeans.modules.php.drupaldevel.ui//DrupalTools//EN",
autostore = false)
@TopComponent.Description(preferredID = "DrupalToolsTopComponent",
iconBase = "org/netbeans/modules/php/drupaldevel/drupal.png",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "navigator", openAtStartup = false)
@ActionID(category = "Window", id = "org.netbeans.modules.php.drupaldevel.ui.DrupalToolsTopComponent")
@ActionReference(path = "Menu/Window/Drupal" /*
 * , position = 333
 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_DrupalToolsAction",
preferredID = "DrupalToolsTopComponent")
public final class DrupalToolsTopComponent extends TopComponent {

    private Lookup.Result result = null;
    private String activeDrupalPath = "";
    private String activeDrupalVersion = "";
    private Project activeProject;
    private JTextComponent activeEditor;
    private PropertyChangeListener propListener;
    private ApiTreeEventListener apiTreeListener;
    private AutoCompleteEventListener acListener;

    public DrupalToolsTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(DrupalToolsTopComponent.class, "CTL_DrupalToolsTopComponent"));
        setToolTipText(NbBundle.getMessage(DrupalToolsTopComponent.class, "HINT_DrupalToolsTopComponent"));
        setActiveVersion();

        propListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                JTextComponent jtc = EditorRegistry.lastFocusedComponent();
                if (jtc != null) {
                    activeEditor = jtc;
                    Project proj = Util.getActiveProject();
                    if (proj != null) {
                        setActiveVersion(proj);
                    }

                }
            }
        };



        apiTreeListener = new ApiTreeEventListener() {

            @Override
            public void itemSelected(ApiTreeEvent evt) {
                ApiTreeItem item = evt.getItem();
                if (evt.getAction().equals(ApiTreeEvent.EVENT_INSERT)) {
                    TemplateParser tp = new TemplateParser(item.getFile());
                    
                    insertCodeToEditor(tp.getTag("template"));
                } else if (evt.getAction().equals(ApiTreeEvent.EVENT_HELP)) {
                    String fName = "";
                    if (item.getItemType().equals("themes")) {
                        fName = "template_" + item.getName();
                    } else if (item.getItemType().equals("hooks")) {
                        fName = "hook_" + item.getName();
                    }

                    if (fName.length() > 0) {
                        try {
                            String url = "http://api.drupal.org/api/search/" + activeDrupalVersion + "/" + URLEncoder.encode(fName, "UTF-8");
                            HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(url));
                        } catch (Exception eee) {
                            return;//nothing much to do
                        }
                    }

                } else if (evt.getAction().equals(ApiTreeEvent.EVENT_LOOKUP)) {
                    DrushTopComponent tc = Util.drushWindow;
                    if (tc == null) {
                        tc = (DrushTopComponent) WindowManager.getDefault().findTopComponent("DrushTopComponent");
                        tc.open();
                        tc.requestActive();
                        
                    } 
                    tc.setActiveProject(activeProject);
                    tc.executeDrush("nb-hook " + item.getName());

                }
            }
        };

    }

    private void insertCodeToEditor(String text) {
        if (this.activeEditor != null) {
            try {

                text = libraryParser.parseVariables(text, this.activeEditor);
                int cursorPos = text.indexOf("${set_cursor}");
                int offset = -1;
                if (cursorPos >= 0) {
                    text = libraryParser.getReplacement(text, "${set_cursor}", "");
                    offset = this.activeEditor.getCaretPosition() + cursorPos;
                }
                DrupalEditorUtilities.insert(text, this.activeEditor);
                if (offset >= 0) {
                    this.activeEditor.setCaretPosition(offset);
                }


            } catch (BadLocationException ble) {
                this.activeEditor.getToolkit().beep();
            } finally {
            }
        }

    }

    private void setActiveVersion(Project proj) {
        String version = DrupalDevelPreferences.getDefaultDrupalVersion();
        String path = null;
        if (proj != null && proj != activeProject) {
            activeProject = proj;
            version = DrupalDevelPreferences.getDrupalVersion(proj);
            path = DrupalDevelPreferences.getLibraryPath(proj);
            setWindowPathItems(path, version);
            this.setDisplayName("Drupal " + version);

        } else {
            if (this.activeDrupalPath.equals("")) {
                apiTree1.setEnabled(false);

            }
        }
    }

    private void setActiveVersion() {
        setActiveVersion(Util.getActiveProject());
    }

    private void setWindowPathItems(String path, String version) {
        this.activeDrupalPath = path;
        apiTree1.setEnabled(true);
        this.activeDrupalVersion = version;
        if (path.equals("")){
            apiTree1.loadPath(DrupalDevelPreferences.libraryInstallPath()  + "/code/" + version);
        } else {
            apiTree1.loadPath(DrupalDevelPreferences.libraryInstallPath()  + "/code/" + version, path + "/code/" + version); 
        }
    }

    @Override
    public void componentOpened() {
        EditorRegistry.addPropertyChangeListener(propListener);
        apiTree1.addApiItemSelection(apiTreeListener);
        //autoComplete1.addAutoCompleteListener(acListener);
        setActiveVersion();
    }

    @Override
    public void componentClosed() {
        EditorRegistry.removePropertyChangeListener(propListener);
        apiTree1.removeApiItemSelection(apiTreeListener);
        //autoComplete1.removeAutoCompleteListener(acListener);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        apiTree1 = new org.netbeans.modules.php.drupaldevel.ui.apitree.ApitTreeUI();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(apiTree1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(apiTree1, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.modules.php.drupaldevel.ui.apitree.ApitTreeUI apiTree1;
    // End of variables declaration//GEN-END:variables

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
