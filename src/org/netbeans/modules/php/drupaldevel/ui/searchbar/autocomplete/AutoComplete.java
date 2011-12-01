/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupaldevel.ui.searchbar.autocomplete;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
public class AutoComplete extends JTextField {

    private String wordFile = "";
    private ArrayList wordList = null;
    private Boolean init = false;

    public void setAutoComplete() {
        NameService nameService = new NameService(this);
        Document autoCompleteDocument = new AutoCompleteDocument(nameService,
                this);
        this.setDocument(autoCompleteDocument);
        System.out.println("SET");
        this.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    fireEnterPressedEvent(new AutoCompleteEvent(this));
                }
            }
        });
    }

    public void setWordFile(String path) {
        this.wordList = new ArrayList<String>();
        if (!init) {
            init = true;
            setAutoComplete();
        } else {
        }
        System.out.println(path);
        StringBuilder text = new StringBuilder();
        if (!this.wordFile.equals(path)) {
            this.wordFile = path;
            try {
                FileInputStream fstream = new FileInputStream(path);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                //Read File Line By Line
                while ((strLine = br.readLine()) != null) {
                    this.wordList.add(strLine);
                }

                in.close();

            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println("DONE");
        }

    }

    private static class NameService implements CompletionService<String> {

        /**
         * Our name data.
         */
        private AutoComplete owner;

        /**
         * Create a new <code>NameService</code> and populate it.
         */
        public NameService(AutoComplete owner) {
            this.owner = owner;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            for (Object o : this.owner.wordList) {
                b.append(o.toString()).append("\n");
            }
            return b.toString();
        }

        /** {@inheritDoc} */
        public String autoComplete(String startsWith) {
            // Naive implementation, but good enough for the sample
            String hit = null;
            String word;

            for (Object o : this.owner.wordList) {
                word = o.toString();

                if (word.startsWith(startsWith)) {
                    hit = word;
                    break;

                }
            }
            return hit;
        }
    }
    
    public void addAutoCompleteListener(AutoCompleteEventListener listener) {
        listenerList.add(AutoCompleteEventListener.class, listener);
    }

    // This methods allows classes to unregister for MyEvents
    public void removeAutoCompleteListener(AutoCompleteEventListener listener) {
        listenerList.remove(AutoCompleteEventListener.class, listener);
    }    
    // This private class is used to fire MyEvents

    void fireEnterPressedEvent(AutoCompleteEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == AutoCompleteEventListener.class) {
                ((AutoCompleteEventListener) listeners[i + 1]).enterPressedEvent(evt);
            }
        }
    }
}
