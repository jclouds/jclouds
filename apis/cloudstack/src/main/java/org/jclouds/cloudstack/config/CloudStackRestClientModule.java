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
package org.jclouds.cloudstack.config;

import static org.jclouds.rest.config.BinderUtils.bindSyncToAsyncHttpApi;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jclouds.Constants;
import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.CloudStackDomainAsyncClient;
import org.jclouds.cloudstack.CloudStackDomainClient;
import org.jclouds.cloudstack.CloudStackGlobalAsyncClient;
import org.jclouds.cloudstack.CloudStackGlobalClient;
import org.jclouds.cloudstack.CloudStackAsyncApi;
import org.jclouds.cloudstack.CloudStackApi;
import org.jclouds.cloudstack.CloudStackDomainAsyncApi;
import org.jclouds.cloudstack.CloudStackDomainApi;
import org.jclouds.cloudstack.CloudStackGlobalAsyncApi;
import org.jclouds.cloudstack.CloudStackGlobalApi;
import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.cloudstack.features.AccountAsyncClient;
import org.jclouds.cloudstack.features.AccountClient;
import org.jclouds.cloudstack.features.AddressAsyncClient;
import org.jclouds.cloudstack.features.AddressClient;
import org.jclouds.cloudstack.features.AsyncJobAsyncClient;
import org.jclouds.cloudstack.features.AsyncJobClient;
import org.jclouds.cloudstack.features.ConfigurationAsyncClient;
import org.jclouds.cloudstack.features.ConfigurationClient;
import org.jclouds.cloudstack.features.DomainAccountAsyncClient;
import org.jclouds.cloudstack.features.DomainAccountClient;
import org.jclouds.cloudstack.features.DomainDomainAsyncClient;
import org.jclouds.cloudstack.features.DomainDomainClient;
import org.jclouds.cloudstack.features.DomainLimitAsyncClient;
import org.jclouds.cloudstack.features.DomainLimitClient;
import org.jclouds.cloudstack.features.DomainUserAsyncClient;
import org.jclouds.cloudstack.features.DomainUserClient;
import org.jclouds.cloudstack.features.EventAsyncClient;
import org.jclouds.cloudstack.features.EventClient;
import org.jclouds.cloudstack.features.FirewallAsyncClient;
import org.jclouds.cloudstack.features.FirewallClient;
import org.jclouds.cloudstack.features.GlobalAccountAsyncClient;
import org.jclouds.cloudstack.features.GlobalAccountClient;
import org.jclouds.cloudstack.features.GlobalAlertAsyncClient;
import org.jclouds.cloudstack.features.GlobalAlertClient;
import org.jclouds.cloudstack.features.GlobalCapacityAsyncClient;
import org.jclouds.cloudstack.features.GlobalCapacityClient;
import org.jclouds.cloudstack.features.GlobalConfigurationAsyncClient;
import org.jclouds.cloudstack.features.GlobalConfigurationClient;
import org.jclouds.cloudstack.features.GlobalDomainAsyncClient;
import org.jclouds.cloudstack.features.GlobalDomainClient;
import org.jclouds.cloudstack.features.GlobalHostAsyncClient;
import org.jclouds.cloudstack.features.GlobalHostClient;
import org.jclouds.cloudstack.features.GlobalOfferingAsyncClient;
import org.jclouds.cloudstack.features.GlobalOfferingClient;
import org.jclouds.cloudstack.features.GlobalPodAsyncClient;
import org.jclouds.cloudstack.features.GlobalPodClient;
import org.jclouds.cloudstack.features.GlobalStoragePoolAsyncClient;
import org.jclouds.cloudstack.features.GlobalStoragePoolClient;
import org.jclouds.cloudstack.features.GlobalUsageAsyncClient;
import org.jclouds.cloudstack.features.GlobalUsageClient;
import org.jclouds.cloudstack.features.GlobalUserAsyncClient;
import org.jclouds.cloudstack.features.GlobalUserClient;
import org.jclouds.cloudstack.features.GlobalVlanAsyncClient;
import org.jclouds.cloudstack.features.GlobalVlanClient;
import org.jclouds.cloudstack.features.GlobalZoneAsyncClient;
import org.jclouds.cloudstack.features.GlobalZoneClient;
import org.jclouds.cloudstack.features.GuestOSAsyncClient;
import org.jclouds.cloudstack.features.GuestOSClient;
import org.jclouds.cloudstack.features.HypervisorAsyncClient;
import org.jclouds.cloudstack.features.HypervisorClient;
import org.jclouds.cloudstack.features.ISOAsyncClient;
import org.jclouds.cloudstack.features.ISOClient;
import org.jclouds.cloudstack.features.LimitAsyncClient;
import org.jclouds.cloudstack.features.LimitClient;
import org.jclouds.cloudstack.features.LoadBalancerAsyncClient;
import org.jclouds.cloudstack.features.LoadBalancerClient;
import org.jclouds.cloudstack.features.NATAsyncClient;
import org.jclouds.cloudstack.features.NATClient;
import org.jclouds.cloudstack.features.NetworkAsyncClient;
import org.jclouds.cloudstack.features.NetworkClient;
import org.jclouds.cloudstack.features.OfferingAsyncClient;
import org.jclouds.cloudstack.features.OfferingClient;
import org.jclouds.cloudstack.features.SSHKeyPairAsyncClient;
import org.jclouds.cloudstack.features.SSHKeyPairClient;
import org.jclouds.cloudstack.features.SecurityGroupAsyncClient;
import org.jclouds.cloudstack.features.SecurityGroupClient;
import org.jclouds.cloudstack.features.SessionAsyncClient;
import org.jclouds.cloudstack.features.SessionClient;
import org.jclouds.cloudstack.features.SnapshotAsyncClient;
import org.jclouds.cloudstack.features.SnapshotClient;
import org.jclouds.cloudstack.features.TemplateAsyncClient;
import org.jclouds.cloudstack.features.TemplateClient;
import org.jclouds.cloudstack.features.VMGroupAsyncClient;
import org.jclouds.cloudstack.features.VMGroupClient;
import org.jclouds.cloudstack.features.VirtualMachineAsyncClient;
import org.jclouds.cloudstack.features.VirtualMachineClient;
import org.jclouds.cloudstack.features.VolumeAsyncClient;
import org.jclouds.cloudstack.features.VolumeClient;
import org.jclouds.cloudstack.features.ZoneAsyncClient;
import org.jclouds.cloudstack.features.ZoneClient;
import org.jclouds.cloudstack.features.AccountAsyncApi;
import org.jclouds.cloudstack.features.AccountApi;
import org.jclouds.cloudstack.features.AddressAsyncApi;
import org.jclouds.cloudstack.features.AddressApi;
import org.jclouds.cloudstack.features.AsyncJobAsyncApi;
import org.jclouds.cloudstack.features.AsyncJobApi;
import org.jclouds.cloudstack.features.ConfigurationAsyncApi;
import org.jclouds.cloudstack.features.ConfigurationApi;
import org.jclouds.cloudstack.features.DomainAccountAsyncApi;
import org.jclouds.cloudstack.features.DomainAccountApi;
import org.jclouds.cloudstack.features.DomainDomainAsyncApi;
import org.jclouds.cloudstack.features.DomainDomainApi;
import org.jclouds.cloudstack.features.DomainLimitAsyncApi;
import org.jclouds.cloudstack.features.DomainLimitApi;
import org.jclouds.cloudstack.features.DomainUserAsyncApi;
import org.jclouds.cloudstack.features.DomainUserApi;
import org.jclouds.cloudstack.features.EventAsyncApi;
import org.jclouds.cloudstack.features.EventApi;
import org.jclouds.cloudstack.features.FirewallAsyncApi;
import org.jclouds.cloudstack.features.FirewallApi;
import org.jclouds.cloudstack.features.GlobalAccountAsyncApi;
import org.jclouds.cloudstack.features.GlobalAccountApi;
import org.jclouds.cloudstack.features.GlobalAlertAsyncApi;
import org.jclouds.cloudstack.features.GlobalAlertApi;
import org.jclouds.cloudstack.features.GlobalCapacityAsyncApi;
import org.jclouds.cloudstack.features.GlobalCapacityApi;
import org.jclouds.cloudstack.features.GlobalConfigurationAsyncApi;
import org.jclouds.cloudstack.features.GlobalConfigurationApi;
import org.jclouds.cloudstack.features.GlobalDomainAsyncApi;
import org.jclouds.cloudstack.features.GlobalDomainApi;
import org.jclouds.cloudstack.features.GlobalHostAsyncApi;
import org.jclouds.cloudstack.features.GlobalHostApi;
import org.jclouds.cloudstack.features.GlobalOfferingAsyncApi;
import org.jclouds.cloudstack.features.GlobalOfferingApi;
import org.jclouds.cloudstack.features.GlobalPodAsyncApi;
import org.jclouds.cloudstack.features.GlobalPodApi;
import org.jclouds.cloudstack.features.GlobalStoragePoolAsyncApi;
import org.jclouds.cloudstack.features.GlobalStoragePoolApi;
import org.jclouds.cloudstack.features.GlobalUsageAsyncApi;
import org.jclouds.cloudstack.features.GlobalUsageApi;
import org.jclouds.cloudstack.features.GlobalUserAsyncApi;
import org.jclouds.cloudstack.features.GlobalUserApi;
import org.jclouds.cloudstack.features.GlobalVlanAsyncApi;
import org.jclouds.cloudstack.features.GlobalVlanApi;
import org.jclouds.cloudstack.features.GlobalZoneAsyncApi;
import org.jclouds.cloudstack.features.GlobalZoneApi;
import org.jclouds.cloudstack.features.GuestOSAsyncApi;
import org.jclouds.cloudstack.features.GuestOSApi;
import org.jclouds.cloudstack.features.HypervisorAsyncApi;
import org.jclouds.cloudstack.features.HypervisorApi;
import org.jclouds.cloudstack.features.ISOAsyncApi;
import org.jclouds.cloudstack.features.ISOApi;
import org.jclouds.cloudstack.features.LimitAsyncApi;
import org.jclouds.cloudstack.features.LimitApi;
import org.jclouds.cloudstack.features.LoadBalancerAsyncApi;
import org.jclouds.cloudstack.features.LoadBalancerApi;
import org.jclouds.cloudstack.features.NATAsyncApi;
import org.jclouds.cloudstack.features.NATApi;
import org.jclouds.cloudstack.features.NetworkAsyncApi;
import org.jclouds.cloudstack.features.NetworkApi;
import org.jclouds.cloudstack.features.OfferingAsyncApi;
import org.jclouds.cloudstack.features.OfferingApi;
import org.jclouds.cloudstack.features.SSHKeyPairAsyncApi;
import org.jclouds.cloudstack.features.SSHKeyPairApi;
import org.jclouds.cloudstack.features.SecurityGroupAsyncApi;
import org.jclouds.cloudstack.features.SecurityGroupApi;
import org.jclouds.cloudstack.features.SessionAsyncApi;
import org.jclouds.cloudstack.features.SessionApi;
import org.jclouds.cloudstack.features.SnapshotAsyncApi;
import org.jclouds.cloudstack.features.SnapshotApi;
import org.jclouds.cloudstack.features.TemplateAsyncApi;
import org.jclouds.cloudstack.features.TemplateApi;
import org.jclouds.cloudstack.features.VMGroupAsyncApi;
import org.jclouds.cloudstack.features.VMGroupApi;
import org.jclouds.cloudstack.features.VirtualMachineAsyncApi;
import org.jclouds.cloudstack.features.VirtualMachineApi;
import org.jclouds.cloudstack.features.VolumeAsyncApi;
import org.jclouds.cloudstack.features.VolumeApi;
import org.jclouds.cloudstack.features.ZoneAsyncApi;
import org.jclouds.cloudstack.features.ZoneApi;
import org.jclouds.cloudstack.filters.AddSessionKeyAndJSessionIdToRequest;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.handlers.CloudStackErrorHandler;
import org.jclouds.cloudstack.handlers.InvalidateSessionAndRetryOn401AndLogoutOnClose;
import org.jclouds.cloudstack.loaders.LoginWithPasswordCredentials;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.Provider;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.implicit.OnlyLocationOrFirstZone;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;

