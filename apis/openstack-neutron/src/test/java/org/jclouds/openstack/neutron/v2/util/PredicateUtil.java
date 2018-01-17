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

package org.jclouds.openstack.neutron.v2.util;

import com.google.common.base.Predicate;
import org.jclouds.javax.annotation.Nullable;

import java.lang.reflect.Field;

public class PredicateUtil {

    public static <T> Predicate<T> createIdEqualsPredicate(final String id) {
        return new Predicate<T>() {
            @Override
            public boolean apply(@Nullable T input) {
                if (input == null) return false;

                try {
                    Class clazz = input.getClass();
                    Field field = ClassUtil.findField(clazz, "id");
                    field.setAccessible(true);
                    String value = (String) field.get(input);
                    field.setAccessible(false);
                    return value != null && value.equals(id);
                } catch (IllegalAccessException iae) {
                    return false;
                }
            }
        };
    }

}
