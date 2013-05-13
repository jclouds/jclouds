/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.azure.management.domain.hostedservice;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ExtendedProperty")
public class ExtendedProperty {

   /**
    * Represents the name of an extended hosted service property. Each extended property must have
    * both a defined name and value. You can have a maximum of 50 extended property name/value
    * pairs.
    * 
    * The maximum length of the Name element is 64 characters, only alphanumeric characters and
    * underscores are valid in the Name, and the name must start with a letter. Attempting to use
    * other characters, starting the Name with a non-letter character, or entering a name that is
    * identical to that of another extended property owned by the same hosted service, will result
    * in a status code 400 (Bad Request) error.
    */
   @XmlElement(name = "Name")
   private String name;

   /**
    * Represents the value of an extended hosted service property. Each extended property must have
    * both a defined name and value. You can have a maximum of 50 extended property name/value
    * pairs, and each extended property value has a maximum length of 255 characters.
    */
   @XmlElement(name = "Value")
   private String value;

   public ExtendedProperty() {
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((value == null) ? 0 : value.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ExtendedProperty other = (ExtendedProperty) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (value == null) {
         if (other.value != null)
            return false;
      } else if (!value.equals(other.value))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "ExtendedProperty [name=" + name + ", value=" + value + "]";
   }

}
