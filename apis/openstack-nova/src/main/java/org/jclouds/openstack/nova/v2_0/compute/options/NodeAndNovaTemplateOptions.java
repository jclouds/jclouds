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
package org.jclouds.openstack.nova.v2_0.compute.options;

import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.compute.domain.NodeMetadata;

import com.google.common.util.concurrent.Atomics;

/**
 * Simple data-structure for holding a NodeMetadata object along with a
 * corresponding NovaTemplateOptions object.
 */
public class NodeAndNovaTemplateOptions {

   private final AtomicReference<NodeMetadata> nodeMetadata;
   private final AtomicReference<NovaTemplateOptions> novaTemplateOptions;

   protected NodeAndNovaTemplateOptions(AtomicReference<NodeMetadata> nodeMetadata, AtomicReference<NovaTemplateOptions> novaTemplateOptions) {
      this.nodeMetadata = nodeMetadata;
      this.novaTemplateOptions = novaTemplateOptions;
   }

   public AtomicReference<NodeMetadata> getNodeMetadata() {
      return nodeMetadata;
   }

   public AtomicReference<NovaTemplateOptions> getNovaTemplateOptions() {
      return novaTemplateOptions;
   }

   public static NodeAndNovaTemplateOptions newReference(AtomicReference<NodeMetadata> node, AtomicReference<NovaTemplateOptions> options) {
      return new NodeAndNovaTemplateOptions(node, options);
   }

   public static AtomicReference<NodeAndNovaTemplateOptions> newAtomicReference(AtomicReference<NodeMetadata> node, AtomicReference<NovaTemplateOptions> options) {
      return Atomics.newReference(NodeAndNovaTemplateOptions.newReference(node, options));
   }
}
