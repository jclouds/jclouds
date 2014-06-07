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
package org.jclouds.cloudstack.predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.alwaysTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.IngressRule;
import org.jclouds.cloudstack.domain.SecurityGroup;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public class SecurityGroupPredicates {
   /**
    * 
    * @return true, if the security group contains an ingress rule with the given port in the port range
    */
   public static Predicate<SecurityGroup> portInRange(final int port) {
      return new Predicate<SecurityGroup>() {

         @Override
         public boolean apply(SecurityGroup group) {
            return Iterables.any(group.getIngressRules(), new Predicate<IngressRule>() {
                  @Override
                  public boolean apply(IngressRule rule) {
                     return rule.getStartPort() <= port && rule.getEndPort() >= port;
                  }
               });
         }

         @Override
         public String toString() {
            return "portInRange(" + port + ")";
         }
      };
   }

   /**
    * 
    * @return true, if the security group contains an ingress rule with the given cidr
    */
   public static Predicate<SecurityGroup> hasCidr(final String cidr) {
      return new Predicate<SecurityGroup>() {

         @Override
         public boolean apply(SecurityGroup group) {
            return Iterables.any(group.getIngressRules(), new Predicate<IngressRule>() {
                  @Override
                  public boolean apply(IngressRule rule) {
                     return rule.getCIDR() != null
                        && rule.getCIDR().equals(cidr);
                  }
               });
         }

         @Override
         public String toString() {
            return "hasCidr(" + cidr + ")";
         }
      };
   }

   /**
    * 
    * @return true, if the security group contains an ingress rule with the given cidr and the given port in range
    */
   public static Predicate<SecurityGroup> portInRangeForCidr(final int port, final String cidr) {
      return new Predicate<SecurityGroup>() {

         @Override
         public boolean apply(SecurityGroup group) {
            return Iterables.any(group.getIngressRules(), new Predicate<IngressRule>() {
                  @Override
                  public boolean apply(IngressRule rule) {
                     return rule.getCIDR() != null
                        && rule.getCIDR().equals(cidr)
                        && rule.getStartPort() <= port
                        && rule.getEndPort() >= port;
                  }
               });
         }

         @Override
         public String toString() {
            return "portInRangeForCidr(" + port + ", " + cidr + ")";
         }
      };
   }

   /**
    * 
    * @return always returns true.
    */
   public static Predicate<SecurityGroup> any() {
      return alwaysTrue();
   }

   /**
    * matches name of the given security group
    * 
    * @param name
    * @return predicate that matches name
    */
   public static Predicate<SecurityGroup> nameEquals(final String name) {
      checkNotNull(name, "name must be defined");

      return new Predicate<SecurityGroup>() {
         @Override
         public boolean apply(SecurityGroup ext) {
            return name.equals(ext.getName());
         }

         @Override
         public String toString() {
            return "nameEquals(" + name + ")";
         }
      };
   }


   /**
    * matches name of the given security group
    * 
    * @param name
    * @return predicate that matches name
    */
   public static Predicate<SecurityGroup> nameMatches(final Predicate<String> name) {
      checkNotNull(name, "name must be defined");

      return new Predicate<SecurityGroup>() {
         @Override
         public boolean apply(SecurityGroup ext) {
            return name.apply(ext.getName());
         }

         @Override
         public String toString() {
            return "nameMatches(" + name + ")";
         }
      };
   }

   /**
    * matches IngressRules with the given protocol, start and end port, and
    * any of the given CIDRs.
    *
    * @param protocol
    * @param startPort
    * @param endPort
    * @param cidrs
    *
    * @return predicate that matches as described
    */
   public static Predicate<IngressRule> ruleCidrMatches(final String protocol,
                                                        final int startPort,
                                                        final int endPort,
                                                        final Set<String> cidrs) {
      checkNotNull(protocol, "protocol");
      checkNotNull(cidrs, "cidrs");

      return new Predicate<IngressRule>() {
         @Override
         public boolean apply(IngressRule rule) {
            return protocol.equals(rule.getProtocol())
                    && startPort == rule.getStartPort()
                    && endPort == rule.getEndPort()
                    && cidrs.contains(rule.getCIDR());
         }

         @Override
         public String toString() {
            return "ruleCidrMatches(protocol:" + protocol
                    + ",startPort:" + startPort
                    + ",endPort:" + endPort
                    + ",cidrs:[" + cidrs
                    + "])";
         }
      };
   }

   /**
    * matches IngressRules with the given protocol, start and end port, and
    * any of the given account/security group name pairs.
    *
    * @param protocol
    * @param startPort
    * @param endPort
    * @param accountGroupNames
    *
    * @return predicate that matches as described
    */
   public static Predicate<IngressRule> ruleGroupMatches(final String protocol,
                                                         final int startPort,
                                                         final int endPort,
                                                         final Multimap<String, String> accountGroupNames) {
      checkNotNull(protocol, "protocol");
      checkNotNull(accountGroupNames, "accountGroupNames");

      return new Predicate<IngressRule>() {
         @Override
         public boolean apply(IngressRule rule) {
            return protocol.equals(rule.getProtocol())
                    && startPort == rule.getStartPort()
                    && endPort == rule.getEndPort()
                    && accountGroupNames.containsEntry(rule.getAccount(), rule.getSecurityGroupName());
         }

         @Override
         public String toString() {
            return "ruleGroupMatches(protocol:" + protocol
                    + ",startPort:" + startPort
                    + ",endPort:" + endPort
                    + ",accountGroupNames:[" + accountGroupNames
                    + "])";
         }
      };
   }
}
