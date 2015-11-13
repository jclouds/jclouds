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
package org.jclouds.filesystem.util;

import static java.nio.file.FileSystems.getDefault;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.Uninterruptibles;

/**
 * Utilities for the filesystem blobstore.
 */
public class Utils {
   /** Private constructor for utility class. */
   private Utils() {
      // Do nothing
   }

   /**
    * Determine if Java is running on a Mac OS
    */
   public static boolean isMacOSX() {
      String osName = System.getProperty("os.name");
      return osName.contains("OS X");
   }

   /**
    * Determine if Java is running on a windows OS
    */
   public static boolean isWindows() {
      return System.getProperty("os.name", "").toLowerCase().contains("windows");
   }

   /** Delete a file or a directory recursively. */
   public static void deleteRecursively(File file) throws IOException {
      if (file.isDirectory()) {
         File[] children = file.listFiles();
         if (children != null) {
            for (File child : children) {
               deleteRecursively(child);
            }
         }
      }

      delete(file);
   }

   public static void delete(File file) throws IOException {
      for (int n = 0; n < 10; n++) {
         try {
            Files.delete(file.toPath());
            if (Files.exists(file.toPath())) {
               Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);
               continue;
            }
            return;
         } catch (DirectoryNotEmptyException dnee) {
            // A previous file delete operation did not finish before this call
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            continue;
         } catch (AccessDeniedException ade) {
            // The file was locked by antivirus, indexing, or another operation triggered by previous file modification
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            continue;
         } catch (NoSuchFileException nse) {
            return; // The file has been eventually deleted after a previous operation that failed. no-op
         }
      }
      // File could not be deleted multiple times. It is very likely locked in another process
      throw new IOException("Could not delete: " + file.toPath());
   }

   /**
    * @return Localized name for the "Everyone" Windows principal.
    */
   public static final String getWindowsEveryonePrincipalName() {
      if (isWindows()) {
         try {
            Process process = new ProcessBuilder("whoami", "/groups").start();
            try {
               String line;
               try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                  while ((line = reader.readLine()) != null) {
                     if (line.indexOf("S-1-1-0") != -1) {
                        return line.split(" ")[0];
                     }
                  }
               }
            } finally {
               process.destroy();
            }
         } catch (IOException e) {
         }
      }
      // Default/fallback value
      return "Everyone";
   }

   public static final String WINDOWS_EVERYONE = getWindowsEveryonePrincipalName();

   /**
    * @param path The path to a Windows file or directory.
    * @return true if path has permissions set to Everyone on windows. The exact permissions are not checked.
    */
   public static boolean isPrivate(Path path) throws IOException {
      UserPrincipal everyone = getDefault().getUserPrincipalLookupService()
            .lookupPrincipalByName(WINDOWS_EVERYONE);
      AclFileAttributeView aclFileAttributes = java.nio.file.Files.getFileAttributeView(
            path, AclFileAttributeView.class);
      for (AclEntry aclEntry : aclFileAttributes.getAcl()) {
         if (aclEntry.principal().equals(everyone)) {
            return false;
         }
      }
      return true;
   }

   /**
    * @param path Remove "Everyone" from this path's Windows ACL permissions.
    */
   public static void setPrivate(Path path) throws IOException {
      UserPrincipal everyone = getDefault().getUserPrincipalLookupService()
            .lookupPrincipalByName(WINDOWS_EVERYONE);
      AclFileAttributeView aclFileAttributes = java.nio.file.Files.getFileAttributeView(
            path, AclFileAttributeView.class);
      CopyOnWriteArrayList<AclEntry> aclList = new CopyOnWriteArrayList(aclFileAttributes.getAcl());
      for (AclEntry aclEntry : aclList) {
         if (aclEntry.principal().equals(everyone) && aclEntry.type().equals(AclEntryType.ALLOW)) {
            aclList.remove(aclEntry);
         }
      }
      aclFileAttributes.setAcl(aclList);
   }

   /**
    * @param path Add "Everyone" with read enabled to this path's Windows ACL permissions.
    */
   public static void setPublic(Path path) throws IOException {
      UserPrincipal everyone = getDefault().getUserPrincipalLookupService()
            .lookupPrincipalByName(WINDOWS_EVERYONE);
      AclFileAttributeView aclFileAttributes = java.nio.file.Files.getFileAttributeView(
            path, AclFileAttributeView.class);
      List<AclEntry> list = aclFileAttributes.getAcl();
      list.add(AclEntry.newBuilder().setPrincipal(everyone).setPermissions(
            AclEntryPermission.READ_DATA,
            AclEntryPermission.READ_ACL,
            AclEntryPermission.READ_ATTRIBUTES,
            AclEntryPermission.READ_NAMED_ATTRS)
            .setType(AclEntryType.ALLOW)
            .build());
      aclFileAttributes.setAcl(list);
   }
}
