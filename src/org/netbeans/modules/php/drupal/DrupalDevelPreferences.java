/*
 * Helper to store and retrieve global and per project preferences.
 */
package org.netbeans.modules.php.drupal;

import org.netbeans.modules.php.drupal.util.LibraryParser;
import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbPreferences;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;

/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
public final class DrupalDevelPreferences {

    private static final String DRUPAL_VERSION = "version"; // NOI18N
    private static final String DRUPAL_LIBRARY_PATH = "librarypath"; // NOI18N
    private static final String DRUPAL_DRUSH_PATH = "drushpath"; // NOI18N
    private static final String DRUPAL_PATH = "drupalpath"; // NOI18N
    private static final String DRUPAL_DEFAULT_VERSION = "6"; // NOI18N

    private DrupalDevelPreferences() {
    }

    /**
     * Retrieves the Drupal version for a specific module. If the version is not found
     * then the global default version is returned
     * 
     * @param phpModule the phpModule to retrieve the version for
     * @return a string containing the Drupal version
     */
    public static String getDrupalVersion(Project phpModule) {
        Preferences preferences = getPreferences(phpModule);
        String drupalVersion = preferences.get(DRUPAL_VERSION, "");
        if (drupalVersion.equals("") || drupalVersion.equals("Default")) {
            drupalVersion = getDefaultDrupalVersion();
        }
        return drupalVersion;
    }

    /**
     * Returns the global default Drupal version as set in the options window.
     * If that option is not set then the hard coded default version is returned
     * from DRUPAL_DEFAULT_VERSION
     * 
     * @return A string containing the default Drupal version
     */
    public static String getDefaultDrupalVersion() {
        String version = NbPreferences.forModule(DrupalDevelTool.class).get("drupalVersion", "");
        if (version.equals("")) {
            version = DRUPAL_DEFAULT_VERSION;
        }
        return version;
    }

    /**
     * Stores the project specific Drupal version in private.properties file of the project
     * 
     * @param phpModule The phpModule the setting applies to
     * @param drupalVersion A string containing the numeric Drupal version.
     */
    public static void setDrupalVersion(Project phpModule, String drupalVersion) {
        getPreferences(phpModule).put(DRUPAL_VERSION, drupalVersion);
    }

    /**
     * Stores the global default Drupal version as defined in the options window
     * 
     * @param drupalVersion A string containing the numeric Drupal version.
     */
    public static void setDefaultDrupalVersion(String drupalVersion) {
        NbPreferences.forModule(DrupalDevelTool.class).put("drupalVersion", drupalVersion);
    }

