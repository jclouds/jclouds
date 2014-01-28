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
package org.jclouds.docker.features.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Named;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarUtils;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

@Test(groups = "unit", testName = "ArchivesTest")
public class ArchivesTest {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private File tmpDir;
   private File outputDir;
   private long checkSum;

   @BeforeClass
   private void init() throws IOException {
      tmpDir = Files.createTempDir();
      outputDir = Files.createTempDir();
      File sampleFile = writeSampleFile("test", "this is a test to tar a hierarchy of folders and files\n");
      checkSum = TarUtils.computeCheckSum(Files.asByteSource(sampleFile).read());
   }

   public void testTarSingleFile() throws Exception {
      File archive = Archives.tar(tmpDir, new File(outputDir + File.separator + "test.tar.gz"));
      List<File> untarredFiles = unTar(archive, outputDir);
      File untarredSampleFile = getOnlyElement(untarredFiles, null);
      assertNotNull(untarredSampleFile);
      assertTrue(checkSum == TarUtils.computeCheckSum(Files.asByteSource(untarredSampleFile).read()));
   }

   private List<File> unTar(final File inputFile, final File outputDir) throws Exception {
      final List<File> untarredFiles = Lists.newArrayList();
      final InputStream is = new FileInputStream(inputFile);
      final TarArchiveInputStream tarArchiveInputStream = (TarArchiveInputStream)
              new ArchiveStreamFactory().createArchiveInputStream("tar", is);
      TarArchiveEntry entry;
      while ((entry = (TarArchiveEntry) tarArchiveInputStream.getNextEntry()) != null) {
         final File outputFile = new File(outputDir, entry.getName());
         if (entry.isDirectory()) {
            if (!outputFile.exists()) {
               if (!outputFile.mkdirs()) {
                  throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
               }
            }
         } else {
            OutputStream outputFileStream = new FileOutputStream(outputFile);
            ByteStreams.copy(tarArchiveInputStream, outputFileStream);
            outputFileStream.close();
         }
         untarredFiles.add(outputFile);
      }
      tarArchiveInputStream.close();
      return untarredFiles;
   }

   private File writeSampleFile(String fileName, final String contents) {
      checkNotNull(fileName, "Provided file name for writing must NOT be null.");
      checkNotNull(contents, "Unable to write null contents.");
      File sampleFile = new File(tmpDir + File.separator + fileName);
      try {
         Files.write(contents.getBytes(), sampleFile);
      } catch (IOException e) {
         logger.error("ERROR trying to write to file '" + fileName + "' - " + e.toString());
         Assert.fail();
      }
      return sampleFile;
   }
}
