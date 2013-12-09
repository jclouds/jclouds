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
package org.jclouds.blobstore.domain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import org.jclouds.blobstore.domain.internal.BlobBuilderImpl;
import org.jclouds.io.Payload;

import com.google.common.io.ByteSource;
import com.google.inject.ImplementedBy;

/**
 * 
 * In case the name was confusing, this indeed builds a Blob.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(BlobBuilderImpl.class)
public interface BlobBuilder {
   /**
    * @param name
    *           The name of the {@link Blob}. Typically refers to an http path.
    */
   BlobBuilder name(String name);

   /**
    * @param type
    *           overrides default type of {@link StorageType#BLOB}
    */
   BlobBuilder type(StorageType type);

   /**
    * @param userMetadata
    *           User defined metadata associated with this {@link Blob}.
    * 
    */
   BlobBuilder userMetadata(Map<String, String> userMetadata);

   /**
    * 
    * @param payload
    *           payload you wish to construct the {@link Blob} with.
    */
   PayloadBlobBuilder payload(Payload payload);

   /**
    * 
    * @param payload
    *           payload you wish to construct the {@link Blob} with.
    */
   PayloadBlobBuilder payload(InputStream payload);

   /**
    * If you are creating a blob only for signing, use this. {@see BlobRequestSigner}
    */
   PayloadBlobBuilder forSigning();

   /**
    * 
    * @param payload
    *           payload you wish to construct the {@link Blob} with.
    * @deprecated see payload(ByteSource.wrap(byte[])
    */
   @Deprecated
   PayloadBlobBuilder payload(byte[] payload);

   /**
    * @param payload payload you wish to construct the {@link Blob} with.
    */
   PayloadBlobBuilder payload(ByteSource byteSource);

   /**
    * 
    * @param payload
    *           payload you wish to construct the {@link Blob} with.
    * @deprecated see payload(ByteSource.wrap(String.getBytes()))
    */
   @Deprecated
   PayloadBlobBuilder payload(String payload);

   /**
    * 
    * @param payload
    *           payload you wish to construct the {@link Blob} with.
    * @deprecated see payload(Files.asByteSource(File))
    */
   @Deprecated
   PayloadBlobBuilder payload(File payload);

   /**
    * This makes a blob from the currently configured parameters.
    * 
    * @return a new blob from the current parameters
    */
   Blob build();

   public interface PayloadBlobBuilder extends BlobBuilder {

      PayloadBlobBuilder contentLength(long contentLength);

      PayloadBlobBuilder contentMD5(byte[] md5);

      PayloadBlobBuilder contentType(String contentType);

      PayloadBlobBuilder contentDisposition(String contentDisposition);

      PayloadBlobBuilder contentLanguage(String contentLanguage);

      PayloadBlobBuilder contentEncoding(String contentEncoding);

      PayloadBlobBuilder expires(Date expires);

      /**
       * @deprecated Callers should instead call BlobBuilder.contentMD5,
       * usually with the results from Guava Hashing.md5().
       * @see Payloads#calculateMD5
       */
      @Deprecated
      PayloadBlobBuilder calculateMD5() throws IOException;

   }
}
