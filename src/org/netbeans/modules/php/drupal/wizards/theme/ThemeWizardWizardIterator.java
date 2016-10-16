/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.php.drupal.wizards.theme;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;

import org.netbeans.modules.php.drupal.DrupalDevelPreferences;
import org.netbeans.modules.php.drupal.util.Util;
import org.netbeans.modules.php.drupal.wizards.WizardUtils;
import org.openide.WizardDescriptor;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.ImageUtilities;

public final class ThemeWizardWizardIterator implements WizardDescriptor.InstantiatingIterator {

    private int index;
    private WizardDescriptor wizard;
    private WizardDescriptor.Panel[] panels;
    private FileObject fo;
    private String moduleName;
    private Component c;
    private String drupalVersion;
    private FileObject targetFolder;
    private ThemeWizardWizardPanel2 panel2;
    private String safeName;
    private ArrayList fileList;
    private static final String fs = File.separator;
    private Project proj;

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
   private WizardDescriptor.Panel[] getPanels() {

        //JOptionPane.showMessageDialog(null, this.fo);
        if (panels == null) {
            this.proj = Util.getActiveProject();
            getTargetFolder();
            panel2 = new ThemeWizardWizardPanel2(getTargetFolder(), DrupalDevelPreferences.getDrupalVersion(this.proj), DrupalDevelPreferences.getLibraryPath(this.proj));
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

    public Set<FileObject> instantiate() throws IOException {
        this.safeName = panel2.getModuleSafeName();
        this.moduleName = panel2.getModuleName();
        this.drupalVersion = DrupalDevelPreferences.getDrupalVersion(this.proj);
        String basePath = targetFolder.getPath();

        ArrayList fileList = panel2.getFileList();
        fo = FileUtil.toFileObject(new File(basePath));

        FileObject subFolder = fo.createFolder(safeName);
        String libraryPath = DrupalDevelPreferences.getLibraryPath(this.proj);
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
        wizard.putProperty(WizardDescriptor.PROP_IMAGE, ImageUtilities.loadImage("org/netbeans/modules/php/drupal/wizards/drupallg.png", true));

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

    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then uncomment
    // the following and call when needed: fireChangeEvent();
    /*
    private Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0
    public final void addChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.add(l);
    }
    }
    public final void removeChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.remove(l);
    }
    }
    protected final void fireChangeEvent() {
    Iterator<ChangeListener> it;
    synchronized (listeners) {
    it = new HashSet<ChangeListener>(listeners).iterator();
    }
    ChangeEvent ev = new ChangeEvent(this);
    while (it.hasNext()) {
    it.next().stateChanged(ev);
    }
    }
     */
    // You could safely ignore this method. Is is here to keep steps which were
    // there before this wizard was instantiated. It should be better handled
    // by NetBeans Wizard API itself rather than needed to be implemented by a
    // client code.
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
