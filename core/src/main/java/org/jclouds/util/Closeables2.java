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

import com.google.common.base.Throwables;
import com.google.common.io.Closeables;

import org.jclouds.javax.annotation.Nullable;

@Deprecated
public class Closeables2 {
   private Closeables2() {
   }

   /**
    * Equivalent to calling {@code Closeables.close(closeable, true)}, but with no IOException in the signature.
    *
    * @param closeable the {@code Closeable} object to be closed, or null, in which case this method
    *     does nothing
    */
   @Deprecated
   public static void closeQuietly(@Nullable Closeable closeable) {
      try {
         Closeables.close(closeable, true);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }
}
