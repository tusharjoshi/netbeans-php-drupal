/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hollyit.drupaldevel.api;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;
import javax.swing.text.JTextComponent;
import org.openide.filesystems.FileObject;
import org.netbeans.modules.editor.NbEditorUtilities;

/**
 *
 * @author jamie
 */
public class libraryParser {

    private String libraryPath = "";

    public libraryParser(String path) {
        this.libraryPath = path;

    }

    public ArrayList getVersions() {
        ArrayList vers = new ArrayList();
        vers = parseTree(this.libraryPath);
        System.out.println(vers);
        return vers;
    }

    public ArrayList getPathItems(String path) {
        ArrayList vers = new ArrayList();
        vers = parseTree(this.libraryPath + "/" + path);
        return vers;
    }

    public static String getTemplate(String tmpPath) {
        return libraryParser.readFileAsString(tmpPath);

    }

    private static String readFileAsString(String filePath) {
        String sb = "";
        try {
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(filePath);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                sb+=strLine + "\n";
            }
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

        return sb.toString();
    }

    public ArrayList getFileItems(String path) {
        ArrayList files = new ArrayList();
        File list = new File(this.libraryPath + "/" + path);
        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".tpl");
            }
        };

        File[] children = list.listFiles(filter);
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                files.add(i, children[i].getName().replace(".tpl", ""));
            }
            Collections.sort(files);
        }
        return files;
    }

    private ArrayList parseTree(String path) {

        ArrayList dirs = new ArrayList();
        File dir = new File(path);
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
                dirs.add(i, children[i].getName());
            }
        }
        Collections.sort(dirs);
        return dirs;
    }
    
    public static String parseVariables (String text, JTextComponent targetComponent){
        String filename = getFileName(targetComponent);
        text = getReplacement(text, "${file_name}", filename);
        
    
        
        return text;
        //return text;
    }
    
    public static String getReplacement(String text, String variable, String replacement){
        variable = Pattern.quote(variable);
        return text.replaceAll(variable, replacement);
 
    }
    static String getFileName(JTextComponent targetComponent) {
        FileObject fo = NbEditorUtilities.getFileObject(targetComponent.getDocument());
        return fo.getName();
    }    
}
