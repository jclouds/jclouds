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

import static org.jclouds.googlecomputeengine.internal.NullSafeCopies.copyOf;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Operation {

   @AutoValue
   public abstract static class Error {
      /** The error type identifier for this error. */
      public abstract String code(); // TODO: enum?

      /** The field in the request which caused the error. */
      @Nullable public abstract String location();

      @Nullable public abstract String message();

      @SerializedNames({ "code", "location", "message" })
      public static Error create(String code, String location, String message) {
         return new AutoValue_Operation_Error(code, location, message);
      }

      Error() {
      }
   }

   public static enum Status {
      PENDING,
      RUNNING,
      DONE
   }

   public abstract String id();

   public abstract URI selfLink();

   public abstract String name();

   @Nullable public abstract String description();

   /** URL of the resource the operation is mutating. */
   public abstract URI targetLink();

   /** Target id which identifies a particular incarnation of the target. */
   @Nullable public abstract String targetId();

   /**
    * Identifier specified by the client when the mutation was initiated. Must be unique for all operation resources in
    * the project.
    */
   @Nullable public abstract String clientOperationId();

   public abstract Status status();

   /** Textual description of the current status of the operation. */
   @Nullable public abstract String statusMessage();

   /** User who requested the operation, for example {@code user@example.com}. */
   public abstract String user();

   /**
    * A progress indicator that ranges from 0 to 100. This should not be used to guess at when the
    * operation will be complete. This number should be monotonically increasing as the operation progresses.
    */
   @Nullable public abstract Integer progress(); // TODO: check really nullable

   /** The time that this operation was requested. */
   public abstract Date insertTime();

   @Nullable public abstract Date startTime();

   @Nullable public abstract Date endTime();

   @Nullable public abstract Integer httpErrorStatusCode();

   @Nullable public abstract String httpErrorMessage();

   /** Examples include insert, update, and delete. */
   public abstract String operationType(); // TODO: enum

   public abstract List<Error> errors();

   @Nullable public abstract URI region();

   @Nullable public abstract URI zone();

   @SerializedNames({ "id", "selfLink", "name", "description", "targetLink", "targetId", "clientOperationId", "status",
         "statusMessage", "user", "progress", "insertTime", "startTime", "endTime", "httpErrorStatusCode",
         "httpErrorMessage", "operationType", "errors", "region", "zone" })
   public static Operation create(String id, URI selfLink, String name, String description, URI targetLink,
         String targetId, String clientOperationId, Status status, String statusMessage, String user, Integer progress,
         Date insertTime, Date startTime, Date endTime, Integer httpErrorStatusCode, String httpErrorMessage,
         String operationType, List<Error> errors, URI region, URI zone) {
      return new AutoValue_Operation(id, selfLink, name, description, targetLink, targetId, clientOperationId, status,
            statusMessage, user, progress, insertTime, startTime, endTime, httpErrorStatusCode, httpErrorMessage,
            operationType, copyOf(errors), region, zone);
   }

   Operation() {
   }
}
