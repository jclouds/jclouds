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
package org.jclouds.compute.extensions.internal;

import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jclouds.compute.config.ComputeServiceAdapterContextModule.AddDefaultCredentialsToImage;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.ImageTemplateBuilder.CloneImageTemplateBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.internal.ImageTemplateImpl;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.suppliers.ImageCacheSupplier;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Futures;

@Test(groups = "unit", testName = "DelegatingImageExtensionTest")
public class DelegatingImageExtensionTest {

   @Test
   public void createImageRegistersInCacheAndAddsCredentials()  {
      ImageCacheSupplier imageCache = createMock(ImageCacheSupplier.class);
      ImageExtension delegate = createMock(ImageExtension.class);
      AddDefaultCredentialsToImage credsToImage = createMock(AddDefaultCredentialsToImage.class);

      ImageTemplate template = new ImageTemplateImpl("test") {
      };
      Image result = new ImageBuilder().id("test")
            .operatingSystem(OperatingSystem.builder().description("test").build()).status(Status.AVAILABLE).build();

      LoginCredentials credentials = LoginCredentials.builder().user("jclouds").password("pass").build();
      Image withCredentials = ImageBuilder.fromImage(result).defaultCredentials(credentials).build();

      expect(delegate.createImage(template)).andReturn(immediateFuture(result));
      expect(credsToImage.apply(result)).andReturn(withCredentials);
      imageCache.registerImage(withCredentials);
      expectLastCall();
      replay(delegate, imageCache, credsToImage);

      new DelegatingImageExtension(imageCache, delegate, credsToImage, null).createImage(template);

      verify(delegate, imageCache, credsToImage);
   }

   @Test
   public void createImageDoesNotRegisterInCacheWhenFailed() {
      ImageCacheSupplier imageCache = createMock(ImageCacheSupplier.class);
      ImageExtension delegate = createMock(ImageExtension.class);
      AddDefaultCredentialsToImage credsToImage = createMock(AddDefaultCredentialsToImage.class);

      ImageTemplate template = new ImageTemplateImpl("test") {
      };

      expect(delegate.createImage(template)).andReturn(Futures.<Image> immediateFailedFuture(new RuntimeException()));
      replay(delegate, imageCache, credsToImage);

      new DelegatingImageExtension(imageCache, delegate, credsToImage, null).createImage(template);

      verify(delegate, imageCache, credsToImage);
   }

   @Test
   public void createImageDoesNotRegisterInCacheWhenCancelled() {
      ImageCacheSupplier imageCache = createMock(ImageCacheSupplier.class);
      ImageExtension delegate = createMock(ImageExtension.class);
      AddDefaultCredentialsToImage credsToImage = createMock(AddDefaultCredentialsToImage.class);

      ImageTemplate template = new ImageTemplateImpl("test") {
      };

      expect(delegate.createImage(template)).andReturn(Futures.<Image> immediateCancelledFuture());
      replay(delegate, imageCache, credsToImage);

      new DelegatingImageExtension(imageCache, delegate, credsToImage, null).createImage(template);

      verify(delegate, imageCache, credsToImage);
   }

   @Test
   public void deleteUnregistersImageFromCache() {
      ImageCacheSupplier imageCache = createMock(ImageCacheSupplier.class);
      ImageExtension delegate = createMock(ImageExtension.class);

      expect(delegate.deleteImage("test")).andReturn(true);
      imageCache.removeImage("test");
      expectLastCall();
      replay(delegate, imageCache);

      new DelegatingImageExtension(imageCache, delegate, null, null).deleteImage("test");

      verify(delegate, imageCache);
   }

   @Test
   public void deleteDoesNotUnregisterImageFromCacheWhenFailed() {
      ImageCacheSupplier imageCache = createMock(ImageCacheSupplier.class);
      ImageExtension delegate = createMock(ImageExtension.class);

      expect(delegate.deleteImage("test")).andReturn(false);
      replay(delegate, imageCache);

      new DelegatingImageExtension(imageCache, delegate, null, null).deleteImage("test");

      verify(delegate, imageCache);
   }

   @Test
   public void createByCloningDoesNothingIfImageHasCredentials() throws InterruptedException, ExecutionException {
      LoginCredentials credentials = LoginCredentials.builder().user("jclouds").password("pass").build();

      ImageCacheSupplier imageCache = createMock(ImageCacheSupplier.class);
      ImageExtension delegate = createMock(ImageExtension.class);
      AddDefaultCredentialsToImage credsToImage = createMock(AddDefaultCredentialsToImage.class);

      ImageTemplate template = new CloneImageTemplateBuilder().name("test").nodeId("node1").build();
      Image result = new ImageBuilder().id("test")
            .operatingSystem(OperatingSystem.builder().description("test").build()).status(Status.AVAILABLE)
            .defaultCredentials(credentials).build();

      expect(delegate.createImage(template)).andReturn(immediateFuture(result));
      replay(delegate, credsToImage);

      Future<Image> image = new DelegatingImageExtension(imageCache, delegate, credsToImage, null)
            .createImage(template);

      // Verify that the exact same instance is returned unmodified
      assertTrue(image.get() == result);

      verify(delegate, credsToImage);
   }

   @Test
   public void createByCloningAddsNodeCredentials() throws InterruptedException, ExecutionException {
      Credentials credentials = LoginCredentials.builder().user("jclouds").password("pass").build();

      ImageCacheSupplier imageCache = createMock(ImageCacheSupplier.class);
      ImageExtension delegate = createMock(ImageExtension.class);
      AddDefaultCredentialsToImage credsToImage = createMock(AddDefaultCredentialsToImage.class);
      Map<String, Credentials> credentialStore = ImmutableMap.of("node#node1", credentials);

      ImageTemplate template = new CloneImageTemplateBuilder().name("test").nodeId("node1").build();
      Image result = new ImageBuilder().id("test")
            .operatingSystem(OperatingSystem.builder().description("test").build()).status(Status.AVAILABLE).build();

      expect(delegate.createImage(template)).andReturn(immediateFuture(result));
      replay(delegate, credsToImage);

      Future<Image> image = new DelegatingImageExtension(imageCache, delegate, credsToImage, credentialStore)
            .createImage(template);

      assertEquals(image.get().getDefaultCredentials(), credentials);

      verify(delegate, credsToImage);
   }

   @Test
   public void createByCloningAddsDefaultImageCredentials() throws InterruptedException, ExecutionException {
      LoginCredentials credentials = LoginCredentials.builder().user("jclouds").password("pass").build();

      ImageCacheSupplier imageCache = createMock(ImageCacheSupplier.class);
      ImageExtension delegate = createMock(ImageExtension.class);
      AddDefaultCredentialsToImage credsToImage = createMock(AddDefaultCredentialsToImage.class);
      Map<String, Credentials> credentialStore = Collections.emptyMap();

      ImageTemplate template = new CloneImageTemplateBuilder().name("test").nodeId("node1").build();
      Image result = new ImageBuilder().id("test")
            .operatingSystem(OperatingSystem.builder().description("test").build()).status(Status.AVAILABLE).build();

      expect(delegate.createImage(template)).andReturn(immediateFuture(result));
      expect(credsToImage.apply(result)).andReturn(
            ImageBuilder.fromImage(result).defaultCredentials(credentials).build());
      replay(delegate, credsToImage);

      Future<Image> image = new DelegatingImageExtension(imageCache, delegate, credsToImage, credentialStore)
            .createImage(template);

      assertEquals(image.get().getDefaultCredentials(), credentials);

      verify(delegate, credsToImage);
   }
}
