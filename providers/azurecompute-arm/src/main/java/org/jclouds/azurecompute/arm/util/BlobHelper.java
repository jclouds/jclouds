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
package org.jclouds.azurecompute.arm.util;

import org.jclouds.ContextBuilder;
import org.jclouds.azure.storage.domain.BoundedSet;
import org.jclouds.azureblob.AzureBlobClient;
import org.jclouds.azureblob.domain.BlobProperties;
import org.jclouds.azureblob.domain.ContainerProperties;
import org.jclouds.azureblob.domain.ListBlobsResponse;
import org.jclouds.azurecompute.arm.domain.VMImage;
import org.jclouds.util.Closeables2;

import java.util.ArrayList;
import java.util.List;

public class BlobHelper {

   public static void deleteContainerIfExists(String storage, String key, String containerName) {
      final AzureBlobClient azureBlob = ContextBuilder.newBuilder("azureblob")
              .credentials(storage, key)
              .buildApi(AzureBlobClient.class);

      try {
         azureBlob.deleteContainer(containerName);
      }
      finally {
         Closeables2.closeQuietly(azureBlob);
      }
   }

   public static boolean customImageExists(String storage, String key) {
      final AzureBlobClient azureBlob = ContextBuilder.newBuilder("azureblob")
              .credentials(storage, key)
              .buildApi(AzureBlobClient.class);

      try {
         return azureBlob.containerExists("system");
      }
      finally {
         Closeables2.closeQuietly(azureBlob);
      }
   }

   public static List<VMImage> getImages(String containerName, String group,
                                         String storageAccountName, String key, String offer, String location) {
      final AzureBlobClient azureBlob = ContextBuilder.newBuilder("azureblob")
              .credentials(storageAccountName, key)
              .buildApi(AzureBlobClient.class);


      List<VMImage> list = new ArrayList<VMImage>();
      try {
         BoundedSet<ContainerProperties> containerList = azureBlob.listContainers();
         for (ContainerProperties props : containerList) {
            if (props.getName().equals("system")) {
               ListBlobsResponse blobList = azureBlob.listBlobs("system");
               String osDisk = "";
               String dataDisk = "";

               for (BlobProperties blob : blobList) {
                  String name = blob.getName();

                  if (dataDisk.length() == 0) dataDisk = name.substring(1 + name.lastIndexOf('/'));
                  else if (osDisk.length() == 0) osDisk = name.substring(1 + name.lastIndexOf('/'));
               }
               final VMImage ref = VMImage.create(group, storageAccountName, osDisk, dataDisk, "test-create-image", "custom", location);
               list.add(ref);
            }
         }
      }
      finally {
         Closeables2.closeQuietly(azureBlob);
      }
      return list;
   }
}
