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

package org.jclouds.util;

import java.io.Closeable;
import java.io.IOException;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;

/**
 * Alternative to {@link com.google.common.io.Closeables}, which allows jclouds
 * to avoid guava incompatibility on said class.
 */
@Beta
public final class Closeables2 {

   /** Closes the closable, swallowing any {@linkplain IOException}. */
   public static void closeQuietly(@Nullable Closeable closeable) {
      if (closeable == null) {
         return;
      }
      try {
         closeable.close();
      } catch (IOException ignored) {
      }
   }

   private Closeables2() {
      throw new AssertionError("intentionally unimplemented");
   }
}
