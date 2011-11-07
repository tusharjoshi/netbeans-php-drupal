/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupaldevel.ui.apitree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.tree.*;

/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
public class ApitTreeUI extends javax.swing.JPanel {
    
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Please open a Drupal project");
    DefaultTreeModel treeModel;
    /**
     * Creates new form ApitTreeUI
     */
    protected EventListenerList listenerList = new EventListenerList();
    public TreePath selectedRow;
    private JPopupMenu contextMenu;
    private ApiTreeItem selItem;
    private JMenuItem[] menuItems = new JMenuItem[3];
    private ArrayList searchResults = new ArrayList();
    private int lastSearchIndex = 0;
    private String lastSearch = "";
    
    public ApitTreeUI() {
        initComponents();
        HTMLEditorKit hed = new HTMLEditorKit();
        StyleSheet sheet = hed.getStyleSheet();
        
        sheet.addRule(".php-boundry {font-weight:bold;}");
        sheet.addRule(".php-function-or-constant {color:#0000AA; }");
        sheet.addRule(".php-string {color:#AA0000; }");
        sheet.addRule(".php-constant {color:#AA3333; }");
        sheet.addRule(".php-variable {color:#333300; }");
        sheet.addRule(".php-keyword {color:#006600; }");
        sheet.addRule("pre {background-color: #F6F6F2; white-space:pre-wrap;}");
        sheet.addRule(".code {background-color: #F6F6F2; white-space:pre-wrap;margin-bottom:5px;margin-top:5px;}");
        sheet.addRule(".code p{margin-left:5px}");
        Document doc = hed.createDefaultDocument();
        txtHelp.setEditorKit(hed);
        tree.setModel(treeModel);        
        tree.setModel(treeModel);
        tree.setCellRenderer(new TreeRenderer());
        ActionListener al = new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JMenuItem item = (JMenuItem) e.getSource();
                if (item.getText().equals("Insert")) {
                    fireApiItemEvent(new ApiTreeEvent(tree, selItem, ApiTreeEvent.EVENT_INSERT));
                } else if (item.getText().equals("Help")) {
                    fireApiItemEvent(new ApiTreeEvent(tree, selItem, ApiTreeEvent.EVENT_HELP));
                } else if (item.getText().equals("Implementations")) {
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
        menuItems[2] = new JMenuItem("Implementations", icn);
        for (int i = 0; i < 3; i++) {
            menuItems[i].addActionListener(al);
            contextMenu.add(menuItems[i]);
        }
        
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                /*
                 * if nothing is selected
                 */
                if (node == null) {
                    return;
                }

                /*
                 * retrieve the node that was selected
                 */
                
                if (node instanceof ApiTreeItem) {
                    ApiTreeItem item = (ApiTreeItem) node;
                    
                    TemplateParser tp = new TemplateParser(item.getFile());
                    String help = tp.getTag("help");
                    if (help.equals("")){
                        help="<h2><center>No help available</center></h2>";
                    }
                    txtHelp.setText(help);
                    txtHelp.setCaretPosition(0);
                    
                }
            }
        });
        
    }
    
    public void loadPath(String path) {
        TreeMerger tm = new TreeMerger();
        HashMap map = tm.mergePaths(path, "");
        populateTree(map);
        
        
        
    }
    
    public void loadPath(String path, String path2) {
        TreeMerger tm = new TreeMerger();
        HashMap map = tm.mergePaths(path, path2);
        populateTree(map);
        
        
        
    }
    
    private void populateTree(HashMap map) {
        root.removeAllChildren();
        DefaultMutableTreeNode nodes = this.parseMap(map, root);
        
        root.add(nodes);
        sortNodes(root);
        treeModel = new DefaultTreeModel(root);
        tree.setRootVisible(false);
        tree.setModel(treeModel);
    }
    
    private DefaultMutableTreeNode parseMap(HashMap map, DefaultMutableTreeNode parent) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("");
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            
            
            if (pairs.getValue() instanceof HashMap) {
                node = new DefaultMutableTreeNode(pairs.getKey());
                HashMap map1 = (HashMap) pairs.getValue();
                node.add(parseMap(map1, node));
            } else {
                node = (ApiTreeItem) pairs.getValue();
                
            }
            parent.add(node);
            it.remove(); // avoids a ConcurrentModificationException
        }
        
        return node;
    }
    
    private void sortNodes(DefaultMutableTreeNode node) {
        ArrayList children = Collections.list(node.children());
        // for getting original location
        ArrayList<String> orgCnames = new ArrayList<String>();
        // new location
        ArrayList<String> cNames = new ArrayList<String>();
        //move the child to here so we can move them back
        DefaultMutableTreeNode temParent = new DefaultMutableTreeNode();
        for (Object child : children) {
            DefaultMutableTreeNode ch = (DefaultMutableTreeNode) child;
            if (ch.getChildCount()>0){
                sortNodes(ch);
            }
            temParent.insert(ch, 0);
            cNames.add(ch.toString().toUpperCase());
            orgCnames.add(ch.toString().toUpperCase());
        }
        Collections.sort(cNames);
        for (String name : cNames) {
            // find the original location to get from children arrayList
            int indx = orgCnames.indexOf(name);
            node.insert((DefaultMutableTreeNode) children.get(indx), node.getChildCount());
        }
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
    
    private int getItemCount() {
        int count = 0;
        Enumeration en = root.breadthFirstEnumeration();

        //iterate through the enumeration 
        while (en.hasMoreElements()) {
            
            count++;
            
        }
        
        return count;
    }

    /** 
     * This method takes the node string and 
     * traverses the tree till it finds the node 
     * matching the string. If the match is found  
     * the node is returned else null is returned 
     *  
     * @param nodeStr node string to search for 
     * @return tree node  
     */
    public DefaultMutableTreeNode searchNode(String nodeStr, int dir) {
        nodeStr = nodeStr.toLowerCase();
        if (!nodeStr.equals(lastSearch)) {
            searchResults = resultsMap(nodeStr);
            lastSearchIndex = (dir == 0) ? -1 : searchResults.size();
            lastSearch = nodeStr;
        }
        
        if (searchResults.size() < 1) {
            return null;
        }
        
        if (dir == 0) {
            lastSearchIndex++;
            if (lastSearchIndex > (searchResults.size() - 1)) {
                lastSearchIndex = 0;
            }
        } else {
            lastSearchIndex--;
            if (lastSearchIndex < 0) {
                lastSearchIndex = searchResults.size() - 1;
            }
        }

        //tree node with string node found return null 
        return (DefaultMutableTreeNode) searchResults.get(lastSearchIndex);
    }
    
    private ArrayList resultsMap(String nodeStr) {
        ArrayList map = new ArrayList();
        DefaultMutableTreeNode node = null;

        //Get the enumeration 
        Enumeration en = root.breadthFirstEnumeration();
        //iterate through the enumeration 

        while (en.hasMoreElements()) {
            //get the node 
            node = (DefaultMutableTreeNode) en.nextElement();

            //match the string with the user-object of the node 
            if (node.getChildCount() == 0 && node.getUserObject().toString().startsWith(nodeStr)) {
                
                map.add(node);
                
            }
        }
        
        return map;
    }
    
    private void doSearch(int dir) {
        btnSearchPrevious.setEnabled(false);
        btnSearchNext.setEnabled(false);
        if (txtFind.getText().length() > 1) {
            DefaultMutableTreeNode node = searchNode(txtFind.getText(), dir);
            if (node != null) {
                //make the node visible by scroll to it 
                TreeNode[] nodes = treeModel.getPathToRoot(node);
                TreePath path = new TreePath(nodes);
                collapseTree(tree, root);
                tree.scrollPathToVisible(path);
                tree.setSelectionPath(path);
                btnSearchPrevious.setEnabled(true);
                btnSearchNext.setEnabled(true);
            }
        }
    }

    /**
     * @param tree com.sun.java.swing.JTree
     * @param start com.sun.java.swing.tree.DefaultMutableTreeNode
     */
    private void expandTree(JTree tree, DefaultMutableTreeNode start) {
        for (Enumeration children = start.children(); children.hasMoreElements();) {
            DefaultMutableTreeNode dtm = (DefaultMutableTreeNode) children.nextElement();
            if (!dtm.isLeaf()) {
                //
                TreePath tp = new TreePath(dtm.getPath());
                tree.expandPath(tp);
                //
                expandTree(tree, dtm);
            }
        }
        return;
    }

    /**
     * @param tree com.sun.java.swing.JTree
     * @param start com.sun.java.swing.tree.DefaultMutableTreeNode
     */
    private void collapseTree(JTree tree, DefaultMutableTreeNode start) {
        for (Enumeration children = start.children(); children.hasMoreElements();) {
            DefaultMutableTreeNode dtm = (DefaultMutableTreeNode) children.nextElement();
            if (!dtm.isLeaf()) {
                //
                TreePath tp = new TreePath(dtm.getPath());
                tree.collapsePath(tp);
                //
                collapseTree(tree, dtm);
            }
        }
        return;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtFind = new javax.swing.JTextField();
        btnSearchPrevious = new javax.swing.JButton();
        btnSearchNext = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtHelp = new javax.swing.JTextPane();

        txtFind.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFindKeyReleased(evt);
            }
        });

        btnSearchPrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/php/drupaldevel/ui/apitree/searchup.png"))); // NOI18N
        btnSearchPrevious.setToolTipText("Find Previous");
        btnSearchPrevious.setEnabled(false);
        btnSearchPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchPreviousActionPerformed(evt);
            }
        });

        btnSearchNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/php/drupaldevel/ui/apitree/searchdown.png"))); // NOI18N
        btnSearchNext.setToolTipText("Find Next Occurence");
        btnSearchNext.setEnabled(false);
        btnSearchNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchNextActionPerformed(evt);
            }
        });

        jSplitPane1.setDividerLocation(120);
        jSplitPane1.setDividerSize(3);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(tree);

        jSplitPane1.setLeftComponent(jScrollPane1);

        txtHelp.setContentType("text/html");
        txtHelp.setEditable(false);
        jScrollPane2.setViewportView(txtHelp);

        jSplitPane1.setRightComponent(jScrollPane2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(txtFind)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearchPrevious, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearchNext, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnSearchNext, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtFind)
                    .addComponent(btnSearchPrevious, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtFindKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFindKeyReleased
        doSearch(0);
    }//GEN-LAST:event_txtFindKeyReleased
    
    private void treeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMousePressed
        int selRow = tree.getRowForLocation(evt.getX(), evt.getY());
        TreePath selPath = tree.getPathForLocation(evt.getX(), evt.getY());
        if (selRow != -1) {
            DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            if (selNode.getClass().getSimpleName().equals("ApiTreeItem")) {
                selItem = (ApiTreeItem) selNode;
                if (evt.getClickCount() == 2) {
                    fireApiItemEvent(new ApiTreeEvent(tree, selItem, ApiTreeEvent.EVENT_INSERT));
                } else if (evt.getClickCount() == 1 && SwingUtilities.isRightMouseButton(evt)) {
                    int x = evt.getX();
                    int y = evt.getY();
                    if (selItem.getItemType().equals("hook")) {
                        menuItems[2].setVisible(true);
                        contextMenu.show(tree, x, y);
                    } else if (selItem.getItemType().equals("theme")) {
                        menuItems[2].setVisible(false);
                    }
                    if (selItem.getSearch().equals("")) {
                        menuItems[1].setVisible(false);
                    }                    
                    contextMenu.show(tree, x, y);
                }
                selectedRow = selPath;
            }
        }
    }//GEN-LAST:event_treeMousePressed
    
    private void btnSearchNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchNextActionPerformed
        doSearch(0);
    }//GEN-LAST:event_btnSearchNextActionPerformed
    
    private void btnSearchPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchPreviousActionPerformed
        doSearch(1);
    }//GEN-LAST:event_btnSearchPreviousActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearchNext;
    private javax.swing.JButton btnSearchPrevious;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTree tree;
    private javax.swing.JTextField txtFind;
    private javax.swing.JTextPane txtHelp;
    // End of variables declaration//GEN-END:variables

    private class TreeRenderer extends DefaultTreeCellRenderer {
        
        Icon hookIcon = new ImageIcon(getClass().getResource("/org/netbeans/modules/php/drupaldevel/ui/apitree/hook.png"));
        Icon themeIcon = new ImageIcon(getClass().getResource("/org/netbeans/modules/php/drupaldevel/ui/apitree/template.png"));
        Icon otherIcon = new ImageIcon(getClass().getResource("/org/netbeans/modules/php/drupaldevel/ui/apitree/code.png"));
        
        @Override
        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
            
            super.getTreeCellRendererComponent(
                    tree, value, sel,
                    expanded, leaf, row,
                    hasFocus);
            // Only apply eyecandy to actual items, not folders.
            if (leaf && value instanceof ApiTreeItem) {
                ApiTreeItem node = (ApiTreeItem) value;
                if (node.getItemType().equals("hook")) {
                    setIcon(hookIcon);
                } else if (node.getItemType().equals("theme")) {
                    setIcon(themeIcon);
                } else {
                    setIcon(otherIcon);
                }
            }
            
            return this;
        }
    }
}
