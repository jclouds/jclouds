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
package org.jclouds.azurecompute.arm.features;

import java.net.URI;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.azurecompute.arm.config.OAuthResource;
import org.jclouds.azurecompute.arm.domain.Certificate;
import org.jclouds.azurecompute.arm.domain.Certificate.CertificateAttributes;
import org.jclouds.azurecompute.arm.domain.Certificate.CertificateBundle;
import org.jclouds.azurecompute.arm.domain.Certificate.CertificateIssuer;
import org.jclouds.azurecompute.arm.domain.Certificate.CertificateOperation;
import org.jclouds.azurecompute.arm.domain.Certificate.CertificatePolicy;
import org.jclouds.azurecompute.arm.domain.Certificate.Contact;
import org.jclouds.azurecompute.arm.domain.Certificate.Contacts;
import org.jclouds.azurecompute.arm.domain.Certificate.DeletedCertificate;
import org.jclouds.azurecompute.arm.domain.Certificate.DeletedCertificateBundle;
import org.jclouds.azurecompute.arm.domain.Certificate.IssuerAttributes;
import org.jclouds.azurecompute.arm.domain.Certificate.IssuerBundle;
import org.jclouds.azurecompute.arm.domain.Certificate.IssuerCredentials;
import org.jclouds.azurecompute.arm.domain.Certificate.IssuerParameters;
import org.jclouds.azurecompute.arm.domain.Certificate.KeyProperties;
import org.jclouds.azurecompute.arm.domain.Certificate.LifetimeAction;
import org.jclouds.azurecompute.arm.domain.Certificate.OrganizationDetails;
import org.jclouds.azurecompute.arm.domain.Certificate.SecretProperties;
import org.jclouds.azurecompute.arm.domain.Certificate.X509CertificateProperties;
import org.jclouds.azurecompute.arm.domain.Key;
import org.jclouds.azurecompute.arm.domain.Key.DeletedKeyBundle;
import org.jclouds.azurecompute.arm.domain.Key.JsonWebKey;
import org.jclouds.azurecompute.arm.domain.Key.KeyAttributes;
import org.jclouds.azurecompute.arm.domain.Key.KeyBundle;
import org.jclouds.azurecompute.arm.domain.Key.KeyOperationResult;
import org.jclouds.azurecompute.arm.domain.Secret;
import org.jclouds.azurecompute.arm.domain.Secret.DeletedSecretBundle;
import org.jclouds.azurecompute.arm.domain.Secret.SecretAttributes;
import org.jclouds.azurecompute.arm.domain.Secret.SecretBundle;
import org.jclouds.azurecompute.arm.domain.Vault;
import org.jclouds.azurecompute.arm.domain.Vault.DeletedVault;
import org.jclouds.azurecompute.arm.domain.VaultProperties;
import org.jclouds.azurecompute.arm.filters.ApiVersionFilter;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Function;

@RequestFilters({ OAuthFilter.class, ApiVersionFilter.class })
@Consumes(MediaType.APPLICATION_JSON)
public interface VaultApi {

   String VAULT_API_STANDARD_ENDPOINT = "https://vault.azure.net";
   String VAULT_API_CHINA_ENDPOINT = "https://vault.azure.cn";

   static class PrependSlashOrEmptyString implements Function<Object, String> {
      public String apply(Object from) {
         if ((from == null) || (from.toString().length() == 0)) {
            return "";
         } else {
            return "/" + from.toString();
         }
      }
   }

   // Vault operations
   @Named("vault:list")
   @SelectJson("value")
   @GET
   @Path("/resourcegroups/{resourcegroup}/providers/Microsoft.KeyVault/vaults")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Vault> listVaults();

   @Named("vault:create_or_update")
   @PUT
   @MapBinder(BindToJsonPayload.class)
   @Path("/resourcegroups/{resourcegroup}/providers/Microsoft.KeyVault/vaults/{vaultName}")
   Vault createOrUpdateVault(@PathParam("vaultName") String vaultName, @PayloadParam("location") String location,
         @PayloadParam("properties") VaultProperties properties, @Nullable @PayloadParam("tags") Map<String, String> tags);

   @Named("vault:get")
   @Path("/resourcegroups/{resourcegroup}/providers/Microsoft.KeyVault/vaults/{vaultName}")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   Vault getVault(@PathParam("vaultName") String vaultName);

