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

import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.ObjectRole;
import org.jclouds.googlecloudstorage.domain.ListObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.ObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.Resource.Kind;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageParseTest;

import com.google.common.collect.ImmutableSet;

public class ObjectAclListTest extends BaseGoogleCloudStorageParseTest<ListObjectAccessControls> {

   private ObjectAccessControls item1 = ObjectAccessControls
            .builder()
            .id("jcloudtestbucket/foo.txt/1394121608485000/user-00b4903a97adfde729f0650133a7379693099d8d85d6b1b18255ca70bf89e31d")
            .selfLink(
                     URI.create("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/o/foo.txt/acl/user-00b4903a97adfde729f0650133a7379693099d8d85d6b1b18255ca70bf89e31d"))
            .bucket("jcloudtestbucket").object("foo.txt").generation(Long.valueOf("1394121608485000"))
            .entity("user-00b4903a97adfde729f0650133a7379693099d8d85d6b1b18255ca70bf89e31d")
            .entityId("00b4903a97adfde729f0650133a7379693099d8d85d6b1b18255ca70bf89e31d").role(ObjectRole.OWNER)
            .etag("CIix/dmj/rwCEAE=").build();

   @Override
   public String resource() {
      return "/object_acl_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListObjectAccessControls expected() {
      return ListObjectAccessControls.builder().kind(Kind.OBJECT_ACCESS_CONTROLS).items(ImmutableSet.of(item1)).build();
   }

}
