/*
 * Copyright 2011 by Anchialas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * $Id: ContextAwareActionWrapper.java 2 2011-07-21 14:26:15Z Anchialas $
 */
package org.netbeans.modules.php.drupaldevel;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import javax.swing.Action;
import org.openide.util.ContextAwareAction;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;


/**
 * ContextAwareActionWrapper
 *
 * @author Anchialas <anchialas@gmail.com>
 * @version $Rev: 2 $
 */
public class ContextAwareActionWrapper implements ContextAwareAction {

   private ContextAwareAction action;


   public ContextAwareActionWrapper(ContextAwareAction action) {
      this.action = action;
   }


   @Override
   public Action createContextAwareInstance(Lookup actionContext) {
      try {
         Method getDelegateMethod = action.getClass().getDeclaredMethod("getDelegate");
         getDelegateMethod.setAccessible(true);
         Action act = (Action) getDelegateMethod.invoke(action);

         String iconBase = (String)action.getValue("iconBase");
         
         if (iconBase != null) {
            act.putValue(SMALL_ICON, ImageUtilities.loadImageIcon(iconBase, true));
         }

         return act;

      } catch (Exception ex) {
         return action.createContextAwareInstance(actionContext);
      }
   }


   @Override
   public Object getValue(String key) {
      return action.getValue(key);
   }


   @Override
   public void putValue(String key, Object value) {
      action.putValue(key, value);
   }


   @Override
   public void setEnabled(boolean b) {
      action.setEnabled(b);
   }


   @Override
   public boolean isEnabled() {
      return action.isEnabled();
   }


   @Override
   public void addPropertyChangeListener(PropertyChangeListener listener) {
      action.addPropertyChangeListener(listener);
   }


   @Override
   public void removePropertyChangeListener(PropertyChangeListener listener) {
      action.removePropertyChangeListener(listener);
   }


   @Override
   public void actionPerformed(ActionEvent e) {
      action.actionPerformed(e);
   }

}
