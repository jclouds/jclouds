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
package org.jclouds.googlecloudstorage.parse;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecloudstorage.domain.Bucket;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.Location;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.StorageClass;
import org.jclouds.googlecloudstorage.domain.ListPage;
import org.jclouds.googlecloudstorage.domain.Resource.Kind;
import org.jclouds.googlecloudstorage.domain.internal.Owner;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageParseTest;

public class NoAclBucketListTest extends BaseGoogleCloudStorageParseTest<ListPage<Bucket>> {

   private Bucket item1 = Bucket.builder().id("bhashbucket")
            .selfLink(URI.create("https://content.googleapis.com/storage/v1/b/bhashbucket")).name("bhashbucket")
            .projectNumber(Long.valueOf("1082289308625"))
            .timeCreated(new SimpleDateFormatDateService().iso8601DateParse("2014-06-02T19:19:41.112z"))
            .metageneration(Long.valueOf(99)).owner(Owner.builder().entity("project-owners-1082289308625").build())
            .location(Location.US).storageClass(StorageClass.STANDARD).etag("CGM=").build();

   @Override
   public String resource() {
      return "/noAcl_bucket_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<Bucket> expected() {
      return ListPage.<Bucket> builder().kind(Kind.BUCKETS).nextPageToken("bhashbucket").addItem(item1).build();
   }

}
