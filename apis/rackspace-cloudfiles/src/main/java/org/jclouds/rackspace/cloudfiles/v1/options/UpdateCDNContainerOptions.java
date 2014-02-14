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
package org.jclouds.rackspace.cloudfiles.v1.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_ENABLED;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_LOG_RETENTION;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_TTL;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_TTL_MAX;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_TTL_MIN;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Contains options supported in the REST API for updating CDN containers.
 * 
 * <a href=
 *    "http://docs.rackspace.com/files/api/v1/cf-devguide/content/Update_CDN-Enabled_Container_Metadata-d1e2787.html">
 *    Update CDN container</a>.
 *
 * @author Jeremy Daggett
 */
public class UpdateCDNContainerOptions extends BaseHttpRequestOptions {
   public static final UpdateCDNContainerOptions NONE = new UpdateCDNContainerOptions();

   /** 
    * Updates TTL
    */
   public UpdateCDNContainerOptions ttl(int ttl) {
      checkState(ttl >= Integer.valueOf(CDN_TTL_MIN), "ttl must be >= " + CDN_TTL_MIN);
      checkState(ttl <= Integer.valueOf(CDN_TTL_MAX), "ttl must be <= " + CDN_TTL_MAX);
      headers.put(CDN_TTL, Integer.toString(ttl));
      return this;
   }

   /** 
    * Enables or disables log retention
    */
   public UpdateCDNContainerOptions logRetention(boolean logRetention) {
      headers.put(CDN_LOG_RETENTION, Boolean.toString(logRetention));
      return this;
   }

   /** 
    * Enables or disables the CDN Container
    * API to enable disable - is this necessary?
    */
   public UpdateCDNContainerOptions enabled(boolean enabled) {
      headers.put(CDN_ENABLED, Boolean.toString(enabled));
      return this;
   }
   
   /**
    * Sets the index page for the Static Website.
    */
   public UpdateCDNContainerOptions staticWebsiteIndexPage(String indexPage) {
      checkNotNull(indexPage, "index page cannot be null");
      headers.put("indexPage", indexPage);
      return this;
   }

   /**
    * Sets the error page for the Static Website.
    */
   public UpdateCDNContainerOptions staticWebsiteErrorPage(String errorPage) {
      checkNotNull(errorPage, "error page cannot be null");
      headers.put("errorPage", errorPage);
      return this;
   }

   /**
    * Enables or disables listings for the Static Website.
    */
   public UpdateCDNContainerOptions staticWebsiteListings(boolean listings) {
      headers.put("enableListings", Boolean.toString(listings));
      return this;
   }
   
   /**
    * Sets the CSS pages for the Static Website.
    */
   public UpdateCDNContainerOptions staticWebsiteListingsCSS(String listingsCSS) {
      checkNotNull(listingsCSS, "listingsCSS page cannot be null");
      headers.put("limit", listingsCSS);
      return this;
   }

   public static class Builder {
      /** @see UpdateCDNContainerOptions#ttl */
      public static UpdateCDNContainerOptions ttl(int ttl) {
         UpdateCDNContainerOptions options = new UpdateCDNContainerOptions();
         return options.ttl(ttl);
      }

      /** 
       * @see UpdateCDNContainerOptions#logRetention 
       */
      public static UpdateCDNContainerOptions logRetention(boolean logRetention) {
         UpdateCDNContainerOptions options = new UpdateCDNContainerOptions();
         return options.logRetention(logRetention);
      }

      /** 
       * @see UpdateCDNContainerOptions#enabled
       */
      public static UpdateCDNContainerOptions enabled(boolean enabled) {
         UpdateCDNContainerOptions options = new UpdateCDNContainerOptions();
         return options.enabled(enabled);
      }

      /** 
       * @see UpdateCDNContainerOptions#staticWebsiteIndexPage 
       */
      public static UpdateCDNContainerOptions staticWebsiteIndexPage(String indexPage) {
         UpdateCDNContainerOptions options = new UpdateCDNContainerOptions();
         return options.staticWebsiteIndexPage(indexPage);
      }

      /** 
       * @see UpdateCDNContainerOptions#staticWebsiteErrorPage 
       */
      public static UpdateCDNContainerOptions staticWebsiteErrorPage(String errorPage) {
         UpdateCDNContainerOptions options = new UpdateCDNContainerOptions();
         return options.staticWebsiteErrorPage(errorPage);
      }

      /** 
       * @see UpdateCDNContainerOptions#staticWebsiteListings 
       */
      public static UpdateCDNContainerOptions staticWebsiteListings(boolean enabled) {
         UpdateCDNContainerOptions options = new UpdateCDNContainerOptions();
         return options.staticWebsiteListings(enabled);
      }

      /** 
       * @see UpdateCDNContainerOptions#staticWebsiteListingsCSS 
       */
      public static UpdateCDNContainerOptions staticWebsiteListingsCSS(String cssPage) {
         UpdateCDNContainerOptions options = new UpdateCDNContainerOptions();
         return options.staticWebsiteListingsCSS(cssPage);
      }
   }

   public static Builder builder() {
      return new Builder();
   }
}
