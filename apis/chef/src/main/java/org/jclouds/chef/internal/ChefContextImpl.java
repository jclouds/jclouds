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
package org.jclouds.chef.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.Context;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.ChefService;
import org.jclouds.internal.BaseView;
import org.jclouds.location.Provider;

import com.google.common.reflect.TypeToken;

/**
 * @deprecated Will be removed in next version. Directly create the
 *             {@link ChefApi} instead and access the {@link ChefService} from
 *             it.
 */
@Singleton
@Deprecated
public class ChefContextImpl extends BaseView implements ChefContext {
   private final ChefService chefService;

   @Inject
   protected ChefContextImpl(@Provider Context backend, @Provider TypeToken<? extends Context> backendType,
         ChefService chefService) {
      super(backend, backendType);
      this.chefService = checkNotNull(chefService, "chefService");
   }

   @Override
   public ChefService getChefService() {
      return chefService;
   }

   @Override
   public void close() throws IOException {
      delegate().close();
   }

}
