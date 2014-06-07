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
package org.jclouds.chef.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.chef.domain.Resource;

import com.google.common.base.Function;

/**
 * Extracts the uri field of the given {@link Resource}.
 */
@Singleton
public class UriForResource implements Function<Object, URI> {

   @Override
   public URI apply(Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Resource,
            "This function can only be applied to Resource objects");
      return ((Resource) input).getUrl();
   }
}
