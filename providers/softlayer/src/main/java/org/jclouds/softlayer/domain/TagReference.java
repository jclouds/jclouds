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

/**
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Tag_Reference"/>
 */
public class TagReference {
   private final int id;
   private final int usrRecordId;
   private final int tagTypeId;
   private final int tagId;
   private final int resourceTableId;
   private final int empRecordId;
   private final Tag tag;
   private final TagType tagType;

   @ConstructorProperties({"id", "usrRecordId", "tagTypeId", "tagId", "resourceTableId", "empRecordId", "tag", "tagType"} )
   public TagReference(int id, int usrRecordId, int tagTypeId, int tagId, int resourceTableId, int empRecordId,
                       @Nullable Tag tag, @Nullable TagType tagType) {
      this.id = id;
      this.usrRecordId = usrRecordId;
      this.tagTypeId = tagTypeId;
      this.tagId = tagId;
      this.resourceTableId = resourceTableId;
      this.empRecordId = empRecordId;
      this.tag = tag;
      this.tagType = tagType;
   }

   public int getId() {
      return id;
   }

   public int getUsrRecordId() {
      return usrRecordId;
   }

   public int getTagTypeId() {
      return tagTypeId;
   }

   public int getTagId() {
      return tagId;
   }

   public int getResourceTableId() {
      return resourceTableId;
   }

   public int getEmpRecordId() {
      return empRecordId;
   }

   public Tag getTag() {
      return tag;
   }

   public TagType getTagType() {
      return tagType;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TagReference that = (TagReference) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.usrRecordId, that.usrRecordId) &&
              Objects.equal(this.tagTypeId, that.tagTypeId) &&
              Objects.equal(this.tagId, that.tagId) &&
              Objects.equal(this.resourceTableId, that.resourceTableId) &&
              Objects.equal(this.empRecordId, that.empRecordId) &&
              Objects.equal(this.tag, that.tag) &&
              Objects.equal(this.tagType, that.tagType);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, usrRecordId, tagTypeId, tagId, resourceTableId, empRecordId,
              tag, tagType);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("usrRecordId", usrRecordId)
              .add("tagTypeId", tagTypeId)
              .add("tagId", tagId)
              .add("resourceTableId", resourceTableId)
              .add("empRecordId", empRecordId)
              .add("tag", tag)
              .add("tagType", tagType)
              .toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromTagReference(this);
   }

   public static class Builder {
      private int id;
      private int usrRecordId;
      private int tagTypeId;
      private int tagId;
      private int resourceTableId;
      private int empRecordId;
      private Tag tag;
      private TagType tagType;

      /**
       * @see TagReference#getId()
       */
      public Builder id(int id) {
         this.id = id;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.TagReference#getUsrRecordId()
       */
      public Builder usrRecordId(int usrRecordId) {
         this.usrRecordId = usrRecordId;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.TagReference#getTagTypeId()
       */
      public Builder tagTypeId(int tagTypeId) {
         this.tagTypeId = tagTypeId;
         return this;
      }

      /**
       * @see TagReference#getTagId()
       */
      public Builder tagId(int tagId) {
         this.tagId = tagId;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.TagReference#getResourceTableId()
       */
      public Builder resourceTableId(int resourceTableId) {
         this.resourceTableId = resourceTableId;
         return this;
      }

      /**
       * @see TagReference#getEmpRecordId()
       */
      public Builder empRecordId(int empRecordId) {
         this.empRecordId = empRecordId;
         return this;
      }

      /**
       * @see TagReference#getTag()
       */
      public Builder tag(Tag tag) {
         this.tag = tag;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.TagReference#getTagType()
       */
      public Builder tagType(TagType tagType) {
         this.tagType = tagType;
         return this;
      }

      public TagReference build() {
         return new TagReference(id, usrRecordId, tagTypeId, tagId, resourceTableId, empRecordId, tag, tagType);
      }

      public Builder fromTagReference(TagReference in) {
         return this
                 .id(in.getId())
                 .usrRecordId(in.getUsrRecordId())
                 .tagTypeId(in.getTagTypeId())
                 .tagId(in.getTagId())
                 .resourceTableId(in.getResourceTableId())
                 .empRecordId(in.getResourceTableId())
                 .tag(in.getTag())
                 .tagType(in.getTagType());
      }
   }
}
