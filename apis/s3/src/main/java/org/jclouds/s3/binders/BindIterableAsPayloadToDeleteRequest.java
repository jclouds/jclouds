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
package org.jclouds.s3.binders;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.hash.Hashing.md5;

import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.jamesmurty.utils.XMLBuilder;

import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.rest.Binder;

public class BindIterableAsPayloadToDeleteRequest implements Binder {

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input is null") instanceof Iterable,
         "this binder is only valid for an Iterable");
      checkNotNull(request, "request is null");

      Iterable<String> keys = (Iterable<String>) input;
      checkArgument(!Iterables.isEmpty(keys), "The list of keys should not be empty.");

      String content;
      try {
         XMLBuilder rootBuilder = XMLBuilder.create("Delete");
         for (String key : keys) {
            XMLBuilder ownerBuilder = rootBuilder.elem("Object");
            XMLBuilder keyBuilder = ownerBuilder.elem("Key").text(key);
         }

         Properties outputProperties = new Properties();
         outputProperties.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
         content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                rootBuilder.asString(outputProperties);
      } catch (ParserConfigurationException pce) {
         throw Throwables.propagate(pce);
      } catch (TransformerException te) {
         throw Throwables.propagate(te);
      }

      Payload payload = Payloads.newStringPayload(content);
      payload.getContentMetadata().setContentType(MediaType.TEXT_XML);
      byte[] md5 = md5().hashString(content, UTF_8).asBytes();
      payload.getContentMetadata().setContentMD5(md5);
      request.setPayload(payload);
      return request;
   }
}
