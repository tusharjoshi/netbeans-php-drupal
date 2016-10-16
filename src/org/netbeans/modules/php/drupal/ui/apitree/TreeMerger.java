/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupal.ui.apitree;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
public class TreeMerger {

    public HashMap mergePaths(String path1, String path2) {
        HashMap ret = this.processDir(path1);
        HashMap ret2 = this.processDir(path2);
        return this.merge(ret, ret2);
    }

    private HashMap merge(HashMap map1, HashMap map2) {

        Iterator it = map2.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            
            
            if (pairs.getValue() instanceof HashMap){
                HashMap mergetemp;
                if (map1.get(pairs.getKey()) != null){
                    HashMap m1 = (HashMap) map1.get(pairs.getKey());
                    HashMap m2 = (HashMap) map2.get(pairs.getKey());
                    mergetemp = this.merge(m1, m2);
                } else {
                    HashMap m1 = new HashMap();
                    HashMap m2 = (HashMap) map2.get(pairs.getKey());
                    mergetemp = this.merge(m1, m2);
                }
               map1.put(pairs.getKey(), mergetemp);
            } else {
                if (map1.get(pairs.getKey()) != null) {
                    map1.remove(pairs.getKey());
                }
               map1.put(pairs.getKey(), pairs.getValue());
            }
            it.remove(); // avoids a ConcurrentModificationException
        }


        return map1;
    }

    private HashMap processDir(String path) {
        HashMap map = new HashMap();
        File dir = new File(path);
        File[] children = dir.listFiles();

        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                String name = children[i].getName();
                String cleanName;
                if (children[i].isFile()) {
                    if (name.endsWith(".xml")) {
                        TemplateParser parse = new TemplateParser(children[i].getPath());
                        cleanName = parse.getTag("title");
                        ApiTreeItem item = new ApiTreeItem(cleanName);
                        item.setFile(children[i].getPath());
                        item.setItemType(parse.getTag("group"));
                        item.setSearch(parse.getTag("external"));
                        item.setName(cleanName);
                        map.put(name, item);
                    }
                }
                if (children[i].isDirectory()) {

                    map.put(children[i].getName(), this.processDir(children[i].getPath()));

                }

            }
        }
        return map;
    }
}
