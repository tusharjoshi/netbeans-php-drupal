/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupaldevel.drush.ui;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;

import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.modules.php.drupaldevel.DrupalDevelPreferences;
import org.netbeans.modules.php.drupaldevel.Util;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//org.netbeans.modules.php.drupaldevel.drush.ui//Drush//EN",
autostore = false)
@TopComponent.Description(preferredID = "DrushTopComponent",
iconBase = "org/netbeans/modules/php/drupaldevel/drush/ui/drush16.png",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window", id = "org.netbeans.modules.php.drupaldevel.drush.ui.DrushTopComponent")
@ActionReference(path = "Menu/Window/Drupal" /*
 * , position = 333
 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_DrushAction",
preferredID = "DrushTopComponent")
public final class DrushTopComponent extends TopComponent {

    private boolean libraryGood;
    private JTextComponent activeEditor;
    private PropertyChangeListener propListener;
    private Project activeProject;
    private Boolean debugMode = false;

    public DrushTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(DrushTopComponent.class, "CTL_DrushTopComponent"));
        setToolTipText(NbBundle.getMessage(DrushTopComponent.class, "HINT_DrushTopComponent"));
        btnCancel.setEnabled(false);
        activeProject = Util.getActiveProject();

        propListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JTextComponent jtc = EditorRegistry.lastFocusedComponent();
                if (jtc != null) {
                    activeEditor = jtc;
                    Project proj = Util.getActiveProject();
                    if (proj != null) {
                        activeProject = proj;
                        String historyPath = proj.getProjectDirectory().getPath() + "/nbproject/private/";

                        cmbCommand.setHistory(historyPath + "drushcommands.txt");
                        cmbHost.setHistory(historyPath + "drushhosts.txt");
                    }

                }
            }
        };
        String libPath = DrupalDevelPreferences.getDrushPath();
        boolean libraryCheck = DrupalDevelPreferences.validateDrushPath(libPath);
        if (!libraryCheck) {
            JOptionPane.showMessageDialog(cmbCommand, "Drush was not found. Please configure the Drush path in the options window.", "Drush Not Found Error", JOptionPane.ERROR_MESSAGE);
        }
        this.cmdProcessor.addCommandListener(new CommandProcessorEventListener() {
            @Override
            public void commandDone(CommandProcessorEvent evt) {
                btnCancel.setEnabled(false);
                btnExecute.setEnabled(true);
                double exTime = cmdProcessor.executionTime() / 1000000000.0;
                String done = "<b>Execution complete in " + exTime + " seconds </b>";
                cmdProcessor.println(done);

            }
        });

        cmbCommand.addEnterListener(new CommandHistoryEventListener() {
            @Override
            public void commandEnter(CommandHistoryEvent evt) {
                cmbHost.writeHistory();
                commandParse();
            }
        });

    }

    public void setActiveProject(Project proj) {
        this.activeProject = proj;
    }

    public void commandParse() {
        String command = cmbCommand.getText();
        if (command.equals("+DEBUG")) {
            cmdProcessor.println("<span style='font-weight:bold;color:#ff0000'>[DEBUG MODE ON]</span>");
            debugMode = true;
            cmbCommand.setBackground(Color.red);
        } else if (command.equals("-DEBUG")) {
            cmdProcessor.println("<span style='font-weight:bold;color:#ff0000'>[DEBUG MODE OFF]</span>");
            cmbCommand.setBackground(Color.white);
            debugMode = false;
        } else {

            executeDrush(command);
        }

    }

    public void executeDrush(String input) {


        String libPath = DrupalDevelPreferences.getDrushPath();
        boolean libraryCheck = DrupalDevelPreferences.validateDrushPath(libPath);
        if (!libraryCheck) {
            cmdProcessor.println("<span style='font-weight:bold;color:#ff0000'>[ERROR]</span> Drush was not found. Please configure the Drush path in the options window.");
            return;
        }

        if (activeProject == null || activeProject.getClass().toString() == null || !activeProject.getClass().toString().equals("class org.netbeans.modules.php.project.PhpProject")) {
            cmdProcessor.println("<span style='font-weight:bold;color:#ff0000'>[ERROR]</span> Active project not detected. Please open a PHP project file in the editor then proceed.");
            return;
        }

        String command = "";
        if (System.getProperty("os.name").startsWith("Windows")) {
            command = "cmd /c " + DrupalDevelPreferences.getDrushPath() + "\\drush.bat ";
        } else {
            command = DrupalDevelPreferences.getDrushPath().replace(" ", "\\s") + "/drush ";
        }
        if (!debugMode) {
            String drupalPath = DrupalDevelPreferences.getDrupalPath(activeProject);

            if (drupalPath.equals("")) {
                drupalPath = DrupalDevelPreferences.getSourceDirectory(activeProject);
            }
            if (System.getProperty("os.name").startsWith("Windows")) {
                command += "-r \"" + drupalPath + "\" --include=\"" + DrupalDevelPreferences.getDrushIncludePath() + "\" ";
            } else {
                command += "-r " + drupalPath + " --include=" + DrupalDevelPreferences.getDrushIncludePath().replace(" ", "\\s") + " ";
            }

            btnCancel.setEnabled(true);
            btnExecute.setEnabled(false);
            if (!cmbHost.getText().equals("")) {
                command += " --uri=" + cmbHost.getText();

            }
            command += " -y " + input;
        } else {
            command += " " + input;
        }

        command = command.replaceAll("\\s+", " ");
        cmdProcessor.println("<b>EXECUTE: </b><i>" + command + "</i>");
        String[] cmd = command.split("\\s");

        btnCancel.setEnabled(true);
        System.out.println(command);
        cmdProcessor.runCommand(cmd);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmdProcessor = new org.netbeans.modules.php.drupaldevel.drush.ui.CommandProcessor();
        cmbCommand = new org.netbeans.modules.php.drupaldevel.drush.ui.CommandHistory();
        jLabel1 = new javax.swing.JLabel();
        cmbHost = new org.netbeans.modules.php.drupaldevel.drush.ui.CommandHistory();
        jLabel2 = new javax.swing.JLabel();
        btnExecute = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(DrushTopComponent.class, "DrushTopComponent.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(DrushTopComponent.class, "DrushTopComponent.jLabel2.text")); // NOI18N

        btnExecute.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/php/drupaldevel/drush/ui/Execute.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnExecute, org.openide.util.NbBundle.getMessage(DrushTopComponent.class, "DrushTopComponent.btnExecute.text")); // NOI18N
        btnExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExecuteActionPerformed(evt);
            }
        });

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/php/drupaldevel/drush/ui/cancel.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnCancel, org.openide.util.NbBundle.getMessage(DrushTopComponent.class, "DrushTopComponent.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmdProcessor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbCommand, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cmbHost, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnExecute, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(cmdProcessor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(cmbHost, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                        .addComponent(cmbCommand, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(btnExecute)
                    .addComponent(btnCancel)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExecuteActionPerformed
        cmbCommand.writeHistory();
        cmbHost.writeHistory();
        commandParse();

    }//GEN-LAST:event_btnExecuteActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        btnCancel.setEnabled(false);
        cmdProcessor.println("<b>Aborting...</b>");
        cmdProcessor.cancelCommand();
    }//GEN-LAST:event_btnCancelActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnExecute;
    private org.netbeans.modules.php.drupaldevel.drush.ui.CommandHistory cmbCommand;
    private org.netbeans.modules.php.drupaldevel.drush.ui.CommandHistory cmbHost;
    private org.netbeans.modules.php.drupaldevel.drush.ui.CommandProcessor cmdProcessor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        EditorRegistry.addPropertyChangeListener(propListener);
        btnCancel.setEnabled(false);
        btnExecute.setEnabled(true);
        Util.drushWindow = this;
    }

    @Override
    public void componentClosed() {
        EditorRegistry.removePropertyChangeListener(propListener);
        cmbCommand.writeHistory();
        cmbHost.writeHistory();
        Util.drushWindow = null;
    }

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
