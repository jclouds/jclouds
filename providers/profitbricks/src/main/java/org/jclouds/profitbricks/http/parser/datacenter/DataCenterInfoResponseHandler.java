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
package org.jclouds.profitbricks.http.parser.datacenter;

import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.http.parser.server.ServerListResponseHandler;
import org.jclouds.profitbricks.http.parser.storage.StorageListResponseHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.inject.Inject;

public class DataCenterInfoResponseHandler extends BaseDataCenterResponseHandler<DataCenter> {

   private final ServerListResponseHandler serverListResponseHandler;
   private final StorageListResponseHandler storageListResponseHandler;

   private boolean done = false;
   private boolean useServerParser = false;
   private boolean useStorageParser = false;

   @Inject
   DataCenterInfoResponseHandler(ServerListResponseHandler serverListResponseHandler, StorageListResponseHandler storageListResponseHandler) {
      this.serverListResponseHandler = serverListResponseHandler;
      this.storageListResponseHandler = storageListResponseHandler;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if ("servers".equals(qName))
         useServerParser = true;
      else if ("storages".equals(qName))
         useStorageParser = true;

      if (useServerParser)
         serverListResponseHandler.startElement(uri, localName, qName, attributes);
      else if (useStorageParser)
         storageListResponseHandler.startElement(uri, localName, qName, attributes);
      else
         super.startElement(uri, localName, qName, attributes);
   }

   @Override
   protected void setPropertyOnEndTag(String qName) {
      super.setPropertyOnEndTag(qName);
      if ("dataCenterName".equals(qName))
         builder.name(textToStringValue());
      else if ("location".equals(qName))
         builder.location(Location.fromId(textToStringValue()));
      else if ("provisioningState".equals(qName))
         builder.state(ProvisioningState.fromValue(textToStringValue()));
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      if (useServerParser)
         serverListResponseHandler.characters(ch, start, length);
      else if (useStorageParser)
         storageListResponseHandler.characters(ch, start, length);
      else
         super.characters(ch, start, length);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (done)
         return;

      if (useServerParser)
         serverListResponseHandler.endElement(uri, localName, qName);
      else if (useStorageParser)
         storageListResponseHandler.endElement(uri, localName, qName);
      else {
         setPropertyOnEndTag(qName);
         if ("return".equals(qName)) {
            done = true;
            builder.servers(serverListResponseHandler.getResult());
            builder.storages(storageListResponseHandler.getResult());
         }
         clearTextBuffer();
      }

      if ("servers".equals(qName))
         useServerParser = false;
      else if ("storages".equals(qName))
         useStorageParser = false;
   }

   @Override
   public DataCenter getResult() {
      return builder.build();
   }

}
