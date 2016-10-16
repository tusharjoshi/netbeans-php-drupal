/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.php.drupal.util;

import java.io.Closeable;

/**
 *
 * @author tusharjoshi
 */
public class ResourceCleaner {
    
    private ResourceCleaner() {
        // utility class no instance needed
    }
    
    public static void close(Closeable closeable) {
        try {
            if( null != closeable ) {
                closeable.close();
            }
        } catch(Exception ex ) {
            // ignore closing exception
        }
    }
}
