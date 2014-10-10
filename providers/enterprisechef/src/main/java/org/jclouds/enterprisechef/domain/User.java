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
package org.jclouds.enterprisechef.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.security.PublicKey;

import com.google.gson.annotations.SerializedName;

/**
 * User object.
 */
public class User {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String username;
      private String firstName;
      private String middleName;
      private String lastName;
      private String displayName;
      private String email;
      private PublicKey publicKey;

      public Builder username(String username) {
         this.username = checkNotNull(username, "username");
         return this;
      }

      public Builder firstName(String firstName) {
         this.firstName = checkNotNull(firstName, "firstName");
         return this;
      }

      public Builder middleName(String middleName) {
         this.middleName = checkNotNull(middleName, "middleName");
         return this;
      }

      public Builder lastName(String lastName) {
         this.lastName = checkNotNull(lastName, "lastName");
         return this;
      }

      public Builder displayName(String displayName) {
         this.displayName = checkNotNull(displayName, "displayName");
         return this;
      }

      public Builder email(String email) {
         this.email = checkNotNull(email, "email");
         return this;
      }

      public Builder publicKey(PublicKey publicKey) {
         this.publicKey = checkNotNull(publicKey, "publicKey");
         return this;
      }

      public User build() {
         return new User(username, firstName, middleName, lastName, displayName, email, publicKey);
      }
   }

   private final String username;
   @SerializedName("first_name")
   private final String firstName;
   @SerializedName("middle_name")
   private final String middleName;
   @SerializedName("last_name")
   private final String lastName;
   @SerializedName("display_name")
   private final String displayName;
   private final String email;
   @SerializedName("public_key")
   private final PublicKey publicKey;

   @ConstructorProperties({ "username", "first_name", "middle_name", "last_name", "display_name", "email", "public_key" })
   protected User(String username, String firstName, String middleName, String lastName, String displayName,
         String email, PublicKey publicKey) {
      this.username = username;
      this.firstName = firstName;
      this.middleName = middleName;
      this.lastName = lastName;
      this.displayName = displayName;
      this.email = email;
      this.publicKey = publicKey;
   }

   public String getUsername() {
      return username;
   }

   public String getFirstName() {
      return firstName;
   }

   public String getMiddleName() {
      return middleName;
   }

   public String getLastName() {
      return lastName;
   }

   public String getDisplayName() {
      return displayName;
   }

   public String getEmail() {
      return email;
   }

   public PublicKey getPublicKey() {
      return publicKey;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (displayName == null ? 0 : displayName.hashCode());
      result = prime * result + (email == null ? 0 : email.hashCode());
      result = prime * result + (firstName == null ? 0 : firstName.hashCode());
      result = prime * result + (lastName == null ? 0 : lastName.hashCode());
      result = prime * result + (middleName == null ? 0 : middleName.hashCode());
      result = prime * result + (publicKey == null ? 0 : publicKey.hashCode());
      result = prime * result + (username == null ? 0 : username.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      User other = (User) obj;
      if (displayName == null) {
         if (other.displayName != null) {
            return false;
         }
      } else if (!displayName.equals(other.displayName)) {
         return false;
      }
      if (email == null) {
         if (other.email != null) {
            return false;
         }
      } else if (!email.equals(other.email)) {
         return false;
      }
      if (firstName == null) {
         if (other.firstName != null) {
            return false;
         }
      } else if (!firstName.equals(other.firstName)) {
         return false;
      }
      if (lastName == null) {
         if (other.lastName != null) {
            return false;
         }
      } else if (!lastName.equals(other.lastName)) {
         return false;
      }
      if (middleName == null) {
         if (other.middleName != null) {
            return false;
         }
      } else if (!middleName.equals(other.middleName)) {
         return false;
      }
      if (publicKey == null) {
         if (other.publicKey != null) {
            return false;
         }
      } else if (!publicKey.equals(other.publicKey)) {
         return false;
      }
      if (username == null) {
         if (other.username != null) {
            return false;
         }
      } else if (!username.equals(other.username)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "User [username=" + username + ", firstName=" + firstName + ", middleName=" + middleName + ", lastName="
            + lastName + ", displayName=" + displayName + ", email=" + email + ", publicKey=" + publicKey + "]";
   }

}
