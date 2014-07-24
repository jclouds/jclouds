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

package org.jclouds.atmos.reference;

/** Atmos error codes. */
public enum AtmosErrorCode {
   /** Operation aborted because of a conflicting operation in progess against the resource. */
   CONFLICTING_OPERATION(1006),
   /** The directory you are attempting to delete is not empty. */
   DIRECTORY_NOT_EMPTY(1023),
   /** The requested object was not found. */
   OBJECT_NOT_FOUND(1003),
   /** The resource you are trying to create already exists. */
   RESOURCE_ALREADY_EXISTS(1016),
   /** The server is busy. Please try again. */
   SERVER_BUSY(1040),
   /** There was a mismatch between the signature in the request and the signature as computed by the server. */
   SIGNATURE_MISMATCH(1032);

   private final int code;

   private AtmosErrorCode(int code) {
      this.code = code;
   }

   public int getCode() {
      return code;
   }
}
