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
package org.jclouds.openstack.swift.v1.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.openstack.swift.v1.reference.TempAuthHeaders.TEMP_AUTH_HEADER_USER;
import static org.jclouds.openstack.swift.v1.reference.TempAuthHeaders.TEMP_AUTH_HEADER_PASS;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Binder to the tempAuthAuthentication
 *
 */
public final class TempAuthBinder implements Binder{
   private final String identityHeaderNameUser;
   private final String identityHeaderNamePass;

   @Inject
   TempAuthBinder(@Named(TEMP_AUTH_HEADER_USER) String identityHeaderNameUser, @Named(TEMP_AUTH_HEADER_PASS) String identityHeaderNamePass) {
      this.identityHeaderNameUser = identityHeaderNameUser;
      this.identityHeaderNamePass = identityHeaderNamePass;
   }

   @Override 
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkNotNull(request, "request");
      checkArgument(input instanceof Credentials, "input must be a non-null org.jclouds.domain.Credentials");
      return (R) request.toBuilder().replaceHeader(identityHeaderNameUser, ((Credentials) input).identity)
	      		.replaceHeader(identityHeaderNamePass, ((Credentials) input).credential).build();
   }
}

