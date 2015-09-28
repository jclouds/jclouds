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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Extension to {@link DataInputStream} which adds method
 * {@link #readStdStreamData()} to allow read multiplexed standard streams.
 */
public final class DockerInputStream extends DataInputStream {

   /**
    * Ctor from superclass.
    *
    * @param in
    * @see DataInputStream#DataInputStream(InputStream)
    */
   public DockerInputStream(InputStream in) {
      super(in);
   }

   /**
    * @return {@link StdStreamData} instance read from the input stream or
    *         <code>null</code> if we reached end of the stream.
    * @throws IOException
    */
   public StdStreamData readStdStreamData() throws IOException {
      byte[] header = new byte[8];
      // try to read first byte from the message header - just to check if we
      // are at the end
      // of stream
      if (-1 == read(header, 0, 1)) {
         return null;
      }
      // read the rest of the header
      readFully(header, 1, 7);
      // decode size as an unsigned int
      long size = (long) (header[4] & 0xFF) << 24 | (header[5] & 0xFF) << 16 | (header[6] & 0xFF) << 8
            | (header[7] & 0xFF);

      byte[] payload;
      // The size from the header is an unsigned int so it can happen the byte
      // array has not a sufficient size and we'll have to truncate the frame
      payload = new byte[(int) Math.min(Integer.MAX_VALUE, size)];
      readFully(payload);
      boolean truncated = false;
      if (size > Integer.MAX_VALUE) {
         truncated = true;
         // skip the rest
         readFully(new byte[(int) (size - Integer.MAX_VALUE)]);
      }
      return new StdStreamData(header[0], payload, truncated);
   }

}
