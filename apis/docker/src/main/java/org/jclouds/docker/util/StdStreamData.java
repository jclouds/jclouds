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

package org.jclouds.docker.util;

import java.util.Arrays;

/**
 * Representation of single message from docker-raw-stream. It holds stream
 * type, data (payload) and flag which says if the payload was truncated. The
 * truncation can occur when the frame size is greater than
 * {@link Integer#MAX_VALUE}.
 */
public final class StdStreamData {

   private final StdStreamType type;
   private final byte[] payload;
   private final boolean truncated;

   /**
    * Ctor.
    *
    * @param streamTypeId
    *           standard stream type (0=stdIn, 1=stdOut, 2=stdErr)
    * @param payload
    *           message data - must not be <code>null</code>
    * @param truncated
    * @throws ArrayIndexOutOfBoundsException
    *            if streamTypeId is not an index in {@link StdStreamType} enum.
    * @throws NullPointerException
    *            if provided payload is <code>null</code>
    */
   StdStreamData(byte streamTypeId, byte[] payload, boolean truncated)
         throws ArrayIndexOutOfBoundsException, NullPointerException {
      this.type = StdStreamType.values()[streamTypeId];
      this.payload = Arrays.copyOf(payload, payload.length);
      this.truncated = truncated;
   }

   /**
    * Type of stream.
    *
    * @return
    */
   public StdStreamType getType() {
      return type;
   }

   /**
    * Data from this message.
    *
    * @return payload.
    */
   public byte[] getPayload() {
      return payload;
   }

   /**
    * Flag which says if the payload was truncated (because of size).
    *
    * @return true if truncated
    */
   public boolean isTruncated() {
      return truncated;
   }

   /**
    * Standard streams enum. The order of entries is important!
    */
   public static enum StdStreamType {
      IN, OUT, ERR;
   }
}
