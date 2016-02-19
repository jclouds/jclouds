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
package org.jclouds.docker.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.jclouds.docker.DockerApi;
import org.jclouds.docker.features.ImageApi;

import com.google.common.base.Preconditions;

/**
 * Utility methods shared by Docker tests.
 */
public class DockerTestUtils {

   /**
    * Read all data from given {@link InputStream} and throw away all the bits.
    * If an {@link IOException} occurs, it's not propagated to user. The given InputStream is closed after the read.
    * 
    * @param is InputStream instance (may be null)
    */
   public static void consumeStreamSilently(InputStream is) {
      if (is == null) {
         return;
      }
      char[] tmpBuff = new char[8 * 1024];
      // throw everything away
      InputStreamReader isr = new InputStreamReader(is);
      try {
         try {
            while (isr.read(tmpBuff) > -1) {
               // empty
            }
         } finally {
            isr.close();
         }
      } catch (IOException e) {
         java.util.logging.Logger.getAnonymousLogger().log(Level.WARNING, "Error ocured during reading InputStream.", e);
      }
   }

   /**
    * Removes Docker image if it's present on the Docker host. Docker Image API
    * is used to inspect and remove image (({@link ImageApi#deleteImage(String)}
    * method).
    *
    * @param dockerApi
    *           DockerApi instance (must be not-<code>null</code>)
    * @param imageName
    *           image to be deleted (must be not-<code>null</code>)
    */
   public static void removeImageIfExists(DockerApi dockerApi, String imageName) {
      Preconditions.checkNotNull(dockerApi, "DockerApi instance has to be provided");
      Preconditions.checkNotNull(imageName, "Docker image name has to be provided");
      final ImageApi imageApi = dockerApi.getImageApi();
      if (null != imageApi.inspectImage(imageName)) {
         consumeStreamSilently(imageApi.deleteImage(imageName));
      }
   }
}
