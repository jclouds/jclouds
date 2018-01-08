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
package org.jclouds.azurecompute.arm.functions;
import com.google.common.base.Function;
import org.jclouds.azurecompute.arm.util.GetEnumValue;
import org.jclouds.http.HttpResponse;

import javax.inject.Singleton;
/**
 * Parses job status from http response
 */
@Singleton
public class ParseJobStatus implements Function<HttpResponse, ParseJobStatus.JobStatus> {
   public enum JobStatus {

      DONE,
      IN_PROGRESS,
      FAILED,
      NO_CONTENT,
      UNRECOGNIZED;

      public static JobStatus fromValue(final String text) {
         return (JobStatus) GetEnumValue.fromValueOrDefault(text, JobStatus.UNRECOGNIZED);
      }
   }
   public JobStatus apply(final HttpResponse from) {
      if (from.getStatusCode() == 202 ){
         return JobStatus.IN_PROGRESS;
      } else if (from.getStatusCode() == 204) {
         return JobStatus.NO_CONTENT;
      } else if (from.getStatusCode() == 200 ){
         return JobStatus.DONE;
      } else {
         return JobStatus.FAILED;
      }
   }
}
