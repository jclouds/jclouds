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
package org.jclouds.softlayer.domain;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

public class SecuritySshKey {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromSecuritySshKey(this);
   }

   public static class Builder {

      protected int id;
      protected String key;
      protected String label;
      protected String fingerprint;
      protected String notes;
      protected String createDate;
      protected String modifyDate;

      /**
       * @see SecuritySshKey#getId()
       */
      public Builder id(int id) {
         this.id = id;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.SecuritySshKey#getKey()
       */
      public Builder key(String key) {
         this.key = key;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.SecuritySshKey#getLabel() ()
       */
      public Builder label(String label) {
         this.label = label;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.SecuritySshKey#getFingerprint()
       */
      public Builder fingerprint(String fingerprint) {
         this.fingerprint = fingerprint;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.SecuritySshKey#getNotes()
       */
      public Builder notes(String notes) {
         this.notes = notes;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.SecuritySshKey#getCreateDate()
       */
      public Builder createDate(String createDate) {
         this.createDate = createDate;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.SecuritySshKey#getModifyDate()
       */
      public Builder modifyDate(String modifyDate) {
         this.modifyDate = modifyDate;
         return this;
      }

      public SecuritySshKey build() {
         return new SecuritySshKey(id, key, label, fingerprint, notes, createDate, modifyDate);
      }

      public Builder fromSecuritySshKey(SecuritySshKey in) {
         return this
                 .id(in.getId())
                 .key(in.getKey())
                 .label(in.getLabel())
                 .fingerprint(in.getFingerprint())
                 .notes(in.getNotes())
                 .createDate(in.getCreateDate())
                 .modifyDate(in.getModifyDate());
      }
   }

   private final int id;
   private final String key;
   private final String label;
   private final String fingerprint;
   private final String notes;
   private final String createDate;
   private final String modifyDate;

   @ConstructorProperties({
           "id", "key", "label", "name", "notes", "createDate", "modifyDate" })
   protected SecuritySshKey(int id, @Nullable String key, @Nullable String label,
                            @Nullable String fingerprint, @Nullable String notes, @Nullable String createDate,
                            @Nullable String modifyDate) {
      this.id = id;
      this.key = key;
      this.label = label;
      this.fingerprint = fingerprint;
      this.notes = notes;
      this.createDate = createDate;
      this.modifyDate = modifyDate;
   }

   public int getId() {
      return id;
   }

   public String getKey() {
      return key;
   }

   public String getLabel() {
      return label;
   }

   public String getFingerprint() {
      return fingerprint;
   }

   public String getNotes() {
      return notes;
   }

   public String getCreateDate() {
      return createDate;
   }

   public String getModifyDate() {
      return modifyDate;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SecuritySshKey that = (SecuritySshKey) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.key, that.key) &&
              Objects.equal(this.label, that.label) &&
              Objects.equal(this.fingerprint, that.fingerprint) &&
              Objects.equal(this.notes, that.notes) &&
              Objects.equal(this.createDate, that.createDate) &&
              Objects.equal(this.modifyDate, that.modifyDate);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, key, label, fingerprint, notes, createDate, modifyDate);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("key", key)
              .add("label", label)
              .add("name", fingerprint)
              .add("notes", notes)
              .add("createDate", createDate)
              .add("modifyDate", modifyDate)
              .toString();
   }
}
