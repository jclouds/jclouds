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
package org.jclouds.atmos.domain;

import com.google.common.base.Objects;

/**
 * Metadata of a Atmos Online object
 */
public class DirectoryEntry implements Comparable<DirectoryEntry> {
   private final String objectid;
   private final FileType type;
   private final String objname;
   private final long size;

   public DirectoryEntry(String objectid, FileType type, String objname, long size) {
      this.objectid = objectid;
      this.objname = objname;
      this.type = type;
      this.size = size;
   }

   public String getObjectID() {
      return objectid;
   }

   public String getObjectName() {
      return objname;
   }

   public FileType getType() {
      return type;
   }

   public long getSize() {
      return size;
   }

   public int compareTo(DirectoryEntry o) {
      if (getObjectName() == null)
         return -1;
      return (this == o) ? 0 : getObjectName().compareTo(o.getObjectName());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(objectid, objname, type, size);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      DirectoryEntry other = (DirectoryEntry) obj;
      return Objects.equal(objectid, other.objectid) &&
            Objects.equal(objname, other.objname) &&
            Objects.equal(type, other.type) &&
            Objects.equal(size, other.size);
   }

   @Override
   public String toString() {
      return "DirectoryEntry [type=" + type + ", objectid=" + objectid + ", objname=" + objname
               + ", size=" + size + "]";
   }
}
