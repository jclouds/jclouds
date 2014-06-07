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
package org.jclouds.cloudfiles.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

public class ContainerCDNMetadata implements Comparable<ContainerCDNMetadata> {

   private String name;
   @Named("cdn_enabled")
   private boolean cdnEnabled;
   @Named("log_retention")
   private boolean logRetention;
   private long ttl;
   @Named("cdn_uri")
   private URI cdnUri;
   @Named("cdn_ssl_uri")
   private URI cdnSslUri;
   @Named("cdn_streaming_uri")
   private URI cdnStreamingUri;
   @Named("cdn_ios_uri")
   private URI cdnIosUri;

   @ConstructorProperties({ "name", "cdn_enabled", "log_retention", "ttl", "cdn_uri", "cdn_ssl_uri",
         "cdn_streaming_uri", "cdn_ios_uri"})
   public ContainerCDNMetadata(String name, boolean cdnEnabled, boolean logRetention, long ttl,
         @Nullable URI cdnUri, @Nullable URI cdnSslUri, @Nullable URI cdnStreamingUri, @Nullable URI cdnIosUri) {
      this.name = checkNotNull(name, "name");
      this.cdnEnabled = checkNotNull(cdnEnabled);
      this.logRetention = checkNotNull(logRetention);
      this.ttl = checkNotNull(ttl);
      this.cdnUri = cdnUri;
      this.cdnSslUri = cdnSslUri;
      this.cdnStreamingUri = cdnStreamingUri;
      this.cdnIosUri = cdnIosUri;
   }

   public ContainerCDNMetadata() {
   }

   /**
    * Beware: The container name is not available from HEAD CDN responses and will be null. return
    * the name of the container to which these CDN settings apply.
    */
   public String getName() {
      return name;
   }

   public boolean isCDNEnabled() {
      return cdnEnabled;
   }

   public boolean isLogRetention() {
      return logRetention;
   }

   public long getTTL() {
      return ttl;
   }

   public URI getCDNUri() {
      return cdnUri;
   }

   public URI getCDNSslUri() {
      return cdnSslUri;
   }

   public URI getCDNStreamingUri() {
      return cdnStreamingUri;
   }

   public URI getCDNIosUri() {
      return cdnIosUri;
   }

   public int compareTo(ContainerCDNMetadata o) {
      if (getName() == null)
         return -1;
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((cdnUri == null) ? 0 : cdnUri.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      
      ContainerCDNMetadata other = (ContainerCDNMetadata) obj;
      if (cdnUri == null) {
         if (other.cdnUri != null)
            return false;
      } else if (!cdnUri.equals(other.cdnUri))
         return false;
      
      return true;
   }

   @Override
   public String toString() {
      return String.format(
               "[name=%s, cdnEnabled=%s, logRetention=%s, ttl=%s, cdnUri=%s, cdnSslUri=%s, cdnStreamingUri=%s, cdnIosUri=%s]",
                 name, cdnEnabled, logRetention, ttl, cdnUri, cdnSslUri, cdnStreamingUri, cdnIosUri);
   }
}
