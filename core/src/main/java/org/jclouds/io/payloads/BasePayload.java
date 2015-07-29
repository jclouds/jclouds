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
package org.jclouds.io.payloads;

import com.google.common.base.Throwables;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BasePayload<V> implements Payload {
   protected final V content;
   protected transient volatile boolean written;
   protected MutableContentMetadata contentMetadata;
   private boolean isSensitive;

   protected BasePayload(V content) {
      this(content, new BaseMutableContentMetadata());
   }

   protected BasePayload(V content, MutableContentMetadata contentMetadata) {
      this.content = checkNotNull(content, "content");
      this.contentMetadata = checkNotNull(contentMetadata, "contentMetadata");
   }

   @Override
   public InputStream getInput() {
      try {
         return openStream();
      } catch (IOException ioe) {
         throw Throwables.propagate(ioe);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public V getRawContent() {
      return content;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((content == null) ? 0 : content.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (!(obj instanceof Payload))
         return false;
      Payload other = (Payload) obj;
      if (content == null) {
         if (other.getRawContent() != null)
            return false;
      } else if (!content.equals(other.getRawContent()))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[content=" + (content != null) + ", contentMetadata=" + contentMetadata + ", written=" + written + ", isSensitive=" + isSensitive + "]";
   }

   /**
    * By default we are repeatable.
    */
   @Override
   public boolean isRepeatable() {
      return true;
   }

   /**
    * By default there are no resources to release.
    */
   @Override
   public void release() {
   }

   /**
    * Delegates to release()
    */
   @Override
   public void close() {
      release();
   }

   /**
    * 
    * {@inheritDoc}
    */
   @Override
   public MutableContentMetadata getContentMetadata() {
      return contentMetadata;
   }

   /**
    * 
    * {@inheritDoc}
    */
   @Override
   public void setContentMetadata(MutableContentMetadata in) {
      this.contentMetadata = in;
   }

   @Override
   public void setSensitive(boolean isSensitive) {
      this.isSensitive = isSensitive;
   }

   @Override
   public boolean isSensitive() {
      return this.isSensitive;
   }
}
