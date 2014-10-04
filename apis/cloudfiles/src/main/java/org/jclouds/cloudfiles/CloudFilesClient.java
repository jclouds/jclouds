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
package org.jclouds.cloudfiles;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.blobstore.BlobStoreFallbacks.NullOnContainerNotFound;
import static org.jclouds.cloudfiles.reference.CloudFilesHeaders.CDN_CONTAINER_PURGE_OBJECT_EMAIL;
import static org.jclouds.cloudfiles.reference.CloudFilesHeaders.CDN_ENABLED;
import static org.jclouds.cloudfiles.reference.CloudFilesHeaders.CDN_LOG_RETENTION;
import static org.jclouds.cloudfiles.reference.CloudFilesHeaders.CDN_TTL;
import static org.jclouds.cloudfiles.reference.CloudFilesHeaders.CDN_WEBSITE_ERROR;
import static org.jclouds.cloudfiles.reference.CloudFilesHeaders.CDN_WEBSITE_INDEX;

import java.net.URI;
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.cloudfiles.binders.BindIterableToHeadersWithPurgeCDNObjectEmail;
import org.jclouds.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.cloudfiles.functions.ParseCdnUriFromHeaders;
import org.jclouds.cloudfiles.functions.ParseContainerCDNMetadataFromHeaders;
import org.jclouds.cloudfiles.options.ListCdnContainerOptions;
import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.swift.Storage;
import org.jclouds.openstack.swift.SwiftClient;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

/** Provides access to Cloud Files via their REST API. */
@RequestFilters(AuthenticateRequest.class)
@Endpoint(Storage.class)
public interface CloudFilesClient extends SwiftClient {

   /**
    * Retrieve a list of existing CDN-enabled containers.
    */
   @Named("ListCDNEnabledContainers")
   @GET
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/")
   @Endpoint(CDNManagement.class)
   Set<ContainerCDNMetadata> listCDNContainers(ListCdnContainerOptions... options);

   /**
    * Get the CDN attributes of the container.
    * 
    * If the container is (or ever has been) CDN-enabled, the URL, TTL, enabled status, and log retention status are 
    * returned in the response headers. Its CDN URL can be combined with any object name within the container to form
    * the publicly accessible URL for that object for distribution over a CDN system. The TTL value is the number of
    * seconds that the object will be cached in the CDN system before being refetched. The enabled status indicates
    * whether the container is currently marked to allow public serving of objects via CDN. The log_retention setting
    * specifies whether the CDN access logs should be collected and stored in the Cloud Files storage system.
    */
   @Named("ListCDNEnabledContainerMetadata")
   @HEAD
   @ResponseParser(ParseContainerCDNMetadataFromHeaders.class)
   @Fallback(NullOnContainerNotFound.class)
   @Path("/{container}")
   @Endpoint(CDNManagement.class)
   ContainerCDNMetadata getCDNMetadata(@PathParam("container") String container);

