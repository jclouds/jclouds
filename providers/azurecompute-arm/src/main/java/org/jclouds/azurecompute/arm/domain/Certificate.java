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
public abstract class Certificate {
   @AutoValue
   public abstract static class CertificateAttributes {
      @Nullable
      public abstract Integer created();

      public abstract boolean enabled();

      @Nullable
      public abstract Integer expiry();

      @Nullable
      public abstract Integer notBefore();

      @Nullable
      public abstract String recoveryLevel();

      @Nullable
      public abstract Integer updated();

      @SerializedNames({"created", "enabled", "exp", "nbf", "recoveryLevel", "updated"})
      public static CertificateAttributes create(final Integer created,
                                                 final boolean enabled,
                                                 final Integer expiry,
                                                 final Integer notBefore,
                                                 final String recoveryLevel,
                                                 final Integer updated) {
         return new AutoValue_Certificate_CertificateAttributes(created, enabled, expiry, notBefore, recoveryLevel, updated);
      }
   }

   @AutoValue
   public abstract static class IssuerParameters {
      @Nullable
      public abstract String certType();

      @Nullable
      public abstract String name();

      @SerializedNames({"cty", "name"})
      public static IssuerParameters create(final String certType,
                                            final String name) {
         return new AutoValue_Certificate_IssuerParameters(certType, name);
      }
   }

   @AutoValue
   public abstract static class KeyProperties {
      @Nullable
      public abstract Boolean exportable();

      @Nullable
      public abstract Integer keySize();

      @Nullable
      public abstract String keyType();

      @Nullable
      public abstract Boolean reuseKey();

      @SerializedNames({"exportable", "key_size", "kty", "reuse_key"})
      public static KeyProperties create(final boolean exportable,
                                         final Integer keySize,
                                         final String keyType,
                                         final boolean reuseKey) {
         return new AutoValue_Certificate_KeyProperties(exportable, keySize, keyType, reuseKey);
      }
   }

   @AutoValue
   public abstract static class LifetimeActionTrigger {
      @Nullable
      public abstract Integer daysBeforeExpiry();

      @Nullable
      public abstract Integer lifetimePercentage();

      @SerializedNames({"days_before_expiry", "lifetime_percentage"})
      public static LifetimeActionTrigger create(final Integer daysBeforeExpiry,
                                                 final Integer lifetimePercentage) {
         return new AutoValue_Certificate_LifetimeActionTrigger(daysBeforeExpiry, lifetimePercentage);
      }
   }

   @AutoValue
   public abstract static class LifetimeActionAction {
      public abstract String actionType();

      @SerializedNames({"action_type"})
      public static LifetimeActionAction create(final String actionType) {
         return new AutoValue_Certificate_LifetimeActionAction(actionType);
      }
   }

   @AutoValue
   public abstract static class LifetimeAction {
      public abstract LifetimeActionAction action();

      public abstract LifetimeActionTrigger trigger();

      @SerializedNames({"action", "trigger"})
      public static LifetimeAction create(final LifetimeActionAction action,
                                          final LifetimeActionTrigger trigger) {
         return new AutoValue_Certificate_LifetimeAction(action, trigger);
      }
   }

   @AutoValue
   public abstract static class SecretProperties {
      public abstract String contentType();

      @SerializedNames({"contentType"})
      public static SecretProperties create(final String contentType) {
         return new AutoValue_Certificate_SecretProperties(contentType);
      }
   }

   @AutoValue
   public abstract static class SubjectAlternativeNames {
      public abstract List<String> dnsNames();

      public abstract List<String> emails();

      public abstract List<String> upns();

      @SerializedNames({"dns_names", "emails", "upns"})
      public static SubjectAlternativeNames create(final List<String> dnsNames,
                                                   final List<String> emails,
                                                   final List<String> upns) {
         return new AutoValue_Certificate_SubjectAlternativeNames(
                 dnsNames != null ? ImmutableList.copyOf(dnsNames) : ImmutableList.<String> of(),
                 emails != null ? ImmutableList.copyOf(emails) : ImmutableList.<String> of(),
                 upns != null ? ImmutableList.copyOf(upns) : ImmutableList.<String> of()
         );
      }
   }

