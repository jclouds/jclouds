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
package org.jclouds.googlecomputeengine.domain;

import java.net.URI;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.annotations.Beta;

@AutoValue
@Beta
public abstract class TargetHttpProxy {

   public abstract String id();
   public abstract Date creationTimestamp();
   public abstract URI selfLink();
   public abstract String name();
   @Nullable public abstract String description();
   public abstract URI urlMap();

   @SerializedNames({"id", "creationTimestamp", "selfLink", "name", "description", "urlMap"})
   public static TargetHttpProxy create(String id, Date creationTimestamp, URI selfLink, String name,
         @Nullable String description, URI urlMap){
      return new AutoValue_TargetHttpProxy(id, creationTimestamp, selfLink, name, description, urlMap);
   }

   TargetHttpProxy(){
   }
}
