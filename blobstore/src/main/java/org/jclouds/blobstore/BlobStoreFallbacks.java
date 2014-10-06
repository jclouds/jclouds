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
package org.jclouds.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static org.jclouds.http.HttpUtils.contains404;

import org.jclouds.Fallback;

public final class BlobStoreFallbacks {
   private BlobStoreFallbacks() {
   }
   
   public static final class ThrowContainerNotFoundOn404 implements Fallback<Object> {
      public Object createOrPropagate(Throwable t) throws Exception {
         if (contains404(checkNotNull(t, "throwable")))
            throw new ContainerNotFoundException(t);
         throw propagate(t);
      }
   }

   public static final class ThrowKeyNotFoundOn404 implements Fallback<Object> {
      public Object createOrPropagate(Throwable t) throws Exception {
         if (contains404(checkNotNull(t, "throwable")))
            throw new KeyNotFoundException(t);
         throw propagate(t);
      }
   }

   public static final class FalseOnContainerNotFound implements Fallback<Boolean> {
      public Boolean createOrPropagate(Throwable t) throws Exception {
         if (checkNotNull(t, "throwable") instanceof ContainerNotFoundException) {
            return false;
         }
         throw propagate(t);
      }
   }

   public static final class FalseOnKeyNotFound implements Fallback<Boolean> {
      public Boolean createOrPropagate(Throwable t) throws Exception {
         if (checkNotNull(t, "throwable") instanceof KeyNotFoundException) {
            return false;
         }
         throw propagate(t);
      }
   }

   public static final class NullOnContainerNotFound implements Fallback<Object> {
      public Object createOrPropagate(Throwable t) throws Exception {
         if (checkNotNull(t, "throwable") instanceof ContainerNotFoundException) {
            return null;
         }
         throw propagate(t);
      }
   }

   public static final class NullOnKeyNotFound implements Fallback<Object> {
      public Object createOrPropagate(Throwable t) throws Exception {
         if (checkNotNull(t, "throwable") instanceof KeyNotFoundException) {
            return null;
         }
         throw propagate(t);
      }
   }

   public static final class NullOnKeyAlreadyExists implements Fallback<Object> {
      public Object createOrPropagate(Throwable t) throws Exception {
         if (checkNotNull(t, "throwable") instanceof KeyAlreadyExistsException) {
            return null;
         }
         throw propagate(t);
      }
   }
}
