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
package org.jclouds.io.payloads;

import java.util.Date;

import org.jclouds.io.ContentMetadata;
import org.jclouds.io.ContentMetadataBuilder;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.hash.HashCode;

public class BaseMutableContentMetadata extends ContentMetadataBuilder implements MutableContentMetadata {

   /**
    * {@inheritDoc}
    */
   @Override
   public Long getContentLength() {
      return contentLength;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentLength(@Nullable Long contentLength) {
      contentLength(contentLength);
   }

   /** @deprecated use {@link #getContentMD5AsHashCode()} instead. */
   @Deprecated
   @Override
   public byte[] getContentMD5() {
      HashCode hashCode = getContentMD5AsHashCode();
      return hashCode == null ? null : hashCode.asBytes();
   }

   @Override
   public HashCode getContentMD5AsHashCode() {
      return contentMD5;
   }

   /** @deprecated use {@link #setContentMD5(HashCode)} instead. */
   @Deprecated
   @Override
   public void setContentMD5(byte[] md5) {
      setContentMD5(md5 == null ? null : HashCode.fromBytes(md5));
   }

   @Override
   public void setContentMD5(HashCode md5) {
      contentMD5(md5);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getContentType() {
      return contentType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentType(@Nullable String contentType) {
      contentType(contentType);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentDisposition(@Nullable String contentDisposition) {
      contentDisposition(contentDisposition);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getContentDisposition() {
      return this.contentDisposition;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentLanguage(@Nullable String contentLanguage) {
      contentLanguage(contentLanguage);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getContentLanguage() {
      return this.contentLanguage;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentEncoding(@Nullable String contentEncoding) {
      contentEncoding(contentEncoding);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getContentEncoding() {
      return this.contentEncoding;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setExpires(@Nullable Date expires) {
      expires(expires);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Date getExpires() {
      return expires;
   }

   @Override
   public BaseMutableContentMetadata toBuilder() {
      return BaseMutableContentMetadata.fromContentMetadata(this);
   }

   public static BaseMutableContentMetadata fromContentMetadata(ContentMetadata in) {
      return (BaseMutableContentMetadata) new BaseMutableContentMetadata().contentType(in.getContentType())
               .contentLength(in.getContentLength()).contentMD5(in.getContentMD5()).contentDisposition(
                        in.getContentDisposition()).contentLanguage(in.getContentLanguage()).contentEncoding(
                        in.getContentEncoding()).expires(in.getExpires());
   }
}
