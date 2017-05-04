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
package org.jclouds.aws.ec2.domain;

import java.util.List;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Amazon EC2 Internet Gateway.
 *
 * @see <a href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_InternetGateway.html" >doc</a>
 */
@AutoValue
public abstract class InternetGateway {

   @Nullable
   public abstract String id();

   @Nullable
   public abstract List<InternetGatewayAttachment> attachmentSet();

   @Nullable
   public abstract Map<String, String> tags();

   @SerializedNames({"internetGatewayId", "attachmentSet", "tagSet"})
   public static InternetGateway create(String id, List<InternetGatewayAttachment> attachmentSet,
                                        Map<String, String> tags) {
      return builder()
         .id(id)
         .attachmentSet(attachmentSet)
         .tags(tags)
         .build();
   }

   InternetGateway() {}

   public static Builder builder() {
      return new AutoValue_InternetGateway.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder id(String id);
      public abstract Builder attachmentSet(List<InternetGatewayAttachment> attachmentSet);
      public abstract Builder tags(Map<String, String> tags);

      @Nullable abstract List<InternetGatewayAttachment> attachmentSet();
      @Nullable abstract Map<String, String> tags();

      abstract InternetGateway autoBuild();

      public InternetGateway build() {
         tags(tags() != null ? ImmutableMap.copyOf(tags()) : ImmutableMap.<String, String>of());
         attachmentSet(attachmentSet() != null
            ? ImmutableList.copyOf(attachmentSet()) : ImmutableList.<InternetGatewayAttachment>of());
         return autoBuild();
      }

   }

}
