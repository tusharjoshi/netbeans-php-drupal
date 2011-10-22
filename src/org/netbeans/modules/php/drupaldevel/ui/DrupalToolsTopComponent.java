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
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.drupaldevel.DrupalDevelPreferences;
import org.netbeans.modules.php.drupaldevel.Util;
import org.netbeans.modules.php.drupaldevel.ui.apitree.*;
import javax.swing.text.BadLocationException;
import org.netbeans.modules.php.drupaldevel.DrupalEditorUtilities;
import org.netbeans.modules.php.drupaldevel.libraryParser;
import org.openide.awt.HtmlBrowser;
import org.netbeans.modules.php.drupaldevel.autocomplete.*;

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
                    PhpModule proj = Util.getActiveProject();
                    if (proj != null) {
                        setActiveVersion(proj);
                    }

                }
            }
        };
        
        acListener = new AutoCompleteEventListener() {

            @Override
            public void enterPressedEvent(AutoCompleteEvent evt) {
                launchSearch();
            }
        };

        apiTreeListener = new ApiTreeEventListener() {

            @Override
            public void itemSelected(ApiTreeEvent evt) {
                insertCodeToEditor(evt.getItemPath());
            }
        };

    }

    private void insertCodeToEditor(String path) {
        if (this.activeEditor != null) {
            try {
                String text = libraryParser.getTemplate(path);
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

    private void setActiveVersion(PhpModule proj) {
        String version = DrupalDevelPreferences.getDefaultDrupalVersion();
        String path = null;
        if (proj != null) {
            version = DrupalDevelPreferences.getDrupalVersion(proj);
            path = DrupalDevelPreferences.getLibraryPath(proj) + "/code/" + version;
            if (!path.equals(this.activeDrupalPath)) {
                setWindowPathItems(path, version);
                this.setDisplayName("Drupal " + version);
            }
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
        apiTree1.loadPath(path);
        
        autoComplete1.setWordFile(DrupalDevelPreferences.getDefaultLibraryPath() + "/search/" + version + ".txt");
    }
    public void launchSearch() {
        
        try {
            String url = "http://api.drupal.org/api/search/" + this.activeDrupalVersion + "/" + URLEncoder.encode(autoComplete1.getText(), "UTF-8");
            HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(url));
        } catch (Exception eee) {
            return;//nothing much to do
        }
    }
    
    @Override
    public void componentOpened() {
        EditorRegistry.addPropertyChangeListener(propListener);
        apiTree1.addApiItemSelection(apiTreeListener);
        autoComplete1.addAutoCompleteListener(acListener);
        setActiveVersion();
    }

    @Override
    public void componentClosed() {
       EditorRegistry.removePropertyChangeListener(propListener);
       apiTree1.removeApiItemSelection(apiTreeListener);
       autoComplete1.removeAutoCompleteListener(acListener);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        apiTree1 = new org.netbeans.modules.php.drupaldevel.ui.apitree.ApiTree();
        jPanel1 = new javax.swing.JPanel();
        autoComplete1 = new org.netbeans.modules.php.drupaldevel.autocomplete.AutoComplete();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        autoComplete1.setText(org.openide.util.NbBundle.getMessage(DrupalToolsTopComponent.class, "DrupalToolsTopComponent.autoComplete1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(DrupalToolsTopComponent.class, "DrupalToolsTopComponent.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(DrupalToolsTopComponent.class, "DrupalToolsTopComponent.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(autoComplete1, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autoComplete1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jLabel1))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(apiTree1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(apiTree1, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        launchSearch();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.modules.php.drupaldevel.ui.apitree.ApiTree apiTree1;
    private org.netbeans.modules.php.drupaldevel.autocomplete.AutoComplete autoComplete1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
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
