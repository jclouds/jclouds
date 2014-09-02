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
package org.jclouds.io.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.inject.Singleton;

import org.jclouds.io.ContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.io.payloads.BaseMutableContentMetadata;
import org.jclouds.io.payloads.ByteSourcePayload;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.hash.HashCode;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

@Singleton
public class BasePayloadSlicer implements PayloadSlicer {

   private static class InputStreamPayloadIterator implements Iterable<Payload>, Iterator<Payload> {

      private final InputStream input;
      private final ContentMetadata metaData;
      private Payload nextPayload;
      private final int readLen;

      InputStreamPayloadIterator(InputStream input, ContentMetadata metaData) {
         this.input = checkNotNull(input, "input");
         this.metaData = checkNotNull(metaData, "metaData");
         this.readLen = checkNotNull(this.metaData.getContentLength(), "content-length").intValue();

         this.nextPayload = getNextPayload();
      }

      @Override
      public boolean hasNext() {
         return nextPayload != null;
      }

      @Override
      public Payload next() {
         Payload payload;

         if (!hasNext())
            throw new NoSuchElementException();

         payload = nextPayload;
         nextPayload = getNextPayload();

         return payload;
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException("Payload iterator does not support removal");
      }

      @Override
      public Iterator<Payload> iterator() {
         return this;
      }

      private Payload getNextPayload() {
         byte[] content = new byte[readLen];
         int offset = 0;

         try {
            while (true) {
               int read = input.read(content, offset, readLen - offset);
               if (read <= 0) {
                  if (offset == 0) {
                     return null;
                  } else {
                     break;
                  }
               }
               offset += read;
            }
         } catch (IOException e) {
            throw Throwables.propagate(e);
         }

         return createPayload((content.length == offset) ? content : Arrays.copyOf(content, offset));
      }

      private Payload createPayload(byte[] content) {
         Payload payload = null;

         if (content.length > 0) {
            payload = Payloads.newByteArrayPayload(content);
            ContentMetadata cm = metaData.toBuilder().contentLength((long)content.length).contentMD5((HashCode) null).build();
            payload.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(cm));
         }

