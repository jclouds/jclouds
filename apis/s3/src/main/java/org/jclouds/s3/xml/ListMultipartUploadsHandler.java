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
package org.jclouds.s3.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;

import java.util.Date;

import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.s3.domain.CanonicalUser;
import org.jclouds.s3.domain.ListMultipartUploadsResponse;
import org.jclouds.s3.domain.ObjectMetadata;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableList;

public final class ListMultipartUploadsHandler extends ParseSax.HandlerWithResult<ListMultipartUploadsResponse> {
   private String bucket;
   private String keyMarker;
   private String uploadIdMarker;
   private String nextKeyMarker;
   private String nextUploadIdMarker;
   private int maxUploads;
   private boolean isTruncated;
   private final ImmutableList.Builder<ListMultipartUploadsResponse.Upload> uploads = ImmutableList.builder();

   private String key;
   private String uploadId;
   private String id;
   private String displayName;
   private CanonicalUser initiator;
   private CanonicalUser owner;
   private ObjectMetadata.StorageClass storageClass;
   private Date initiated;

   private final DateService dateParser;
   private final StringBuilder currentText = new StringBuilder();
   private boolean inUpload;
   private boolean inInitiator;
   private boolean inOwner;

   @Inject
   public ListMultipartUploadsHandler(DateService dateParser) {
      this.dateParser = dateParser;
   }

   public ListMultipartUploadsResponse getResult() {
      return ListMultipartUploadsResponse.create(bucket, keyMarker, uploadIdMarker, nextKeyMarker, nextUploadIdMarker, maxUploads, isTruncated, uploads.build());
   }

   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (qName.equals("Upload")) {
         inUpload = true;
      } else if (qName.equals("Initiator")) {
         inInitiator = true;
      } else if (qName.equals("Owner")) {
         inOwner = true;
      }
      currentText.setLength(0);
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Bucket")) {
         bucket = currentOrNull(currentText);
      } else if (qName.equals("KeyMarker")) {
         keyMarker = currentOrNull(currentText);
      } else if (qName.equals("UploadIdMarker")) {
         uploadIdMarker = currentOrNull(currentText);
      } else if (qName.equals("NextKeyMarker")) {
         nextKeyMarker = currentOrNull(currentText);
      } else if (qName.equals("NextUploadIdMarker")) {
         nextUploadIdMarker = currentOrNull(currentText);
      } else if (qName.equals("MaxUploads")) {
         maxUploads = Integer.parseInt(currentOrNull(currentText));
      } else if (qName.equals("IsTruncated")) {
         isTruncated = Boolean.parseBoolean(currentOrNull(currentText));
      } else if (qName.equals("Key")) {
         key = currentOrNull(currentText);
      } else if (qName.equals("UploadId")) {
         uploadId = currentOrNull(currentText);
      } else if (qName.equals("StorageClass")) {
         storageClass = ObjectMetadata.StorageClass.valueOf(currentOrNull(currentText));
      } else if (qName.equals("Initiated")) {
         initiated = dateParser.iso8601DateOrSecondsDateParse(currentOrNull(currentText));
      } else if (qName.equals("Upload")) {
         uploads.add(ListMultipartUploadsResponse.Upload.create(key, uploadId, initiator, owner, storageClass, initiated));
         key = null;
         uploadId = null;
         id = null;
         displayName = null;
         initiator = null;
         owner = null;
         storageClass = null;
         initiated = null;
         inUpload = false;
      } else if (qName.equals("Initiator")) {
         initiator = new CanonicalUser(id, displayName);
         id = null;
         displayName = null;
         inInitiator = false;
      } else if (qName.equals("Owner")) {
         owner = new CanonicalUser(id, displayName);
         id = null;
         displayName = null;
         inOwner = false;
      }
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
