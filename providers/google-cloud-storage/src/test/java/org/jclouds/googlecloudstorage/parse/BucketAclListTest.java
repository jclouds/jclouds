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

import org.jclouds.googlecloudstorage.domain.BucketAccessControls;
import org.jclouds.googlecloudstorage.domain.BucketAccessControls.ProjectTeam;
import org.jclouds.googlecloudstorage.domain.BucketAccessControls.ProjectTeam.Team;
import org.jclouds.googlecloudstorage.domain.BucketAccessControls.Role;
import org.jclouds.googlecloudstorage.domain.ListBucketAccessControls;
import org.jclouds.googlecloudstorage.domain.Resource.Kind;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageParseTest;
import com.google.common.collect.ImmutableSet;

public class BucketAclListTest extends BaseGoogleCloudStorageParseTest<ListBucketAccessControls> {

   private BucketAccessControls item_1 = BucketAccessControls.builder().id("jcloudtestbucket/allUsers")
            .selfLink(URI.create("https://content.googleapis.com/storage/v1/b/jcloudtestbucket/acl/allUsers"))
            .bucket("jcloudtestbucket").entity("allUsers").role(Role.READER).etag("CAc=").build();

   private BucketAccessControls item_2 = BucketAccessControls
            .builder()
            .id("jcloudtestbucket/project-owners-1082289308625")
            .selfLink(
                     URI.create("https://content.googleapis.com/storage/v1/b/jcloudtestbucket/acl/project-owners-1082289308625"))
            .projectTeam(new ProjectTeam("1082289308625", Team.owners)).bucket("jcloudtestbucket")
            .entity("project-owners-1082289308625").role(Role.OWNER).etag("CAc=").build();

   @Override
   public String resource() {
      return "/bucketacl_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListBucketAccessControls expected() {
      return ListBucketAccessControls.builder().kind(Kind.BUCKET_ACCESS_CONTROLS)
               .items(ImmutableSet.of(item_1, item_2)).build();
   }
}
