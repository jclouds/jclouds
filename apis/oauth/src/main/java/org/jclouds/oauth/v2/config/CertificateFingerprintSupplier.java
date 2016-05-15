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
package org.jclouds.oauth.v2.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static org.jclouds.crypto.Pems.x509Certificate;
import static org.jclouds.oauth.v2.config.OAuthProperties.CERTIFICATE;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javax.inject.Named;

import org.jclouds.domain.Credentials;
import org.jclouds.location.Provider;
import org.jclouds.oauth.v2.domain.CertificateFingerprint;
import org.jclouds.rest.AuthorizationException;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.hash.Hashing;
import static com.google.common.io.BaseEncoding.base64;
import com.google.common.hash.HashCode;

/**
 * Loads the fingerprint of a certificate associated with the {@link PrivateKey} from a pem X509Certificate.
 */
@Singleton // due to cache
final class CertificateFingerprintSupplier implements Supplier<CertificateFingerprint> {

    private final Supplier<Credentials> creds;
    private final LoadingCache<Credentials, CertificateFingerprint> certCache;

    @Inject CertificateFingerprintSupplier(@Provider Supplier<Credentials> creds, CertificateFingerprintForCredentials loader) {
        this.creds = creds;
        // throw out the certificate fingerprint related to old credentials
        this.certCache = CacheBuilder.newBuilder().maximumSize(2).build(checkNotNull(loader, "loader"));
    }

    /**
     * it is relatively expensive to extract a certificate from a PEM and calculate it's fingerprint.
     * cache the relationship between current credentials so that the fingerprint is only recalculated once.
     */
    @VisibleForTesting
    static final class CertificateFingerprintForCredentials extends CacheLoader<Credentials, CertificateFingerprint> {
        @Inject(optional = true) @Named(CERTIFICATE) String certInPemFormat;

        @Override public CertificateFingerprint load(Credentials in) {
            try {
                /**
                 * CERTIFICATE made optional on injection so that it's not required when other OAuth methods
                 * are used.
                 */
                if (certInPemFormat == null) {
                    throw new IllegalArgumentException("certificate not specified.");
                }
                X509Certificate cert = null;
                cert = x509Certificate(certInPemFormat);

                /** Get the fingerprint in Base64 format */
                byte[] encodedCert = cert.getEncoded();
                HashCode hash = Hashing.sha1().hashBytes(encodedCert);
                String fingerprint = base64().encode(hash.asBytes());

                return CertificateFingerprint.create(fingerprint, cert);
            } catch (CertificateException e) {
                throw new AssertionError(e);
            } catch (IOException e) {
                throw propagate(e);
            } catch (IllegalArgumentException e) {
                throw new AuthorizationException("cannot parse cert. " + e.getMessage(), e);
            }
        }
    }

    @Override public CertificateFingerprint get() {
        try {
            // loader always throws UncheckedExecutionException so no point in using get()
            return certCache.getUnchecked(checkNotNull(creds.get(), "credential supplier returned null"));
        } catch (UncheckedExecutionException e) {
            AuthorizationException authorizationException = getFirstThrowableOfType(e, AuthorizationException.class);
            if (authorizationException != null) {
                throw authorizationException;
            }
            throw e;
        }
    }
}

