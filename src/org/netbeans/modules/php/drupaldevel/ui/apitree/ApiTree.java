/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupaldevel.ui.apitree;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
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
    private JPopupMenu contextMenu;
    private ApiTreeItem selItem;
    private JMenuItem[] menuItems = new JMenuItem[3];

    public ApiTree() {

        super(new BorderLayout());
        tree = new JTree(root);
        JScrollPane treeView = new JScrollPane(tree);
        add(treeView, BorderLayout.CENTER);
        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JMenuItem item = (JMenuItem) e.getSource();
                if (item.getText().equals("Insert")) {
                    fireApiItemEvent(new ApiTreeEvent(tree, selItem, ApiTreeEvent.EVENT_INSERT));
                } else if (item.getText().equals("Help")) {
                    fireApiItemEvent(new ApiTreeEvent(tree, selItem, ApiTreeEvent.EVENT_HELP));
                } else if (item.getText().equals("Implimentations")) {
                    fireApiItemEvent(new ApiTreeEvent(tree, selItem, ApiTreeEvent.EVENT_LOOKUP));
                }

            }
        };

        contextMenu = new JPopupMenu();
        Icon icn = new ImageIcon(getClass().getResource("/org/netbeans/modules/php/drupaldevel/ui/apitree/add.png"));

        menuItems[0] = new JMenuItem("Insert", icn);
        icn = new ImageIcon(getClass().getResource("/org/netbeans/modules/php/drupaldevel/ui/apitree/help.png"));
        menuItems[1] = new JMenuItem("Help", icn);
        icn = new ImageIcon(getClass().getResource("/org/netbeans/modules/php/drupaldevel/ui/apitree/find.png"));
        menuItems[2] = new JMenuItem("Implimentations", icn);
        for (int i = 0; i < 3; i++) {
            menuItems[i].addActionListener(al);
            contextMenu.add(menuItems[i]);
        }
        MouseListener ml = new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                    if (selNode.getClass().getSimpleName().equals("ApiTreeItem")) {
                        selItem = (ApiTreeItem) selNode;
                        if (e.getClickCount() == 2) {
                            fireApiItemEvent(new ApiTreeEvent(tree, selItem, ApiTreeEvent.EVENT_INSERT));


                        } else if (e.getClickCount() == 1 && SwingUtilities.isRightMouseButton(e)) {
                            System.out.println(selItem.getName());
                            int x = e.getX();
                            int y = e.getY();
                            if (selItem.getItemType().equals("hooks")) {
                                menuItems[2].setVisible(true);
                                contextMenu.show(tree, x, y);
                            } else if (selItem.getItemType().equals("themes")) {
                                menuItems[2].setVisible(false);
                            }

                            contextMenu.show(tree, x, y);
                        }

                        selectedRow = selPath;
                    }
                }
            }
        };
        tree.addMouseListener(ml);
        setSize(400, 300);
    }

    public void loadPath(String path) {
        root.removeAllChildren();
        root.add(this.getTree(path, root, 0, ""));

        TreeModel mod = new DefaultTreeModel(root);
        tree.setRootVisible(false);
        tree.setModel(mod);


    }

    private DefaultMutableTreeNode getTree(String path, DefaultMutableTreeNode parent, int level, String type) {
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
                    item.setItemType(type);
                    item.setName(name);                    
                    node = item;

                } else {
                    node = new DefaultMutableTreeNode(name);
                }
                //System.out.println(children[i]);
                if (children[i].isDirectory()) {
                    if (level == 0) {
                        type = children[i].getName();
                        System.out.println(type);
                    }
                    level++;
                    node.add(this.getTree(children[i].getPath(), node, level, type));
                    level--;
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
