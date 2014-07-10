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

import static com.google.common.collect.Lists.newArrayList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Collections;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;

public class MultipartForm extends BasePayload<Iterable<? extends Part>> {
   public static final String BOUNDARY = "--JCLOUDS--";
   private static final String rn = "\r\n";
   private static final String dd = "--";

   private final String boundary;
   private final Iterable<? extends Part> content;
   private final boolean isRepeatable;

   @SuppressWarnings("unchecked")
   public MultipartForm(String boundary, Iterable<? extends Part> content) {
      super(content);
      this.boundary = boundary;
      this.content = content;

      getContentMetadata().setContentType("multipart/form-data; boundary=" + boundary);
      String boundaryrn = boundary + rn;
      boolean isRepeatable = true;
      long contentLength = 0;
      for (Part part : content) {
         if (!part.isRepeatable())
            isRepeatable = false;
         contentLength += part.getContentMetadata().getContentLength()
            + createHeaders(boundaryrn, part).length()
            + createRn().length();
      }
      contentLength += createFooter(boundary).length();
      getContentMetadata().setContentLength(contentLength);
      this.isRepeatable = isRepeatable;
   }

   public MultipartForm(String boundary, Part... parts) {
      this(boundary, newArrayList(parts));
   }

   public MultipartForm(Part... parts) {
      this(BOUNDARY, parts);
   }

   private String createRn() {
      return rn;
   }

   private String createHeaders(String boundaryrn, Part part) {
      StringBuilder builder = new StringBuilder(dd).append(boundaryrn);
      for (Entry<String, String> entry : part.getHeaders().entries()) {
         String header = String.format("%s: %s%s", entry.getKey(), entry.getValue(), rn);
         builder.append(header);
      }
      builder.append(rn);
      return builder.toString();
   }

   private static String createFooter(String boundary) {
      return dd + boundary + dd + rn;
   }

   @Override
   public InputStream openStream() throws IOException {
      String boundaryrn = boundary + rn;
      ImmutableList.Builder<InputStream> builder = ImmutableList.builder();
      for (Part part : content) {
         builder.add(new ByteArrayInputStream(createHeaders(boundaryrn, part).getBytes()))
            .add(part.openStream())
            .add(new ByteArrayInputStream(createRn().getBytes()));
      }
      builder.add(new ByteArrayInputStream(createFooter(boundary).getBytes()));
      return new SequenceInputStream(Collections.enumeration(builder.build()));
   }

   @Override
   public boolean isRepeatable() {
      return isRepeatable;
   }

   @Override
   public void release() {
      for (Part part : content)
         part.release();
   }

}
