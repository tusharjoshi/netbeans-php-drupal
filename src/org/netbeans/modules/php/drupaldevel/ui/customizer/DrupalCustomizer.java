/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupaldevel.ui.customizer;

import javax.swing.JComponent;
import org.netbeans.api.project.Project;
import org.netbeans.modules.php.drupaldevel.Util;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
public class DrupalCustomizer  implements ProjectCustomizer.CompositeCategoryProvider {
 private static final String PHP_DOC = "PhpDoc"; // NOI18N

    @Override
    public Category createCategory(Lookup context) {
        return ProjectCustomizer.Category.create(
                PHP_DOC,
                NbBundle.getMessage(DrupalCustomizer.class, "LBL_Config_Drupal"),
                null,
                (ProjectCustomizer.Category[]) null);
    }

    @Override
    public JComponent createComponent(Category category, Lookup context) {
        Project phpModule = Util.lookupPhpModule(context);
        return new DrupalCustomizerPanel(category, phpModule);
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
        projectType = "org-netbeans-modules-php-project",
        position = 360
    )
    public static DrupalCustomizer createCustomizer() {
        return new DrupalCustomizer();
    }    
}
