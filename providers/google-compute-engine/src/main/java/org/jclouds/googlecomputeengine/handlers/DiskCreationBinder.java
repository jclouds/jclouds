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
package org.jclouds.googlecomputeengine.handlers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import org.jclouds.googlecomputeengine.options.DiskCreationOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.inject.Inject;

public class DiskCreationBinder extends BindToJsonPayload {

   @Inject
   public DiskCreationBinder(Json jsonBinder) {
      super(jsonBinder);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      DiskCreationOptions options = (DiskCreationOptions) checkNotNull(postParams.get("options"), "diskCreationOptions");
      String name = (String) checkNotNull(postParams.get("name"), "name");
      int sizeGb = (int) checkNotNull(postParams.get("sizeGb"), "sizeGb");
      DiskCreationBinderHelper diskCreationOptionsExtended = new DiskCreationBinderHelper(name, sizeGb, options);
      return super.bindToRequest(request, diskCreationOptionsExtended);
   }

   private class DiskCreationBinderHelper{

      /**
       * Values used to bind DiskCreationOptions to json request.
       */
      @SuppressWarnings("unused")
      private String name;
      @SuppressWarnings("unused")
      private int sizeGb;
      @SuppressWarnings("unused")
      private URI type;
      @SuppressWarnings("unused")
      private URI sourceImage;
      @SuppressWarnings("unused")
      private URI sourceSnapshot;

      private DiskCreationBinderHelper(String name, int sizeGb, DiskCreationOptions diskCreationOptions){
         this.name = name;
         this.sizeGb = sizeGb;
         this.type = diskCreationOptions.getType();
         this.sourceImage = diskCreationOptions.getSourceImage();
         this.sourceSnapshot = diskCreationOptions.getSourceSnapshot();
      }
   }
}
