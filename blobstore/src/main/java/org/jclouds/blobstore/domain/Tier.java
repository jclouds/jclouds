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
package org.jclouds.blobstore.domain;

/**
 * Store data with different strategies, ranging from most performant to lowest
 * cost.  Tiering is best-effort and some providers will map lower tiers to
 * higher ones.
 */
public enum Tier {
   /** Optimize for access speed. */
   STANDARD,
   /** Balance access speed against storage cost. */
   INFREQUENT,
   /**
    * Optimize for storage cost.  Some providers may require a separate call to
    * set the blob to STANDARD tier before access.
    */
   ARCHIVE;
}
