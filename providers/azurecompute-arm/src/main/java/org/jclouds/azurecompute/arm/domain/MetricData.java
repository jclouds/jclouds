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
package org.jclouds.azurecompute.arm.domain;

import java.util.Date;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/**
 *
 */
@AutoValue
public abstract class MetricData
{

    /**
     * The timestamp for the metric value in ISO 8601 format.
     */
    public abstract Date timeStamp();

    /**
     * The average value in the time range
     */
    @Nullable
    public abstract Double total();

    /**
     * The sum of all of the values in the time range.
     */
    @Nullable
    public abstract Double average();

    /**
     * The least value in the time range.
     */
    @Nullable
    public abstract Double minimum();

    /**
     * The greatest value in the time range.
     */
    @Nullable
    public abstract Double maximum();

    /**
     * The number of samples in the time range.
     */
    @Nullable
    public abstract Long count();

    @SerializedNames({"timeStamp", "total", "average", "minimum", "maximum", "count"})
    public static MetricData create(final Date timeStamp, final Double total, final Double average,
        final Double minimum, final Double maximum, final Long count)
    {
        return new AutoValue_MetricData(timeStamp, total, average, minimum, maximum, count);
    }
}
