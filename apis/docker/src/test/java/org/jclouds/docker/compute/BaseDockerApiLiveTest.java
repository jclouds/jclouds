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
package org.jclouds.docker.compute;

import static com.google.common.base.Charsets.UTF_8;
import static org.jclouds.util.Closeables2.closeQuietly;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.exporter.TarExporter;
import org.jclouds.Constants;
import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.docker.DockerApi;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.CharStreams;
import com.google.inject.Module;

@Test(groups = "live")
public class BaseDockerApiLiveTest extends BaseApiLiveTest<DockerApi> {

   protected static final String DEFAULT_IMAGE = "alpine";
   protected static final String DEFAULT_TAG = "3.2";
   protected static final String ALPINE_IMAGE_TAG = String.format("%s:%s", DEFAULT_IMAGE, DEFAULT_TAG);


   public BaseDockerApiLiveTest() {
      provider = "docker";
   }

   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module>of(getLoggingModule(), new SshjSshClientModule());
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.setProperty(ComputeServiceProperties.IMAGE_LOGIN_USER, "root:password");
      setIfTestSystemPropertyPresent(overrides,  provider + ".cacert.path");
      setIfTestSystemPropertyPresent(overrides, Constants.PROPERTY_TRUST_ALL_CERTS);
      return overrides;
   }

   protected String consumeStream(InputStream stream) {
      try {
         return CharStreams.toString(new InputStreamReader(stream, UTF_8));
      } catch (IOException e) {
         throw new AssertionError(e);
      } finally {
         closeQuietly(stream);
      }
   }

   public static Payload tarredDockerfile() throws IOException {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      ShrinkWrap.create(GenericArchive.class, "archive.tar")
            .add(new ClassLoaderAsset("Dockerfile"), "Dockerfile")
            .as(TarExporter.class).exportTo(bytes);

      return Payloads.newByteArrayPayload(bytes.toByteArray());
   }
}
