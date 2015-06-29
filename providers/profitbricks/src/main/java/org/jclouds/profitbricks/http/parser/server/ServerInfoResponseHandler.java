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
package org.jclouds.profitbricks.http.parser.server;

import com.google.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.http.parser.nic.NicListResponseHandler;
import org.jclouds.profitbricks.http.parser.storage.StorageListResponseHandler;
import org.xml.sax.SAXException;

public class ServerInfoResponseHandler extends BaseServerResponseHandler<Server> {

   private boolean done = false;

   @Inject
   ServerInfoResponseHandler(DateService dateService, StorageListResponseHandler storageListResponseHandler,
           NicListResponseHandler nicListResponseHandler) {
      super(dateService, storageListResponseHandler, nicListResponseHandler);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (done)
         return;

      if (useStorageParser)
         storageListResponseHandler.endElement(uri, localName, qName);
      else if (useNicParser)
         nicListResponseHandler.endElement(uri, localName, qName);
      else {
         setPropertyOnEndTag(qName);
         if ("return".equals(qName)) {
            done = true;
            builder
                    .dataCenter(dataCenterBuilder.build())
                    .storages(storageListResponseHandler.getResult())
                    .nics(nicListResponseHandler.getResult());
         }
         clearTextBuffer();
      }

      if ("connectedStorages".equals(qName))
         useStorageParser = false;
      else if ("nics".equals(qName))
         useNicParser = false;
   }

   @Override
   public Server getResult() {
      return builder.build();
   }

}
