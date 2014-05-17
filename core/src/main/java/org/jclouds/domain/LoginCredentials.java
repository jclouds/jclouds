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
package org.jclouds.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.crypto.Pems.PRIVATE_PKCS1_MARKER;
import static org.jclouds.crypto.Pems.PRIVATE_PKCS8_MARKER;

import org.jclouds.crypto.Pems;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Optional;

public class LoginCredentials extends Credentials {

   private static boolean isPrivateKeyCredential(String credential) {
      return credential != null
            && (credential.startsWith(PRIVATE_PKCS1_MARKER) || credential.startsWith(PRIVATE_PKCS8_MARKER));
   }

   public static LoginCredentials fromCredentials(Credentials creds) {
      if (creds == null)
         return null;
      if (creds instanceof LoginCredentials)
         return LoginCredentials.class.cast(creds);
      return builder(creds).build();
   }

   public static Builder builder(Credentials creds) {
      if (creds == null)
         return builder();
      if (creds instanceof LoginCredentials)
         return LoginCredentials.class.cast(creds).toBuilder();
      else
         return builder().identity(creds.identity).credential(creds.credential);
   }

   public static Builder builder() {
      return new Builder();
   }
   
   public static class Builder extends Credentials.Builder<LoginCredentials> {
      private boolean authenticateSudo;
      private Optional<String> password = Optional.absent();
      private Optional<String> privateKey = Optional.absent();

      public Builder identity(String identity) {
         return Builder.class.cast(super.identity(identity));
      }

      public Builder user(String user) {
         return identity(user);
      }

      public Builder password(String password) {
         this.password = Optional.fromNullable(password);
         return this;
      }

      public Builder noPassword() {
         this.password = Optional.absent();
         return this;
      }

      public Builder privateKey(String privateKey) {
         this.privateKey = Optional.fromNullable(privateKey);
         return this;
      }

      public Builder noPrivateKey() {
         this.privateKey = Optional.absent();
         return this;
      }

      public Builder credential(String credential) {
         if (isPrivateKeyCredential(credential))
            return noPassword().privateKey(credential);
         else if (credential != null)
            return password(credential).noPrivateKey();
         return this;
      }

      public Builder authenticateSudo(boolean authenticateSudo) {
         this.authenticateSudo = authenticateSudo;
         return this;
      }

      public LoginCredentials build() {
         if (identity == null && !password.isPresent() && !privateKey.isPresent() && !authenticateSudo)
            return null;
         return new LoginCredentials(identity, password, privateKey, authenticateSudo);
      }
   }

   private final boolean authenticateSudo;
   private final Optional<String> password;
   private final Optional<String> privateKey;

   private LoginCredentials(String username, Optional<String> password, Optional<String> privateKey, boolean authenticateSudo) {
      super(username, privateKey.isPresent() && isPrivateKeyCredential(privateKey.get())
                    ? privateKey.get()
                    : password.orNull());
      this.authenticateSudo = authenticateSudo;
      this.password = checkNotNull(password, "password");
      this.privateKey = checkNotNull(privateKey, "privateKey");
   }

   /**
    * @return the login user
    */
   public String getUser() {
      return identity;
   }

   /**
    * @return the password of the login user or null
    * 
    * @deprecated since 1.8; instead use {@link #getOptionalPassword()}
    */
   @Nullable
   @Deprecated
   public String getPassword() {
      return password.orNull();
   }

   /**
    * @return the optional password of the user (Optional.absent if none supplied).
    */
   public Optional<String> getOptionalPassword() {
      return password;
   }

   /**
    * @return the private ssh key of the user or null
    * 
    * @deprecated since 1.8; instead use {@link #getOptionalPrivateKey()}
    */
   @Nullable
   @Deprecated
   public String getPrivateKey() {
      return privateKey.orNull();
   }

   /**
    * @return true if there is a private key attached that is not encrypted
    */
   public boolean hasUnencryptedPrivateKey() {
      return getOptionalPrivateKey().isPresent()
         && !getOptionalPrivateKey().get().isEmpty()
         && !getOptionalPrivateKey().get().contains(Pems.PROC_TYPE_ENCRYPTED);
   }

   /**
    * @return the optional private ssh key of the user (Optional.absent if none supplied).
    */
   public Optional<String> getOptionalPrivateKey() {
      return privateKey;
   }

   /**
    * secures access to root requires a password. This password is required to
    * access either the console or run sudo as root.
    * <p/>
    * ex. {@code echo 'password' |sudo -S command}
    * 
    * @return if a password is required to access the root user
    */
   public boolean shouldAuthenticateSudo() {
      return authenticateSudo;
   }

   @Override
   public Builder toBuilder() {
      Builder builder = new Builder().user(identity).authenticateSudo(authenticateSudo);
      if (password != null) {
         if (password.isPresent()) {
            builder = builder.password(password.get());
         } else {
            builder = builder.noPassword();
         }
      }
      if (privateKey != null) {
         if (privateKey.isPresent()) {
            builder = builder.privateKey(privateKey.get());
         } else {
            builder = builder.noPrivateKey();
         }
      }
      return builder;
   }

   @Override
   public String toString() {
      return "[user=" + getUser() + ", passwordPresent=" + password.isPresent() + ", privateKeyPresent="
            + privateKey.isPresent() + ", shouldAuthenticateSudo=" + authenticateSudo + "]";
   }
}