         return payload;
      }

   }

   private static class ByteSourcePayloadIterator implements Iterable<Payload>, Iterator<Payload> {
      private final ByteSource input;
      private final ContentMetadata metaData;
      private Payload nextPayload;
      private long offset = 0;
      private final long readLen;

      ByteSourcePayloadIterator(ByteSource input, ContentMetadata metaData) {
         this.input = checkNotNull(input, "input");
         this.metaData = checkNotNull(metaData, "metaData");
         this.readLen = checkNotNull(this.metaData.getContentLength(), "content-length").longValue();
         this.nextPayload = getNextPayload();
      }

      @Override
      public boolean hasNext() {
         return nextPayload != null;
      }

      @Override
      public Payload next() {
         if (!hasNext()) {
            throw new NoSuchElementException();
         }

         Payload payload = nextPayload;
         nextPayload = getNextPayload();

         return payload;
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException("Payload iterator does not support removal");
      }

      @Override
      public Iterator<Payload> iterator() {
         return this;
      }

      private Payload getNextPayload() {
         ByteSource byteSource;
         long byteSourceSize;
         try {
            if (offset >= input.size()) {
               return null;
            }
            byteSource = input.slice(offset, readLen);
            byteSourceSize = byteSource.size();
         } catch (IOException e) {
            throw Throwables.propagate(e);
         }

         Payload nextPayload = new ByteSourcePayload(byteSource);
         ContentMetadata cm = metaData.toBuilder()
               .contentLength(byteSourceSize)
               .contentMD5((HashCode) null)
               .build();
         nextPayload.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(cm));
         offset += byteSourceSize;
         return nextPayload;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Payload slice(Payload input, long offset, long length) {
      checkNotNull(input);
      checkArgument(offset >= 0, "offset is negative");
      checkArgument(length >= 0, "length is negative");
      Payload returnVal;
      if (input.getRawContent() instanceof File) {
         returnVal = doSlice((File) input.getRawContent(), offset, length);
      } else if (input.getRawContent() instanceof String) {
         returnVal = doSlice((String) input.getRawContent(), offset, length);
      } else if (input.getRawContent() instanceof byte[]) {
         returnVal = doSlice((byte[]) input.getRawContent(), offset, length);
      } else if (input.getRawContent() instanceof InputStream) {
         returnVal = doSlice((InputStream) input.getRawContent(), offset, length);
      } else if (input.getRawContent() instanceof ByteSource) {
         returnVal = doSlice((ByteSource) input.getRawContent(), offset, length);
      } else {
         returnVal = doSlice(input, offset, length);
      }
      return copyMetadataAndSetLength(input, returnVal, length);
   }

   protected Payload doSlice(Payload content, long offset, long length) {
      return doSlice(content.getInput(), offset, length);
   }

   protected Payload doSlice(String content, long offset, long length) {
      return doSlice(content.getBytes(), offset, length);
   }

   protected Payload doSlice(File content, long offset, long length) {
      return doSlice(Files.asByteSource(content), offset, length);
   }

   protected Payload doSlice(InputStream content, long offset, long length) {
      try {
         ByteStreams.skipFully(content, offset);
      } catch (IOException ioe) {
         throw Throwables.propagate(ioe);
      }
      return Payloads.newInputStreamPayload(ByteStreams.limit(content, length));
   }

   protected Payload doSlice(ByteSource content, long offset, long length) {
      return Payloads.newByteSourcePayload(content.slice(offset, length));
   }

   protected Payload doSlice(byte[] content, long offset, long length) {
      checkArgument(offset <= Integer.MAX_VALUE, "offset is too big for an array");
      checkArgument(length <= Integer.MAX_VALUE, "length is too big for an array");
      // TODO(adriancole): Make ByteArrayPayload carry offset, length as opposed to wrapping here.
      return Payloads.newByteSourcePayload(ByteSource.wrap(content).slice(offset, length));
   }

   protected Payload copyMetadataAndSetLength(Payload input, Payload returnVal, long length) {
      returnVal.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(input.getContentMetadata()
            .toBuilder().contentLength(length).contentMD5((HashCode) null).build()));
      return returnVal;
   }

   @Override
   public Iterable<Payload> slice(Payload input, long size) {
      checkNotNull(input, "input");
      checkArgument(size >= 0, "size must be non-negative but was: %s", size);

      ContentMetadata meta = BaseMutableContentMetadata.fromContentMetadata(input.getContentMetadata())
                                                       .toBuilder()
                                                       .contentLength(size)
                                                       .contentMD5((HashCode) null)
                                                       .build();
      Object rawContent = input.getRawContent();
      if (rawContent instanceof File) {
         return doSlice((File) rawContent, meta);
      } else if (rawContent instanceof String) {
         return doSlice((String) rawContent, meta);
      } else if (rawContent instanceof byte[]) {
         return doSlice((byte[]) rawContent, meta);
      } else if (rawContent instanceof InputStream) {
         return doSlice((InputStream) rawContent, meta);
      } else if (rawContent instanceof ByteSource) {
         return doSlice((ByteSource) rawContent, meta);
      } else {
         return doSlice(input, meta);
      }

   }

   protected Iterable<Payload> doSlice(Payload input, ContentMetadata meta) {
      return doSlice(input.getInput(), meta);
   }

   protected Iterable<Payload> doSlice(String rawContent, ContentMetadata meta) {
      return doSlice(ByteSource.wrap(rawContent.getBytes(Charsets.UTF_8)), meta);
   }

   protected Iterable<Payload> doSlice(byte[] rawContent, ContentMetadata meta) {
      return doSlice(ByteSource.wrap(rawContent), meta);
   }

   protected Iterable<Payload> doSlice(File rawContent, ContentMetadata meta) {
      return doSlice(Files.asByteSource(rawContent), meta);
   }

   protected Iterable<Payload> doSlice(InputStream rawContent, ContentMetadata meta) {
      return new InputStreamPayloadIterator(rawContent, meta);
   }

   protected Iterable<Payload> doSlice(ByteSource rawContent, ContentMetadata meta) {
      return new ByteSourcePayloadIterator(rawContent, meta);
   }
}