    /**
     * Helper function to return the preference for a PHP project
     * @param phpModule the phpModule to retrieve the preference for
     * @return the Preference or null if not set.
     */
    private static Preferences getPreferences(Project phpModule) {
        try {
            return ProjectUtils.getPreferences(phpModule, DrupalDevelPreferences.class, false);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Returns the default library path as set in the options window. If the library path
     * is not set or is invalid then the default cluster files are used.
     * 
     * @return A string containing the absolute path to the library files.
     */
    public static String getDefaultLibraryPath() {
        String path = NbPreferences.forModule(DrupalDevelTool.class).get("drupalLibraryPath", "");
        File f = new File(path);

        return path;
    }

    /**
     * Retrieves the cluster installation library path
     * @return a string containing the absolute path to the cluster library
     */
    public static String libraryInstallPath() {
        File emulatorBinary = InstalledFileLocator.getDefault().locate(
                "DrupalDevel", "org.netbeans.modules.php.dupaldevel", false);
        String path = emulatorBinary.getAbsolutePath();
        
                
        return path;
    }

    /**
     * Stores the system wide library path as defined under the options window
     * 
     * @param path A string containing the absolute path to the library
     */
    public static void setDefaultLibraryPath(String path) {
        NbPreferences.forModule(DrupalDevelTool.class).put("drupalLibraryPath", path);
    }

    /**
     * Retrieve the library path for a given project or the global default if the library
     * path is not set in the project properties window.
     * 
     * @param phpModule The phpModule to perform the lookup on
     * @return A string containing the absolute path to the library files.
     */
    public static String getLibraryPath(Project phpModule) {
        return getLibraryPath(phpModule, true);
    }

    public static String getLibraryPath(Project phpModule, Boolean includeDefault) {
        Preferences preferences = getPreferences(phpModule);
        String drupalVersion = preferences.get(DRUPAL_LIBRARY_PATH, "");
        if (includeDefault && (drupalVersion.isEmpty() || drupalVersion.equals(""))) {
            drupalVersion = getDefaultLibraryPath();
        }
        return drupalVersion;
    }

    /**
     * Sets the projects library path in the project's private.properties file
     * 
     * @param phpModule the phpModule to save the setting with
     * @param path A string containing the absolute path to the library
     */
    public static void setLibraryPath(Project phpModule, String path) {
        getPreferences(phpModule).put(DRUPAL_LIBRARY_PATH, path);
    }

    /**
     * A helper function searching for key items required in a library path.
     * 
     * @param path A string containing the absolute path to the library
     * @return TRUE if the library is valid, otherwise returns FALSE;
     */
    public static Boolean validateLibraryPath(String path) {
        Boolean hasCode = false;
        Boolean hasFiles = false;

        ArrayList items = LibraryParser.parseTree(path);
        for (int i = 0; i < items.size(); i++) {
            String item = items.get(i).toString();

            if (item.equals("code")) {
                hasCode = true;
            }
            if (item.equals("files")) {
                hasFiles = true;
            }
        }
        return hasCode && hasFiles;
    }

    /**
     * Sets the projects library path in the project's private.properties file
     * 
     * @param phpModule the phpModule to save the setting with
     * @param path A string containing the absolute path to the library
     */
    public static void setDrushPath(String path) {
        NbPreferences.forModule(DrupalDevelTool.class).put("drupalDrushPath", path);
    }

    /**
     * Retrieve the library path for a given project or the global default if the library
     * path is not set in the project properties window.
     * 
     * @param phpModule The phpModule to perform the lookup on
     * @return A string containing the absolute path to the library files.
     */
    public static String getDrushPath() {
        String path = NbPreferences.forModule(DrupalDevelTool.class).get("drupalDrushPath", "");

        return path;

    }

    /**
     * A helper function searching for key items required in a library path.
     * 
     * @param path A string containing the absolute path to the library
     * @return TRUE if the library is valid, otherwise returns FALSE;
     */
    public static Boolean validateDrushPath(String path) {
        Boolean hasCode = false;
        File list = new File(path);
        File[] children = list.listFiles();
        ArrayList items = LibraryParser.parseTree(path);
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                if (children[i].getName().equals("drush")) {
                    hasCode = true;
                }
            }
        }
        return hasCode;
    }

    public static String getDrushIncludePath() {

        File emulatorBinary = InstalledFileLocator.getDefault().locate(
                "NBDrush", "org.netbeans.modules.php.dupaldevel", false);
        String path = emulatorBinary.getAbsolutePath();
        return path;
    }

    /**
     * Retrieve the library path for a given project or the global default if the library
     * path is not set in the project properties window.
     * 
     * @param phpModule The phpModule to perform the lookup on
     * @return A string containing the absolute path to the library files.
     */
    public static String getDrupalPath(Project phpModule) {
        Preferences preferences = getPreferences(phpModule);
        String drupalVersion = preferences.get(DRUPAL_PATH, "");
        return drupalVersion;
    }

    /**
     * Sets the projects library path in the project's private.properties file
     * 
     * @param phpModule the phpModule to save the setting with
     * @param path A string containing the absolute path to the library
     */
    public static void setDrupalPath(Project phpModule, String path) {
        getPreferences(phpModule).put(DRUPAL_PATH, path);
    }

    public static String getSourceDirectory(Project project) {
        String path = "";
        
        Sources sources = (Sources) project.getLookup().lookup(Sources.class);
        SourceGroup[] groups = sources.getSourceGroups(Sources.TYPE_GENERIC);
        for (int i=0; i<groups.length; i++){
            if (groups[i].getName().equals("${src.dir}")){
                path = groups[i].getRootFolder().getPath();
            }
        }
        
        return path;
    }
}
