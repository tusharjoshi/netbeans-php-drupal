/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupal.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import javax.swing.text.JTextComponent;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.*;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.php.drupal.DrupalDevelPreferences;
import org.netbeans.modules.php.drupal.Util;
import org.netbeans.api.project.Project;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplateManager;
import org.netbeans.modules.php.drupal.drush.ui.DrushTopComponent;
import org.netbeans.modules.php.drupal.libraryParser;
import org.netbeans.modules.php.drupal.ui.apitree.ApiTreeEvent;
import org.netbeans.modules.php.drupal.ui.apitree.ApiTreeEventListener;
import org.netbeans.modules.php.drupal.ui.apitree.ApiTreeItem;
import org.netbeans.modules.php.drupal.ui.apitree.TemplateParser;
import org.netbeans.modules.php.drupal.ui.searchbar.autocomplete.AutoCompleteEventListener;
import org.openide.awt.HtmlBrowser;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//org.netbeans.modules.php.drupal.ui//DrupalTools//EN",
autostore = false)
@TopComponent.Description(preferredID = "DrupalToolsTopComponent",
iconBase = "org/netbeans/modules/php/drupal/drupal.png",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "navigator", openAtStartup = false)
@ActionID(category = "Window", id = "org.netbeans.modules.php.drupal.ui.DrupalToolsTopComponent")
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


        Project proj = Util.getActiveProject();
        if (proj != null) {
            setActiveVersion(proj);
        }

        apiTreeListener = new ApiTreeEventListener() {

            @Override
            public void itemSelected(ApiTreeEvent evt) {
                ApiTreeItem item = evt.getItem();
                if (evt.getAction().equals(ApiTreeEvent.EVENT_INSERT)) {
                    TemplateParser tp = new TemplateParser(item.getFile());

                    insertCodeToEditor(tp.getTag("template"));
                } else if (evt.getAction().equals(ApiTreeEvent.EVENT_HELP)) {
                    String fName = item.getSearch();

                    if (fName.length() > 0) {
                        try {

                            HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(fName));
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
        this.activeEditor.requestFocus();
        CodeTemplateManager tempManager = CodeTemplateManager.get(this.activeEditor.getDocument());
        text = libraryParser.parseVariables(text, this.activeEditor);
        text = libraryParser.getReplacement(text, "${set_cursor}", "\\$\\{cursor\\}");
        CodeTemplate ct = tempManager.createTemporary(text);

        ct.insert(this.activeEditor);
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
            version = DrupalDevelPreferences.getDefaultDrupalVersion();
            path = DrupalDevelPreferences.getDefaultLibraryPath();
            setWindowPathItems(path, version);
            this.setDisplayName("Drupal " + version);            
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

        if (path.equals("")) {
            apiTree1.loadPath(DrupalDevelPreferences.libraryInstallPath() + "/code/" + version);
        } else {
            apiTree1.loadPath(DrupalDevelPreferences.libraryInstallPath() + "/code/" + version, path + "/code/" + version);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        apiTree1 = new org.netbeans.modules.php.drupal.ui.apitree.ApitTreeUI();

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
    private org.netbeans.modules.php.drupal.ui.apitree.ApitTreeUI apiTree1;
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