   @AutoValue
   public abstract static class X509CertificateProperties {
      public abstract List<String> enhancedKeyUsage();

      public abstract List<String> keyUsage();

      @Nullable
      public abstract SubjectAlternativeNames subjectAltNames();

      @Nullable
      public abstract String subject();

      @Nullable
      public abstract Integer validityMonths();

      @SerializedNames({"ekus", "key_usage", "sans", "subject", "validity_months"})
      public static X509CertificateProperties create(final List<String> enhancedKeyUsage,
                                                     final List<String> keyUsage,
                                                     final SubjectAlternativeNames subjectAltNames,
                                                     final String subject,
                                                     final Integer validityMonths) {
         return new AutoValue_Certificate_X509CertificateProperties(
                 enhancedKeyUsage != null ? ImmutableList.copyOf(enhancedKeyUsage) : ImmutableList.<String> of(),
                 keyUsage != null ? ImmutableList.copyOf(keyUsage) : ImmutableList.<String> of(),
                 subjectAltNames,
                 subject,
                 validityMonths
         );
      }
   }

   @AutoValue
   public abstract static class CertificatePolicy {
      @Nullable
      public abstract CertificateAttributes attributes();

      @Nullable
      public abstract String id();

      @Nullable
      public abstract IssuerParameters issuer();

      @Nullable
      public abstract KeyProperties keyProps();

      public abstract List<LifetimeAction> lifetimeActions();

      @Nullable
      public abstract SecretProperties secretProps();

      @Nullable
      public abstract X509CertificateProperties x509props();

      @SerializedNames({"attributes", "id", "issuer", "key_props", "lifetime_actions", "secret_props", "x509_props"})
      public static CertificatePolicy create(final CertificateAttributes attributes,
                                             final String id,
                                             final IssuerParameters issuer,
                                             final KeyProperties keyProps,
                                             final List<LifetimeAction> lifetimeActions,
                                             final SecretProperties secretProperties,
                                             final X509CertificateProperties x509Props) {
         return new AutoValue_Certificate_CertificatePolicy(
                 attributes,
                 id,
                 issuer,
                 keyProps,
                 lifetimeActions != null ? ImmutableList.copyOf(lifetimeActions) : ImmutableList.<LifetimeAction>of(),
                 secretProperties,
                 x509Props
         );
      }
   }

   @AutoValue
   public abstract static class CertificateError {
      @Nullable
      public abstract String code();

      @Nullable
      public abstract String message();

      @SerializedNames({"code", "message"})
      public static CertificateError create(final String code,
                                            final String message) {
         return new AutoValue_Certificate_CertificateError(code, message);
      }
   }

   @AutoValue
   public abstract static class CertificateOperation {
      @Nullable
      public abstract Boolean cancellationRequested();

      @Nullable
      public abstract String csr();

      @Nullable
      public abstract CertificateError error();

      @Nullable
      public abstract String id();

      @Nullable
      public abstract IssuerParameters issuer();

      @Nullable
      public abstract String requestId();

      @Nullable
      public abstract String status();

      @Nullable
      public abstract String statusDetails();

      @Nullable
      public abstract String target();

      @SerializedNames({"cancellation_requested", "csr", "error", "id", "issuer", "request_id", "status", "status_details", "target"})
      public static CertificateOperation create(final boolean cancellationRequested,
                                                final String csr,
                                                final CertificateError error,
                                                final String id,
                                                final IssuerParameters issuer,
                                                final String requestId,
                                                final String status,
                                                final String statusDetails,
                                                final String target) {
         return new AutoValue_Certificate_CertificateOperation(
                 cancellationRequested,
                 csr,
                 error,
                 id,
                 issuer,
                 requestId,
                 status,
                 statusDetails,
                 target);
      }
   }

   @AutoValue
   public abstract static class CertificateBundle {
      @Nullable
      public abstract CertificateAttributes attributes();

      @Nullable
      public abstract String certificate();

