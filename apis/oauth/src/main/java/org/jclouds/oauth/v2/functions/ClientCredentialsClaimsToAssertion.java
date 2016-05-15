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
package org.jclouds.oauth.v2.functions;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Joiner.on;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.io.BaseEncoding.base64Url;
import static org.jclouds.oauth.v2.config.OAuthProperties.JWS_ALG;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.json.Json;
import org.jclouds.oauth.v2.config.Authorization;
import org.jclouds.oauth.v2.domain.CertificateFingerprint;
import org.jclouds.rest.AuthorizationException;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

public final class ClientCredentialsClaimsToAssertion implements Function<Object, String> {
    private static final List<String> SUPPORTED_ALGS = ImmutableList.of("RS256", "none");

    private final Supplier<PrivateKey> privateKey;
    private final Supplier<CertificateFingerprint> certFingerprint;
    private final Json json;
    private final String alg;

    @Inject ClientCredentialsClaimsToAssertion(@Named(JWS_ALG) String alg,
                                               @Authorization Supplier<PrivateKey> privateKey,
                                               @Authorization Supplier<CertificateFingerprint> certFingerprint,
                                               Json json) {
        this.alg = alg;
        checkArgument(SUPPORTED_ALGS.contains(alg), "%s %s not in supported list", JWS_ALG, alg, SUPPORTED_ALGS);
        this.privateKey = privateKey;
        this.certFingerprint = certFingerprint;
        this.json = json;
    }

    @Override public String apply(Object input) {
        String encodedHeader = String.format("{\"alg\":\"%s\",\"typ\":\"JWT\",\"x5t\":\"%s\"}", alg, certFingerprint.get().fingerprint());
        String encodedClaimSet = json.toJson(input);

        encodedHeader = base64Url().omitPadding().encode(encodedHeader.getBytes(UTF_8));
        encodedClaimSet = base64Url().omitPadding().encode(encodedClaimSet.getBytes(UTF_8));

        byte[] signature = alg.equals("none")
                ? null
                : sha256(privateKey.get(), on(".").join(encodedHeader, encodedClaimSet).getBytes(UTF_8));
        String encodedSignature = signature != null ?  base64Url().omitPadding().encode(signature) : "";

        // the final assertion in base 64 encoded {header}.{claimSet}.{signature} format
        return on(".").join(encodedHeader, encodedClaimSet, encodedSignature);
    }

    static byte[] sha256(PrivateKey privateKey, byte[] input) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(input);
            return signature.sign();
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        } catch (SignatureException e) {
            throw new AuthorizationException(e);
        } catch (InvalidKeyException e) {
            throw new AuthorizationException(e);
        }
    }
}
