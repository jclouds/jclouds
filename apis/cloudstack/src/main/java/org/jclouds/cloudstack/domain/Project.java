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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Representation of the API project response
 */
public class Project implements Comparable<Project> {

   public static enum State {
      ACTIVE, DISABLED, SUSPENDED, UNRECOGNIZED;

      @Override
      public String toString() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
      }

      public static State fromValue(String state) {
         try {
            return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(state, "state")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromDomain(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String account;
      protected String displayText;
      protected String domain;
      protected String domainId;
      protected String name;
      protected State state;
      protected Set<Tag> tags = ImmutableSet.of();

      /**
       * @see org.jclouds.cloudstack.domain.Project#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see org.jclouds.cloudstack.domain.Project#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see org.jclouds.cloudstack.domain.Project#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see org.jclouds.cloudstack.domain.Project#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see org.jclouds.cloudstack.domain.Project#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see org.jclouds.cloudstack.domain.Project#getDisplayText()
       */
      public T displayText(String displayText) {
         this.displayText = displayText;
         return self();
      }

      /**
       * @see org.jclouds.cloudstack.domain.Project#getState()
       */
      public T state(State state) {
         this.state = state;
         return self();
      }

      /**
       * @see Project#getTags()
       */
      public T tags(Set<Tag> tags) {
         this.tags = ImmutableSet.copyOf(checkNotNull(tags, "tags"));
         return self();
      }

      public T tags(Tag... in) {
         return tags(ImmutableSet.copyOf(in));
      }

      public Project build() {
         return new Project(id, account, displayText, domain, domainId, name, state, tags);
      }

      public T fromDomain(Project in) {
         return this
               .id(in.getId())
               .account(in.getAccount())
               .displayText(in.getDisplayText())
               .domain(in.getDomain())
               .domainId(in.getDomainId())
               .name(in.getName())
               .state(in.getState())
               .tags(in.getTags());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String account;
   private final String displayText;
   private final String domain;
   private final String domainId;
   private final String name;
   private final State state;
   private final Set<Tag> tags;

   @ConstructorProperties({
         "id", "account", "displaytext", "domain", "domainid", "name", "state", "tags"
   })
   protected Project(String id, String account, String displayText, String domain, String domainId,
                     String name, State state, @Nullable Set<Tag> tags) {
      this.id = checkNotNull(id, "id");
      this.account = account;
      this.displayText = displayText;
      this.domain = domain;
      this.domainId = domainId;
      this.name = name;
      this.state = checkNotNull(state, "state");
      this.tags = tags != null ? ImmutableSet.copyOf(tags) : ImmutableSet.<Tag> of();
   }

   public String getId() {
      return this.id;
   }

   @Nullable
   public String getAccount() {
      return this.account;
   }

   @Nullable
   public String getDisplayText() {
      return this.displayText;
   }

   @Nullable
   public String getDomain() {
      return this.domain;
   }

   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   public State getState() {
      return this.state;
   }

   /**
    * @return the tags for the project
    */
   public Set<Tag> getTags() {
      return this.tags;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, account, displayText, domain, domainId, name, state, tags);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Project that = Project.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.account, that.account)
            && Objects.equal(this.displayText, that.displayText)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.state, that.state)
            && Objects.equal(this.tags, that.tags);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("id", id).add("account", account).add("displayText", displayText)
            .add("domain", domain).add("domainId", domainId).add("name", name).add("state", state)
            .add("tags", tags);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(Project other) {
      return id.compareTo(other.getId());
   }

}
