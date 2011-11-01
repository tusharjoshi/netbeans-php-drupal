/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupaldevel.drush.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
public class CommandProcessor extends JPanel implements ActionListener, ItemListener {

    private HashMap actions;
    private JTextPane textArea;
    private CommandRunner runner;
    private String commandOutput = "";
    private HTMLEditorKit HTMLkit = new HTMLEditorKit();
    private HTMLDocument HTMLdoc = new HTMLDocument();
    protected EventListenerList listenerList = new EventListenerList();

    public CommandProcessor() {
        super(new BorderLayout());
        textArea = new JTextPane();
        textArea.setEditable(false);
        textArea.setContentType("text/html");
        textArea.setEditorKit(HTMLkit);
        textArea.setDocument(HTMLdoc);
        HyperlinkListener hyperlinkListener = new ActivatedHyperlinkListener(textArea);
        textArea.addHyperlinkListener(hyperlinkListener);
        StyleSheet sheet = HTMLkit.getStyleSheet();

        JScrollPane scroller = new JScrollPane(textArea);
        add(scroller, BorderLayout.CENTER);
        addPopupMenu();
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setPreferredSize(new Dimension(100, 150));
    }

    private void addPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        textArea.add(menu);
        textArea.setComponentPopupMenu(menu);

        createActionTable();
        try {
            menu.add(getActionByName(DefaultEditorKit.copyAction, "Copy"));
            menu.add(getActionByName(DefaultEditorKit.selectAllAction, "Select All"));
        } catch (Exception e) {
            // Nothing to see here. Just so we can load it in the GUI editor.
        }
        menu.add(new JSeparator());
        JMenuItem clearAll = new JMenuItem("Clear");
        clearAll.addActionListener(this);
        menu.add(clearAll);


    }

    private Action getActionByName(String name, String description) {
        Action a = null;

        a = (Action) (actions.get(name));
        a.putValue(Action.NAME, description);
        return a;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem) (e.getSource());
        clearScreen();
    }

    public void clearScreen() {
        textArea.setText("");
    }

    public void println(String text) {
        appendTextArea(text + "\n");


    }

    public long executionTime() {
        return runner.getExecutionTime();
    }

    private void appendTextArea(String text) {
        text = text.replace("\n", "<br>");
        text = text.replaceAll("\\u001B(.*?)m", "");
        text = text.replaceAll("\\[warning\\]", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style='font-weight:bold;color:#C8B560;'>[WARNING]</span>");
        text = text.replaceAll("\\[error\\]", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style='font-weight:bold;color:#C11B17'>[ERROR]</span>");
        try {
            HTMLkit.insertHTML(HTMLdoc, HTMLdoc.getLength(), text, 0, 0, null);
        } catch (Exception e) {
        }

    }

    public void itemStateChanged(ItemEvent e) {
        // Nothing to do here
    }

    public String getCommandOutput() {
        return this.commandOutput;
    }

    private void createActionTable() {
        actions = new HashMap();
        Action[] actionsArray = textArea.getActions();
        for (int i = 0; i < actionsArray.length; i++) {
            Action a = actionsArray[i];
            actions.put(a.getValue(Action.NAME), a);
        }
    }

    public void runCommand(String[] command) {
        commandOutput = "";
        runner = new CommandRunner();
        runner.cp = this;
        runner.runCommand(command);
    }

    public void cancelCommand() {
        runner.cancelProcess();
    }

    private void fireCommandProcessorEvent() {
        CommandProcessorEvent evt = new CommandProcessorEvent(this);
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == CommandProcessorEventListener.class) {
                ((CommandProcessorEventListener) listeners[i + 1]).commandDone(evt);
            }
        }
    }

    public void addCommandListener(CommandProcessorEventListener listener) {
        listenerList.add(CommandProcessorEventListener.class, listener);
    }

    public void removeCommandListener(CommandProcessorEventListener listener) {
        listenerList.remove(CommandProcessorEventListener.class, listener);
    }

    private class CommandRunner extends SwingWorker<Integer, String> {

        private Process process;
        private Integer result = -1;
        public CommandProcessor cp;
        private long executionStart = 0;
        private long executionTime = 0;

        public void cancelProcess() {
            this.cancel(true);
        }

        @Override
        public Integer doInBackground() {
            InputStreamReader in = null;

            try {
                in = new InputStreamReader(process.getInputStream());

                char[] buf = new char[1 << 10]; // 1KiB buffer
                int numRead = -1;

                while ((numRead = in.read(buf)) > -1) {
                    publish(new String(buf, 0, numRead));
                }

                result = new Integer(process.waitFor());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) { /*
                     * ignore
                     */

                }
            }

            return result;
        }

        @Override
        protected void process(java.util.List<String> chunks) {
            for (String text : chunks) {
                appendTextArea(text);
                commandOutput += text;
            }
        }

        @Override
        protected void done() {
            try {
                this.executionTime = System.nanoTime() - executionStart;
                this.cp.fireCommandProcessorEvent();
            } catch (Exception ignore) {
            }
        }

        public long getExecutionTime() {
            return executionTime;
        }

        private String loadStream(InputStream s) throws Exception {
            BufferedReader br = new BufferedReader(new InputStreamReader(s));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }

        public void runCommand(String[] command) {
            try {
                executionStart = System.nanoTime();
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.redirectErrorStream(true);
                process = pb.start();
       


                execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class ActivatedHyperlinkListener implements HyperlinkListener {

    JTextPane editorPane;

    public ActivatedHyperlinkListener(JTextPane editorPane) {

        this.editorPane = editorPane;
    }

    public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {

        HyperlinkEvent.EventType type = hyperlinkEvent.getEventType();
        final URL url = hyperlinkEvent.getURL();

        if (type == HyperlinkEvent.EventType.ENTERED) {
        } else if (type == HyperlinkEvent.EventType.ACTIVATED) {
            System.out.println("URL: " + url);
        }
    }
}