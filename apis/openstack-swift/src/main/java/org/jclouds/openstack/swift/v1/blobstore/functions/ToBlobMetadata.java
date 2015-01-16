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
package org.jclouds.openstack.swift.v1.blobstore.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.blobstore.strategy.IfDirectoryReturnNameStrategy;
import org.jclouds.blobstore.strategy.internal.MarkersIfDirectoryReturnNameStrategy;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;

import com.google.common.base.Function;

public class ToBlobMetadata implements Function<SwiftObject, MutableBlobMetadata> {

   private final IfDirectoryReturnNameStrategy ifDirectoryReturnName = new MarkersIfDirectoryReturnNameStrategy();
   private final Container container;

   public ToBlobMetadata(Container container) {
      this.container = checkNotNull(container, "container");
   }

   @Override
   public MutableBlobMetadata apply(SwiftObject from) {
      if (from == null)
         return null;
      MutableBlobMetadata to = new MutableBlobMetadataImpl();
      to.setContainer(container.getName());
      if (container.getAnybodyRead().isPresent()) {
         to.setPublicUri(from.getUri());
      }
      to.setUri(from.getUri());
      to.setETag(from.getETag());
      to.setName(from.getName());
      to.setLastModified(from.getLastModified());
      to.setContentMetadata(from.getPayload().getContentMetadata());
      to.getContentMetadata().setContentMD5(from.getPayload().getContentMetadata().getContentMD5AsHashCode());
      to.getContentMetadata().setExpires(from.getPayload().getContentMetadata().getExpires());
      to.setUserMetadata(from.getMetadata());
      String directoryName = ifDirectoryReturnName.execute(to);
      if (directoryName != null) {
         to.setName(directoryName);
         to.setType(StorageType.RELATIVE_PATH);
      } else {
         to.setType(StorageType.BLOB);
      }
      to.setSize(from.getPayload().getContentMetadata().getContentLength());
      return to;
   }

   @Override
   public String toString() {
      return "ObjectToBlobMetadata(" + container + ")";
   }
}
