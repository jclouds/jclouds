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

import static org.jclouds.util.Closeables2.closeQuietly;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.ContextBuilder;
import org.jclouds.azureblob.AzureBlobClient;
import org.jclouds.azureblob.domain.BlobProperties;
import org.jclouds.azureblob.domain.ContainerProperties;
import org.jclouds.azureblob.domain.ListBlobsResponse;
import org.jclouds.azurecompute.arm.domain.VMImage;

public class BlobHelper implements Closeable {

   private final String storageAccount;
   private final AzureBlobClient azureBlob;

   public BlobHelper(String storageAccount, String key) {
      this.storageAccount = storageAccount;
      this.azureBlob = ContextBuilder.newBuilder("azureblob").credentials(storageAccount, key)
            .buildApi(AzureBlobClient.class);
   }

   @Override
   public void close() throws IOException {
      closeQuietly(azureBlob);
   }

   public void deleteContainerIfExists(String containerName) {
      azureBlob.deleteContainer(containerName);
   }

   public boolean hasContainers() {
      return !azureBlob.listContainers().isEmpty();
   }

   public boolean customImageExists() {
      return azureBlob.containerExists("system");
   }

   public List<VMImage> getImages(String containerName, String group, String offer, String location) {
      List<VMImage> list = new ArrayList<VMImage>();

      ContainerProperties systemContainer = azureBlob.getContainerProperties("system");
      if (systemContainer != null) {
         ListBlobsResponse blobList = azureBlob.listBlobs(systemContainer.getName());
         for (BlobProperties blob : blobList) {
            String name = blob.getName();

            if (name.contains("-osDisk")) {
               String imageName = name.substring(name.lastIndexOf('/') + 1, name.indexOf("-osDisk"));
               String imageUrl = blob.getUrl().toString();

               list.add(VMImage.customImage().group(group).storage(storageAccount).vhd1(imageUrl).name(imageName)
                     .offer(offer).location(location).build());
            }
         }
      }

      return list;
   }

}
