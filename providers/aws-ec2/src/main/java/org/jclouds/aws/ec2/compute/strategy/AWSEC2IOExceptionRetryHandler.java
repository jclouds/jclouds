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
package org.jclouds.aws.ec2.compute.strategy;

import java.io.IOException;

import org.jclouds.aws.reference.FormParameters;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.io.Payload;

public class AWSEC2IOExceptionRetryHandler extends BackoffLimitedRetryHandler {

   private static final String DESCRIBE_ACTION = FormParameters.ACTION + "=Describe";

   @Override
   public boolean shouldRetryRequest(HttpCommand command, IOException error) {
      HttpRequest request = command.getCurrentRequest();
      if ("POST".equals(request.getMethod())) {
         Payload payload = request.getPayload();
         if (!payload.getRawContent().toString().contains(DESCRIBE_ACTION)){
            logger.error("Command not considered safe to retry because request method is POST and action may not be idempotent: %1$s",
               command);
            return false;
         }
      }
      return super.shouldRetryRequest(command, error);
   }

}
