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

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.Module;
import org.jclouds.Constants;
import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.docker.DockerApi;
import org.jclouds.docker.features.internal.Archives;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

@Test(groups = "live")
public class BaseDockerApiLiveTest extends BaseApiLiveTest<DockerApi> {

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
      overrides.setProperty(Constants.PROPERTY_MAX_RETRIES, "15");
      overrides.setProperty("jclouds.ssh.retry-auth", "true");
      return overrides;
   }

   protected String consumeStream(InputStream stream, boolean swallowIOException) {
      String result = null;
      try {
         result = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
         Closeables.close(stream, swallowIOException);
      } catch (IOException e) {
         Assert.fail();
      }
      return result;
   }

   protected Payload createPayload() throws IOException {
      String folderPath = System.getProperty("user.dir") + "/docker/src/test/resources";
      File parentDir = new File(folderPath + "/archive");
      parentDir.mkdirs();
      URL url = Resources.getResource("Dockerfile");
      String content = Resources.toString(url, Charsets.UTF_8);
      final File dockerfile = new File(parentDir.getAbsolutePath() + File.separator + "Dockerfile");
      Files.write(content.getBytes(), dockerfile);
      File archive = Archives.tar(parentDir, folderPath + "/archive/archive.tar");
      FileInputStream data = new FileInputStream(archive);
      Payload payload = Payloads.newInputStreamPayload(data);
      payload.getContentMetadata().setContentLength(data.getChannel().size());
      payload.getContentMetadata().setContentType("application/tar");
      return payload;
   }

}