   @Named("vault:delete")
   @Path("/resourcegroups/{resourcegroup}/providers/Microsoft.KeyVault/vaults/{vaultName}")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   boolean deleteVault(@PathParam("vaultName") String vaultName);

   @Named("vault:list_deleted_vaults")
   @Path("/providers/Microsoft.KeyVault/deletedVaults")
   @GET
   @SelectJson("value")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<DeletedVault> listDeletedVaults();

   @Named("vault:get_deleted")
   @GET
   @Path("/providers/Microsoft.KeyVault/locations/{location}/deletedVaults/{vaultName}")
   @Fallback(NullOnNotFoundOr404.class)
   DeletedVault getDeletedVault(@PathParam("location") String location, @PathParam("vaultName") String vaultName);

   @Named("vault:purge")
   @POST
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/providers/Microsoft.KeyVault/locations/{location}/deletedVaults/{vaultName}/purge")
   boolean purgeVault(@PathParam("location") String location, @PathParam("vaultName") String vaultName);

   // Key operations
   @Named("key:list")
   @SelectJson("value")
   @GET
   @Fallback(EmptyListOnNotFoundOr404.class)
   @Path("/keys")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   List<Key> listKeys(@EndpointParam URI keyVaultUri);

   @Named("key:create")
   @POST
   @MapBinder(BindToJsonPayload.class)
   @Path("/keys/{keyName}/create")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   KeyBundle createKey(@EndpointParam URI vaultBaseUrl, @PathParam("keyName") String keyName,
         @Nullable @PayloadParam("attributes") KeyAttributes attributes,
         @Nullable @PayloadParam("crv") String curveName, @Nullable @PayloadParam("key_ops") List<String> keyOps,
         @PayloadParam("key_size") int keySize, @PayloadParam("kty") String keyType,
         @Nullable @PayloadParam("tags") Map<String, String> tags);

   @Named("key:import")
   @PUT
   @MapBinder(BindToJsonPayload.class)
   @Path("/keys/{keyName}")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   KeyBundle importKey(@EndpointParam URI vaultBaseUrl, @PathParam("keyName") String keyName,
         @PayloadParam("Hsm") boolean hsm, @Nullable @PayloadParam("attributes") KeyAttributes attributes,
         @Nullable @PayloadParam("key") JsonWebKey key, @Nullable @PayloadParam("tags") Map<String, String> tags);

   @Named("key:get")
   @GET
   @Path("/keys/{keyName}")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   KeyBundle getKey(@EndpointParam URI vaultBaseUrl, @PathParam("keyName") String keyName);

   @Named("key:delete")
   @DELETE
   @Path("/keys/{keyName}")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   DeletedKeyBundle deleteKey(@EndpointParam URI vaultBaseUrl, @PathParam("keyName") String keyName);

