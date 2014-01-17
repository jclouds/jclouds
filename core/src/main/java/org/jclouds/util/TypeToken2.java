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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

import org.jclouds.reflect.Reflection2;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

/*
 * FIXME: remove this class ASAP!
 *
 * Evil stuff, adapted from https://code.google.com/p/guava-libraries/source/browse/guava/src/com/google/common/reflect/TypeToken.java#236.
 * See https://issues.apache.org/jira/browse/JCLOUDS-427 and
 * https://code.google.com/p/guava-libraries/issues/detail?id=1635
 */
public class TypeToken2<T> extends TypeToken<T> {
   private static final long serialVersionUID = 1L;

   @SuppressWarnings("unchecked")
   public <X, Y> TypeToken<T> where(TypeParameter2<X> typeParam1,
         TypeToken<X> typeArg1, TypeParameter2<Y> typeParam2, TypeToken<Y> typeArg2) {
      /*
       * Resolving both parameters in one shot seems to work around 1635, but
       * TypeResolver and where(Map) are package-private in Guava 14.x
       */
      Type resolvedType;
      try {
         Object resolver = newTypeResolver();
         Invokable<Object, Object> whereWithMap = Reflection2.method(
               (Class<Object>) resolver.getClass(), "where", Map.class);
         resolver = whereWithMap.invoke(resolver, ImmutableMap.of(
               typeParam1.getTypeVariable(), typeArg1.getType(),
               typeParam2.getTypeVariable(), typeArg2.getType()));
         Invokable<Object, Type> resolveType = Reflection2.method(
               (Class<Object>) resolver.getClass(), "resolveType", Type.class);
         resolvedType = resolveType.invoke(resolver, getType());
      } catch (IllegalAccessException exception) {
         // should never happen
         throw new IllegalStateException(exception);
      } catch (InvocationTargetException exception) {
         // should never happen
         throw new IllegalStateException(exception);
      } catch (SecurityException exception) {
         // should never happen
         throw new IllegalStateException(exception);
      } catch (IllegalArgumentException exception) {
         // should never happen
         throw new IllegalStateException(exception);
      } catch (NoSuchFieldException exception) {
         // should never happen
         throw new IllegalStateException(exception);
      } catch (NoSuchMethodException exception) {
         // should never happen
         throw new IllegalStateException(exception);
      }
      return (TypeToken<T>) TypeToken.of(resolvedType);
   }

   private static Object newTypeResolver() throws SecurityException, NoSuchFieldException,
         IllegalArgumentException, IllegalAccessException, InvocationTargetException,
         NoSuchMethodException {
      Field typeResolverField = TypeToken.class.getDeclaredField("typeResolver");
      Invokable<?, ?> typeResolverContrucutor =
         Invokable.from(typeResolverField.getType().getConstructor());
      return typeResolverContrucutor.invoke(null);
   }

   public <X, Y> TypeToken<T> where(TypeParameter2<X> typeParam1, Class<X> typeArg1,
         TypeParameter2<Y> typeParam2, Class<Y> typeArg2) {
      return where(typeParam1, of(typeArg1), typeParam2, of(typeArg2));
   }

   public abstract static class TypeParameter2<T> extends TypeParameter<T> {
      TypeVariable<?> getTypeVariable() {
         // duplicated from TypeCapture, where it's package-private
         Type superclass = getClass().getGenericSuperclass();
         return (TypeVariable<?>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
      }
   }
}
