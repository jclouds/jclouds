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
package org.jclouds.rackspace.cloudfiles.v1.domain;

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;

import javax.inject.Named;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents a CDN Container in Rackspace Cloud Files.
 */
public class CDNContainer implements Comparable<CDNContainer> {

   private String name;
   @Named("cdn_enabled")
   private boolean enabled;
   @Named("log_retention")
   private boolean logRetention;
   private int ttl;
   @Named("cdn_uri")
   private URI uri;
   @Named("cdn_ssl_uri")
   private URI sslUri;
   @Named("cdn_streaming_uri")
   private URI streamingUri;
   @Named("cdn_ios_uri")
   private URI iosUri;

   @ConstructorProperties({ "name", "cdn_enabled", "log_retention", "ttl", "cdn_uri", "cdn_ssl_uri", "cdn_streaming_uri", "cdn_ios_uri"})
   public CDNContainer(String name, boolean enabled, boolean logRetention, int ttl, URI uri, URI sslUri, URI streamingUri, URI iosUri) {
      this.name = checkNotNull(name, "name required");
      this.enabled = enabled;
      this.logRetention = logRetention;
      this.ttl = ttl;
      this.uri = checkNotNull(uri, "uri required");
      this.sslUri = checkNotNull(sslUri, "sslUri required");
      this.streamingUri = checkNotNull(streamingUri, "streamingUri required");
      this.iosUri = checkNotNull(iosUri, "iosUri required");
   }

   /**
    * <h3>NOTE</h3>
    * The container name is not available from HEAD CDN responses and will be null.
    *
    * @return The name of this CDN container.
    */
   public String getName() {
      return name;
   }

   /**
    * @return {@code true} if the container is CDN enabled, {@code false} if not.
    */
   public boolean isEnabled() {
      return enabled;
   }

   /**
    * @return {@code true} if the logs will be retained for this CDN container, {@code false} if not.
    */
   public boolean isLogRetentionEnabled() {
      return logRetention;
   }

   /**
    * @return the TTL for this CDN container.
    */
   public int getTtl() {
      return ttl;
   }

   /**
    * @return the {@link URI} for this CDN container.
    */
   public URI getUri() {
      return uri;
   }

   /**
    * @return the SSL {@link URI} for this CDN container.
    */
   public URI getSslUri() {
      return sslUri;
   }

   /**
    * @return the streaming {@link URI} for this CDN container.
    */
   public URI getStreamingUri() {
      return streamingUri;
   }

   /**
    * @return the iOS {@link URI} for this CDN container.
    */
   public URI getIosUri() {
      return iosUri;
   }
   
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      CDNContainer that = CDNContainer.class.cast(obj);
      return Objects.equal(this.name, that.name)
               && Objects.equal(this.enabled, that.enabled)
               && Objects.equal(this.logRetention, that.logRetention)
               && Objects.equal(this.ttl, that.ttl)
               && Objects.equal(this.uri, that.uri)
               && Objects.equal(this.sslUri, that.sslUri)
               && Objects.equal(this.streamingUri, that.streamingUri)
               && Objects.equal(this.iosUri, that.iosUri);
   }
   

   @Override
   public int hashCode() {
      return Objects.hashCode(getName(), isEnabled(), isLogRetentionEnabled(), getTtl(), getUri(), getSslUri(), getStreamingUri(), getIosUri());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return toStringHelper("").omitNullValues()
            .add("name", getName())
            .add("enabled", isEnabled())
            .add("logRetention", isLogRetentionEnabled())
            .add("ttl", getTtl())
            .add("uri", getUri())
            .add("sslUri", getSslUri())
            .add("streamingUri", getStreamingUri())
            .add("iosUri", getIosUri());
   }

   @Override
   public int compareTo(CDNContainer that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.getName().compareTo(that.getName());
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      
      private String name;
      private boolean enabled;
      private boolean logRetention;
      private int ttl;
      private URI uri;
      private URI sslUri;
      private URI streamingUri;
      private URI iosUri;
      
      /**
       * @see CDNContainer#getName()
       */
      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      /**
       * @see CDNContainer#isEnabled()
       */
      public Builder enabled(boolean enabled) {
         this.enabled = enabled;
         return this;
      }

      /**
       * @see CDNContainer#isLogRetentionEnabled()
       */
      public Builder logRetention(boolean logRetention) {
         this.logRetention = logRetention;
         return this;
      }

      /**
       * @see CDNContainer#getTtl()
       */
      public Builder ttl(int ttl) {
         this.ttl = ttl;
         return this;
      }

      /**
       * @see CDNContainer#getUri()
       */
      public Builder uri(URI uri) {
         this.uri = uri;
         return this;
      }

      /**
       * @see CDNContainer#getSslUri()
       */
      public Builder sslUri(URI sslUri) {
         this.sslUri = sslUri;
         return this;
      }

      /**
       * @see CDNContainer#getStreamingUri()
       */
      public Builder streamingUri(URI streamingUri) {
         this.streamingUri = streamingUri;
         return this;
      }

      /**
       * @see CDNContainer#getIosUri()
       */
      public Builder iosUri(URI iosUri) {
         this.iosUri = iosUri;
         return this;
      }

      public CDNContainer build() {
         return new CDNContainer(name, enabled, logRetention, ttl, uri, sslUri, streamingUri, iosUri);
      }

      public Builder fromContainer(CDNContainer from) {
         return name(from.getName())
               .enabled(from.isEnabled())
               .logRetention(from.isLogRetentionEnabled())
               .ttl(from.getTtl())
               .uri(from.getUri())
               .sslUri(from.getSslUri())
               .streamingUri(from.getStreamingUri())
               .iosUri(from.getIosUri());
      }
   }
}
