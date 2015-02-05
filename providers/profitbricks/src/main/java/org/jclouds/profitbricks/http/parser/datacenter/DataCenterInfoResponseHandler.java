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

import java.util.List;

import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.domain.Storage;
import org.jclouds.profitbricks.http.parser.server.ServerInfoResponseHandler;
import org.jclouds.profitbricks.http.parser.storage.StorageInfoResponseHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class DataCenterInfoResponseHandler extends BaseDataCenterResponseHandler<DataCenter> {

   private final ServerInfoResponseHandler serverInfoResponseHandler;
   private final StorageInfoResponseHandler storageInfoResponseHandler;

   private final List<Server> servers = Lists.newArrayList();
   private final List<Storage> storages = Lists.newArrayList();

   private boolean done = false;
   private boolean useServerParser = false;
   private boolean useStorageParser = false;

   @Inject
   DataCenterInfoResponseHandler(ServerInfoResponseHandler serverInfoResponseHandler, StorageInfoResponseHandler storageInforResponseHandler) {
      this.serverInfoResponseHandler = serverInfoResponseHandler;
      this.storageInfoResponseHandler = storageInforResponseHandler;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if ("servers".equals(qName))
         useServerParser = true;
      else if ("storages".equals(qName))
         useStorageParser = true;
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
         serverInfoResponseHandler.characters(ch, start, length);
      else if (useStorageParser)
         storageInfoResponseHandler.characters(ch, start, length);
      else
         super.characters(ch, start, length);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (done)
         return;

      if ("servers".equals(qName)) {
         useServerParser = false;
         servers.add(serverInfoResponseHandler.getResult());
      } else if ("storages".equals(qName)) {
         useStorageParser = false;
         storages.add(storageInfoResponseHandler.getResult());
      }

      if (useServerParser)
         serverInfoResponseHandler.endElement(uri, localName, qName);
      else if (useStorageParser)
         storageInfoResponseHandler.endElement(uri, localName, qName);
      else {
         setPropertyOnEndTag(qName);
         if ("return".equals(qName)) {
            done = true;
            builder.servers(servers);
            builder.storages(storages);
         }
         clearTextBuffer();
      }
   }

   @Override
   public DataCenter getResult() {
      return builder.build();
   }

}
