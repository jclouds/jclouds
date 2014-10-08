/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.swift.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.ForwardingList;

/**
 * Represents a list of objects in a container.
 * 
 * @see Container 
 * @see SwiftObject
 * @see org.jclouds.openstack.swift.v1.features.ObjectApi#list()
 */
public class ObjectList extends ForwardingList<SwiftObject> {

   public static ObjectList create(List<SwiftObject> objects, Container container) {
      return new ObjectList(objects, container);
   }

   private final List<SwiftObject> objects;
   private final Container container;

   protected ObjectList(List<SwiftObject> objects, Container container) {
      this.objects = checkNotNull(objects, "objects");
      this.container = checkNotNull(container, "container");
   }

   /**
    * @return the parent {@link Container} the objects reside in.
    */
   public Container getContainer() {
      return container;
   }

   @Override
   protected List<SwiftObject> delegate() {
      return objects;
   }
}
