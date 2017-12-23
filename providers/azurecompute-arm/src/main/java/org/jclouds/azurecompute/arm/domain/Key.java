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
package org.jclouds.azurecompute.arm.domain;

import java.util.Map;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Key {
    @AutoValue
    public abstract static class JsonWebKey {
        @Nullable
        public abstract String crv();

        @Nullable
        public abstract String d();

        @Nullable
        public abstract String dp();

        @Nullable
        public abstract String dq();

        @Nullable
        public abstract String e();

        @Nullable
        public abstract String k();

        @Nullable
        public abstract String keyHsm();

        public abstract List<String> keyOps();

        @Nullable
        public abstract String kid();

        @Nullable
        public abstract String kty();

        @Nullable
        public abstract String n();

        @Nullable
        public abstract String p();

        @Nullable
        public abstract String q();

        @Nullable
        public abstract String qi();

        @Nullable
        public abstract String x();

        @Nullable
        public abstract String y();

        @SerializedNames({"crv", "d", "dp", "dq", "e", "k", "key_hsm", "key_ops", "kid", "kty", "n", "p", "q", "qi", "x", "y"})
        public static JsonWebKey create(final String crv, final String d, final String dp, final String dq,
                                        final String e, final String k, final String keyHsm, final List<String> keyOps,
                                        final String kid, final String kty, final String n, final String p,
                                        final String q, final String qi, final String x, final String y) {
            return new AutoValue_Key_JsonWebKey(
                    crv, d, dp, dq, e, k, keyHsm,
                    keyOps != null ? ImmutableList.copyOf(keyOps) : ImmutableList.<String> of(),
                    kid, kty, n, p, q, qi, x, y);
        }
    }

    @AutoValue
    public abstract static class KeyAttributes {
        @Nullable
        public abstract Boolean enabled();

        @Nullable
        public abstract Integer created();

        @Nullable
        public abstract Integer expires();

        @Nullable
        public abstract Integer notBefore();

        @Nullable
        public abstract String recoveryLevel();

        @Nullable
        public abstract Integer updated();

        @SerializedNames({"enabled", "created", "expires", "notBefore", "recoveryLevel", "updated"})
        public static KeyAttributes create(final Boolean enabled,
                                           final Integer created,
                                           final Integer expires,
                                           final Integer notBefore,
                                           final String recoveryLevel,
                                           final Integer updated) {
            return new AutoValue_Key_KeyAttributes(enabled, created, expires, notBefore, recoveryLevel, updated);
        }

        KeyAttributes() {
        }
    }

    @AutoValue
    public abstract static class KeyBundle {
        @Nullable
        public abstract KeyAttributes attributes();

        @Nullable
        public abstract JsonWebKey key();

        @Nullable
        public abstract Boolean managed();

        @Nullable
        public abstract Map<String, String> tags();

        @SerializedNames({"attributes", "key", "managed", "tags"})
        public static KeyBundle create(final KeyAttributes attributes, final JsonWebKey key, final boolean managed, final Map<String, String> tags) {
            return new AutoValue_Key_KeyBundle(
                    attributes,
                    key,
                    managed,
                    tags != null ? ImmutableMap.copyOf(tags) : null
            );
        }
    }

    @AutoValue
    public abstract static class DeletedKeyBundle {
        @Nullable
        public abstract KeyAttributes attributes();

        @Nullable
        public abstract String deletedDate();

        @Nullable
        public abstract JsonWebKey key();

        @Nullable
        public abstract Boolean managed();

        @Nullable
        public abstract String recoveryId();

        @Nullable
        public abstract String scheduledPurgeDate();

        @Nullable
        public abstract Map<String, String> tags();

        @SerializedNames({"attributes", "deletedDate", "key", "managed", "recoveryId", "scheduledPurgeDate", "tags"})
        public static DeletedKeyBundle create(final KeyAttributes attributes, final String deletedDate, final JsonWebKey key, final boolean managed, final String recoveryId, final String scheduledPurgeDate, final Map<String, String> tags) {
            return new AutoValue_Key_DeletedKeyBundle(
                    attributes,
                    deletedDate,
                    key,
                    managed,
                    recoveryId,
                    scheduledPurgeDate,
                    tags != null ? ImmutableMap.copyOf(tags) : null

            );
        }
    }

    @AutoValue
    public abstract static class KeyOperationResult {
        @Nullable
        public abstract String keyId();

        @Nullable
        public abstract String value();

        @SerializedNames({"kid", "value"})
        public static KeyOperationResult create(final String keyId, final String value) {
            return new AutoValue_Key_KeyOperationResult(
                    keyId,
                    value
            );
        }
    }

    @Nullable
    public abstract String kid();

    public abstract KeyAttributes attributes();

    @Nullable
    public abstract Boolean managed();

    @Nullable
    public abstract Map<String, String> tags();

    @SerializedNames({"kid", "attributes", "managed", "tags"})
    public static Key create(final String kid, final KeyAttributes attributes, final boolean managed, final Map<String, String> tags) {
        return new AutoValue_Key(
                kid,
                attributes,
                managed,
                tags != null ? ImmutableMap.copyOf(tags) : null
        );
    }

    Key() {
    }
}
