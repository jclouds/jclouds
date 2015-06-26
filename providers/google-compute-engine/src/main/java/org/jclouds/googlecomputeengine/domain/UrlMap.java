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
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class UrlMap {

   /**
    * An urlMap hostRule used to filter requests based on hostname. Controls what traffic is sent to
    * which path matcher.
    */
   @AutoValue
   public abstract static class HostRule {
      @Nullable public abstract String description();
      public abstract List<String> hosts();
      public abstract String pathMatcher();

      @SerializedNames({ "description", "hosts", "pathMatcher" })
      public static HostRule create(String description, List<String> hosts,
                        String pathMatcher) {
          return new AutoValue_UrlMap_HostRule(description, hosts, pathMatcher);
       }

      HostRule(){
      }
   }

   @AutoValue
   public abstract static class PathMatcher {

      @AutoValue
      public abstract static class PathRule{
         public abstract List<String> paths();
         public abstract URI service();

         @SerializedNames({"paths", "service"})
         public static PathRule create(List<String> paths, URI service) {
            return new AutoValue_UrlMap_PathMatcher_PathRule(paths, service);
         }

         PathRule(){
         }
      }

      public abstract String name();
      @Nullable public abstract String description();
      public abstract URI defaultService();
      public abstract List<PathRule> pathRules();

      @SerializedNames({ "name", "description", "defaultService", "pathRules" })
      public static PathMatcher create(String name, @Nullable String description,
                     URI defaultService, @Nullable List<PathRule> pathRules) {
         return new AutoValue_UrlMap_PathMatcher(name, description, defaultService, pathRules);
      }

      PathMatcher(){
      }
   }

   @AutoValue
   public abstract static class UrlMapTest{

      @Nullable public abstract String description();
      public abstract String host();
      public abstract String path();
      public abstract URI service();

      @SerializedNames({"description", "host", "path", "service"})
      public static UrlMapTest create(@Nullable String description, String host, String path, URI service) {
         return new AutoValue_UrlMap_UrlMapTest(description, host, path, service);
      }
      UrlMapTest(){
      }
   }

   public abstract String id();
   public abstract Date creationTimestamp();
   public abstract URI selfLink();
   public abstract String name();
   @Nullable public abstract String description();
   @Nullable public abstract List<HostRule> hostRules();
   @Nullable public abstract List<PathMatcher> pathMatchers();
   @Nullable public abstract List<UrlMapTest> urlMapTests();
   public abstract URI defaultService();
   public abstract String fingerprint();


   @SerializedNames({
      "id", "creationTimestamp", "selfLink", "name", "description", "hostRules", "pathMatchers",
      "tests", "defaultService", "fingerprint"})
   public static UrlMap create (String id, Date creationTimestamp, URI selfLink, String name,
               @Nullable String description, @Nullable List<HostRule> hostRules,
               @Nullable List<PathMatcher> pathMatchers,
               @Nullable List<UrlMapTest> urlMapTests, URI defaultService,
               @Nullable String fingerprint) {
      return new AutoValue_UrlMap(id, creationTimestamp, selfLink, name, description, hostRules, pathMatchers,
            urlMapTests, defaultService, fingerprint);
   }

   UrlMap(){
   }
}
