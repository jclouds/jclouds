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
package org.jclouds.docker.compute.config;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.docker.compute.functions.ContainerToNodeMetadata;
import org.jclouds.docker.compute.functions.ImageToImage;
import org.jclouds.docker.compute.functions.StateToStatus;
import org.jclouds.docker.compute.options.DockerTemplateOptions;
import org.jclouds.docker.compute.strategy.DockerComputeServiceAdapter;
import org.jclouds.docker.domain.Container;
import org.jclouds.docker.domain.Image;
import org.jclouds.docker.domain.State;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;

public class DockerComputeServiceContextModule extends
        ComputeServiceAdapterContextModule<Container, Hardware, Image, Location> {

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<Container, Hardware, Image, Location>>() {
      }).to(DockerComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<Container, NodeMetadata>>() {
      }).to(ContainerToNodeMetadata.class);
      bind(new TypeLiteral<Function<Image, org.jclouds.compute.domain.Image>>() {
      }).to(ImageToImage.class);
      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<State, NodeMetadata.Status>>() {
      }).to(StateToStatus.class);
      bind(TemplateOptions.class).to(DockerTemplateOptions.class);
   }

}
