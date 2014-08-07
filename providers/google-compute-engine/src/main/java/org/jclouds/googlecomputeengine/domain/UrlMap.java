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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a url map resource.
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/latest/urlMaps"/>
 * @see <a href="https://developers.google.com/compute/docs/load-balancing/http/url-map"/>
 */
@Beta
public final class UrlMap extends Resource {

   private final Set<HostRule> hostRules;
   private final Set<PathMatcher> pathMatchers;
   private final Set<UrlMapTest> urlMapTests;
   private final URI defaultService;
   private final Optional<String> fingerprint;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "hostRules","pathMatchers",
           "tests", "defaultService", "fingerprint"
   })
   protected UrlMap(String id, Date creationTimestamp, URI selfLink, String name,
                    @Nullable String description, @Nullable Set<HostRule> hostRules,
                    @Nullable Set<PathMatcher> pathMatchers,
                    @Nullable Set<UrlMapTest> urlMapTests, URI defaultService,
                    @Nullable String fingerprint) {
      super(Kind.URL_MAP, id, creationTimestamp, selfLink, name, description);
      this.defaultService = checkNotNull(defaultService, "default service");
      this.pathMatchers = pathMatchers == null ? ImmutableSet.<PathMatcher>of() : pathMatchers;
      this.urlMapTests = urlMapTests == null ? ImmutableSet.<UrlMapTest>of() : urlMapTests;
      this.hostRules = hostRules == null ? ImmutableSet.<HostRule>of() : hostRules;
      this.fingerprint = fromNullable(fingerprint);
   }

   /**
    * @return the hostRules for this urlMap.
    */
   public Set<HostRule> getHostRules() {
      return hostRules;
   }

   /**
    * @return the pathMatchers for this urlMap.
    */
   public Set<PathMatcher> getPathMatchers() {
      return pathMatchers;
   }

   /**
    * @return the tests for this urlMap.
    */
   public Set<UrlMapTest> getTests() {
      return urlMapTests;
   }

   /**
    * @return the defaultService for this urlMap.
    */
   public URI getDefaultService() {
      return defaultService;
   }

   /**
    * @return the fingerprint for this urlMap.
    */
   public Optional<String> getFingerprint() {
      return fingerprint;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name, kind, hostRules, pathMatchers, urlMapTests,
                              defaultService);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      UrlMap that = UrlMap.class.cast(obj);
      return equal(this.name, that.name)
              && equal(this.kind, that.kind)
              && equal(this.hostRules, that.hostRules)
              && equal(this.pathMatchers, that.pathMatchers)
              && equal(this.urlMapTests, that.urlMapTests)
              && equal(this.defaultService, that.defaultService);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("hostRules", hostRules)
              .add("pathMatchers", pathMatchers)
              .add("tests", urlMapTests)
              .add("defaultService", defaultService)
              .add("fingerprint", fingerprint.orNull());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromUrlMap(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private ImmutableSet.Builder<HostRule> hostRules = ImmutableSet.builder();
      private ImmutableSet.Builder<PathMatcher> pathMatchers = ImmutableSet.builder();
      private ImmutableSet.Builder<UrlMapTest> urlMapTests = ImmutableSet.builder();
      private URI defaultService;
      private String fingerprint;

      /**
       * @see UrlMap#getHostRules()
       */
      public Builder addHostRule(HostRule hostRule) {
         this.hostRules.add(checkNotNull(hostRule, "hostRule"));
         return this;
      }

      /**
       * @see UrlMap#getHostRules()
       */
      public Builder hostRules(Set<HostRule> hostRules) {
         this.hostRules = ImmutableSet.builder();
         this.hostRules.addAll(hostRules);
         return this;
      }
      
      /**
       * @see UrlMap#getPathMatchers()
       */
      public Builder addPathMatcher(PathMatcher pathMatcher) {
         this.pathMatchers.add(checkNotNull(pathMatcher, "pathMatcher"));
         return this;
      }

      /**
       * @see UrlMap#getPathMatchers()
       */
      public Builder pathMatchers(Set<PathMatcher> pathMatchers) {
         this.pathMatchers = ImmutableSet.builder();
         this.pathMatchers.addAll(pathMatchers);
         return this;
      }
      
      /**
       * @see UrlMap#getTests()
       */
      public Builder addUrlMapTest(UrlMapTest urlMapTest) {
         this.urlMapTests.add(checkNotNull(urlMapTest, "test"));
         return this;
      }

      /**
       * @see UrlMap#getTests()
       */
      public Builder urlMapTests(Set<UrlMapTest> urlMapTests) {
         this.urlMapTests = ImmutableSet.builder();
         this.urlMapTests.addAll(urlMapTests);
         return this;
      }
      
      /**
       * @see UrlMap#getDefaultService()
       */
      public Builder defaultService(URI defaultService) {
         this.defaultService = defaultService;
         return this;
      }
      
      /**
       * @see UrlMap#getFingerprint()
       */
      public Builder fingerprint(String fingerprint) {
         this.fingerprint = fingerprint;
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public UrlMap build() {
         return new UrlMap(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, hostRules.build(), pathMatchers.build(), urlMapTests.build(),
                 defaultService, fingerprint);
      }

      public Builder fromUrlMap(UrlMap in) {
         return super.fromResource(in).hostRules(in.getHostRules()).pathMatchers(in.getPathMatchers())
                  .urlMapTests(in .getTests()).defaultService(in.getDefaultService())
                  .fingerprint(in.getFingerprint().orNull());
      }

   }

   /**
    * An urlMap hostRule used to filter requests based on hostname. Controls what traffic is sent to
    * which path matcher.
    *
    * @see <a href="https://developers.google.com/compute/docs/reference/latest/urlMaps"/>
    * @see <a href="https://developers.google.com/compute/docs/load-balancing/http/url-map#adding_host_rules"/>
    */
   public static final class HostRule {

      private final Optional<String> description;
      private final Set<String> hosts;
      private final String pathMatcher;
      
      @ConstructorProperties({
              "description", "hosts", "pathMatcher"
      })
      private HostRule(@Nullable String description, @Nullable Set<String> hosts,
                       @Nullable String pathMatcher) {
         this.pathMatcher = checkNotNull(pathMatcher, "pathMatcher");
         this.hosts = hosts == null ? ImmutableSet.<String>of() : hosts;
         this.description = fromNullable(description);
      }

      /**
       * @return the description.
       */
      public Optional<String> getDescription() {
         return description;
      }

      /**
       * @return the hosts.
       */
      public Set<String> getHosts() {
         return hosts;
      }

      /**
       * @return the pathMatcher this hostRule uses.
       */
      public String getPathMatcher() {
         return pathMatcher;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public int hashCode() {
         return Objects.hashCode(hosts, pathMatcher);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null || getClass() != obj.getClass()) return false;
         HostRule that = HostRule.class.cast(obj);
         return equal(this.hosts, that.hosts)
                 && equal(this.pathMatcher, that.pathMatcher);
      }

      /**
       * {@inheritDoc}
       */
      public Objects.ToStringHelper string() {
         return toStringHelper(this)
                 .omitNullValues()
                 .add("hosts", hosts)
                 .add("pathMatcher", pathMatcher);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String toString() {
         return string().toString();
      }

      public static Builder builder() {
         return new Builder();
      }

      public Builder toBuilder() {
         return builder().fromHostRule(this);
      }

      public static final class Builder {

         private String description;
         private ImmutableSet.Builder<String> hosts = ImmutableSet.<String>builder();
         private String pathMatcher;

         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMap.HostRule#getDescription()
          */
         public Builder description(String description) {
            this.description = description;
            return this;
         }

         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMap.HostRule#getHosts()
          */
         public Builder addHost(String host) {
            this.hosts.add(checkNotNull(host, "host"));
            return this;
         }

         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMap.HostRule#getHosts()
          */
         public Builder hosts(Set<String> hosts) {
            this.hosts = ImmutableSet.builder();
            this.hosts.addAll(hosts);
            return this;
         }

         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMap.HostRule#getPathMatcher()
          */
         public Builder pathMatcher(String pathMatcher) {
            this.pathMatcher = pathMatcher;
            return this;
         }

         public HostRule build() {
            return new HostRule(description, hosts.build(), pathMatcher);
         }

         public Builder fromHostRule(HostRule hostRule) {
            return new Builder().description(hostRule.getDescription().orNull())
                                .hosts(hostRule.getHosts())
                                .pathMatcher(hostRule.getPathMatcher());
         }
      }

   }
   
   /**
    * An urlMap PathMatcher used to route requests based on the url given.
    *
    * @see <a href="https://developers.google.com/compute/docs/reference/latest/urlMaps"/>
    * @see <a href="https://developers.google.com/compute/docs/load-balancing/http/url-map#adding_path_matchers"/>
    */
   public static final class PathMatcher {
      
      private final String name;
      private final Optional<String> description;
      private final URI defaultService;
      private final Set<PathRule> pathRules;
      
      @ConstructorProperties({
              "name", "description", "defaultService", "pathRules"
      })
      private PathMatcher(String name, @Nullable String description,
                          URI defaultService, @Nullable Set<PathRule> pathRules) {
         this.name = checkNotNull(name, "name");
         this.description = fromNullable(description);
         this.defaultService = checkNotNull(defaultService, "defaultService");
         this.pathRules = pathRules == null ? ImmutableSet.<PathRule>of() : pathRules;
      }

      /**
       * @return the name.
       */
      public String getName() {
         return name;
      }

      /**
       * @return the description.
       */
      public Optional<String> getDescription() {
         return description;
      }

      /**
       * @return the defaultService this PathMatcher will send unmatched traffic to.
       */
      public URI getDefaultService() {
         return defaultService;
      }

      /**
       * @return the pathRules this PathMatcher compares requests against.
       */
      public Set<PathRule> getPathRules() {
         return pathRules;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public int hashCode() {
         return Objects.hashCode(name, defaultService, pathRules);
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null || getClass() != obj.getClass()) return false;
         PathMatcher that = PathMatcher.class.cast(obj);
         return equal(this.name, that.name)
                 && equal(this.defaultService, that.defaultService)
                 && equal(this.pathRules, that.pathRules);
      }

      /**
       * {@inheritDoc}
       */
      public Objects.ToStringHelper string() {
         return toStringHelper(this)
                 .omitNullValues()
                 .add("name", name)
                 .add("defaultService", defaultService)
                 .add("pathRules", pathRules);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String toString() {
         return string().toString();
      }

      public static Builder builder() {
         return new Builder();
      }

      public Builder toBuilder() {
         return builder().fromPathMatcher(this);
      }

      public static final class Builder {
         
         private String name;
         private String description;
         private URI defaultService;
         private ImmutableSet.Builder<PathRule> pathRules = ImmutableSet.<PathRule>builder();
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher#getName()
          */
         public Builder name(String name) {
            this.name = name;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher#getDescription()
          */
         public Builder description(String description) {
            this.description = description;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher#getDefaultService()
          */
         public Builder defaultService(URI defaultService) {
            this.defaultService = defaultService;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher#getPathRules()
          */
         public Builder addPathRule(PathRule pathRule) {
            this.pathRules.add(checkNotNull(pathRule, "pathRule"));
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher#getPathRules()
          */
         public Builder pathRules(Set<PathRule> pathRules) {
            this.pathRules = ImmutableSet.builder();
            this.pathRules.addAll(pathRules);
            return this;
         }
         
         public PathMatcher build() {
            return new PathMatcher(name, description, defaultService, pathRules.build());
         }
         
         public Builder fromPathMatcher(PathMatcher pathMatcher) {
            return new Builder().name(pathMatcher.getName())
                                .description(pathMatcher.getDescription().orNull())
                                .defaultService(pathMatcher.getDefaultService())
                                .pathRules(pathMatcher.getPathRules());
         }
      }
      
   }
   
   /**
    * An urlMap PathRule used to route requests based on the url given.
    *
    * @see <a href="https://developers.google.com/compute/docs/reference/latest/urlMaps"/>
    * @see <a href="https://developers.google.com/compute/docs/load-balancing/http/url-map#adding_path_matchers"/>
    */
   public static final class PathRule {
      
      private final Set<String> paths;
      private final URI service;
      
      @ConstructorProperties({
              "paths", "service"
      })
      private PathRule(Set<String> paths, URI service) {
         this.paths = checkNotNull(paths, "paths");
         this.service = checkNotNull(service, "service");
      }

      /**
       * @return the paths this PathRule compares requests against.
       */
      public Set<String> getPaths() {
         return paths;
      }

      /**
       * @return the service requests will be routed to if they match a path.
       */
      public URI getService() {
         return service;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public int hashCode() {
         return Objects.hashCode(paths, service);
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null || getClass() != obj.getClass()) return false;
         PathRule that = PathRule.class.cast(obj);
         return equal(this.paths, that.paths)
                 && equal(this.service, that.service);
      }

      /**
       * {@inheritDoc}
       */
      public Objects.ToStringHelper string() {
         return toStringHelper(this)
                 .omitNullValues()
                 .add("paths", paths)
                 .add("service", service);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String toString() {
         return string().toString();
      }

      public static Builder builder() {
         return new Builder();
      }

      public Builder toBuilder() {
         return builder().fromPathRule(this);
      }

      public static final class Builder {
         
         private ImmutableSet.Builder<String> paths = ImmutableSet.<String>builder();
         private URI service;
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMap.PathRule#getPaths()
          */
         public Builder addPath(String path) {
            this.paths.add(checkNotNull(path, "path"));
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMap.PathRule#getPaths()
          */
         public Builder paths(Set<String> paths) {
            this.paths = ImmutableSet.builder();
            this.paths.addAll(paths);
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMap.PathRule#getService()
          */
         public Builder service(URI service) {
            this.service = service;
            return this;
         }
         
         public PathRule build() {
            return new PathRule(paths.build(), service);
         }
         
         public Builder fromPathRule(PathRule pathRule) {
            return new Builder().paths(pathRule.getPaths()).service(pathRule.getService());
         }
      }
   }
   
   /**
    * An urlMap Test which validates that host rules and path rules behave as they should.
    *
    * @see <a href="https://developers.google.com/compute/docs/reference/latest/urlMaps"/>
    * @see <a href="https://developers.google.com/compute/docs/load-balancing/http/url-map#testing_url_maps"/>
    */
   public static final class UrlMapTest {
      
      private final Optional<String> description;
      private final String host;
      private final String path;
      private final URI service;
      
      @ConstructorProperties({
              "description", "host", "path", "service"
      })
      private UrlMapTest(@Nullable String description, String host, String path, URI service) {
         this.description = fromNullable(description);
         this.host = checkNotNull(host, "host");
         this.path = checkNotNull(path, "path");
         this.service = checkNotNull(service, "service");
      }

      /**
       * @return description of this test.
       */
      public Optional<String> getDescription() {
         return description;
      }

      /**
       * @return the host used in the test request.
       */
      public String getHost() {
         return host;
      }

      /**
       * @return the path used in the test request.
       */
      public String getPath() {
         return path;
      }

      /**
       * @return the service that the request should map to.
       */
      public URI getService() {
         return service;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public int hashCode() {
         return Objects.hashCode(host, path, service);
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null || getClass() != obj.getClass()) return false;
         UrlMapTest that = UrlMapTest.class.cast(obj);
         return equal(this.host, that.host)
                 && equal(this.path, that.path)
                 && equal(this.service, that.service);
      }

      /**
       * {@inheritDoc}
       */
      public Objects.ToStringHelper string() {
         return toStringHelper(this)
                 .omitNullValues()
                 .add("description", description.orNull())
                 .add("host", host)
                 .add("path", path)
                 .add("service", service);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String toString() {
         return string().toString();
      }
      
      public static Builder builder() {
         return new Builder();
      }

      public Builder toBuilder() {
         return builder().fromTest(this);
      }

      public static final class Builder {
         
         private String description;
         private String host;
         private String path;
         private URI service;
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMap.UrlMapTest#getDesciption()
          */
         public Builder description(String description) {
            this.description = description;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMap.UrlMapTest#getHost()
          */
         public Builder host(String host) {
            this.host = host;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMap.UrlMapTest#getPath()
          */
         public Builder path(String path) {
            this.path = path;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMap.UrlMapTest#getService()
          */
         public Builder service(URI service) {
            this.service = service;
            return this;
         }
         
         public UrlMapTest build() {
            return new UrlMapTest(description, host, path, service);
         }
         
         public Builder fromTest(UrlMapTest urlMapTest) {
            return new Builder().description(urlMapTest.getDescription().orNull())
                                .host(urlMapTest.getHost())
                                .path(urlMapTest.getPath())
                                .service(urlMapTest.getService());
         }
      }
   }
}