      @Nullable
      public abstract String contentType();

      @Nullable
      public abstract String id();

      @Nullable
      public abstract String keyId();

      @Nullable
      public abstract CertificatePolicy policy();

      @Nullable
      public abstract String secretId();

      @Nullable
      public abstract Map<String, String> tags();

      @Nullable
      public abstract String thumbprint();

      @SerializedNames({"attributes", "cer", "contentType", "id", "kid", "policy", "sid", "tags", "x5t"})
      public static CertificateBundle create(final CertificateAttributes attributes,
                                             final String certificate,
                                             final String contentType,
                                             final String id,
                                             final String keyId,
                                             final CertificatePolicy policy,
                                             final String secretId,
                                             final Map<String, String> tags,
                                             final String thumbprint) {
         return new AutoValue_Certificate_CertificateBundle(attributes,
                 certificate,
                 contentType,
                 id,
                 keyId,
                 policy,
                 secretId,
                 tags != null ? ImmutableMap.copyOf(tags) : null,
                 thumbprint
         );
      }
   }

   @AutoValue
   public abstract static class CertificateIssuer {
      public abstract String id();

      public abstract String provider();

      @SerializedNames({"id", "provider"})
      public static CertificateIssuer create(final String id,
                                             final String provider) {
         return new AutoValue_Certificate_CertificateIssuer(id, provider);
      }
   }

   @AutoValue
   public abstract static class IssuerAttributes {
      @Nullable
      public abstract Integer created();

      @Nullable
      public abstract Boolean enabled();

      @Nullable
      public abstract Integer updated();

      @SerializedNames({"created", "enabled", "updated"})
      public static IssuerAttributes create(final Integer created,
                                            final Boolean enabled,
                                            final Integer updated) {
         return new AutoValue_Certificate_IssuerAttributes(created, enabled, updated);
      }
   }

   @AutoValue
   public abstract static class IssuerCredentials {
      @Nullable
      public abstract String accountId();

      @Nullable
      public abstract String password();

      @SerializedNames({"account_id", "pwd"})
      public static IssuerCredentials create(final String accountId,
                                             final String password) {
         return new AutoValue_Certificate_IssuerCredentials(accountId, password);
      }
   }

   @AutoValue
   public abstract static class OrganizationDetails {
      public abstract List<AdministrationDetails> adminDetails();

      @Nullable
      public abstract String id();

      @SerializedNames({"admin_details", "id"})
      public static OrganizationDetails create(final List<AdministrationDetails> adminDetails,
                                               final String id) {
         return new AutoValue_Certificate_OrganizationDetails(
                 adminDetails != null ? ImmutableList.copyOf(adminDetails) : ImmutableList.<AdministrationDetails> of(),
                 id
         );
      }
   }

   @AutoValue
   public abstract static class AdministrationDetails {
      @Nullable
      public abstract String email();

      @Nullable
      public abstract String firstName();

      @Nullable
      public abstract String lastName();

      @Nullable
      public abstract String phoneNumber();

      @SerializedNames({"email", "first_name", "last_name", "phone"})
      public static AdministrationDetails create(final String email,
                                                 final String firstName,
                                                 final String lastName,
                                                 final String phoneNumber) {
         return new AutoValue_Certificate_AdministrationDetails(email, firstName, lastName, phoneNumber);
      }
   }

   @AutoValue
   public abstract static class IssuerBundle {
      @Nullable
      public abstract IssuerAttributes attributes();

      @Nullable
      public abstract IssuerCredentials credentials();

      @Nullable
      public abstract String id();

      @Nullable
      public abstract OrganizationDetails organizationDetails();

      @Nullable
      public abstract String provider();

      @SerializedNames({"attributes", "credentials", "id", "org_details", "provider"})
      public static IssuerBundle create(final IssuerAttributes attributes,
                                        final IssuerCredentials credentials,
                                        final String id,
                                        final OrganizationDetails orgDetails,
                                        final String provider) {
         return new AutoValue_Certificate_IssuerBundle(attributes, credentials, id, orgDetails, provider);
      }
   }

