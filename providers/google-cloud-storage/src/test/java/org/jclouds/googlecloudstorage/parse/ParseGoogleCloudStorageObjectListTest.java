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
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.StorageClass;
import org.jclouds.googlecloudstorage.domain.ListPageWithPrefixes;
import org.jclouds.googlecloudstorage.domain.Owner;
import org.jclouds.googlecloudstorage.domain.GoogleCloudStorageObject;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;


@Test(groups = "unit", testName = "ParseGoogleCloudStorageObjectListTest")
public class ParseGoogleCloudStorageObjectListTest extends BaseGoogleCloudStorageParseTest<ListPageWithPrefixes<GoogleCloudStorageObject>> {

   @Override
   public String resource() {
      return "/object_list.json";
   }

   GoogleCloudStorageObject object1 = GoogleCloudStorageObject.create(
         "test/file_name/1000", //id
         URI.create("https://www.googleapis.com/storage/v1/b/test/o/file_name"), //selfLink
         "etag", // etag
         "file_name", // name
         "test", // bucket
         (long) 1000, //generation
         (long) 8, // metageneration
         "application/x-tar", // contentType
         parse("2014-09-27T00:01:44.819"), // updated
         null, // timeDeleted
         StorageClass.STANDARD, // storageClass
         (long) 1000, //size,
         "md5Hash", // md5Hash
         URI.create("https://www.googleapis.com/download/storage/v1/b/test/o/file_name?generation=1000&alt=media"), // mediaLink
         null, // metadata
         null, // contentEncoding
         null, // contentDisposition,
         null, // contentLanguage
         null, // cacheControl
         null, // acl
         Owner.create("entity", "entityId"), // owner,
         "crc32c", // crc32c,
         null); //componentCount

   GoogleCloudStorageObject object2 = GoogleCloudStorageObject.create(
         "test/file_name2/1000", //id
         URI.create("https://www.googleapis.com/storage/v1/b/test/o/file_name2"), //selfLink
         "etag", // etag
         "file_name2", // name
         "test", // bucket
         (long) 1001, //generation
         (long) 9, // metageneration
         "image/png", // contentType
         parse("2014-09-27T00:01:44.819"), // updated
         null, // timeDeleted
         StorageClass.STANDARD, // storageClass
         (long) 10, //size,
         "md5Hash", // md5Hash
         URI.create("https://www.googleapis.com/download/storage/v1/b/test/o/file_name2?generation=1001&alt=media"), // mediaLink
         null, // metadata
         null, // contentEncoding
         null, // contentDisposition,
         null, // contentLanguage
         null, // cacheControl
         null, // acl
         Owner.create("entity", "entityId"), // owner,
         "crc32c", // crc32c,
         null); //componentCount

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPageWithPrefixes<GoogleCloudStorageObject> expected() {
      List<GoogleCloudStorageObject> items = ImmutableList.of(object1, object2);
      return new ListPageWithPrefixes<GoogleCloudStorageObject>(items, null, null);
   }
}
