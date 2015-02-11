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

package org.jclouds.s3.filters;

import com.google.common.collect.ImmutableMap;

import java.net.URI;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AwsHostNameUtils {

   private static final Pattern S3_ENDPOINT_PATTERN = Pattern.compile("^(?:.+\\.)?s3[.-]([a-z0-9-]+)$");

   private static final Pattern STANDARD_CLOUDSEARCH_ENDPOINT_PATTERN = Pattern.compile("^(?:.+\\.)?([a-z0-9-]+)\\.cloudsearch$");

   private static final Pattern EXTENDED_CLOUDSEARCH_ENDPOINT_PATTERN = Pattern.compile("^(?:.+\\.)?([a-z0-9-]+)\\.cloudsearch\\..+");

   private static final ImmutableMap<String, String> HOST_REGEX_TO_REGION_MAPPINGS = new ImmutableMap.Builder<String, String>()
      .put("(.+\\.)?s3\\.amazonaws\\.com", "us-east-1")
      .put("(.+\\.)?s3-external-1\\.amazonaws\\.com", "us-east-1")
      .put("(.+\\.)?s3-fips-us-gov-west-1\\.amazonaws\\.com", "us-gov-west-1")
      .build();

   /**
    * Attempts to parse the region name from an endpoint based on conventions
    * about the endpoint format.
    *
    * @param host      the hostname to parse
    * @param serviceHint an optional hint about the service for the endpoint
    * @return the region parsed from the hostname, or
    * &quot;us-east-1&quot; if no region information
    * could be found
    */
   public static String parseRegionName(final String host, final String serviceHint) {

      String regionNameInInternalConfig = parseRegionNameByInternalConfig(host);
      if (regionNameInInternalConfig != null) {
         return regionNameInInternalConfig;
      }

      if (host.endsWith(".amazonaws.com")) {
         int index = host.length() - ".amazonaws.com".length();
         return parseStandardRegionName(host.substring(0, index));
      }

      if (serviceHint != null) {
         if (serviceHint.equals("cloudsearch")
            && !host.startsWith("cloudsearch.")) {

            // CloudSearch domains use the nonstandard domain format
            // [domain].[region].cloudsearch.[suffix].

            Matcher matcher = EXTENDED_CLOUDSEARCH_ENDPOINT_PATTERN
               .matcher(host);

            if (matcher.matches()) {
               return matcher.group(1);
            }
         }

         // If we have a service hint, look for 'service.[region]' or
         // 'service-[region]' in the endpoint's hostname.
         Pattern pattern = Pattern.compile(
            "^(?:.+\\.)?"
               + Pattern.quote(serviceHint)
               + "[.-]([a-z0-9-]+)\\."
         );

         Matcher matcher = pattern.matcher(host);
         if (matcher.find()) {
            return matcher.group(1);
         }
      }

      // Endpoint is totally non-standard; guess us-east-1 for lack of a
      // better option.

      return "us-east-1";
   }

   /**
    * Parses the region name from a standard (*.amazonaws.com) endpoint.
    *
    * @param fragment the portion of the endpoint excluding
    *             &quot;.amazonaws.com&quot;
    * @return the parsed region name (or &quot;us-east-1&quot; as a
    * best guess if we can't tell for sure)
    */
   private static String parseStandardRegionName(final String fragment) {

      Matcher matcher = S3_ENDPOINT_PATTERN.matcher(fragment);
      if (matcher.matches()) {
         // host was 'bucket.s3-[region].amazonaws.com'.
         return matcher.group(1);
      }

      matcher = STANDARD_CLOUDSEARCH_ENDPOINT_PATTERN.matcher(fragment);
      if (matcher.matches()) {
         // host was 'domain.[region].cloudsearch.amazonaws.com'.
         return matcher.group(1);
      }

      int index = fragment.lastIndexOf('.');
      if (index == -1) {
         // host was 'service.amazonaws.com', guess us-east-1
         // for lack of a better option.
         return "us-east-1";
      }

      // host was 'service.[region].amazonaws.com'.
      String region = fragment.substring(index + 1);

      // Special case for iam.us-gov.amazonaws.com, which is actually
      // us-gov-west-1.
      if ("us-gov".equals(region)) {
         region = "us-gov-west-1";
      }

      return region;
   }

   /**
    * @return the configured region name if the given host name matches any of
    * the host-to-region mappings in the internal config; otherwise
    * return null.
    */
   private static String parseRegionNameByInternalConfig(String host) {
      for (Map.Entry<String, String> mapping : HOST_REGEX_TO_REGION_MAPPINGS.entrySet()) {
         String hostNameRegex = mapping.getKey();
         if (host.matches(hostNameRegex)) {
            return mapping.getValue();
         }
      }

      return null;
   }

   /**
    * Parses the service name from an endpoint. Can only handle endpoints of
    * the form 'service.[region.]amazonaws.com'.
    * or
    * bucket.s3.[region.]awazonaws.com
    */
   public static String parseServiceName(URI endpoint) {
      String host = endpoint.getHost();

      if (!host.endsWith(".amazonaws.com") && !host.endsWith(".amazonaws.com.cn")) {
         return "s3";  // cannot parse name, assume s3
      }

      String serviceAndRegion = host.substring(0, host.indexOf(".amazonaws.com"));

      // Special cases for S3 endpoints with bucket names embedded.
      if (serviceAndRegion.endsWith(".s3") || S3_ENDPOINT_PATTERN.matcher(serviceAndRegion).matches()) {
         return "s3";
      }

      char separator = '.';

      // If we don't detect a separator between service name and region, then
      // assume that the region is not included in the hostname, and it's only
      // the service name (ex: "http://iam.amazonaws.com").
      if (serviceAndRegion.indexOf(separator) == -1) {
         return serviceAndRegion;
      }

      return serviceAndRegion.substring(0, serviceAndRegion.indexOf(separator));
   }
}