   @AutoValue
   public abstract static class Contact {
      @Nullable
      public abstract String email();

      @Nullable
      public abstract String name();

      @Nullable
      public abstract String phone();

      @SerializedNames({"email", "name", "phone"})
      public static Contact create(final String email,
                                   final String name,
                                   final String phone) {
         return new AutoValue_Certificate_Contact(email, name, phone);
      }
   }

   @AutoValue
   public abstract static class Contacts {
      public abstract List<Contact> contacts();

      @Nullable
      public abstract String id();

      @SerializedNames({"contacts", "id"})
      public static Contacts create(final List<Contact> contacts,
                                    final String id) {
         return new AutoValue_Certificate_Contacts(
                 contacts != null ? ImmutableList.copyOf(contacts) : ImmutableList.<Contact> of(),
                 id
         );
      }
   }

   @AutoValue
   public abstract static class DeletedCertificateBundle {
      @Nullable
      public abstract CertificateAttributes attributes();

      @Nullable
      public abstract String bytes();

      @Nullable
      public abstract Integer deletedDate();

      @Nullable
      public abstract String id();

      @Nullable
      public abstract String keyId();

      @Nullable
      public abstract String recoveryId();

      @Nullable
      public abstract Integer scheduledPurgeDate();

      @Nullable
      public abstract String secredId();

      @Nullable
      public abstract Map<String, String> tags();

      @Nullable
      public abstract String thumbprint();

      @SerializedNames({"attributes", "cer", "deletedDate", "id", "kid", "recoveryId", "scheduledPurgeDate", "sid", "tags", "x5t"})
      public static DeletedCertificateBundle create(final CertificateAttributes attributes,
                                                    final String bytes,
                                                    final Integer deletedDate,
                                                    final String id,
                                                    final String keyId,
                                                    final String recoveryId,
                                                    final Integer scheduledPurgeDate,
                                                    final String secretId,
                                                    final Map<String, String> tags,
                                                    final String thumbprint) {
         return new AutoValue_Certificate_DeletedCertificateBundle(
                 attributes,
                 bytes,
                 deletedDate,
                 id,
                 keyId,
                 recoveryId,
                 scheduledPurgeDate,
                 secretId,
                 tags != null ? ImmutableMap.copyOf(tags) : null,
                 thumbprint
         );
      }
   }

   @AutoValue
   public abstract static class DeletedCertificate {
      @Nullable
      public abstract CertificateAttributes attributes();

      @Nullable
      public abstract Integer deletedDate();

      @Nullable
      public abstract String id();

      @Nullable
      public abstract String recoveryId();

      @Nullable
      public abstract Integer scheduledPurgeDate();

      @Nullable
      public abstract Map<String, String> tags();

      @Nullable
      public abstract String thumbprint();

      @SerializedNames({"attributes", "deletedDate", "id", "recoveryId", "scheduledPurgeDate", "tags", "x5t"})
      public static DeletedCertificate create(final CertificateAttributes attributes,
                                              final Integer deletedDate,
                                              final String id,
                                              final String recoveryId,
                                              final Integer scheduledPurgeDate,
                                              final Map<String, String> tags,
                                              final String thumbprint) {
         return new AutoValue_Certificate_DeletedCertificate(
                 attributes,
                 deletedDate,
                 id,
                 recoveryId,
                 scheduledPurgeDate,
                 tags != null ? ImmutableMap.copyOf(tags) : null,
                 thumbprint
         );
      }
   }

   @Nullable
   public abstract CertificateAttributes attributes();

   @Nullable
   public abstract String id();

   @Nullable
   public abstract Map<String, String> tags();

   @Nullable
   public abstract String thumbprint();

   @SerializedNames({"attributes", "id", "tags", "x5t"})
   public static Certificate create(final CertificateAttributes attributes,
                                    final String id,
                                    final Map<String, String> tags,
                                    final String thumbprint) {
      return new AutoValue_Certificate(
              attributes,
              id,
              tags != null ? ImmutableMap.copyOf(tags) : null,
              thumbprint
      );
   }

   Certificate() {
   }
}
