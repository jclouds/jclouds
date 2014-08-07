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
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * Result of calling validate on an UrlMap resource.
 * 
 * @see <a href="https://developers.google.com/compute/docs/reference/latest/urlMaps/validate"/>
 */
public class UrlMapValidateResult {
   
   private final Boolean loadSucceeded;
   private final Set<String> loadErrors;
   private final Optional<Boolean> testPassed;
   private final Set<TestFailure> testFailures;
   
   @ConstructorProperties({
           "loadSucceeded", "loadErrors", "testPassed", "testFailures"
   })
   private UrlMapValidateResult(Boolean loadSucceeded, @Nullable Set<String> loadErrors,
                                @Nullable Boolean testPassed,
                                @Nullable Set<TestFailure> testFailures) {
      this.loadSucceeded = loadSucceeded;
      this.loadErrors = loadErrors == null ? ImmutableSet.<String>of() : loadErrors;
      this.testPassed = fromNullable(testPassed);
      this.testFailures = testFailures == null ? ImmutableSet.<TestFailure>of() : testFailures;
   }
   
   /**
    * @return if the loadSucceeded.
    */
   public Boolean getLoadSucceeded() {
      return loadSucceeded;
   }

   /**
    * @return the loadErrors.
    */
   public Set<String> getLoadErrors() {
      return loadErrors;
   }

   /**
    * @return if the testPassed.
    */
   public Optional<Boolean> getTestPassed() {
      return testPassed;
   }

   /**
    * @return the testFailures.
    */
   public Set<TestFailure> getTestFailures() {
      return testFailures;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(loadSucceeded, loadErrors, testPassed,
                              testFailures);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      UrlMapValidateResult that = UrlMapValidateResult.class.cast(obj);
      return equal(this.loadSucceeded, that.loadSucceeded)
              && equal(this.loadErrors, that.loadErrors)
              && equal(this.testPassed, that.testPassed)
              && equal(this.testFailures, that.testFailures);
   }
   
   /**
    **
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this)
              .omitNullValues()
              .add("loadSucceeded", loadSucceeded)
              .add("loadErrors", loadErrors)
              .add("testPassed", testPassed.orNull())
              .add("testFailures", testFailures);
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
      return new Builder().fromUrlMapValidateResult(this);
   }
   
   public static class Builder {
      
      private Boolean loadSucceeded;
      private ImmutableSet.Builder<String> loadErrors = ImmutableSet.<String>builder();
      private Boolean testPassed;
      private ImmutableSet.Builder<TestFailure> testFailures = ImmutableSet.<TestFailure>builder();
      
      /**
       * @see UrlMapValidateResult#getLoadSucceeded()
       */
      public Builder loadSucceeded(Boolean loadSucceeded) {
         this.loadSucceeded = loadSucceeded;
         return this;
      }
      
      /**
       * @see UrlMapValidateResult#getLoadErrors()
       */
      public Builder addLoadError(String loadError) {
         this.loadErrors.add(checkNotNull(loadError, "loadError"));
         return this;
      }
      
      /**
       * @see UrlMapValidateResult#getLoadErrors()
       */
      public Builder loadErrors(Set<String> loadErrors) {
         this.loadErrors = ImmutableSet.builder();
         this.loadErrors.addAll(loadErrors);
         return this;
      }
      
      /**
       * @see UrlMapValidateResult#getTestPassed()
       */
      public Builder testPassed(Boolean testPassed) {
         this.testPassed = testPassed;
         return this;
      }
      
      /**
       * @see UrlMapValidateResult#getTestFailure()
       */
      public Builder addTestFailure(TestFailure testFailure) {
         this.testFailures.add(checkNotNull(testFailure, "testFailure"));
         return this;
      }
      
      /**
       * @see UrlMapValidateResult#getTestFailure()
       */
      public Builder testFailures(Set<TestFailure> testFailures) {
         this.testFailures = ImmutableSet.builder();
         this.testFailures.addAll(testFailures);
         return this;
      }
      
      public UrlMapValidateResult build() {
         return new UrlMapValidateResult(loadSucceeded, loadErrors.build(),
                                         testPassed, testFailures.build());
      }
      
      public Builder fromUrlMapValidateResult(UrlMapValidateResult in) {
         return new Builder().loadErrors(in.getLoadErrors())
                             .loadSucceeded(in.getLoadSucceeded())
                             .testFailures(in.getTestFailures())
                             .testPassed(in.getTestPassed().orNull());
      }
   }

   public final static class TestFailure {
      
      private final String host;
      private final String path;
      private final URI expectedService;
      private final URI actualService;
      
      @ConstructorProperties({
              "host", "path", "expectedService", "actualService"
      })
      private TestFailure(String host, String path, URI expectedService,
                          URI actualService) {
         this.host = checkNotNull(host);
         this.path = checkNotNull(path);
         this.expectedService = checkNotNull(expectedService);
         this.actualService = checkNotNull(actualService);
      }

      /**
       * @return the host.
       */
      public String getHost() {
         return host;
      }

      /**
       * @return the path.
       */
      public String getPath() {
         return path;
      }

      /**
       * @return the expectedService.
       */
      public URI getExpectedService() {
         return expectedService;
      }

      /**
       * @return the actualService.
       */
      public URI getActualService() {
         return actualService;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public int hashCode() {
         return Objects.hashCode(host, path, expectedService, actualService);
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null || getClass() != obj.getClass()) return false;
         TestFailure that = TestFailure.class.cast(obj);
         return equal(this.host, that.host)
                 && equal(this.path, that.path)
                 && equal(this.expectedService, that.expectedService)
                 && equal(this.actualService, that.actualService);
      }
      
      /**
       **
       * {@inheritDoc}
       */
      protected Objects.ToStringHelper string() {
         return toStringHelper(this)
                 .omitNullValues()
                 .add("host", host)
                 .add("path", path)
                 .add("expectedService", expectedService)
                 .add("actualService", actualService);
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
      
      public static class Builder {
         
         private String host;
         private String path;
         private URI expectedService;
         private URI actualService;
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMapValidateResult.TestFailure#getHost()
          */
         public Builder host(String host) {
            this.host = host;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMapValidateResult.TestFailure#getPath()
          */
         public Builder path(String path) {
            this.path = path;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMapValidateResult.TestFailure#getExpectedService()
          */
         public Builder expectedService(URI expectedService) {
            this.expectedService = expectedService;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.UrlMapValidateResult.TestFailure#getActualService()
          */
         public Builder actualService(URI actualService) {
            this.actualService = actualService;
            return this;
         }
         
         public TestFailure build() {
            return new TestFailure(host, path, expectedService, actualService);
         }
      }
   }
}
