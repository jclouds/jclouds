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
package org.jclouds.digitalocean2.domain;

import static com.google.common.collect.ImmutableList.copyOf;

import java.net.URI;
import java.util.List;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class DropletCreate {
   public abstract Droplet droplet();
   public abstract Links links();

   @AutoValue
   public abstract static class Links {
      
      @AutoValue
      public abstract static class ActionLink {
         public abstract int id();
         public abstract String rel();
         public abstract URI href();
         
         @SerializedNames({"id", "rel", "href"})
         public static ActionLink create(int id, String rel, URI href) {
            return new AutoValue_DropletCreate_Links_ActionLink(id, rel, href);
         }
         
         ActionLink() {}
      }
      
      public abstract List<ActionLink> actions();

      @SerializedNames({ "actions" })
      public static Links create(List<ActionLink> actions) {
         return new AutoValue_DropletCreate_Links(copyOf(actions));
      }

      Links() {}
   }

   @SerializedNames({ "droplet", "links" })
   public static DropletCreate create(Droplet droplet, Links links) {
      return new AutoValue_DropletCreate(droplet, links);
   }

   DropletCreate() {}
}
