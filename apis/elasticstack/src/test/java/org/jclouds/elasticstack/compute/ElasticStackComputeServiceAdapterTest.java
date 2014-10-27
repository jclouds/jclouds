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
package org.jclouds.elasticstack.compute;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Map;
import java.util.UUID;

import org.easymock.EasyMock;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume.Type;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.compute.domain.internal.TemplateImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.elasticstack.ElasticStackApi;
import org.jclouds.elasticstack.domain.DriveInfo;
import org.jclouds.elasticstack.domain.DriveMetrics;
import org.jclouds.elasticstack.domain.ImageConversionType;
import org.jclouds.elasticstack.domain.WellKnownImage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Maps;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.MoreExecutors;

@Test(groups = "unit", testName = "ElasticStackComputeServiceAdapterTest")
public class ElasticStackComputeServiceAdapterTest {

   private Image image;
   private Hardware hardware;
   private Template template;

   @BeforeMethod
   public void setup() {
      image = new ImageBuilder().ids("mock").operatingSystem(OperatingSystem.builder().description("mock").build())
            .status(Status.AVAILABLE).build();
      hardware = new HardwareBuilder().ids("mock").volume(new VolumeBuilder().type(Type.LOCAL).size(1.0f).build())
            .processor(new Processor(1.0, 1.0)).build();
      template = new TemplateImpl(image, hardware, new LocationBuilder().id("mock").scope(LocationScope.PROVIDER)
            .description("mock").build(), TemplateOptions.NONE);
   }

   @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "imageDrive call failed")
   public void testDiskIsDestroyedIfImageFails() {
      Supplier<Map<String, WellKnownImage>> preInstalledImageSupplier = Suppliers.ofInstance(Maps
            .<String, WellKnownImage> newHashMap());
      LoadingCache<String, DriveInfo> driveCache = CacheBuilder.newBuilder().build(
            new CacheLoader<String, DriveInfo>() {
               @Override
               public DriveInfo load(String key) throws Exception {
                  throw new IllegalStateException("cache should not be used");
               }
            });

      ElasticStackApi api = EasyMock.createMock(ElasticStackApi.class);
      Predicate<DriveInfo> driveNotClaimed = Predicates.alwaysTrue();
      DriveInfo mockDrive = new DriveInfo.Builder().uuid(UUID.randomUUID().toString()).name("mock")
            .metrics(new DriveMetrics.Builder().build()).build();

      expect(api.createDrive(anyObject(DriveInfo.class))).andReturn(mockDrive);
      api.imageDrive(image.getId(), mockDrive.getUuid(), ImageConversionType.GUNZIP);
      // Set a custom exception message to make sure the exception is thrown at the right point
      expectLastCall().andThrow(new IllegalStateException("imageDrive call failed"));
      api.destroyDrive(mockDrive.getUuid());
      expectLastCall();

      replay(api);

      ElasticStackComputeServiceAdapter adapter = new ElasticStackComputeServiceAdapter(api, driveNotClaimed,
            preInstalledImageSupplier, driveCache, "12345678", MoreExecutors.sameThreadExecutor());

      try {
         adapter.createNodeWithGroupEncodedIntoName("mock-group", "mock-name", template);
      } catch (IllegalStateException ex) {
         verify(api);
         throw ex;
      }
   }
}
