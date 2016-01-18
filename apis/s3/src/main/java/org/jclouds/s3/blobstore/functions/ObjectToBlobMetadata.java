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
package org.jclouds.s3.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpUtils;
import org.jclouds.s3.domain.ObjectMetadata;

import com.google.common.base.Function;

@Singleton
public class ObjectToBlobMetadata implements Function<ObjectMetadata, MutableBlobMetadata> {
   private final Function<String, Location> locationOfBucket;

   @Inject
   public ObjectToBlobMetadata(Function<String, Location> locationOfBucket) {
      this.locationOfBucket = locationOfBucket;
   }

   public MutableBlobMetadata apply(ObjectMetadata from) {
      if (from == null)
         return null;
      MutableBlobMetadata to = new MutableBlobMetadataImpl();
      HttpUtils.copy(from.getContentMetadata(), to.getContentMetadata());
      to.setUri(from.getUri());
      to.setContainer(from.getBucket());
      to.setETag(from.getETag());
      to.setName(from.getKey());
      to.setLastModified(from.getLastModified());
      to.setUserMetadata(from.getUserMetadata());
      to.setLocation(locationOfBucket.apply(from.getBucket()));
      to.setType(StorageType.BLOB);
      to.setSize(from.getContentMetadata().getContentLength());
      return to;
   }
}
