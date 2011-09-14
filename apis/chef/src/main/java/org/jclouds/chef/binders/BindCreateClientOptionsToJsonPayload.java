/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.chef.binders;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.chef.options.CreateClientOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * Bind the parameters of a {@link CreateClientOptions} to the payload, taking care of transforming
 * all boolean strings to boolean values.
 * 
 * @author Ignasi Barrera
 */
public class BindCreateClientOptionsToJsonPayload extends BindToJsonPayload
{
    @Inject
    public BindCreateClientOptionsToJsonPayload(Json jsonBinder) {
        super(jsonBinder);
    }

    @Override
    public <R extends HttpRequest> R bindToRequest(R request, Map<String, String> postParams)
    {
        Map<String, Object> params =
            Maps.transformValues(postParams, new Function<String, Object>() {
                @Override
                public Object apply(String input) {
                    // Transform boolean values to Boolean objects so they are serialized as boolean
                    return input.equals("true") || input.equals("false") ? Boolean.valueOf(input)
                        : input;
                }
            });

        return bindToRequest(request, (Object) params);
    }

}
