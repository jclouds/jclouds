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

import java.util.List;
import java.util.Map;

import org.jclouds.googlecloudstorage.domain.ObjectAccessControls;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.MediaType;

public class ObjectTemplate {

   private String name;
   private Long size;
   private String cacheControl;
   private String contentDisposition;
   private String contentEncoding;
   private String contentLanguage;
   private String contentType;
   private String crc32c;
   private String md5Hash;
   private Map<String, String> metadata = Maps.newLinkedHashMap();
   private List<ObjectAccessControls> acl = Lists.newArrayList();

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

   public ObjectTemplate crc32c(String crc32c) {
      this.crc32c = crc32c;
      return this;
   }

   public ObjectTemplate md5Hash(String md5Hash) {
      this.md5Hash = crc32c;
      return this;
   }

   public ObjectTemplate addAcl(ObjectAccessControls acl) {
      this.acl.add(acl);
      return this;
   }

   public ObjectTemplate acl(List<ObjectAccessControls> acl) {
      this.acl.addAll(acl);
      return this;
   }

   public String cacheControl() {
      return cacheControl;
   }

   public String contentDisposition() {
      return contentDisposition;
   }

   public String contentEncoding() {
      return contentEncoding;
   }

   public String contentLanguage() {
      return contentLanguage;
   }

   public String contentType() {
      return contentType;
   }

   public Map<String, String> metadata() {
      return metadata;
   }

   public String name() {
      return name;
   }

   public Long size() {
      return size;
   }

   public List<ObjectAccessControls> acl() {
      return acl;
   }
}
