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
package org.jclouds.openstack.swift.v1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.io.Payload;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * Represents an object in OpenStack Object Storage.
 * 
 * 
 * @see ObjectApi
 */
public class SwiftObject implements Comparable<SwiftObject> {

   private final String name;
   private final URI uri;
   private final String etag;
   private final Date lastModified;
   private final Multimap<String, String> headers;
   private final Map<String, String> metadata;
   private final Payload payload;

   protected SwiftObject(String name, URI uri, String etag, Date lastModified,
         Multimap<String, String> headers, Map<String, String> metadata, Payload payload) {
      this.name = checkNotNull(name, "name");
      this.uri = checkNotNull(uri, "uri of %s", uri);
      this.etag = checkNotNull(etag, "etag of %s", name).replace("\"", "");
      this.lastModified = checkNotNull(lastModified, "lastModified of %s", name);
      this.headers = headers == null ? ImmutableMultimap.<String, String> of() : checkNotNull(headers, "headers of %s", name);
      this.metadata = metadata == null ? ImmutableMap.<String, String> of() : metadata;
      this.payload = checkNotNull(payload, "payload of %s", name);
   }

   /**
    * @return The name of this object.
    */
   public String getName() {
      return name;
   }

   /**
    * @return The {@link URI} for this object.
    */
   public URI getUri() {
      return uri;
   }

   /**
    * @return The ETag of the content of this object.
    * @deprecated Please use {@link #getETag()} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   public String getEtag() {
      return etag;
   }

   /**
    * @return The ETag of the content of this object.
    */
   public String getETag() {
      return etag;
   }

   /**
    * @return The {@link Date} that this object was last modified.
    */
   public Date getLastModified() {
      return lastModified;
   }

   /**
    * @return The HTTP headers for this object.
    */
   public Multimap<String, String> getHeaders() {
      return headers;
   }

   /**
    * <h3>NOTE</h3>
    * In current swift implementations, headers keys are lower-cased. This means
    * characters such as turkish will probably not work out well.
    * 
    * @return a {@code Map<String, String>} containing this object's metadata. The map is empty
    *         except in {@link ObjectApi#head(String) GetObjectMetadata} or
    *         {@link ObjectApi#get(String) GetObject} commands.
    */
   public Map<String, String> getMetadata() {
      return metadata;
   }

   /**
    * <h3>NOTE</h3>
    * The object will only have a {@link Payload#getInput()} when retrieved via the
    * {@link ObjectApi#get(String) GetObject} command.
    * 
    * @return The {@link Payload} for this object.
    */
   public Payload getPayload() {
      return payload;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof SwiftObject) {
         final SwiftObject that = SwiftObject.class.cast(object);
         return equal(getName(), that.getName())
               && equal(getUri(), that.getUri())
               && equal(getETag(), that.getETag());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getName(), getUri(), getETag());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return toStringHelper(this)
            .add("name", getName())
            .add("uri", getUri())
            .add("etag", getETag())
            .add("lastModified", getLastModified())
            .add("metadata", getMetadata());
   }

   @Override
   public int compareTo(SwiftObject that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.getName().compareTo(that.getName());
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromObject(this);
   }

   public static class Builder {
      protected String name;
      protected URI uri;
      protected String etag;
      protected Date lastModified;
      protected Payload payload;
      protected Multimap<String, String> headers = ImmutableMultimap.of();
      protected Map<String, String> metadata = ImmutableMap.of();

      /**
       * @see SwiftObject#getName()
       */
      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      /**
       * @see SwiftObject#getUri()
       */
      public Builder uri(URI uri) {
         this.uri = checkNotNull(uri, "uri");
         return this;
      }

      /**
       * @see SwiftObject#getETag()
       */
      public Builder etag(String etag) {
         this.etag = etag;
         return this;
      }

      /**
       * @see SwiftObject#getLastModified()
       */
      public Builder lastModified(Date lastModified) {
         this.lastModified = lastModified;
         return this;
      }

      /**
       * @see SwiftObject#getPayload()
       */
      public Builder payload(Payload payload) {
         this.payload = payload;
         return this;
      }

      /**
       * @see SwiftObject#getHeaders()
       */
      public Builder headers(Multimap<String, String> headers) {
         this.headers = headers;
         return this;
      }

      /**
       * Will lower-case all metadata keys due to a swift implementation
       * decision.
       * 
       * @see SwiftObject#getMetadata()
       */
      public Builder metadata(Map<String, String> metadata) {
         ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String> builder();
         for (Entry<String, String> entry : checkNotNull(metadata, "metadata").entrySet()) {
            builder.put(entry.getKey().toLowerCase(), entry.getValue());
         }
         this.metadata = builder.build();
         return this;
      }

      public SwiftObject build() {
         return new SwiftObject(name, uri, etag, lastModified, headers, metadata, payload);
      }

      public Builder fromObject(SwiftObject from) {
         return name(from.getName())
               .uri(from.getUri())
               .etag(from.getETag())
               .lastModified(from.getLastModified())
               .headers(from.getHeaders())
               .metadata(from.getMetadata())
               .payload(from.getPayload());
      }
   }
}
