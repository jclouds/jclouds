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
package org.jclouds.s3;

import static org.jclouds.Constants.PROPERTY_RELAX_HOSTNAME;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_BLOBSTORE_DIRECTORY_SUFFIX;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.jclouds.reflect.Reflection2.typeToken;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_SERVICE_PATH;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.rest.internal.BaseHttpApiMetadata;
import org.jclouds.s3.blobstore.S3BlobStoreContext;
import org.jclouds.s3.blobstore.config.S3BlobStoreContextModule;
import org.jclouds.s3.config.S3HttpApiModule;
import org.jclouds.s3.reference.S3Headers;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Amazon's S3 api.
 * 
 * <h3>note</h3>
 * <p/>
 * This class allows overriding of types {@code A}(api), so that children can
 * add additional methods not declared here, such as new features from AWS.
 * <p/>
 * 
 * As this is a popular api, we also allow overrides for type {@code C}
 * (context). This allows subtypes to add in new feature groups or extensions,
 * not present in the base api. For example, you could make a subtype for
 * context, that exposes admin operations.
 */
@AutoService(ApiMetadata.class)
public class S3ApiMetadata extends BaseHttpApiMetadata {

   @Override
   public Builder<?, ?> toBuilder() {
      return new ConcreteBuilder().fromApiMetadata(this);
   }

   public S3ApiMetadata() {
      this(new ConcreteBuilder());
   }

   protected S3ApiMetadata(Builder<?, ?> builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_HEADER_TAG, S3Headers.DEFAULT_AMAZON_HEADERTAG);
      properties.setProperty(PROPERTY_S3_SERVICE_PATH, "/");
      properties.setProperty(PROPERTY_S3_VIRTUAL_HOST_BUCKETS, "false");
      properties.setProperty(PROPERTY_RELAX_HOSTNAME, "true");
      properties.setProperty(PROPERTY_BLOBSTORE_DIRECTORY_SUFFIX, "/");
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, String.format("x-${%s}-meta-", PROPERTY_HEADER_TAG));
      return properties;
   }
   
   public abstract static class Builder<A extends S3Client, T extends Builder<A, T>> extends
         BaseHttpApiMetadata.Builder<A, T> {

      protected Builder() {
         this(Class.class.cast(S3Client.class));
      }

      protected Builder(Class<A> syncClient) {
         super(syncClient);
         id("s3")
         .name("Amazon Simple Storage Service (S3) API")
         .identityName("Access Key ID")
         .credentialName("Secret Access Key")
         .defaultEndpoint("http://localhost")
         .documentation(URI.create("http://docs.amazonwebservices.com/AmazonS3/latest/API"))
         .version("2006-03-01")
         .defaultProperties(S3ApiMetadata.defaultProperties())
         .view(typeToken(S3BlobStoreContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(S3HttpApiModule.class, S3BlobStoreContextModule.class));
      }

      @Override
      public ApiMetadata build() {
         return new S3ApiMetadata(this);
      }
   }
   
   private static class ConcreteBuilder extends Builder<S3Client, ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
}
