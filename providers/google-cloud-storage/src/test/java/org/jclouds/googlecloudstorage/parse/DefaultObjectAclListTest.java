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

import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.ObjectRole;
import org.jclouds.googlecloudstorage.domain.DefaultObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.ListDefaultObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.Resource.Kind;
import org.jclouds.googlecloudstorage.domain.internal.ProjectTeam;
import org.jclouds.googlecloudstorage.domain.internal.ProjectTeam.Team;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageParseTest;

import com.google.common.collect.ImmutableSet;

public class DefaultObjectAclListTest extends BaseGoogleCloudStorageParseTest<ListDefaultObjectAccessControls> {

   private DefaultObjectAccessControls item_1 = DefaultObjectAccessControls.builder()
            .entity("project-owners-1082289308625").role(ObjectRole.OWNER)
            .projectTeam(new ProjectTeam("1082289308625", Team.owners)).etag("CAk=").build();

   @Override
   public String resource() {
      return "/default_objectacl_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListDefaultObjectAccessControls expected() {
      return ListDefaultObjectAccessControls.builder().kind(Kind.OBJECT_ACCESS_CONTROLS).items(ImmutableSet.of(item_1))
               .build();
   }
}
