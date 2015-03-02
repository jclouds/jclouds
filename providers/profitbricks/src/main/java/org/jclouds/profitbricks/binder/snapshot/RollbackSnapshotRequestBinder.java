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
package org.jclouds.profitbricks.binder.snapshot;

import org.jclouds.profitbricks.binder.BaseProfitBricksRequestBinder;
import org.jclouds.profitbricks.domain.Snapshot;

import static java.lang.String.format;

public class RollbackSnapshotRequestBinder extends BaseProfitBricksRequestBinder<Snapshot.Request.RollbackPayload> {

    protected final StringBuilder requestBuilder;

    protected RollbackSnapshotRequestBinder() {
        super("snapshot");
        this.requestBuilder = new StringBuilder(128);
    }

    @Override
    protected String createPayload(Snapshot.Request.RollbackPayload payload) {
        requestBuilder.append("<ws:rollbackSnapshot>")
                .append("<request>")
                .append(format("<snapshotId>%s</snapshotId>", payload.snapshotId()))
                .append(format("<storageId>%s</storageId>", payload.storageId()))
                .append("</request>")
                .append("</ws:rollbackSnapshot>");
        return requestBuilder.toString();
    }
}
