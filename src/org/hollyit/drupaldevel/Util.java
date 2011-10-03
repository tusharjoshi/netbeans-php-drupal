package org.hollyit.drupaldevel;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import javax.swing.Action;
import javax.swing.Icon;
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
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseDocument;
//import org.netbeans.editor.DocumentUtilities;
import org.openide.loaders.DataObject;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbPreferences;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;

/**
 * Action related utility methods.
 *
 * @author Jamie Holly/HollyIT <jamie@hollyit.org>
 * @version $Rev: 1 $
 */
public class Util {

    private Util() {
        // omitted
    }

    // for use from layers
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

    public static Action lookupActionInLayer(String actionPath) {
        try {
            return (Action) DataObject.find(FileUtil.getConfigFile(actionPath)).getLookup().lookup(InstanceCookie.class).instanceCreate();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

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

    public static void browseDrupalLibraryPath(Component parent, JTextField textField) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(NbBundle.getMessage(Util.class, "LBL_SelectPhpInterpreter"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //chooser.setCurrentDirectory(LastUsedFolders.getOptionsPhpInterpreter());
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(parent)) {
            File phpInterpreter = FileUtil.normalizeFile(chooser.getSelectedFile());
            //LastUsedFolders.setOptionsPhpInterpreter(phpInterpreter);
            textField.setText(phpInterpreter.getAbsolutePath());
        }
    }

    public static String getLibraryPath() {
        String path = "";
        path = NbPreferences.forModule(DrupalDevelTool.class).get("drupalLibraryPath", "");
        File f = new File(path);

        if (!f.exists()) {
            File emulatorBinary = InstalledFileLocator.getDefault().locate(
                    "DrupalDevel", "org.hollyit.dupaldevel", false);
            path = emulatorBinary.getAbsolutePath() ;
        }
        return path;
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

    public static Boolean insertIntoEditor(String text, final JTextComponent target) {
        if (target != null) {
            if (!target.isEditable() || !target.isEnabled()) {
                target.getToolkit().beep();
                return false;
            }
            final Caret caret = target.getCaret();
            final BaseDocument doc = Util.getDocument(target);
            doc.runAtomicAsUser(new Runnable() {

                public void run() {
                    DocumentUtilities.setTypingModification(doc, true);
                    try {
                        if (Util.isSelectionShowing(caret)){
                            
                        } else {
                            
                        }
                    } catch (Exception e) {
                        target.getToolkit().beep();
                    } finally {
                        DocumentUtilities.setTypingModification(doc, false);
                    }
                }
            });
        }
        return false;
    }

    public static BaseDocument getDocument(JTextComponent target) {
        Document doc = target.getDocument();
        return (doc instanceof BaseDocument) ? (BaseDocument) doc : null;
    }
    
    public static boolean isSelectionShowing(Caret caret) {
        return caret.isSelectionVisible() && caret.getDot() != caret.getMark();
    }    
}
