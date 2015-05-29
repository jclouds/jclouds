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

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.ObjectRole;
import org.jclouds.googlecloudstorage.domain.ObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.ProjectTeam;
import org.jclouds.googlecloudstorage.domain.ProjectTeam.Team;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageParseTest;

public class ObjectAclGetTest extends BaseGoogleCloudStorageParseTest<ObjectAccessControls> {

   @Override
   public String resource() {
      return "/object_acl_get.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ObjectAccessControls expected() {
      return ObjectAccessControls
               .builder()
               .bucket("jcloudstestbucket")
               .object("foo.txt")
               .generation(1394121608485000L)
               .entity("project-owners-1082289308625")
               .role(ObjectRole.OWNER)
               .projectTeam(ProjectTeam.create("1082289308625", Team.OWNERS))
               .id("jcloudstestbucket/foo.txt/1394121608485000/project-owners-1082289308625").build();
   }

}
