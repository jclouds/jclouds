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
package org.jclouds.profitbricks.http.parser.publicip;

import com.google.common.collect.Lists;
import java.util.List;
import org.jclouds.profitbricks.domain.IpBlock.PublicIp;
import org.xml.sax.SAXException;

public class PublicIpListResponseHandler extends BasePublicIpResponseHandler<List<PublicIp>> {

   private List<PublicIp> publicIps;

   PublicIpListResponseHandler() {
      this.publicIps = Lists.newArrayList();
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      setPropertyOnEndTag(qName);

      if ("publicIps".equals(qName))
         publicIps.add(builder.build());
      clearTextBuffer();
   }

   @Override
   public void reset() {
      this.publicIps = Lists.newArrayList();
   }

   @Override
   public List<PublicIp> getResult() {
      return publicIps;
   }

}
