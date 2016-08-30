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
package org.jclouds.docker.domain;

import static org.jclouds.docker.internal.NullSafeCopies.copyOf;
import java.util.List;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

// TODO it may be redundant (we already have Container value class)
@AutoValue
public abstract class ContainerSummary {

   public abstract String id();

   public abstract List<String> names();

   public abstract String created();

   public abstract String image();

   public abstract String command();

   public abstract List<Port> ports();

   public abstract String status();

   ContainerSummary() {
   }

   @SerializedNames({"Id", "Names", "Created", "Image", "Command", "Ports", "Status"})
   public static ContainerSummary create(String id, List<String> names, String created, String image, String command, List<Port> ports, String status) {
      return new AutoValue_ContainerSummary(id, copyOf(names), created, image, command, copyOf(ports), status);
   }

}
