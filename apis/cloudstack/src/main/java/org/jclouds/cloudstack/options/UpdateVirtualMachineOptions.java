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
package org.jclouds.cloudstack.options;

import com.google.common.collect.ImmutableSet;
import org.jclouds.http.options.BaseHttpRequestOptions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.BaseEncoding.base64;

/**
 * Options for updating virtual machines.
 *
 * @see <a
 *      href="http://download.cloud.com/releases/3.0.3/api_3.0.3/root_admin/updateVirtualMachine.html"
 *      />
 */
public class UpdateVirtualMachineOptions extends BaseHttpRequestOptions {

    public static final UpdateVirtualMachineOptions NONE = new UpdateVirtualMachineOptions();

    /**
     * sets the displayName - just for display purposes. We don't pass this
     * parameter to the backend.
     *
     * @param displayName an optional user generated name for the virtual machine
     */
    public UpdateVirtualMachineOptions displayName(String displayName) {
        this.queryParameters.replaceValues("displayname", ImmutableSet.of(displayName));
        return this;
    }

    /**
     * @param group an optional group for the virtual machine
     */
    public UpdateVirtualMachineOptions group(String group) {
        this.queryParameters.replaceValues("group", ImmutableSet.of(group));
        return this;
    }

    /**
     * @param haEnable true if high-availability is enabled for the virtual machine, false otherwise
     */
    public UpdateVirtualMachineOptions haEnable(boolean haEnable) {
        this.queryParameters.replaceValues("haenable", ImmutableSet.of(String.valueOf(haEnable)));
        return this;
    }

    /**
     * @param osTypeId the ID of the OS type that best represents this VM.
     */
    public UpdateVirtualMachineOptions osTypeId(String osTypeId) {
        this.queryParameters.replaceValues("ostypeid", ImmutableSet.of(String.valueOf(osTypeId)));
        return this;
    }

    /**
     * @param unencodedData an optional binary data that can be sent to the virtual machine
     *                      upon a successful deployment. This binary data must be base64
     *                      encoded before adding it to the request. Currently only HTTP GET
     *                      is supported. Using HTTP GET (via querystring), you can send up
     *                      to 2KB of data after base64 encoding.
     */
    public UpdateVirtualMachineOptions userData(byte[] unencodedData) {
        int length = checkNotNull(unencodedData, "unencodedData").length;
        checkArgument(length > 0, "userData cannot be empty");
        checkArgument(length <= 2 * 1024, "userData cannot be larger than 2kb");
        this.queryParameters.replaceValues("userdata", ImmutableSet.of(base64().encode(unencodedData)));
        return this;
    }


    public static class Builder {

        /**
         * @see UpdateVirtualMachineOptions#displayName
         */
        public static UpdateVirtualMachineOptions displayName(String displayName) {
            UpdateVirtualMachineOptions options = new UpdateVirtualMachineOptions();
            return options.displayName(displayName);
        }

        /**
         * @see UpdateVirtualMachineOptions#group
         */
        public static UpdateVirtualMachineOptions group(String group) {
            UpdateVirtualMachineOptions options = new UpdateVirtualMachineOptions();
            return options.group(group);
        }

        /**
         * @see UpdateVirtualMachineOptions#haEnable
         */
        public static UpdateVirtualMachineOptions haEnable(boolean haEnable) {
            UpdateVirtualMachineOptions options = new UpdateVirtualMachineOptions();
            return options.haEnable(haEnable);
        }

        /**
         * @see UpdateVirtualMachineOptions#osTypeId
         */
        public static UpdateVirtualMachineOptions osTypeId(String osTypeId) {
            UpdateVirtualMachineOptions options = new UpdateVirtualMachineOptions();
            return options.osTypeId(osTypeId);
        }

        /**
         * @see UpdateVirtualMachineOptions#userData
         */
        public static UpdateVirtualMachineOptions userData(byte[] unencodedData) {
            UpdateVirtualMachineOptions options = new UpdateVirtualMachineOptions();
            return options.userData(unencodedData);
        }
    }
}
