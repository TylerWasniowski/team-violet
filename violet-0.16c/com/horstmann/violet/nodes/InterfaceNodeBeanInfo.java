/*
Violet - A program for editing UML diagrams.

Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.horstmann.violet.nodes;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
   The bean info for the InterfaceNode type.
*/
public class InterfaceNodeBeanInfo extends SimpleBeanInfo
{
   /**
    * gets the property descriptors for this node
    * @return an array of the property descriptors
    */
   public PropertyDescriptor[] getPropertyDescriptors()
   {
      try
      {
         PropertyDescriptor nameDescriptor 
            = new PropertyDescriptor("name", InterfaceNode.class);
         nameDescriptor.setValue("priority", new Integer(1));
         PropertyDescriptor methodsDescriptor
            = new PropertyDescriptor("methods", InterfaceNode.class);
         methodsDescriptor.setValue("priority", new Integer(2));
         return new PropertyDescriptor[]
            {
               nameDescriptor,
               methodsDescriptor
            };
      }
      catch (IntrospectionException exception)
      {
         return null;
      }
   }
}

