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
package org.jclouds.rackspace.cloudservers.uk.config;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneHttpApiModule.aliasBinder;

import java.net.URI;

import org.jclouds.openstack.nova.v2_0.config.NovaHttpApiModule;
import org.jclouds.openstack.nova.v2_0.extensions.ExtensionNamespaces;
import org.jclouds.rest.ConfiguresHttpApi;

import com.google.inject.multibindings.MapBinder;

/**
 * Configures the Rackspace connection.
 */
@ConfiguresHttpApi
public class CloudServersUKHttpApiModule extends NovaHttpApiModule {

    @Override
    protected void configure() {
        super.configure();
        MapBinder<URI, URI> aliases = aliasBinder(binder());
        aliases.addBinding(URI.create(ExtensionNamespaces.VOLUME_ATTACHMENTS)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/volumes/api/v1.1"));
    }

}
