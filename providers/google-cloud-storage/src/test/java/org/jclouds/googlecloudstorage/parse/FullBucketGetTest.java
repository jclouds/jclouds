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

import java.util.Arrays;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecloudstorage.domain.Bucket;
import org.jclouds.googlecloudstorage.domain.Bucket.Cors;
import org.jclouds.googlecloudstorage.domain.BucketAccessControls;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Location;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.ObjectRole;
import org.jclouds.googlecloudstorage.domain.BucketAccessControls.Role;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.StorageClass;
import org.jclouds.googlecloudstorage.domain.ObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.Owner;
import org.jclouds.googlecloudstorage.domain.ProjectTeam;
import org.jclouds.googlecloudstorage.domain.ProjectTeam.Team;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageParseTest;

public class FullBucketGetTest extends BaseGoogleCloudStorageParseTest<Bucket> {

   private final BucketAccessControls acl1 = BucketAccessControls
            .builder()
            .id("jcloudtestbucket3500/project-owners-1082289308625")
            .bucket("jcloudtestbucket3500").entity("project-owners-1082289308625").role(Role.OWNER)
            .projectTeam(ProjectTeam.create("1082289308625", Team.OWNERS))
            .build();

   private final ObjectAccessControls defObjectAcl = ObjectAccessControls.builder()
            .entity("project-owners-1082289308625").role(ObjectRole.OWNER).build();

   private final Cors bucketCors = Cors
         .create(Arrays.asList("http://example.appspot.com"), Arrays.asList("GET", "HEAD"),
               Arrays.asList("x-meta-goog-custom"), 10);

   @Override
   public String resource() {
      return "/full_bucket_get.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Bucket expected() {
      return Bucket.create(
            "jcloudtestbucket3500", // id
            "jcloudtestbucket3500", // name
            1082289308625l, // projectNumber
            new SimpleDateFormatDateService().iso8601DateParse("2014-06-19T14:03:22.345Z"), // timeCreated
            10l, // metageneration
            Arrays.asList(acl1), // acl
            Arrays.asList(defObjectAcl), // defaultObjectAcl
            Owner.create("project-owners-1082289308625", null), // owner
            Location.US, // location
            null, // website
            null, // logging
            null, // versioning
            Arrays.asList(bucketCors), // cors
            null, // lifeCycle
            StorageClass.STANDARD // storageClass
      );
   }
}
