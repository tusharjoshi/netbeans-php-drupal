/**
 * This file contains various utilities to help with the processing of the
 * module
 */
package org.netbeans.modules.php.drupal;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import java.io.File;
import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.php.drupal.drush.ui.DrushTopComponent;
import org.openide.loaders.DataObject;
import org.openide.util.Parameters;
import org.openide.windows.TopComponent;

/**
 * @author Jamie Holly/HollyIT <jamie@hollyit.org>
 * @version $Rev: 1 $
 */
public class Util {
    public static String PHPModule = "org.netbeans.modules.php.project.PhpProject";
    public static DrushTopComponent drushWindow;
    private Util() {
        // omitted
    }

    /**
     * Helper function to lookup layers in form.
     * 
     * @param m A Map to perform the lookup on
     * @return The ContextAwareAction or null on not found.
     */
    public static ContextAwareAction alwaysEnabled(Map m) {
        try {
            Class<?> aeaClass = Lookup.getDefault().lookup(ClassLoader.class).loadClass("org.openide.awt.AlwaysEnabledAction");
            Method createMethod = aeaClass.getDeclaredMethod("create", Map.class);
            createMethod.setAccessible(true);
            ContextAwareAction action = (ContextAwareAction) createMethod.invoke(aeaClass, m);
            return new ContextAwareActionWrapper(action);

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    /**
     * Performas an action lookup o on the actionPath
     * 
     * @param actionPath A string containing the path to perform the lookup on
     * @return The action found or null on not found
     */
    public static Action lookupActionInLayer(String actionPath) {
        try {
            return (Action) DataObject.find(FileUtil.getConfigFile(actionPath)).getLookup().lookup(InstanceCookie.class).instanceCreate();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    /**
     * Creates an action for selecting a node.
     * 
     * @param fo
     * @param isProjectTab
     * @return
     * @throws Exception 
     */
    static Action createSelectNodeAction(FileObject fo, boolean isProjectTab) throws Exception {
        // Use reflection instead a implementation dependency to the project.ui module
        ClassLoader cl = Lookup.getDefault().lookup(ClassLoader.class);
        Class<?> cSelectNodeAction = cl.loadClass("org.netbeans.modules.project.ui.actions.SelectNodeAction");
        Constructor<?> con = cSelectNodeAction.getDeclaredConstructor(Icon.class, String.class, Lookup.class);
        con.setAccessible(true);

        String tab = isProjectTab ? "Project" : "Files";
        String img = "org/netbeans/modules/project/ui/resources/" + tab + "Tab.png";
        String msg = isProjectTab ? "LBL_SelectInProjectsAction_MainMenuName" : "LBL_SelectInFilesAction_MainMenuName";

        Action act = (Action) con.newInstance(ImageUtilities.loadImageIcon(img, false),
                NbBundle.getMessage(cSelectNodeAction, msg),
                Lookups.singleton(fo));
        Field findInField = cSelectNodeAction.getDeclaredField("findIn");
        findInField.setAccessible(true);
        findInField.set(act, isProjectTab ? "projectTabLogical_tc" : "projectTab_tc");
        return act;
    }

    /**
     * Helper function to launch a file/path browser dialog
     * 
     * @param parent The parent component of this component
     * @param textField the textfield the browser is bound to.
     */
    public static void browseDrupalLibraryPath(Component parent, JTextField textField) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(NbBundle.getMessage(Util.class, "LBL_SelectPhpInterpreter"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(parent)) {
            File phpInterpreter = FileUtil.normalizeFile(chooser.getSelectedFile());
            textField.setText(phpInterpreter.getAbsolutePath());
        }
    }

    /**
     * @return the selected folder or <code>null</code>.
     */
    public static File browseLocationAction(final Component parent, File currentDirectory, String title) {
        return browseAction(parent, currentDirectory, title, JFileChooser.DIRECTORIES_ONLY);
    }

    private static File browseAction(final Component parent, File currentDirectory, String title, int mode) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(mode);
        if (currentDirectory != null
                && currentDirectory.exists()) {
            chooser.setSelectedFile(currentDirectory);
        }
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(parent)) {
            return FileUtil.normalizeFile(chooser.getSelectedFile());
        }
        return null;
    }

    /**
     * Returns the base document of target
     * 
     * @param target The JTextComponent to return the document of
     * @return The found BaseDocument or null if not found.
     */
    public static BaseDocument getDocument(JTextComponent target) {
        Document doc = target.getDocument();
        return (doc instanceof BaseDocument) ? (BaseDocument) doc : null;
    }

    /**
     * A helper function to convert strings to ArrayList objects
     * 
     * @param item The string to convert
     * @return A new object containing the string.
     */
    public static Object makeObj(final String item) {
        return new Object() {

            @Override
            public String toString() {
                return item;
            }
        };
    }

    /**
     * Retrieves the active project based upon the focused editor
     * 
     * @return The actual PhpModule found or null on not found.
     */
    public static Project getActiveProject() {
        FileObject fo;

        try {
            TopComponent tc = TopComponent.getRegistry().getActivated();

            DataObject mainDataObj = tc.getLookup().lookup(DataObject.class);
            fo = mainDataObj.getPrimaryFile();
            Project proj = FileOwnerQuery.getOwner(fo);
     
            return proj;
        } catch (Exception e) {
        }
        return null;
    }
    
    public static Project lookupPhpModule(Lookup lookup) {
        Parameters.notNull("lookup", lookup);

        // try directly
        Project result = lookup.lookup(Project.class);
        if (result != null) {
            return result;
        }
        // try through Project instance
        Project project = lookup.lookup(Project.class);
        if (project != null) {
            result = project.getLookup().lookup(Project.class);
            if (result != null) {
                return result;
            }
        }
        return null;
    }    
}
