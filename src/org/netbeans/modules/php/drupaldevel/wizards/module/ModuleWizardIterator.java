/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupaldevel.wizards.module;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;

import org.netbeans.modules.php.drupaldevel.DrupalDevelPreferences;
import org.netbeans.modules.php.drupaldevel.Util;
import org.netbeans.modules.php.drupaldevel.wizards.WizardUtils;
import org.openide.WizardDescriptor;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.ImageUtilities;

public final class ModuleWizardIterator implements WizardDescriptor.InstantiatingIterator {

    private int index;
    private WizardDescriptor wizard;
    private WizardDescriptor.Panel[] panels;
    private FileObject fo;
    private String moduleName;
    private Component c;
    private String drupalVersion;
    private FileObject targetFolder;
    private ModuleWizardPanel2 panel2;
    private String safeName;
    private String libraryPath;
    private static final String fs = File.separator;
    private Project proj;
    private static final Logger LOGGER = Logger.getLogger(ModuleWizardIterator.class.getName());

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {

        this.proj = Util.getActiveProject();
        this.drupalVersion = DrupalDevelPreferences.getDrupalVersion(this.proj);
        this.libraryPath = WizardUtils.WizardTemplatePath(proj, "module", this.drupalVersion);
        if (panels == null) {
            getTargetFolder();
            panel2 = new ModuleWizardPanel2(getTargetFolder(), drupalVersion, this.libraryPath);
            panels = new WizardDescriptor.Panel[]{
                panel2
            };
            String[] steps = createSteps();
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                if (steps[i] == null) {
                    // Default step name to component name of panel. Mainly
                    // useful for getting the name of the target chooser to
                    // appear in the list of steps.
                    steps[i] = c.getName();
                }
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
        }
        return panels;
    }

    @SuppressWarnings("unchecked")
    public Set<FileObject> instantiate() throws IOException {
        this.safeName = panel2.getModuleSafeName();
        this.moduleName = panel2.getModuleName();
        this.drupalVersion = DrupalDevelPreferences.getDrupalVersion(this.proj);
        String basePath = targetFolder.getPath();

        ArrayList fileList = panel2.getFileList();
        fo = FileUtil.toFileObject(new File(basePath));
        FileObject subFolder = fo.createFolder(safeName);
        for (int i = 0; i < fileList.size(); i++) {
            String src = fileList.get(i).toString();
            String name = WizardUtils.fileName(src, this.safeName);
            FileObject file = subFolder.createData(name);
            WizardUtils.generateFile(file, libraryPath, name, src, this.safeName, this.moduleName);
            DataObject dObj = DataObject.find(file);
            OpenCookie oc = (OpenCookie) dObj.getCookie(OpenCookie.class);
            if (oc != null) {
                oc.open();
            }
        }
        //(final FileObject file, String ver, String type, String ext, Object params)
        return Collections.EMPTY_SET;
    }

    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
        wizard.putProperty(WizardDescriptor.PROP_IMAGE, ImageUtilities.loadImage("org/netbeans/modules/php/drupaldevel/wizards/drupallg.png", true));

    }

    private FileObject getTargetFolder() {
        this.targetFolder = Templates.getTargetFolder(wizard);

        return this.targetFolder;
    }

    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }

    public WizardDescriptor.Panel current() {
        return getPanels()[index];
    }

    public String name() {
        return index + 1 + ". from " + getPanels().length;
    }

    public boolean hasNext() {
        return index < getPanels().length - 1;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    public void addChangeListener(ChangeListener l) {
    }

    public void removeChangeListener(ChangeListener l) {
    }

    private String[] createSteps() {
        String[] beforeSteps = null;
        Object prop = wizard.getProperty("WizardPanel_contentData");
        if (prop != null && prop instanceof String[]) {
            beforeSteps = (String[]) prop;
        }

        if (beforeSteps == null) {
            beforeSteps = new String[0];
        }

        String[] res = new String[(beforeSteps.length - 1) + panels.length];
        for (int i = 0; i < res.length; i++) {
            if (i < (beforeSteps.length - 1)) {
                res[i] = beforeSteps[i];
            } else {
                res[i] = panels[i - beforeSteps.length + 1].getComponent().getName();
            }
        }
        return res;
    }
}
