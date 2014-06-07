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
package org.jclouds.openstack.trove.v1.utils;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.trove.v1.TroveApi;
import org.jclouds.openstack.trove.v1.domain.Instance;
import org.jclouds.openstack.trove.v1.internal.BaseTroveApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests TroveUtils
 */
@Test(groups = "unit", testName = "InstanceApiExpectTest")
public class TroveUtilsExpectTest extends BaseTroveApiExpectTest {
    
    public void testHelperCreateInstance() {
        HttpRequest createInstance = authenticatedGET().endpoint(URI.create("http://172.16.0.1:8776/v1/3456/instances"))
                                                       .method("POST")
                                                       .payload(payloadFromResourceWithContentType("/instance_create_request.json", MediaType.APPLICATION_JSON))
                                                       .build();
        HttpResponse createInstanceSuccess = HttpResponse.builder().statusCode(200).payload(payloadFromResource("/instance_create.json")).build();
        HttpResponse createInstanceFail = HttpResponse.builder().statusCode(404).payload(payloadFromResource("/instance_create.json")).build();
        HttpRequest getInstance = authenticatedGET().endpoint(URI.create("http://172.16.0.1:8776/v1/3456/instances/44b277eb-39be-4921-be31-3d61b43651d7")).build();
        HttpResponse badStatus = HttpResponse.builder().statusCode(200).payload(payloadFromResource("/instance_get_bad_instance.json")).build();
        HttpResponse goodStatus = HttpResponse.builder().statusCode(200).payload(payloadFromResource("/instance_get.json")).build();
        HttpResponse deletedStatus = HttpResponse.builder().statusCode(404).payload(payloadFromResource("/instance_get.json")).build();
        HttpRequest deleteInstance = authenticatedGET().endpoint(URI.create("http://172.16.0.1:8776/v1/3456/instances/44b277eb-39be-4921-be31-3d61b43651d7")).method("DELETE").build();
        HttpResponse deleteInstanceResponse = HttpResponse.builder().statusCode(202).build();

        List<HttpRequest> requests = ImmutableList.of(  keystoneAuthWithUsernameAndPasswordAndTenantName, createInstance,     createInstance,     createInstance,        getInstance, deleteInstance,         getInstance,   createInstance,     createInstance,        getInstance);
        List<HttpResponse> responses = ImmutableList.of(responseWithKeystoneAccess,                       createInstanceFail, createInstanceFail, createInstanceSuccess, badStatus,   deleteInstanceResponse, deletedStatus, createInstanceFail, createInstanceSuccess, goodStatus); 

        TroveApi api = orderedRequestsSendResponses(requests, responses);

        TroveUtils utils = new TroveUtils(api);
        Instance instance = utils.getWorkingInstance("RegionOne", "json_rack_instance", "1", 2);
        assertEquals(instance.getSize(), 2);
        assertEquals(instance.getName(), "json_rack_instance");  
    }
}
