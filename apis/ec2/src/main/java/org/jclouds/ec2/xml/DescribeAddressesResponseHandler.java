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
package org.jclouds.ec2.xml;

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.http.functions.ParseSax.HandlerForGeneratedRequestWithResult;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;
import org.xml.sax.Attributes;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

public class DescribeAddressesResponseHandler extends HandlerForGeneratedRequestWithResult<Set<PublicIpInstanceIdPair>> {

   @Resource
   protected Logger logger = Logger.NULL;
   private Set<PublicIpInstanceIdPair> pairs = Sets.newLinkedHashSet();
   private String ipAddress;
   private StringBuilder currentText = new StringBuilder();
   @Inject
   @Region
   Supplier<String> defaultRegion;
   private String instanceId;
   private final TagSetHandler tagSetHandler;
   private boolean inTagSet;
   private Map<String, String> tagResults;

   @Inject
   DescribeAddressesResponseHandler(final TagSetHandler tagSetHandler) {
      this.tagSetHandler = tagSetHandler;
   }

   @Override
   public void startElement(final String uri, final String name, final String qName, final Attributes attrs) {
      if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = true;
      }
      if (inTagSet) {
         tagSetHandler.startElement(uri, name, qName, attrs);
      }
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   @Override
   public void endElement(final String uri, final String name, final String qName) {
      if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = false;
         tagResults = tagSetHandler.getResult();
      } else if (inTagSet) {
         tagSetHandler.endElement(uri, name, qName);
      } else if (qName.equals("publicIp")) {
         ipAddress = currentOrNull();
      } else if (qName.equals("instanceId")) {
         instanceId = currentOrNull();
      } else if (qName.equals("item")) {
         String region = AWSUtils.findRegionInArgsOrNull(getRequest());
         if (region == null)
            region = defaultRegion.get();

         pairs.add(new PublicIpInstanceIdPair(region, ipAddress, instanceId, tagResults));
         ipAddress = null;
         instanceId = null;
         tagResults = null;
      }

      currentText.setLength(0);
   }

   @Override
   public void characters(final char[] ch, final int start, final int length) {
      if (inTagSet) {
         tagSetHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

   @Override
   public Set<PublicIpInstanceIdPair> getResult() {
      return pairs;
   }

}
