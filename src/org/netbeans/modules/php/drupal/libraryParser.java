/*
 * A class to process the files inside a Drupal code library
 *
 */
package org.netbeans.modules.php.drupal;

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

    /**
     * Constructor for the libraryParser
     * 
     * @param path A string containing the absolute path to the library
     */
    public libraryParser(String path) {
        this.libraryPath = path;

    }

    /**
     * Return the available Drupal versions found inside the library path.
     * @return an ArrayList containing the versions
     */
    public ArrayList getVersions() {
        ArrayList vers = new ArrayList();
        vers = parseTree(this.libraryPath);
        return vers;
    }

    /**
     * Return the directory items found in path (relative to this.libraryPath)
     * 
     * @param path A string containing the relative path to this.libraryPath
     * @return An ArrayList containing the items
     */
    public ArrayList getPathItems(String path) {
        ArrayList vers = new ArrayList();
        vers = parseTree(this.libraryPath + "/" + path);
        return vers;
    }

    /**
     * Read a Drupal template file 
     * 
     * @param tmpPath A string containing the absolute path to the template file
     * @return A string containing the unprocessed template.
     */
    public static String getTemplate(String tmpPath) {
        return libraryParser.readFileAsString(tmpPath);

    }

    /**
     * Reads a file from the filesystem into a string
     * 
     * @param filePath The absolute path to the file
     * @return A string containing the file
     */
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
                sb += strLine + "\n";
            }
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

        return sb.toString();
    }

    /**
     * Returns an ArrayList containing all .tpl files found under path
     * 
     * @param path A string containing the absolute path to the directory to parse
     * @return An ArrayList containing all found template files
     */
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

    /**
     * Returns a list of all directories found inside of path (non-recursive)
     * 
     * @param path A String containing the absolute path to the directory to search
     * @return An array list containing all directories and their paths
     */
    public static ArrayList parseTree(String path) {

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

    /**
     * Parses template replacement variables found in a .tpl file
     * 
     * @param text The text to perform the replacement on
     * @param targetComponent The JTextComponent the action is being performed on
     * @return A string containing the processed text
     */
    public static String parseVariables(String text, JTextComponent targetComponent) {
        String filename = getFileName(targetComponent);
        text = getReplacement(text, "${file_name}", filename);
        return text;
    }

    /**
     * Helper function to perform a regex replacement on a string
     * 
     * @param text A string containing the text to process
     * @param variable A string containing the variable name
     * @param replacement A string containing the replacement text of variable
     * @return A string containing the processed text.
     */
    public static String getReplacement(String text, String variable, String replacement) {
        variable = Pattern.quote(variable);
        return text.replaceAll(variable, replacement);

    }

    /**
     * Returns the file name of targetComponent
     * 
     * @param targetComponent a JTextComponent to perform the lookup on.
     * @return A sting containing the filename of targetComponent.
     */
    static String getFileName(JTextComponent targetComponent) {
        FileObject fo = NbEditorUtilities.getFileObject(targetComponent.getDocument());
        String fileName = fo.getName();
        if (fileName.contains(".")){
            fileName = fileName.substring(0,fileName.indexOf("."));
        }
        return fileName;
    }
}
