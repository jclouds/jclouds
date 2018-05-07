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
package org.jclouds.openstack.keystone.v3.domain;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Auth {
   public abstract Identity identity();

   @Nullable
   public abstract Object scope();

   @SerializedNames({ "identity", "scope" })
   public static Auth create(Identity identity, Object scope) {
      return new AutoValue_Auth(identity, scope);
   }

   @AutoValue
   public abstract static class Identity {
      public abstract List<String> methods();

      @Nullable
      public abstract Id token();

      @Nullable
      public abstract PasswordAuth password();

      @SerializedNames({ "methods", "token", "password" })
      public static Identity create(List<String> methods, Id token, PasswordAuth password) {
         return new AutoValue_Auth_Identity(methods, token, password);
      }

      @AutoValue
      public abstract static class PasswordAuth {
         public abstract UserAuth user();

         @SerializedNames({ "user" })
         public static PasswordAuth create(UserAuth user) {
            return new AutoValue_Auth_Identity_PasswordAuth(user);
         }

         @AutoValue
         public abstract static class UserAuth {
            public abstract String name();

            public abstract DomainAuth domain();

            public abstract String password();

            @SerializedNames({ "name", "domain", "password" })
            public static UserAuth create(String name, DomainAuth domain, String password) {
               return new AutoValue_Auth_Identity_PasswordAuth_UserAuth(name, domain, password);
            }

            @AutoValue
            public abstract static class DomainAuth {
               @Nullable
               public abstract String name();

               @SerializedNames({ "name" })
               public static DomainAuth create(String name) {
                  return new AutoValue_Auth_Identity_PasswordAuth_UserAuth_DomainAuth(name);
               }
            }
         }
      }
   }

   @AutoValue
   public abstract static class Id {
      public abstract String id();

      @SerializedNames({ "id" })
      public static Id create(String id) {
         return new AutoValue_Auth_Id(id);
      }
   }

   @AutoValue
   public abstract static class Name {
      @Nullable
      public abstract String name();

      @SerializedNames({ "name" })
      public static Name create(String name) {
         return new AutoValue_Auth_Name(name);
      }
   }

   public static class Scope {
      public static final String PROJECT = "project";
      public static final String PROJECT_ID = "projectId";
      public static final String DOMAIN = "domain";
      public static final String DOMAIN_ID = "domainId";
      public static final String UNSCOPED = "unscoped";
   }

   @AutoValue
   public abstract static class ProjectScope {
      public abstract ProjectName project();

      @SerializedNames({ Scope.PROJECT })
      public static ProjectScope create(ProjectName project) {
         return new AutoValue_Auth_ProjectScope(project);
      }

      @AutoValue
      public abstract static class ProjectName {
         public abstract String name();

         @Nullable
         public abstract Object domain();

         @SerializedNames({ "name", Scope.DOMAIN })
         public static ProjectName create(String name, Object domain) {
            return new AutoValue_Auth_ProjectScope_ProjectName(name, domain);
         }

         public static ProjectName create(String name, Name domain) {
            return new AutoValue_Auth_ProjectScope_ProjectName(name, domain);
         }

         public static ProjectName create(String name, Id domain) {
            return new AutoValue_Auth_ProjectScope_ProjectName(name, domain);
         }
      }
   }

   @AutoValue
   public abstract static class ProjectIdScope {
      public abstract ProjectId project();

      @SerializedNames({ Scope.PROJECT })
      public static ProjectIdScope create(ProjectId project) {
         return new AutoValue_Auth_ProjectIdScope(project);
      }

      @AutoValue
      public abstract static class ProjectId {
         public abstract String id();

         @Nullable
         public abstract Object domain();

         @SerializedNames({ "id", Scope.DOMAIN })
         public static ProjectId create(String id, Object domain) {
            return new AutoValue_Auth_ProjectIdScope_ProjectId(id, domain);
         }

         public static ProjectId create(String id, Name domain) {
            return new AutoValue_Auth_ProjectIdScope_ProjectId(id, domain);
         }

         public static ProjectId create(String id, Id domain) {
            return new AutoValue_Auth_ProjectIdScope_ProjectId(id, domain);
         }
      }
   }

   @AutoValue
   public abstract static class DomainIdScope {
      public abstract Id domain();

      @SerializedNames({ Scope.DOMAIN })
      public static DomainIdScope create(Id id) {
         return new AutoValue_Auth_DomainIdScope(id);
      }
   }

   @AutoValue
   public abstract static class DomainScope {
      public abstract Name domain();

      @SerializedNames({ Scope.DOMAIN })
      public static DomainScope create(Name name) {
         return new AutoValue_Auth_DomainScope(name);
      }
   }
}
