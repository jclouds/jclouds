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

import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.ObjectRole;
import org.jclouds.googlecloudstorage.domain.ObjectAccessControls;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageParseTest;

public class ObjectAclInsertTest extends BaseGoogleCloudStorageParseTest<ObjectAccessControls> {

   @Override
   public String resource() {
      return "/object_acl_insert_response.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ObjectAccessControls expected() {
      return ObjectAccessControls
               .builder()
               .selfLink(
                        URI.create("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/o/foo.txt/acl/user-00b4903a97adfde729f0650133a7379693099d8d85d6b1b18255ca70bf89e31d"))
               .bucket("jcloudtestbucket").object("foo.txt")
               .entity("user-00b4903a97adfde729f0650133a7379693099d8d85d6b1b18255ca70bf89e31d")
               .entityId("00b4903a97adfde729f0650133a7379693099d8d85d6b1b18255ca70bf89e31d").role(ObjectRole.OWNER)
               .etag("CIix/dmj/rwCEAE=").build();

   }

}
