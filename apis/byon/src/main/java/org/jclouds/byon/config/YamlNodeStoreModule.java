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
package org.jclouds.byon.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.byon.Node;
import org.jclouds.byon.domain.YamlNode;
import org.jclouds.byon.functions.NodesFromYamlStream;
import org.jclouds.byon.suppliers.NodesParsedFromSupplier;
import org.jclouds.collect.TransformingMap;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.ByteSource;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

@ConfiguresNodeStore
@Beta
public class YamlNodeStoreModule extends AbstractModule {
   private static final Map<String, ByteSource> BACKING = new ConcurrentHashMap<String, ByteSource>();
   private final Map<String, ByteSource> backing;

   public YamlNodeStoreModule(Map<String, ByteSource> backing) {
      this.backing = backing;
   }

   public YamlNodeStoreModule() {
      this(null);
   }

   @Override
   protected void configure() {
      bind(new TypeLiteral<Supplier<LoadingCache<String, Node>>>() {
      }).to(NodesParsedFromSupplier.class);
      bind(new TypeLiteral<Function<ByteSource, LoadingCache<String, Node>>>() {
      }).to(NodesFromYamlStream.class);
      bind(new TypeLiteral<Function<YamlNode, ByteSource>>() {
      }).toInstance(org.jclouds.byon.domain.YamlNode.yamlNodeToByteSource);
      bind(new TypeLiteral<Function<ByteSource, YamlNode>>() {
      }).toInstance(org.jclouds.byon.domain.YamlNode.byteSourceToYamlNode);
      bind(new TypeLiteral<Function<Node, YamlNode>>() {
      }).toInstance(org.jclouds.byon.domain.YamlNode.nodeToYamlNode);
      bind(new TypeLiteral<Function<YamlNode, Node>>() {
      }).toInstance(org.jclouds.byon.domain.YamlNode.toNode);
      if (backing != null) {
         bind(new TypeLiteral<Map<String, ByteSource>>() {
         }).annotatedWith(Names.named("yaml")).toInstance(backing);
      } else {
         bind(new TypeLiteral<Map<String, ByteSource>>() {
         }).annotatedWith(Names.named("yaml")).toInstance(BACKING);
      }

   }

   @Provides
   @Singleton
   protected LoadingCache<String, Node> provideNodeStore(Map<String, YamlNode> backing, Function<Node, YamlNode> yamlSerializer,
         Function<YamlNode, Node> yamlDeserializer) {
      return CacheBuilder.newBuilder().build(CacheLoader.from(Functions.forMap(new TransformingMap<String, YamlNode, Node>(backing, yamlDeserializer, yamlSerializer))));
   }

   @Provides
   @Singleton
   protected Map<String, YamlNode> provideYamlStore(@Named("yaml") Map<String, ByteSource> backing,
         Function<YamlNode, ByteSource> yamlSerializer, Function<ByteSource, YamlNode> yamlDeserializer) {
      return new TransformingMap<String, ByteSource, YamlNode>(backing, yamlDeserializer, yamlSerializer);
   }
}
