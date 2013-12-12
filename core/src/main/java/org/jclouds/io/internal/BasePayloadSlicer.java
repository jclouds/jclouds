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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.inject.Singleton;

import org.jclouds.io.ContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.io.payloads.BaseMutableContentMetadata;
import org.jclouds.io.payloads.ByteArrayPayload;
import org.jclouds.io.payloads.InputStreamPayload;
import org.jclouds.io.payloads.InputStreamSupplierPayload;

import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BasePayloadSlicer implements PayloadSlicer {

   public static class PayloadIterator implements Iterable<Payload>, Iterator<Payload> {

      private final InputStream input;
      private final ContentMetadata metaData;
      private Payload nextPayload;
      private final int readLen;

      public PayloadIterator(InputStream input, ContentMetadata meta) {
         this.input = checkNotNull(input, "input");
         this.metaData = checkNotNull(meta, "meta");
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
         throw new UnsupportedOperationException();
      }

      @Override
      public Iterator<Payload> iterator() {
         return this;
      }

      private Payload getNextPayload() {
         byte[] content = new byte[readLen];
         int read = 0;

         try {
            if ((read = input.read(content)) == -1) {
               return null;
            }
         } catch (IOException e) {
            throw Throwables.propagate(e);
         }

         return createPayload((content.length == read) ? content : Arrays.copyOf(content, read));
      }

      private Payload createPayload(byte[] content) {
         Payload payload = null;

         if (content.length > 0) {
            payload = new ByteArrayPayload(content);
            ContentMetadata cm = metaData.toBuilder().contentLength((long)content.length).contentMD5(null).build();
            payload.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(cm));
         }

         return payload;
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
      } else {
         returnVal = doSlice(input, offset, length);
      }
      return copyMetadataAndSetLength(input, returnVal, length);
   }

   protected Payload doSlice(Payload content, long offset, long length) {
      return doSlice((InputSupplier<? extends InputStream>) content, offset, length);
   }

   protected Payload doSlice(String content, long offset, long length) {
      return doSlice(content.getBytes(), offset, length);
   }

   protected Payload doSlice(File content, long offset, long length) {
      return doSlice(Files.newInputStreamSupplier(content), offset, length);
   }

   protected Payload doSlice(InputStream content, long offset, long length) {
      try {
         ByteStreams.skipFully(content, offset);
      } catch (IOException ioe) {
         throw Throwables.propagate(ioe);
      }
      return new InputStreamPayload(ByteStreams.limit(content, length));
   }

   protected Payload doSlice(InputSupplier<? extends InputStream> content, long offset, long length) {
      return new InputStreamSupplierPayload(ByteStreams.slice(content, offset, length));
   }

   protected Payload doSlice(byte[] content, long offset, long length) {
      Payload returnVal;
      checkArgument(offset <= Integer.MAX_VALUE, "offset is too big for an array");
      checkArgument(length <= Integer.MAX_VALUE, "length is too big for an array");
      returnVal = new InputStreamSupplierPayload(
            ByteStreams.newInputStreamSupplier(content, (int) offset, (int) length));
      return returnVal;
   }

   protected Payload copyMetadataAndSetLength(Payload input, Payload returnVal, long length) {
      returnVal.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(input.getContentMetadata()
            .toBuilder().contentLength(length).contentMD5(null).build()));
      return returnVal;
   }

   @Override
   public Iterable<Payload> slice(Payload input, long size) {
      checkNotNull(input, "input");
      checkArgument(size >= 0, "size must be non-negative but was: %s", size);

      ContentMetadata meta = BaseMutableContentMetadata.fromContentMetadata(input.getContentMetadata())
                                                       .toBuilder()
                                                       .contentLength(size)
                                                       .contentMD5(null)
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
      } else {
         return doSlice(input, meta);
      }

   }

   protected Iterable<Payload> doSlice(Payload input, ContentMetadata meta) {
      return doSlice(input.getInput(), meta);
   }

   protected Iterable<Payload> doSlice(String rawContent, ContentMetadata meta) {
      try {
         return doSlice(rawContent.getBytes("UTF-8"), meta);
      } catch (UnsupportedEncodingException e) {
         throw Throwables.propagate(e);
      }
   }

   protected Iterable<Payload> doSlice(byte[] rawContent, ContentMetadata meta) {
      return doSlice(new ByteArrayInputStream(rawContent), meta);
   }

   protected Iterable<Payload> doSlice(File rawContent, ContentMetadata meta) {
      try {
         return doSlice(new FileInputStream(rawContent), meta);
      } catch (FileNotFoundException e) {
         throw Throwables.propagate(e);
      }
   }

   protected Iterable<Payload> doSlice(InputStream rawContent, ContentMetadata meta) {
      return new PayloadIterator(rawContent, meta);
   }

}
