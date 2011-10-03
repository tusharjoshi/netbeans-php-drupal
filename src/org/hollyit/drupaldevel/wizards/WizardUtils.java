/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hollyit.drupaldevel.wizards;

/**
 *
 * @author Jamie Holly/HollyIT <jamie@hollyit.org>
 */
import java.io.FileFilter;
import java.util.regex.Pattern;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import org.hollyit.drupaldevel.Util;
import org.hollyit.drupaldevel.api.libraryParser;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.util.Exceptions;

public class WizardUtils {

    public static ArrayList getMajorVersions(final String templateType) {

        ArrayList dirs = new ArrayList();
        File dir = new File(Util.getLibraryPath() + "/files/" + templateType);

        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File dir) {
                if (dir.isDirectory() && !dir.toString().startsWith(".") && !dir.toString().startsWith("_")) {
                    return true;
                }
                return false;
            }
        };
        File[] children = dir.listFiles(filter);
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                //JOptionPane.showMessageDialog(null, Util.getLibraryPath() + "/_templates/");
                dirs.add(i, children[i].getName());
            }
        }
        Collections.sort(dirs);
        return dirs;


    }

    public static ArrayList getFilesList(final String templateType, final String version) {

        ArrayList dirs = new ArrayList();
        File dir = new File(Util.getLibraryPath() + "/files/" + templateType + "/" + version);
        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File dir) {
                if (!dir.isDirectory()) {
                    return true;
                }
                return false;
            }
        };
        File[] children = dir.listFiles(filter);
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                String fn = children[i].getName();
                
                dirs.add(i, fn);
            }
        }
        Collections.sort(dirs);
        return dirs;


    }

    public static String fileName (String file, String moduleName){
        String name = file.replaceAll("_drupal_safe_name_", moduleName);
        
        return name;
    }
    public static void generateFile(final FileObject file, String ver, String type, String name, String src, String safeName, String realName) throws IOException {
        try {
            FileSystem fs = file.getFileSystem();
            String text = libraryParser.getTemplate(Util.getLibraryPath() + "/files/" + type + "/" + ver + "/" + src);
            //JOptionPane.showMessageDialog(null, Util.getLibraryPath() + "/files/" + type + "/" + ver + "/" + src);
            text = text.replaceAll(Pattern.quote("${real_name}"), realName);
            text = text.replaceAll(Pattern.quote("${safe_name}"), safeName);
            final String textOut = text;
            fs.runAtomicAction(new FileSystem.AtomicAction() {

                public void run() throws IOException {
                    FileLock lock = file.lock();
                    try {
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(file.getOutputStream(lock)));

                        bw.write(textOut);
                        bw.close();
                    } finally {
                        lock.releaseLock();
                    }
                }
            });

        } catch (FileStateInvalidException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
