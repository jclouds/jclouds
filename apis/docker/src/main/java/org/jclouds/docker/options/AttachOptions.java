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
package org.jclouds.docker.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

public class AttachOptions extends BaseHttpRequestOptions {

   /**
    * @param stream When TTY is enabled, the stream is the raw data from the process PTY and client's stdin.
    *               When TTY is disabled, the stream is multiplexed to separate stdout and stderr.
    * @return AttachOptions
    */
   public AttachOptions stream(Boolean stream) {
      this.queryParameters.put("stream", stream.toString());
      return this;
   }

   /**
    * @param logs require logs to be attached. Default false.
    * @return AttachOptions
    */
   public AttachOptions logs(Boolean logs) {
      this.queryParameters.put("logs", logs.toString());
      return this;
   }

   /**
    * @param stdin if stream=true, attach to stdin. Default false
    * @return AttachOptions
    */
   public AttachOptions stdin(Boolean stdin) {
      this.queryParameters.put("stdin", stdin.toString());
      return this;
   }

   /**
    * @param stdout if logs=true, return stdout log, if stream=true, attach to stdout. Default false
    * @return
    */
   public AttachOptions stdout(Boolean stdout) {
      this.queryParameters.put("stdout", stdout.toString());
      return this;
   }

   /**
    *
    * @param stderr if logs=true, return stderr log, if stream=true, attach to stderr. Default false
    * @return
    */
   public AttachOptions stderr(Boolean stderr) {
      this.queryParameters.put("stderr", stderr.toString());
      return this;
   }

   public static class Builder {

      /**
       * @see org.jclouds.docker.options.AttachOptions#stream
       */
      public static AttachOptions stream(Boolean stream) {
         AttachOptions options = new AttachOptions();
         return options.stream(stream);
      }

      /**
       * @see org.jclouds.docker.options.AttachOptions#logs(Boolean)
       */
      public static AttachOptions logs(Boolean logs) {
         AttachOptions options = new AttachOptions();
         return options.logs(logs);
      }

      /**
       * @see org.jclouds.docker.options.AttachOptions#stdin(Boolean)
       */
      public static AttachOptions stdin(Boolean stdin) {
         AttachOptions options = new AttachOptions();
         return options.stdin(stdin);
      }

      /**
       * @see org.jclouds.docker.options.AttachOptions#stdout(Boolean)
       */
      public static AttachOptions stdout(Boolean stdout) {
         AttachOptions options = new AttachOptions();
         return options.stdout(stdout);
      }

      /**
       * @see org.jclouds.docker.options.AttachOptions#stderr(Boolean)
       */
      public static AttachOptions stderr(Boolean stderr) {
         AttachOptions options = new AttachOptions();
         return options.stderr(stderr);
      }

   }

}
