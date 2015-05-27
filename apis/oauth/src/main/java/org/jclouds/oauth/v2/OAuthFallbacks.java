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
package org.jclouds.oauth.v2;

import static com.google.common.base.Throwables.propagate;
import static org.jclouds.http.HttpUtils.returnValueOnCodeOrNull;

import org.jclouds.Fallback;
import org.jclouds.rest.AuthorizationException;

import com.google.common.base.Predicate;

public class OAuthFallbacks {

   /**
    * Fallback used to propagate an {@link AuthorizationException} on any 4xx
    * response.
    * <p>
    * Since OAuth requests will take place as part of the OAuth filter
    * execution, providers will not have direct access to configure its
    * behavior. Any failure in the filter execution due to a client side error
    * (such as malformed ids, etc) should be mapped to an
    * <code>AuthorizationException</code> so providers and clients can properly
    * handle the error.
    * <p>
    * Note that we use a fallback instead of an error handler to allow each
    * provider to bind their own handlers in a clean way, without having to
    * rebind the OAuth one.
    */
   public static final class AuthorizationExceptionOn4xx implements Fallback<Object> {
      public Void createOrPropagate(Throwable t) throws Exception {
         AuthorizationException ex = returnValueOnCodeOrNull(t, new AuthorizationException(t.getMessage(), t),
               new Predicate<Integer>() {
                  @Override
                  public boolean apply(Integer input) {
                     return input >= 400 && input < 500;
                  }
               });
         throw ex != null ? ex : propagate(t);
      }
   }

}
