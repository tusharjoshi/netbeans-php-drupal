/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupal.ui.searchbar.autocomplete;

/**
 * A service providing autocompletion support.
 * 
 * @see AutoCompleteDocument
 * 
 * @author Samuel Sjoberg, http://samuelsjoberg.com
 * @version 1.0.0
 * 
 * @param <T>
 *            the type to be returned by the service
 */
public interface CompletionService<T> {

    /**
     * Autocomplete the passed string. The method will return the matching
     * object when one single object matches the search criteria. As long as
     * multiple objects stored in the service matches, the method will return
     * <code>null</code>.
     * 
     * @param startsWith
     *            prefix string
     * @return the matching object or <code>null</code> if multiple matches are
     *         found.
     */
    T autoComplete(String startsWith);
}
