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
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class UrlMapValidateResult {

   public abstract UrlMapValidateResultInternal result();

   @SerializedNames({"result"})
   public static UrlMapValidateResult create(UrlMapValidateResultInternal result){
      return new AutoValue_UrlMapValidateResult(result);
   }

   public static UrlMapValidateResult create(Boolean loadSucceeded, List<String> loadErrors,
                              Boolean testPassed, List<UrlMapValidateResultInternal.TestFailure> testFailures) {
      return create(UrlMapValidateResultInternal.create(loadSucceeded, loadErrors, testPassed, testFailures));
   }

   public static UrlMapValidateResult allPass(){
      return create(true, null, true, null);
   }

   @AutoValue
   public abstract static class UrlMapValidateResultInternal {

      public abstract Boolean loadSucceeded();
      @Nullable public abstract List<String> loadErrors();
      @Nullable public abstract Boolean testPassed();
      @Nullable public abstract List<TestFailure> testFailures();

      @SerializedNames({"loadSucceeded", "loadErrors", "testPassed", "testFailures"})
      public static UrlMapValidateResultInternal create(Boolean loadSucceeded, List<String> loadErrors,
                                 Boolean testPassed, List<TestFailure> testFailures) {
         return new AutoValue_UrlMapValidateResult_UrlMapValidateResultInternal(loadSucceeded, loadErrors, testPassed, testFailures);
      }

       UrlMapValidateResultInternal(){
      }

      @AutoValue
      public abstract static class TestFailure {

         public abstract String host();
         public abstract String path();
         public abstract URI expectedService();
         public abstract URI actualService();

         @SerializedNames({"host", "path", "expectedService", "actualService"})
         public static TestFailure create(String host, String path, URI expectedService, URI actualService){
            return new AutoValue_UrlMapValidateResult_UrlMapValidateResultInternal_TestFailure(host, path, expectedService, actualService);
         }

         TestFailure(){
         }
      }
   }

   UrlMapValidateResult(){
   }
}
