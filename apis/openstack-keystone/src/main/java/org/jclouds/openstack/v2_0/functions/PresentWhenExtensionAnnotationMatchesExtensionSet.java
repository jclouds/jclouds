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
package org.jclouds.openstack.v2_0.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.any;
import static org.jclouds.openstack.v2_0.predicates.ExtensionPredicates.aliasEquals;
import static org.jclouds.openstack.v2_0.predicates.ExtensionPredicates.nameEquals;
import static org.jclouds.openstack.v2_0.predicates.ExtensionPredicates.namespaceOrAliasEquals;
import static org.jclouds.util.Optionals2.unwrapIfOptional;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.openstack.keystone.v2_0.config.NamespaceAliases;
import org.jclouds.openstack.v2_0.domain.Extension;
import org.jclouds.reflect.InvocationSuccess;
import org.jclouds.rest.functions.ImplicitOptionalConverter;

import com.google.common.base.Optional;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * We use the annotation {@link Extension} to bind a class that implements an extension
 * API to an {@link Extension}.
 *
 * Match in the following order:
 *
 * 1. Match by namespace
 * 2. Match by namespace aliases
 * 3. Match by alias
 * 4. Match by name
 *
 * New versions of openstack have no namespaces anymore.
 * Alias is different than a namespace alias - it's an alternative namespace URL to match against.
 */
public class PresentWhenExtensionAnnotationMatchesExtensionSet implements
      ImplicitOptionalConverter {
   private final LoadingCache<String, Set<? extends Extension>> extensions;
   private final Map<URI, Set<URI>> aliases;

   @Inject
   PresentWhenExtensionAnnotationMatchesExtensionSet(
         LoadingCache<String, Set<? extends Extension>> extensions, @NamespaceAliases Map<URI, Set<URI>> aliases) {
      this.extensions = extensions;
      this.aliases = aliases == null ? ImmutableMap.<URI, Set<URI>> of() : ImmutableMap.copyOf(aliases);
   }

   private boolean checkExtension(String invocationArg, URI namespace,
         Set<URI> aliasesForNamespace, String alias, String name) {
      if (any(extensions.getUnchecked(invocationArg), namespaceOrAliasEquals(namespace, aliasesForNamespace)))
         return true;
      // Could not find extension by namespace or namespace alias. Try to find it by alias next:
      if ( !"".equals(alias)) {
         if (any(extensions.getUnchecked(invocationArg), aliasEquals(alias)))
            return true;
      }
      // Could not find extension by namespace or namespace alias or alias. Try to find it by name next:
      if ( !"".equals(name)) {
         if (any(extensions.getUnchecked(invocationArg), nameEquals(name)))
            return true;
      }
      return false;
   }

   @Override
   public Optional<Object> apply(InvocationSuccess input) {
      Class<?> target = unwrapIfOptional(input.getInvocation().getInvokable().getReturnType());
      Optional<org.jclouds.openstack.v2_0.services.Extension> ext = Optional.fromNullable(target
            .getAnnotation(org.jclouds.openstack.v2_0.services.Extension.class));
      if (ext.isPresent()) {
         URI namespace = URI.create(ext.get().namespace());
         List<Object> args = input.getInvocation().getArgs();
         Set<URI> aliasesForNamespace = aliases.containsKey(namespace) ? aliases.get(namespace) : Sets.<URI> newHashSet();
         String name = ext.get().name();
         String alias = ext.get().alias();

         if (args.isEmpty()) {
            if (checkExtension("", namespace, aliasesForNamespace, alias, name)) {
               return input.getResult();
            }
         } else if (args.size() == 1) {
            String arg0 = checkNotNull(args.get(0), "arg[0] in %s", input).toString();
            if (checkExtension(arg0, namespace, aliasesForNamespace, alias, name)) {
               return input.getResult();
            }
         } else {
            throw new RuntimeException(String.format("expecting zero or one args %s", input));
         }

         return Optional.absent();
      } else {
         // No extension annotation, should check whether to return absent
         return input.getResult();
      }
   }

   @Override
   public String toString() {
      return "PresentWhenExtensionAnnotationMatchesExtensionSet()";
   }

}
