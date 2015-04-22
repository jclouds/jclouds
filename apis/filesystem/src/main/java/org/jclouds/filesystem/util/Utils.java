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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Utilities for the filesystem blobstore.
 */
public class Utils {
   /** Private constructor for utility class. */
   private Utils() {
      // Do nothing
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
      Files.delete(file.toPath());
   }

   /**
    * Determine if Java is running on a windows OS
    */
   public static boolean isWindows() {
      return System.getProperty("os.name", "").toLowerCase().contains("windows");
   }

   /**
    * @param path The path to a Windows file or directory.
    * @return true if path has permissions set to Everyone on windows. The exact permissions are not checked.
    */
   public static boolean isPrivate(Path path) throws IOException {
      UserPrincipal everyone = getDefault().getUserPrincipalLookupService()
               .lookupPrincipalByName("Everyone");
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
            .lookupPrincipalByName("Everyone");
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
            .lookupPrincipalByName("Everyone");
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
