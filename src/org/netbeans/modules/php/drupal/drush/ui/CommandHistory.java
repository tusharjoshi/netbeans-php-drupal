/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupal.drush.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
public class CommandHistory extends JPanel {

    private final JTextField tf;
    private final JComboBox combo = new JComboBox();
    private Vector<String> v = new Vector<String>();
    private String historyFile = "";
    public Integer historySize = 20;
    protected EventListenerList listenerList = new EventListenerList();

    public CommandHistory() {
        super(new GridLayout(1, 1));
        combo.setEditable(true);
        tf = (JTextField) combo.getEditor().getEditorComponent();
        tf.addKeyListener(new KeyAdapter() {

            public void keyTyped(KeyEvent e) {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        String text = tf.getText();
                        if (text.length() == 0) {
                            combo.hidePopup();
                            setModel(new DefaultComboBoxModel(v), "");
                        } else {
                            DefaultComboBoxModel m = getSuggestedModel(v, text);
                            if (m.getSize() == 0 || hide_flag) {
                                combo.hidePopup();
                                hide_flag = false;
                            } else {
                                setModel(m, text);
                                combo.showPopup();
                            }
                        }
                    }
                });
            }

            public void keyPressed(KeyEvent e) {
                String text = tf.getText();
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_ENTER && tf.getText().length() > 0) {
                    writeHistory();
                    fireEnterCommandHistoryEvent();
                } else if (code == KeyEvent.VK_ESCAPE) {
                    hide_flag = true;
                } else if (code == KeyEvent.VK_RIGHT) {
                    for (int i = 0; i < v.size(); i++) {
                        String str = v.elementAt(i);
                        if (str.startsWith(text)) {
                            combo.setSelectedIndex(-1);
                            tf.setText(str);
                            return;
                        }
                    }
                }
            }
        });

        JPanel p = new JPanel(new GridLayout(1, 1));

        p.add(combo);
        add(p);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setPreferredSize(new Dimension(100, 30));
    }

    public String getText(){
        return tf.getText();
    }
    
    public void writeHistory() {
        String text = tf.getText();
        if (!v.contains(text)) {

            //Not in history so let's add it to the top of the vector
            v.add(0, text);

            // Make sure our history size doesn't get out of control!
            if (v.size() > (historySize - 1)) {
                for (Integer r = 0; r < v.size(); r++) {
                    if (r >= (historySize - 1)) {
                        v.remove(v.size() - 1);
                    }
                }

            }
            setModel(getSuggestedModel(v, text), text);
        } else {
            // Already in history, add the item to the top of the history vector
            v.removeElement(text);
            v.add(0, text);
            setModel(getSuggestedModel(v, text), text);

        }
        tf.setSelectionStart(0);
        tf.setSelectionEnd(tf.getText().length());
        hide_flag = true;
        saveHistory();
    }

    public void saveHistory() {

        try {
            
            Writer output = new BufferedWriter(new FileWriter(this.historyFile));
            //FileWriter always assumes default encoding is OK!
            for (String s : v) {
                output.write(s + "\n");
            }
            output.close();
        } catch (IOException e) {
            System.out.println(e);
        } finally {
        }

    }

    public void setHistory(String file) {
        String[] items = {};

        if (!historyFile.equals("") && !historyFile.equals(file)) {
            saveHistory();
        }

        this.historyFile = file;
        v = new Vector<String>();
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                v.addElement(strLine);
            }
            in.close();
        } catch (Exception e) {
        }

        setModel(new DefaultComboBoxModel(v), "");
    }

    public void clearHistory(Boolean clearFile) {
        v = new Vector<String>();
        setModel(new DefaultComboBoxModel(v), "");
        if (clearFile) {
        }

    }
    private boolean hide_flag = false;

    public void addEnterListener(CommandHistoryEventListener listener) {
        listenerList.add(CommandHistoryEventListener.class, listener);
    }

    public void removeEnterListener(CommandHistoryEventListener listener) {
        listenerList.remove(CommandHistoryEventListener.class, listener);
    }

    private void fireEnterCommandHistoryEvent() {
        CommandHistoryEvent evt = new CommandHistoryEvent(this, tf.getText());
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == CommandHistoryEventListener.class) {
                ((CommandHistoryEventListener) listeners[i + 1]).commandEnter(evt);
            }
        }
    }

    private void setModel(DefaultComboBoxModel mdl, String str) {
        combo.setModel(mdl);
        combo.setSelectedIndex(-1);

        tf.setText(str);
    }

    private static DefaultComboBoxModel getSuggestedModel(java.util.List<String> list, String text) {
        DefaultComboBoxModel m = new DefaultComboBoxModel();
        for (String s : list) {
            if (s.startsWith(text)) {
                m.addElement(s);
            }
        }
        return m;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new CommandHistory());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}