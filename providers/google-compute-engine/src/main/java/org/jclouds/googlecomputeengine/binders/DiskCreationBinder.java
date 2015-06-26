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
package org.jclouds.googlecomputeengine.binders;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.jclouds.googlecomputeengine.options.DiskCreationOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;

import com.google.gson.stream.JsonWriter;

public final class DiskCreationBinder implements MapBinder {

   @Override public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      DiskCreationOptions options = (DiskCreationOptions) postParams.get("options");
      Writer out = new StringWriter();
      JsonWriter json = new JsonWriter(out);
      json.setSerializeNulls(false);
      try {
         json.beginObject();
         json.name("name").value(postParams.get("name").toString());
         json.name("sizeGb").value(options.sizeGb());
         json.name("type").value(options.type() != null ? options.type().toString() : null);
         json.name("sourceSnapshot")
               .value(options.sourceSnapshot() != null ? options.sourceSnapshot().toString() : null);
         json.name("description").value(options.description());
         json.endObject();
         json.close();
      } catch (IOException e) {
         throw new AssertionError(e); // should be impossible as we are writing a string!
      }
      request.setPayload(out.toString());
      request.getPayload().getContentMetadata().setContentType("application/json");
      return request;
   }

   @Override public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new UnsupportedOperationException();
   }
}
