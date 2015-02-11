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
package org.jclouds.s3.filters;

import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteStreams;
import org.jclouds.http.HttpException;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.BaseMutableContentMetadata;
import org.jclouds.io.payloads.BasePayload;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.ByteStreams.readBytes;
import static org.jclouds.s3.filters.Aws4SignerBase.hash;
import static org.jclouds.s3.filters.Aws4SignerBase.hex;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.CHUNK_SIGNATURE_HEADER;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.CHUNK_STRING_TO_SIGN_PREFIX;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.CLRF;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.FINAL_CHUNK;
import static org.jclouds.util.Strings2.toInputStream;

public class ChunkedUploadPayload extends BasePayload<Payload> {
   private static final byte[] TRAILER = CLRF.getBytes(UTF_8);

   private final Payload payload;
   private final int chunkedBlockSize;
   private final String timestamp;
   private final String scope;
   private final ByteProcessor<byte[]> hmacSHA256;
   private String lastComputedSignature;

   public ChunkedUploadPayload(Payload payload, int blockSize, String timestamp, String scope,
         ByteProcessor<byte[]> hmacSHA256, String seedSignature) {
      super(payload);
      this.payload = payload;
      this.chunkedBlockSize = blockSize;
      this.timestamp = timestamp;
      this.scope = scope;
      this.hmacSHA256 = hmacSHA256;
      this.lastComputedSignature = seedSignature;

      // init content metadata
      MutableContentMetadata contentMetadata = BaseMutableContentMetadata.fromContentMetadata(
            payload.getContentMetadata());
      long totalLength = Aws4SignerForChunkedUpload.calculateChunkedContentLength(
            payload.getContentMetadata().getContentLength(),
            chunkedBlockSize);
      contentMetadata.setContentLength(totalLength);
      this.setContentMetadata(contentMetadata);
   }

   /**
    * Returns a chunk for upload consisting of the signed 'header' or chunk
    * prefix plus the user data. The signature of the chunk incorporates the
    * signature of the previous chunk (or, if the first chunk, the signature of
    * the headers portion of the request).
    *
    * @param userDataLen The length of the user data contained in userData
    * @param userData   Contains the user data to be sent in the upload chunk
    * @return A new buffer of data for upload containing the chunk header plus
    * user data
    */
   protected byte[] constructSignedChunk(int userDataLen, byte[] userData) {
      // to keep our computation routine signatures simple, if the userData
      // buffer contains less data than it could, shrink it. Note the special case
      // to handle the requirement that we send an empty chunk to complete
      // our chunked upload.
      byte[] dataToChunk;
      if (userDataLen == 0) {
         dataToChunk = FINAL_CHUNK;
      } else {
         if (userDataLen < userData.length) {
            // shrink the chunkdata to fit
            dataToChunk = new byte[userDataLen];
            System.arraycopy(userData, 0, dataToChunk, 0, userDataLen);
         } else {
            dataToChunk = userData;
         }
      }

      // string(IntHexBase(chunk-size)) + ";chunk-signature=" + signature + \r\n + chunk-data + \r\n
      StringBuilder chunkHeader = new StringBuilder();

      // start with size of user data
      // IntHexBase(chunk-size)
      chunkHeader.append(Integer.toHexString(dataToChunk.length));

      // chunk-signature

      // nonsig-extension; we have none in these samples
      String nonsigExtension = "";

      // if this is the first chunk, we package it with the signing result
      // of the request headers, otherwise we use the cached signature
      // of the previous chunk

      // sig-extension
      StringBuilder buffer = new StringBuilder();
      buffer.append(CHUNK_STRING_TO_SIGN_PREFIX);
      buffer.append("\n");
      buffer.append(timestamp).append("\n");
      buffer.append(scope).append("\n");
      buffer.append(lastComputedSignature).append("\n");
      buffer.append(hex(hash(nonsigExtension))).append("\n");
      buffer.append(hex(hash(dataToChunk)));

      String chunkStringToSign = buffer.toString();

      // compute the V4 signature for the chunk
      String chunkSignature;
      try {
         chunkSignature = hex(readBytes(toInputStream(chunkStringToSign), hmacSHA256));
      } catch (IOException e) {
         throw new HttpException("hmac sha256 chunked signature error");
      }

      // cache the signature to include with the next chunk's signature computation
      lastComputedSignature = chunkSignature;

      // construct the actual chunk, comprised of the non-signed extensions, the
      // 'headers' we just signed and their signature, plus a newline then copy
      // that plus the user's data to a payload to be written to the request stream
      chunkHeader.append(nonsigExtension + CHUNK_SIGNATURE_HEADER + chunkSignature);
      chunkHeader.append(CLRF);

      byte[] header = chunkHeader.toString().getBytes(UTF_8);
      byte[] signedChunk = new byte[header.length + dataToChunk.length + TRAILER.length];
      System.arraycopy(header, 0, signedChunk, 0, header.length);
      // chunk-data
      System.arraycopy(dataToChunk, 0, signedChunk, header.length, dataToChunk.length);
      System.arraycopy(TRAILER, 0, signedChunk, header.length + dataToChunk.length, TRAILER.length);

      // this is the total data for the chunk that will be sent to the request stream
      return signedChunk;
   }

   @Override
   public void release() {
      this.payload.release();
   }

   @Override
   public boolean isRepeatable() {
      return this.payload.isRepeatable();
   }

   @Override
   public InputStream openStream() throws IOException {
      return new SequenceInputStream(new ChunkedInputStreamEnumeration(this.payload.openStream(), chunkedBlockSize));
   }

   private class ChunkedInputStreamEnumeration implements Enumeration<InputStream> {
      private final InputStream inputStream;
      private boolean lastChunked;
      private byte[] buffer;

      ChunkedInputStreamEnumeration(InputStream inputStream, int chunkedBlockSize) {
         this.inputStream = new BufferedInputStream(inputStream, chunkedBlockSize);
         buffer = new byte[chunkedBlockSize];
         lastChunked = false;
      }

      @Override
      public boolean hasMoreElements() {
         return !lastChunked;
      }

      @Override
      public InputStream nextElement() {
         int bytesRead;
         try {
            bytesRead = ByteStreams.read(inputStream, buffer, 0, buffer.length);
         } catch (IOException e) {
            // IO EXCEPTION
            throw new ChunkedUploadException("read from input stream error", e);
         }

         // buffer
         byte[] chunk;

         // ByteStreams.read(InputStream, byte[], int, int) returns the number of bytes read
         // InputStream.read(byte[], int, int) returns -1 if the end of the stream has been reached.
         if (bytesRead > 0) {
            // process into a chunk
            chunk = constructSignedChunk(bytesRead, buffer);
         } else {
            // construct last chunked block
            chunk = constructSignedChunk(0, buffer);
            lastChunked = true;
         }
         return new ByteArrayInputStream(chunk);
      }
   }

}