/**
 * Configures the cloudstack connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class CloudStackRestClientModule extends RestClientModule<CloudStackClient, CloudStackAsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
            .put(ZoneClient.class, ZoneAsyncClient.class)
            .put(GlobalZoneClient.class, GlobalZoneAsyncClient.class)
            .put(TemplateClient.class, TemplateAsyncClient.class)
            .put(OfferingClient.class, OfferingAsyncClient.class)
            .put(NetworkClient.class, NetworkAsyncClient.class)
            .put(VirtualMachineClient.class, VirtualMachineAsyncClient.class)
            .put(SecurityGroupClient.class, SecurityGroupAsyncClient.class)
            .put(AsyncJobClient.class, AsyncJobAsyncClient.class)
            .put(AddressClient.class, AddressAsyncClient.class)
            .put(NATClient.class, NATAsyncClient.class)
            .put(FirewallClient.class, FirewallAsyncClient.class)
            .put(LoadBalancerClient.class, LoadBalancerAsyncClient.class)
            .put(GuestOSClient.class, GuestOSAsyncClient.class)
            .put(HypervisorClient.class, HypervisorAsyncClient.class)
            .put(ConfigurationClient.class, ConfigurationAsyncClient.class)
            .put(GlobalConfigurationClient.class, GlobalConfigurationAsyncClient.class)
            .put(AccountClient.class, AccountAsyncClient.class)
            .put(DomainAccountClient.class, DomainAccountAsyncClient.class)
            .put(DomainUserClient.class, DomainUserAsyncClient.class)
            .put(DomainDomainClient.class, DomainDomainAsyncClient.class)
            .put(GlobalDomainClient.class, GlobalDomainAsyncClient.class)
            .put(GlobalAccountClient.class, GlobalAccountAsyncClient.class)
            .put(GlobalUserClient.class, GlobalUserAsyncClient.class)
            .put(EventClient.class, EventAsyncClient.class)
            .put(LimitClient.class, LimitAsyncClient.class)
            .put(DomainLimitClient.class, DomainLimitAsyncClient.class)
            .put(SSHKeyPairClient.class, SSHKeyPairAsyncClient.class)
            .put(VMGroupClient.class, VMGroupAsyncClient.class)
            .put(ISOClient.class, ISOAsyncClient.class)
            .put(VolumeClient.class, VolumeAsyncClient.class)
            .put(SnapshotClient.class, SnapshotAsyncClient.class)
            .put(GlobalAlertClient.class, GlobalAlertAsyncClient.class)
            .put(GlobalCapacityClient.class, GlobalCapacityAsyncClient.class)
            .put(GlobalOfferingClient.class, GlobalOfferingAsyncClient.class)
            .put(GlobalHostClient.class, GlobalHostAsyncClient.class)
            .put(GlobalStoragePoolClient.class, GlobalStoragePoolAsyncClient.class)
            .put(GlobalUsageClient.class, GlobalUsageAsyncClient.class)
            .put(GlobalPodClient.class, GlobalPodAsyncClient.class)
            .put(GlobalVlanClient.class, GlobalVlanAsyncClient.class)
            .put(SessionClient.class, SessionAsyncClient.class)
            .put(ZoneApi.class, ZoneAsyncApi.class)
            .put(GlobalZoneApi.class, GlobalZoneAsyncApi.class)
            .put(TemplateApi.class, TemplateAsyncApi.class)
            .put(OfferingApi.class, OfferingAsyncApi.class)
            .put(NetworkApi.class, NetworkAsyncApi.class)
            .put(VirtualMachineApi.class, VirtualMachineAsyncApi.class)
            .put(SecurityGroupApi.class, SecurityGroupAsyncApi.class)
            .put(AsyncJobApi.class, AsyncJobAsyncApi.class)
            .put(AddressApi.class, AddressAsyncApi.class)
            .put(NATApi.class, NATAsyncApi.class)
            .put(FirewallApi.class, FirewallAsyncApi.class)
            .put(LoadBalancerApi.class, LoadBalancerAsyncApi.class)
            .put(GuestOSApi.class, GuestOSAsyncApi.class)
            .put(HypervisorApi.class, HypervisorAsyncApi.class)
            .put(ConfigurationApi.class, ConfigurationAsyncApi.class)
            .put(GlobalConfigurationApi.class, GlobalConfigurationAsyncApi.class)
            .put(AccountApi.class, AccountAsyncApi.class)
            .put(DomainAccountApi.class, DomainAccountAsyncApi.class)
            .put(DomainUserApi.class, DomainUserAsyncApi.class)
            .put(DomainDomainApi.class, DomainDomainAsyncApi.class)
            .put(GlobalDomainApi.class, GlobalDomainAsyncApi.class)
            .put(GlobalAccountApi.class, GlobalAccountAsyncApi.class)
            .put(GlobalUserApi.class, GlobalUserAsyncApi.class)
            .put(EventApi.class, EventAsyncApi.class)
            .put(LimitApi.class, LimitAsyncApi.class)
            .put(DomainLimitApi.class, DomainLimitAsyncApi.class)
            .put(SSHKeyPairApi.class, SSHKeyPairAsyncApi.class)
            .put(VMGroupApi.class, VMGroupAsyncApi.class)
            .put(ISOApi.class, ISOAsyncApi.class)
            .put(VolumeApi.class, VolumeAsyncApi.class)
            .put(SnapshotApi.class, SnapshotAsyncApi.class)
            .put(GlobalAlertApi.class, GlobalAlertAsyncApi.class)
            .put(GlobalCapacityApi.class, GlobalCapacityAsyncApi.class)
            .put(GlobalOfferingApi.class, GlobalOfferingAsyncApi.class)
            .put(GlobalHostApi.class, GlobalHostAsyncApi.class)
            .put(GlobalStoragePoolApi.class, GlobalStoragePoolAsyncApi.class)
            .put(GlobalUsageApi.class, GlobalUsageAsyncApi.class)
            .put(GlobalPodApi.class, GlobalPodAsyncApi.class)
            .put(GlobalVlanApi.class, GlobalVlanAsyncApi.class)
            .put(SessionApi.class, SessionAsyncApi.class)
            .build();

   public CloudStackRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      bind(new TypeLiteral<RestContext<CloudStackDomainClient, CloudStackDomainAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<CloudStackDomainClient, CloudStackDomainAsyncClient>>() {
      });
      bind(new TypeLiteral<RestContext<CloudStackGlobalClient, CloudStackGlobalAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<CloudStackGlobalClient, CloudStackGlobalAsyncClient>>() {
      });
      bind(CredentialType.class).toProvider(CredentialTypeFromPropertyOrDefault.class);
      // session client is used directly for filters and retry handlers, so let's bind it explicitly
      bindSyncToAsyncHttpApi(binder(), SessionClient.class, SessionAsyncClient.class);
      bindSyncToAsyncHttpApi(binder(), CloudStackDomainClient.class, CloudStackDomainAsyncClient.class);
      bindSyncToAsyncHttpApi(binder(), CloudStackGlobalClient.class, CloudStackGlobalAsyncClient.class);
      bindSyncToAsyncHttpApi(binder(), SessionApi.class, SessionAsyncApi.class);
      bindSyncToAsyncHttpApi(binder(), CloudStackDomainApi.class, CloudStackDomainAsyncApi.class);
      bindSyncToAsyncHttpApi(binder(), CloudStackGlobalApi.class, CloudStackGlobalAsyncApi.class);
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(InvalidateSessionAndRetryOn401AndLogoutOnClose.class);
      
      super.configure();
   }

   @Override
   protected void installLocations() {
      super.installLocations();
      bind(ImplicitLocationSupplier.class).to(OnlyLocationOrFirstZone.class).in(Scopes.SINGLETON);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(CloudStackErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(CloudStackErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(CloudStackErrorHandler.class);
   }

   @Singleton
   static class CredentialTypeFromPropertyOrDefault implements javax.inject.Provider<CredentialType> {
      /**
       * use optional injection to supply a default value for credential type. so that we don't have
       * to set a default property.
       */
      @Inject(optional = true)
      @Named(CloudStackProperties.CREDENTIAL_TYPE)
      String credentialType = CredentialType.API_ACCESS_KEY_CREDENTIALS.toString();

      @Override
      public CredentialType get() {
         return CredentialType.fromValue(credentialType);
      }
   }

   /**
    * we use the type of credentials specified at login to determine which way we want to filter the
    * request. <br/>
    * for ex, if we are getting passwords, we know we will need to login/logout. Otherwise we are
    * signing requests.
    */
   @Provides
   @Singleton
   protected AuthenticationFilter authenticationFilterForCredentialType(CredentialType credentialType,
            AddSessionKeyAndJSessionIdToRequest addSessionKeyAndJSessionIdToRequest, QuerySigner querySigner) {
      switch (credentialType) {
         case PASSWORD_CREDENTIALS:
            return addSessionKeyAndJSessionIdToRequest;
         case API_ACCESS_KEY_CREDENTIALS:
            return querySigner;
         default:
            throw new IllegalArgumentException("credential type not supported: " + credentialType);
      }
   }

   // PROPERTY_SESSION_INTERVAL is default to 60 seconds
   @Provides
   @Singleton
   protected LoadingCache<Credentials, LoginResponse> provideLoginResponseCache(
            LoginWithPasswordCredentials getLoginResponse,
            @Named(Constants.PROPERTY_SESSION_INTERVAL) int seconds) {
      return CacheBuilder.newBuilder().expireAfterWrite(seconds, TimeUnit.SECONDS).build(getLoginResponse);
   }

   // Temporary conversion of a cache to a supplier until there is a single-element cache
   // http://code.google.com/p/guava-libraries/issues/detail?id=872
   @Provides
   @Singleton
   protected Supplier<LoginResponse> provideLoginResponseSupplier(final LoadingCache<Credentials, LoginResponse> cache,
         @Provider final Supplier<Credentials> creds) {
      return new Supplier<LoginResponse>() {
         @Override
         public LoginResponse get() {
            return cache.getUnchecked(creds.get());
         }
      };
   }
}