   /**
    * Before a container can be CDN-enabled, it must exist in the storage system. When a container is CDN-enabled, any
    * objects stored in it are publicly accessible over the Content Delivery Network by combining the container's CDN
    * URL with the object name.
    * 
    * Any CDN-accessed objects are cached in the CDN for the specified amount of time called the TTL, or Time to Live.
    * The default TTL value is 259200 seconds, or 72 hours. Each time the object is accessed after the TTL expires, the
    * CDN refetches and caches the object for the TTL period.
    * 
    * You specify the TTL for an object via the ttl parameter. Setting the TTL is the same as setting the HTTP Expires
    * and Cache-Control headers for the cached object. The minimum TTL is 15 minutes and the maximum is 1 year for a
    * range of 900 to 31536000 seconds. Setting a TTL for a long time, however, does not guarantee that the content
    * will stay populated on CDN edge servers for the entire period. The most popular objects stay cached based on the
    * edge location's logic.    
    */
   @Named("CDNEnableContainer")
   @PUT
   @Path("/{container}")
   @Headers(keys = CDN_ENABLED, values = "True")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CDNManagement.class)
   URI enableCDN(@PathParam("container") String container,
                 @HeaderParam(CDN_TTL) long ttl,
                 @HeaderParam(CDN_LOG_RETENTION) boolean logRetention);


   /**
    * @see CloudFilesClient#enableCDN(String, long, boolean)
    */
   @Named("CDNEnableContainer")
   @PUT
   @Path("/{container}")
   @Headers(keys = CDN_ENABLED, values = "True")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CDNManagement.class)
   URI enableCDN(@PathParam("container") String container, @HeaderParam(CDN_TTL) long ttl);
   
   /**
    * @see CloudFilesClient#enableCDN(String, long, boolean)
    */
   @Named("CDNEnableContainer")
   @PUT
   @Path("/{container}")
   @Headers(keys = CDN_ENABLED, values = "True")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CDNManagement.class)
   URI enableCDN(@PathParam("container") String container);
   
   /**
    * @see CloudFilesClient#enableCDN(String, long, boolean)
    */
   @Named("UpdateCDNEnabledContainerMetadata")
   @POST
   @Path("/{container}")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CDNManagement.class)
   URI updateCDN(@PathParam("container") String container,
                 @HeaderParam(CDN_TTL) long ttl,
                 @HeaderParam(CDN_LOG_RETENTION) boolean logRetention);

   /**
    * @see CloudFilesClient#enableCDN(String, long, boolean)
    */
   @Named("UpdateCDNEnabledContainerMetadata")
   @POST
   @Path("/{container}")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CDNManagement.class)
   URI updateCDN(@PathParam("container") String container, @HeaderParam(CDN_LOG_RETENTION) boolean logRetention);

   /**
    * @see CloudFilesClient#enableCDN(String, long, boolean)
    */
   @Named("UpdateCDNEnabledContainerMetadata")
   @POST
   @Path("/{container}")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CDNManagement.class)
   URI updateCDN(@PathParam("container") String container, @HeaderParam(CDN_TTL) long ttl);

   /**
    * Remove the container from the CDN. Please note, however, that objects remain public until their TTL expires.
    */
   @Named("DisableCDNEnabledContainer")
   @POST
   @Path("/{container}")
   @Headers(keys = CDN_ENABLED, values = "False")
   @Endpoint(CDNManagement.class)
   boolean disableCDN(@PathParam("container") String container);
   
   /**
    * You can purge a CDN-enabled object when you find it absolutely necessary to remove the object from public access
    * and you cannot wait for the TTL to expire. You should limit object purges to situations where there could be
    * serious personal, business, or security consequences if it remained in the CDN. For example, someone published
    * your company's quarterly earnings too early. You can manually purge CDN-enabled objects without having to wait
    * for the TTL to expire, and you can optionally be notified by email that the object has been purged. However, you
    * may only DELETE up to 25 objects per day. Any attempt to delete more than this will result in a 498 status code
    * error (Rate Limited).
    * 
    * There are two ways you may purge objects from the edge: (1) individually using purgeCDNObject() in the API or
    * (2) by creating a support ticket to purge entire containers. The 25-object limit does not apply when purging an
    * entire container via Support.    
    */
   @Named("PurgeCDNEnabledObject")
   @DELETE
   @Path("/{container}/{object}")
   @Headers(keys = CDN_CONTAINER_PURGE_OBJECT_EMAIL, values = "{email}")
   @Endpoint(CDNManagement.class)
   boolean purgeCDNObject(@PathParam("container") String container, @PathParam("object") String object,
         @BinderParam(BindIterableToHeadersWithPurgeCDNObjectEmail.class) Iterable<String> emails);

   
   /**
    * @see CloudFilesClient#purgeCDNObject(String, String, Iterable)
    */
   @Named("PurgeCDNEnabledObject")
   @DELETE
   @Path("/{container}/{object}")
   @Endpoint(CDNManagement.class)
   boolean purgeCDNObject(@PathParam("container") String container, @PathParam("object") String object);

   /**
    * You may use your Cloud Files account to create a static website on the World Wide Web. First, you must CDN-enable
    * a storage container. Any HTML or static web pages in the container will become available through a static website
    * once you set the parameter index to "index.html" or other index page of your choice. You may also create
    * subdirectories in your website by creating pseudo-directories (objects with a '/' in the name).
    * 
    * The page you set for index becomes the index page for every subdirectory in your website; each of your
    * pseudo-directories should contain a file with that name. So, if you set index to "index.html", you should have an
    * index.html page in each pseudo-directory. If you do not have the named index page, visits to myhost/subdir/ will
    * return a 404 error.
    * 
    * To setup a domain name for your static website, create a CNAME with your DNS Server (or name server). This is the
    * domain name of your site (such as www.example.com). Your CNAME is set up with your individual DNS Server, which
    * is outside the scope of this documentation. Once you have your CNAME established, map your domain name to your
    * Cloud Files CDN URL to get your site up and running on the Web.    
    */
   @Named("UpdateCDNEnabledContainerMetadata")
   @POST
   @Path("/{container}")
   @Headers(keys = CDN_WEBSITE_INDEX, values = "{index}")
   boolean setCDNStaticWebsiteIndex(@PathParam("container") String container, @PathParam("index") String index);

   /**
    * You may create and set custom error pages for visitors to your website; currently, only 401 (Unauthorized) and
    * 404 (Not Found) errors are supported. To do this, set the error parameter.
    * 
    * Error pages are served with the <status> code prepended to the name of the error page you set. For instance, if
    * you set error to "error.html", 401 errors will display the page 401error.html. Similarly, 404 errors will display
    * 404error.html. You must have both of these pages created in your container when you set the error parameter, or
    * your site will display generic error pages.
    * 
    * You need only set the error parameter once for your entire static website.    
    */
   @Named("UpdateCDNEnabledContainerMetadata")
   @POST
   @Path("/{container}")
   @Headers(keys = CDN_WEBSITE_ERROR, values = "{error}")
   boolean setCDNStaticWebsiteError(@PathParam("container") String container, @PathParam("error") String error);
}