   @Named("key:get_versions")
   @GET
   @SelectJson("value")
   @Path("/keys/{keyName}/versions")
   @Fallback(EmptyListOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   List<Key> getKeyVersions(@EndpointParam URI vaultBaseUrl, @PathParam("keyName") String keyName);

   @Named("key:update")
   @PATCH
   @MapBinder(BindToJsonPayload.class)
   @Path("/keys/{keyName}{keyVersion}")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   KeyBundle updateKey(@EndpointParam URI vaultBaseUrl,
                       @PathParam("keyName") String keyName,
                       @Nullable @PathParam("keyVersion") @ParamParser(PrependSlashOrEmptyString.class) String keyVersion,
                       @Nullable @PayloadParam("attributes") KeyAttributes attributes,
                       @Nullable @PayloadParam("key_ops") List<String> keyOps,
                       @Nullable @PayloadParam("tags") Map<String, String> tags);

   @Named("key:backup")
   @POST
   @SelectJson("value")
   @Path("/keys/{keyName}/backup")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   String backupKey(@EndpointParam URI vaultBaseUrl, @PathParam("keyName") String keyName);

   @Named("key:restore")
   @POST
   @MapBinder(BindToJsonPayload.class)
   @Path("/keys/restore")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   KeyBundle restoreKey(@EndpointParam URI vaultBaseUrl, @PayloadParam("value") String keyInfo);

   // Soft-delete key operations
   @Named("key:list_deleted")
   @GET
   @SelectJson("value")
   @Path("/deletedkeys")
   @Fallback(EmptyListOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   List<DeletedKeyBundle> listDeletedKeys(@EndpointParam URI vaultBaseUrl);

   @Named("key:get_deleted")
   @GET
   @Path("/deletedkeys/{keyName}")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   DeletedKeyBundle getDeletedKey(@EndpointParam URI vaultBaseUrl, @PathParam("keyName") String keyName);

   @Named("key:recover_deleted")
   @POST
   @Path("/deletedkeys/{keyName}/recover")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   KeyBundle recoverDeletedKey(@EndpointParam URI vaultBaseUrl, @PathParam("keyName") String keyName);

   @Named("key:purge_deleted")
   @DELETE
   @Path("/deletedkeys/{keyName}")
   @Fallback(FalseOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   boolean purgeDeletedKey(@EndpointParam URI vaultBaseUrl, @PathParam("keyName") String keyName);

   // Key cryptographic operations
   @Named("key:crypto_encrypt")
   @POST
   @Path("/keys/{keyName}{keyVersion}/encrypt")
   @MapBinder(BindToJsonPayload.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   KeyOperationResult encrypt(@EndpointParam URI vaultBaseUrl,
                              @PathParam("keyName") String keyName,
                              @Nullable @PathParam("keyVersion") @ParamParser(PrependSlashOrEmptyString.class) String keyVersion,
                              @PayloadParam("alg") String algorithm,
                              @PayloadParam("value") String value);

   @Named("key:crypto_decrypt")
   @POST
   @Path("/keys/{keyName}{keyVersion}/decrypt")
   @MapBinder(BindToJsonPayload.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   KeyOperationResult decrypt(@EndpointParam URI vaultBaseUrl,
                              @PathParam("keyName") String keyName,
                              @Nullable @PathParam("keyVersion") @ParamParser(PrependSlashOrEmptyString.class) String keyVersion,
                              @PayloadParam("alg") String algorithm,
                              @PayloadParam("value") String value);

   @Named("key:crypto_sign")
   @POST
   @Path("/keys/{keyName}{keyVersion}/sign")
   @MapBinder(BindToJsonPayload.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   KeyOperationResult sign(@EndpointParam URI vaultBaseUrl,
                           @PathParam("keyName") String keyName,
                           @Nullable @PathParam("keyVersion") @ParamParser(PrependSlashOrEmptyString.class) String keyVersion,
                           @PayloadParam("alg") String algorithm,
                           @PayloadParam("value") String value);

   @Named("key:crypto_verify")
   @POST
   @Path("/keys/{keyName}{keyVersion}/verify")
   @MapBinder(BindToJsonPayload.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   boolean verify(@EndpointParam URI vaultBaseUrl,
                  @PathParam("keyName") String keyName,
                  @Nullable @PathParam("keyVersion")  @ParamParser(PrependSlashOrEmptyString.class) String keyVersion,
                  @PayloadParam("alg") String algorithm,
                  @PayloadParam("digest") String digest,
                  @PayloadParam("value") String value);

   @Named("key:crypto_wrap")
   @POST
   @Path("/keys/{keyName}{keyVersion}/wrapkey")
   @MapBinder(BindToJsonPayload.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   KeyOperationResult wrap(@EndpointParam URI vaultBaseUrl,
                           @PathParam("keyName") String keyName,
                           @Nullable @PathParam("keyVersion") @ParamParser(PrependSlashOrEmptyString.class) String keyVersion,
                           @PayloadParam("alg") String algorithm,
                           @PayloadParam("value") String value);

   @Named("key:crypto_unwrap")
   @POST
   @Path("/keys/{keyName}{keyVersion}/unwrapkey")
   @MapBinder(BindToJsonPayload.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   KeyOperationResult unwrap(@EndpointParam URI vaultBaseUrl,
                             @PathParam("keyName") String keyName,
                             @Nullable @PathParam("keyVersion") @ParamParser(PrependSlashOrEmptyString.class) String keyVersion,
                             @PayloadParam("alg") String algorithm,
                             @PayloadParam("value") String value);

   // Secret operations
   @Named("secret:list")
   @SelectJson("value")
   @GET
   @Fallback(EmptyListOnNotFoundOr404.class)
   @Path("/secrets")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   List<Secret> listSecrets(@EndpointParam URI keyVaultUri);

   @Named("secret:set")
   @PUT
   @MapBinder(BindToJsonPayload.class)
   @Path("/secrets/{secretName}")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   SecretBundle setSecret(@EndpointParam URI keyVaultUri, @PathParam("secretName") String secretName,
         @Nullable @PayloadParam("attributes") SecretAttributes attributes,
         @Nullable @PayloadParam("contentType") String contentType,
         @Nullable @PayloadParam("tags") Map<String, String> tags, @PayloadParam("value") String value);

   @Named("secret:get")
   @GET
   @Path("/secrets/{secretName}{secretVersion}")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   SecretBundle getSecret(@EndpointParam URI vaultBaseUrl,
                          @PathParam("secretName") String secretName,
                          @Nullable @PathParam("secretVersion") @ParamParser(PrependSlashOrEmptyString.class) String secretVersion);

   @Named("secret:delete")
   @DELETE
   @Path("/secrets/{secretName}")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   DeletedSecretBundle deleteSecret(@EndpointParam URI vaultBaseUrl, @PathParam("secretName") String secretName);

   @Named("secret:get_versions")
   @GET
   @SelectJson("value")
   @Path("/secrets/{secretName}/versions")
   @Fallback(EmptyListOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   List<Secret> getSecretVersions(@EndpointParam URI vaultBaseUrl, @PathParam("secretName") String secretName);

   @Named("secret:update")
   @PATCH
   @MapBinder(BindToJsonPayload.class)
   @Path("/secrets/{secretName}{secretVersion}")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   SecretBundle updateSecret(@EndpointParam URI vaultBaseUrl,
                             @PathParam("secretName") String secretName,
                             @Nullable @PathParam("secretVersion") @ParamParser(PrependSlashOrEmptyString.class) String secretVersion,
                             @Nullable @PayloadParam("attributes") SecretAttributes attributes,
                             @Nullable @PayloadParam("contentType") String contentType,
                             @Nullable @PayloadParam("tags") Map<String, String> tags);

   @Named("secret:backup")
   @POST
   @SelectJson("value")
   @Path("/secrets/{secretName}/backup")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   String backupSecret(@EndpointParam URI vaultBaseUrl, @PathParam("secretName") String secretName);

   @Named("secret:restore")
   @POST
   @MapBinder(BindToJsonPayload.class)
   @Path("/secrets/restore")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   SecretBundle restoreSecret(@EndpointParam URI vaultBaseUrl, @PayloadParam("value") String secretInfo);

   // Soft-delete secret operations
   @Named("secret:list_deleted")
   @GET
   @SelectJson("value")
   @Path("/deletedsecrets")
   @Fallback(EmptyListOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   List<DeletedSecretBundle> listDeletedSecrets(@EndpointParam URI vaultBaseUrl);

   @Named("secret:get_deleted")
   @GET
   @Path("/deletedsecrets/{secretName}")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   DeletedSecretBundle getDeletedSecret(@EndpointParam URI vaultBaseUrl, @PathParam("secretName") String secretName);

   @Named("secret:recover_deleted")
   @POST
   @Path("/deletedsecrets/{secretName}/recover")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   SecretBundle recoverDeletedSecret(@EndpointParam URI vaultBaseUrl, @PathParam("secretName") String secretName);

   @Named("secret:purge_deleted")
   @DELETE
   @Path("/deletedsecrets/{secretName}")
   @Fallback(FalseOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   boolean purgeDeletedSecret(@EndpointParam URI vaultBaseUrl, @PathParam("secretName") String secretName);

   // Certificate operations
   @Named("certificate:create")
   @POST
   @MapBinder(BindToJsonPayload.class)
   @Path("/certificates/{certificateName}/create")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   CertificateOperation createCertificate(@EndpointParam URI vaultBaseUrl,
         @PathParam("certificateName") String certificateName,
         @Nullable @PayloadParam("attributes") CertificateAttributes attributes,
         @Nullable @PayloadParam("policy") CertificatePolicy policy,
         @Nullable @PayloadParam("tags") Map<String, String> tags);

   @Named("certificate:get")
   @GET
   @Path("/certificates/{certificateName}{certificateVersion}")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   CertificateBundle getCertificate(@EndpointParam URI vaultBaseUrl,
                                    @PathParam("certificateName") String certificateName,
                                    @Nullable @PathParam("certificateVersion") @ParamParser(PrependSlashOrEmptyString.class) String certificateVersion);

   @Named("certificate:delete")
   @DELETE
   @Path("/certificates/{certificateName}")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   DeletedCertificateBundle deleteCertificate(@EndpointParam URI vaultBaseUrl,
         @PathParam("certificateName") String certificateName);

   @Named("certificate:list")
   @GET
   @SelectJson("value")
   @Path("/certificates")
   @Fallback(EmptyListOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   List<Certificate> getCertificates(@EndpointParam URI vaultBaseUrl);

   @Named("certificate:list_deleted")
   @GET
   @SelectJson("value")
   @Path("/deletedcertificates")
   @Fallback(EmptyListOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   List<DeletedCertificate> getDeletedCertificates(@EndpointParam URI vaultBaseUrl);

   @Named("certificate:get_deleted")
   @GET
   @Path("/deletedcertificates/{certificateName}")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   DeletedCertificateBundle getDeletedCertificate(@EndpointParam URI vaultBaseUrl,
         @PathParam("certificateName") String certificateName);

   @Named("certificate:recover_deleted")
   @POST
   @Path("/deletedcertificates/{certificateName}/recover")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   CertificateBundle recoverDeletedCertificate(@EndpointParam URI vaultBaseUrl,
         @PathParam("certificateName") String certificateName);

   @Named("certificate:purge_deleted")
   @DELETE
   @Path("/deletedcertificates/{certificateName}")
   @Fallback(FalseOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   boolean purgeDeletedCertificate(@EndpointParam URI vaultBaseUrl, @PathParam("certificateName") String certificateName);

   @Named("certificate:get_versions")
   @GET
   @SelectJson("value")
   @Path("/certificates/{certificateName}/versions")
   @Fallback(EmptyListOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   List<Certificate> getCertificateVersions(@EndpointParam URI vaultBaseUrl,
         @PathParam("certificateName") String certificateName);

   @Named("certificate:update")
   @PATCH
   @MapBinder(BindToJsonPayload.class)
   @Path("/certificates/{certificateName}{certificateVersion}")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   CertificateBundle updateCertificate(@EndpointParam URI vaultBaseUrl,
                                       @PathParam("certificateName") String certificateName,
                                       @Nullable @PathParam("certificateVersion") @ParamParser(PrependSlashOrEmptyString.class) String certificateVersion,
                                       @Nullable @PayloadParam("attributes") CertificateAttributes attributes,
                                       @Nullable @PayloadParam("policy") CertificatePolicy policy,
                                       @Nullable @PayloadParam("tags") Map<String, String> tags);

   @Named("certificate:import")
   @POST
   @MapBinder(BindToJsonPayload.class)
   @Path("/certificates/{certificateName}/import")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   CertificateBundle importCertificate(@EndpointParam URI vaultBaseUrl,
         @PathParam("certificateName") String certificateName,
         @Nullable @PayloadParam("attributes") CertificateAttributes attributes,
         @Nullable @PayloadParam("policy") CertificatePolicy policy, @Nullable @PayloadParam("pwd") String password,
         @Nullable @PayloadParam("tags") Map<String, String> tags, @PayloadParam("value") String value);

   @Named("certificate:merge")
   @POST
   @MapBinder(BindToJsonPayload.class)
   @Path("/certificates/{certificateName}/pending/merge")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   CertificateBundle mergeCertificate(@EndpointParam URI vaultBaseUrl,
         @PathParam("certificateName") String certificateName,
         @Nullable @PayloadParam("attributes") CertificateAttributes attributes,
         @Nullable @PayloadParam("tags") Map<String, String> tags, @PayloadParam("x5c") List<String> value);

   @Named("certificate:get_operation")
   @GET
   @Path("/certificates/{certificateName}/pending")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   CertificateOperation getCertificateOperation(@EndpointParam URI vaultBaseUrl,
         @PathParam("certificateName") String certificateName);

   @Named("certificate:update_operation")
   @PATCH
   @Path("/certificates/{certificateName}/pending")
   @MapBinder(BindToJsonPayload.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   CertificateOperation updateCertificateOperation(@EndpointParam URI vaultBaseUrl,
         @PathParam("certificateName") String certificateName,
         @PayloadParam("cancellation_requested") boolean cancellationRequested);

   @Named("certificate:delete_operation")
   @DELETE
   @Path("/certificates/{certificateName}/pending")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   CertificateOperation deleteCertificateOperation(@EndpointParam URI vaultBaseUrl,
         @PathParam("certificateName") String certificateName);

   @Named("certificate:set_issuer")
   @PUT
   @Path("/certificates/issuers/{issuerName}")
   @MapBinder(BindToJsonPayload.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   IssuerBundle setCertificateIssuer(@EndpointParam URI vaultBaseUrl, @PathParam("issuerName") String issuerName,
         @Nullable @PayloadParam("attributes") IssuerAttributes attributes,
         @Nullable @PayloadParam("credentials") IssuerCredentials credentials,
         @Nullable @PayloadParam("org_details") OrganizationDetails orgDetails,
         @PayloadParam("provider") String provider);

   @Named("certificate:get_issuers")
   @GET
   @SelectJson("value")
   @Path("/certificates/issuers")
   @Fallback(EmptyListOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   List<CertificateIssuer> getCertificateIssuers(@EndpointParam URI vaultBaseUrl);

   @Named("certificate:get_issuer")
   @GET
   @Path("/certificates/issuers/{issuerName}")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   IssuerBundle getCertificateIssuer(@EndpointParam URI vaultBaseUrl, @PathParam("issuerName") String issuerName);

   @Named("certificate:update_issuer")
   @PATCH
   @Path("/certificates/issuers/{issuerName}")
   @MapBinder(BindToJsonPayload.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   IssuerBundle updateCertificateIssuer(@EndpointParam URI vaultBaseUrl, @PathParam("issuerName") String issuerName,
         @Nullable @PayloadParam("attributes") IssuerAttributes attributes,
         @Nullable @PayloadParam("credentials") IssuerCredentials credentials,
         @Nullable @PayloadParam("org_details") OrganizationDetails orgDetails,
         @PayloadParam("provider") String provider);

   @Named("certificate:delete_issuer")
   @DELETE
   @Path("/certificates/issuers/{issuerName}")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   IssuerBundle deleteCertificateIssuer(@EndpointParam URI vaultBaseUrl, @PathParam("issuerName") String issuerName);

   @Named("certificate:get_contacts")
   @GET
   @Path("/certificates/contacts")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   Contacts getCertificateContacts(@EndpointParam URI vaultBaseUrl);

   @Named("certificate:set_contacts")
   @PUT
   @Path("/certificates/contacts")
   @MapBinder(BindToJsonPayload.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   Contacts setCertificateContacts(@EndpointParam URI vaultBaseUrl, @PayloadParam("contacts") List<Contact> contacts);

   @Named("certificate:delete_contacts")
   @DELETE
   @Path("/certificates/contacts")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   Contacts deleteCertificateContacts(@EndpointParam URI vaultBaseUrl);

   @Named("certificate:get_policy")
   @GET
   @Path("/certificates/{certificateName}/policy")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   CertificatePolicy getCertificatePolicy(@EndpointParam URI vaultBaseUrl,
         @PathParam("certificateName") String certificateName);

   @Named("certificate:update_policy")
   @PATCH
   @MapBinder(BindToJsonPayload.class)
   @Path("/certificates/{certificateName}/policy")
   @OAuthResource(value = VAULT_API_STANDARD_ENDPOINT, chinaEndpoint = VAULT_API_CHINA_ENDPOINT)
   CertificatePolicy updateCertificatePolicy(@EndpointParam URI vaultBaseUrl,
         @PathParam("certificateName") String certificateName,
         @Nullable @PayloadParam("attributes") CertificateAttributes attributes,
         @Nullable @PayloadParam("issuer") IssuerParameters issuer,
         @Nullable @PayloadParam("key_props") KeyProperties keyProps,
         @Nullable @PayloadParam("lifetime_actions") List<LifetimeAction> lifetimeActions,
         @Nullable @PayloadParam("secret_props") SecretProperties secretProps,
         @Nullable @PayloadParam("x509_props") X509CertificateProperties x509Props);
}
