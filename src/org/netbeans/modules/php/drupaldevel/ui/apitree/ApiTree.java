/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupaldevel.ui.apitree;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
public class ApiTree extends JPanel {

    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Please open a Drupal project");
    DefaultTreeModel treeModel;
    JTree tree;
    protected EventListenerList listenerList = new EventListenerList();
    public TreePath selectedRow;

    public ApiTree() {

        super(new BorderLayout());
        tree = new JTree(root);
        JScrollPane treeView = new JScrollPane(tree);
        add(treeView, BorderLayout.CENTER);
        final ApiTree me = this;
        MouseListener ml = new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    
                    if (e.getClickCount() == 2) {
                        String strPath;
                        DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                        if (selNode.getClass().getSimpleName().equals("ApiTreeItem")){
                            ApiTreeItem selItem = (ApiTreeItem) selNode;
                            fireApiItemEvent(new ApiTreeEvent(tree, selItem.getFile()));
                            
                        }
                     
                        me.selectedRow = selPath;

                     
                    }
                }
            }
        };
        tree.addMouseListener(ml);
        setSize(400, 300);
    }

    public void loadPath(String path) {
        root.removeAllChildren();
        root.add(this.getTree(path, root, 0));

        TreeModel mod = new DefaultTreeModel(root);
        tree.setRootVisible(false);
        tree.setModel(mod);


    }

    private DefaultMutableTreeNode getTree(String path, DefaultMutableTreeNode parent, int level) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("");

        File dir = new File(path);
        File[] children = dir.listFiles();
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                String name = children[i].getName();
                if (children[i].isFile()) {
                    
                    name = name.replace(".tpl", "");
                    ApiTreeItem item = new ApiTreeItem(name);
                    item.setFile(children[i].getPath());
                    node = item;
     
                } else {
                    node = new DefaultMutableTreeNode(name);
                }
                //System.out.println(children[i]);
                if (children[i].isDirectory()) {
                    node.add(this.getTree(children[i].getPath(), node, 0));
                }
                parent.add(node);
            }
        }

        return node;

    }

    public void addApiItemSelection(ApiTreeEventListener listener) {
        listenerList.add(ApiTreeEventListener.class, listener);
    }

    public void removeApiItemSelection(ApiTreeEventListener listener) {
        listenerList.remove(ApiTreeEventListener.class, listener);
    }

    void fireApiItemEvent(ApiTreeEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == ApiTreeEventListener.class) {
                ((ApiTreeEventListener) listeners[i + 1]).itemSelected(evt);
            }
        }
    }
}
