/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hollyit.drupaldevel;

/**
 *
 * @author Jamie Holly/HollyIT <jamie@hollyit.org>
 */
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.openide.util.Exceptions;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.filesystems.FileObject;
import org.netbeans.modules.editor.NbEditorUtilities;
public class DrupalEditorUtilities {

    public static void insert(String s, JTextComponent target)
            throws BadLocationException {
        insert(s, target, true);
    }

    public static void insert(final String s, final JTextComponent target, final boolean reformat)
            throws BadLocationException {
        final Document _doc = target.getDocument();
       
        
        if (_doc == null || !(_doc instanceof BaseDocument)) {
            return;
        }

        BaseDocument doc = (BaseDocument) _doc;
        final Reformat reformatter = Reformat.get(doc);
        reformatter.lock();
        try {
            doc.runAtomic(new Runnable() {

                public void run() {
                    try {
                        String s2 = s == null ? "" : s;
                        int start = insert(s2, target, _doc);

                    } catch (BadLocationException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            });

        } finally {
            reformatter.unlock();
        }

    }

    static String getFileName(JTextComponent targetComponent) {
        FileObject fo = NbEditorUtilities.getFileObject(targetComponent.getDocument());
        return fo.getName();
    }

    private static int insert(String s, JTextComponent target, Document doc)
            throws BadLocationException {

        int start = -1;
        try {
            //at first, find selected text range
            Caret caret = target.getCaret();
            int p0 = Math.min(caret.getDot(), caret.getMark());
            int p1 = Math.max(caret.getDot(), caret.getMark());
            doc.remove(p0, p1 - p0);

            //replace selected text by the inserted one
            start = caret.getDot();
            doc.insertString(start, s, null);
        } catch (BadLocationException ble) {
        }

        return start;
    }
}
