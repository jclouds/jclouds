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
package org.jclouds.http.internal;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import org.jclouds.Constants;
import org.jclouds.logging.Logger;
import org.jclouds.logging.internal.Wire;

import javax.annotation.Resource;
import javax.inject.Named;

public class HttpWire extends Wire {

   @Resource
   @Named(Constants.LOGGER_HTTP_WIRE)
   Logger wireLog = Logger.NULL;

   @VisibleForTesting
   @Inject(optional = true)
   @Named(Constants.PROPERTY_LOGGER_WIRE_LOG_SENSITIVE_INFO)
   boolean logSensitiveInformation = false;

   public Logger getWireLog() {
      return wireLog;
   }

   @Override
   protected boolean isLogSensitiveInformation() {
      return logSensitiveInformation;
   }
}
