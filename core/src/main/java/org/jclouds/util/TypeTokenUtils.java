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
package org.jclouds.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.google.common.annotations.Beta;
import com.google.common.base.Throwables;
import com.google.common.reflect.TypeToken;

@Beta
public final class TypeTokenUtils {
   private static final Method IS_SUPERTYPE_OF_TYPE;
   private static final Method IS_SUPERTYPE_OF_TYPETOKEN;
   static {
      Method isSuperTypeOfType;
      Method isSuperTypeOfTypeToken;
      try {
         // Guava 19 and later method
         isSuperTypeOfType = TypeToken.class.getDeclaredMethod("isSupertypeOf", Type.class);
         isSuperTypeOfTypeToken = TypeToken.class.getDeclaredMethod("isSupertypeOf", TypeToken.class);
      } catch (NoSuchMethodException nsme) {
         try {
            // Guava 18 and earlier method
            isSuperTypeOfType = TypeToken.class.getDeclaredMethod("isAssignableFrom", Type.class);
            isSuperTypeOfTypeToken = TypeToken.class.getDeclaredMethod("isAssignableFrom", TypeToken.class);
         } catch (NoSuchMethodException nsme2) {
            throw Throwables.propagate(nsme2);
         }
      }
      IS_SUPERTYPE_OF_TYPE = isSuperTypeOfType;
      IS_SUPERTYPE_OF_TYPETOKEN = isSuperTypeOfTypeToken;
   }

   private TypeTokenUtils() {
      throw new AssertionError("intentionally not implemented");
   }

   public static <C> boolean isSupertypeOf(TypeToken<C> token, Type type) {
      try {
         return (Boolean) IS_SUPERTYPE_OF_TYPE.invoke(token, type);
      } catch (IllegalAccessException iae) {
         throw Throwables.propagate(iae);
      } catch (InvocationTargetException ite) {
         throw Throwables.propagate(ite);
      }
   }

   public static <C, D> boolean isSupertypeOf(TypeToken<C> token, TypeToken<D> token2) {
      try {
         return (Boolean) IS_SUPERTYPE_OF_TYPETOKEN.invoke(token, token2);
      } catch (IllegalAccessException iae) {
         throw Throwables.propagate(iae);
      } catch (InvocationTargetException ite) {
         throw Throwables.propagate(ite);
      }
   }
}
