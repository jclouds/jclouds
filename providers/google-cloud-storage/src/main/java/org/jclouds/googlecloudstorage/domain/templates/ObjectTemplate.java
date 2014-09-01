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

package org.jclouds.googlecloudstorage.domain.templates;

import java.util.Map;
import java.util.Set;

import org.jclouds.googlecloudstorage.domain.DomainUtils;
import org.jclouds.googlecloudstorage.domain.ObjectAccessControls;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import com.google.common.io.BaseEncoding;
import com.google.common.net.MediaType;

public class ObjectTemplate {

   protected String name;
   protected Long size;
   protected String cacheControl;
   protected String contentDisposition;
   protected String contentEncoding;
   protected String contentLanguage;
   protected String contentType;
   protected String crc32c;
   protected String md5Hash;
   private Map<String, String> metadata = Maps.newHashMap();
   protected Set<ObjectAccessControls> acl = Sets.newHashSet();

   public ObjectTemplate name(String name) {
      this.name = name;
      return this;
   }

   public ObjectTemplate size(Long size) {
      this.size = size;
      return this;
   }

   public ObjectTemplate cacheControl(String cacheControl) {
      this.cacheControl = cacheControl;
      return this;
   }

   public ObjectTemplate contentDisposition(String contentDisposition) {
      this.contentDisposition = contentDisposition;
      return this;
   }

   public ObjectTemplate contentEncoding(String contentEncoding) {
      this.contentEncoding = contentEncoding;
      return this;
   }

   public ObjectTemplate contentLanguage(String contentLanguage) {
      this.contentLanguage = contentLanguage;
      return this;
   }

   public ObjectTemplate contentType(MediaType contentType) {
      this.contentType = contentType.toString();
      return this;
   }

   public ObjectTemplate contentType(String contentType) {
      this.contentType = contentType;
      return this;
   }

   public ObjectTemplate customMetadata(Map<String, String> metadata) {
      this.metadata.putAll(metadata);
      return this;
   }

   public ObjectTemplate customMetadata(String key, String value) {
      this.metadata.put(key, value);
      return this;
   }

   public ObjectTemplate crc32c(HashCode crc32c) {
      this.crc32c = BaseEncoding.base64().encode(DomainUtils.reverse(crc32c.asBytes()));
      return this;
   }

   public ObjectTemplate md5Hash(HashCode md5Hash) {
      this.md5Hash = BaseEncoding.base64().encode(md5Hash.asBytes());
      return this;
   }

   public ObjectTemplate addAcl(ObjectAccessControls acl) {
      this.acl.add(acl);
      return this;
   }

   public ObjectTemplate acl(Set<ObjectAccessControls> acl) {
      this.acl.addAll(acl);
      return this;
   }

   public String getCacheControl() {
      return cacheControl;
   }

   public String getContentDisposition() {
      return contentDisposition;
   }

   public String getContentEncoding() {
      return contentEncoding;
   }

   public String getContentLanguage() {
      return contentLanguage;
   }

   public String getContentType() {
      return contentType;
   }

   public HashCode getCrc32cHashcode() {
      if (crc32c != null) {
         HashCode hc = HashCode.fromBytes(DomainUtils.reverse(BaseEncoding.base64().decode(crc32c)));
         return hc;
      }
      return null;
   }

   public HashCode getMd5HashCode() {
      if (md5Hash != null) {
         HashCode hc = HashCode.fromBytes(BaseEncoding.base64().decode(md5Hash));
         return hc;
      }
      return null;
   }

   public Map<String, String> getAllCustomMetadata() {
      return metadata;
   }

   public String getName() {
      return name;
   }

   public Long getSize() {
      return size;
   }

   public Set<ObjectAccessControls> getAcl() {
      return acl;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static ObjectTemplate fromObjectTemplate(ObjectTemplate objectTemplate) {
      return Builder.fromObjectTemplate(objectTemplate);
   }

   public static class Builder {

      public static ObjectTemplate fromObjectTemplate(ObjectTemplate in) {
         return new ObjectTemplate().name(in.getName()).size(in.getSize()).acl(in.getAcl())
                  .cacheControl(in.getCacheControl()).contentDisposition(in.getContentDisposition())
                  .contentEncoding(in.getContentEncoding()).contentLanguage(in.getContentLanguage())
                  .contentType(in.getContentType()).md5Hash(in.getMd5HashCode())
                  .customMetadata(in.getAllCustomMetadata()).crc32c(in.getCrc32cHashcode());

      }
   }
}
