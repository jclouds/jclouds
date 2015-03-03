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
package org.jclouds.profitbricks.http.parser.ipblock;

import com.google.inject.Inject;
import org.jclouds.profitbricks.domain.IpBlock;
import org.jclouds.profitbricks.http.parser.publicip.PublicIpListResponseHandler;
import org.xml.sax.SAXException;

public class IpBlockResponseHandler extends BaseIpBlockResponseHandler<IpBlock> {

   private boolean done = false;

   @Inject
   IpBlockResponseHandler(PublicIpListResponseHandler publicIpListResponseHandler) {
      super(publicIpListResponseHandler);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (done)
         return;

      if (usePublicIpListParser)
         publicIpListResponseHandler.endElement(uri, localName, qName);
      else {
         setPropertyOnEndTag(qName);
         if ("return".equals(qName)) {
            done = true;
            builder.publicIps(publicIpListResponseHandler.getResult());
            builder.ips(ips);
         }
         clearTextBuffer();
      }

      if ("publicIps".equals(qName))
         usePublicIpListParser = false;
   }

   @Override
   public void reset() {
      this.builder = IpBlock.builder();
   }

   @Override
   public IpBlock getResult() {
      return builder.build();
   }

}
