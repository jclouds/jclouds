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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.getLast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import com.google.common.base.Splitter;
import com.google.common.io.Files;

public class Archives {

   public static File tar(File baseDir, String archivePath) throws IOException {
      return tar(baseDir, new File(archivePath));
   }

   public static File tar(File baseDir, File tarFile) throws IOException {
      // Check that the directory is a directory, and get its contents
      checkArgument(baseDir.isDirectory(), "%s is not a directory", baseDir);
      File[] files = baseDir.listFiles();
      String token = getLast(Splitter.on("/").split(baseDir.getAbsolutePath()));
      TarArchiveOutputStream tos = new TarArchiveOutputStream(new FileOutputStream(tarFile));
      tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
      try {
         for (File file : files) {
            TarArchiveEntry tarEntry = new TarArchiveEntry(file);
            tarEntry.setName("/" + getLast(Splitter.on(token).split(file.toString())));
            tos.putArchiveEntry(tarEntry);
            if (!file.isDirectory()) {
               Files.asByteSource(file).copyTo(tos);
            }
            tos.closeArchiveEntry();
         }
      } finally {
         tos.close();
      }
      return tarFile;
   }

}
