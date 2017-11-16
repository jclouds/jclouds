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
package org.jclouds.softlayer.features;

import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.softlayer.domain.Network;
import org.jclouds.softlayer.domain.Subnet;

import javax.inject.Named;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;

import javax.ws.rs.core.MediaType;
import java.io.Closeable;
import java.util.List;

/**
 * Provides access to Network via their REST API.
 * <p/>
 * @see <a href="http://sldn.softlayer.com/article/REST" />
 */
@RequestFilters(BasicAuthentication.class)
@Path("/v{jclouds.api-version}")
@Consumes(MediaType.APPLICATION_JSON)
public interface NetworkApi extends Closeable {

   String NAME_MASK = "mask.subnets";

   /**
    * returns a list of networks belong to the account
    * @return an account's associated network objects.
    * @see <a href="http://sldn.softlayer.com/reference/services/softlayer_network/getallobjects" />
    */
   @GET
   @Named("Network:getAlllObjects")
   @Path("/SoftLayer_Network/getAllObjects")
   @Produces(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "objectMask", values = NAME_MASK)
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<Network> listNetworks();

   /**
    * returns the name of the network of the given id
    * @param id id of the network
    * @return String or null
    * @see <a href="http://sldn.softlayer.com/reference/services/softlayer_network/getname" />
    */
   @GET
   @Named("Network:getName")
   @Path("/SoftLayer_Network/{id}/getName")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   String getName(@PathParam("id") long id);

   /**
    * returns the notes of the network of the given id
    * @param id id of the network
    * @return String or null
    * @see <a href="http://sldn.softlayer.com/reference/services/softlayer_network/getnotes" />
    */
   @GET
   @Named("Network:getNotes")
   @Path("/SoftLayer_Network/{id}/getNotes")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   String getNotes(@PathParam("id") long id);

   /**
    * returns the details of the network of the given id
    * @param id id of the network
    * @return String or null
    * @see <a href="http://sldn.softlayer.com/reference/services/softlayer_network/getObject" />
    */
   @GET
   @Named("Network:getObject")
   @Path("/SoftLayer_Network/{id}/getObject")
   @Produces(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "objectMask", values = NAME_MASK)
   @Fallback(NullOnNotFoundOr404.class)
   Network getNetwork(@PathParam("id") long id);

   /**
    * returns the subnets of the network of the given id
    * @param id id of the network
    * @return list of subnets under the network or null
    * @see <a href="http://sldn.softlayer.com/reference/services/softlayer_network/getSubnet" />
    */
   @GET
   @Named("Network:getSubnets")
   @Path("/SoftLayer_Network/{id}/getSubnets")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<Subnet> getSubnets(@PathParam("id") long id);

   /**
    * creates a network
    * @param networkToBeCreated the network creation definition of Network.CreateNetwork
    * @return Network created
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_network/createObject" />
    */
   @POST
   @Named("network:createObject")
   @Path("/SoftLayer_Network/createObject")
   @Produces(MediaType.APPLICATION_JSON)
   Network createNetwork(@WrapWith("parameters") List<Network.CreateNetwork> networkToBeCreated);

   /**
    * modifies a network with the given id
    * @param id the id of the network to be edited
    * @param networkToBeEdited the network edition definition of Network.EditNetwork
    * @return true when edition was successful and false for unsuccessful edition
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_network/editObject" />
    */
   @PUT
   @Named("Network:editObject")
   @Path("/SoftLayer_Network/{id}/editObject")
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean editNetwork(@PathParam("id") long id, @WrapWith("parameters") List<Network.EditNetwork> networkToBeEdited);

   /**
    * deletes a network with the given id
    * @param id the id of the network to be deleted
    * @return boolean value true for successful deletion and false for failed deletion
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_network/deleteObject" />
    */
   @DELETE
   @Named("network:deleteObject")
   @Path("/SoftLayer_Network/{id}")
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean deleteNetwork(@PathParam("id") long id);

   /**
    * creates a subnet on the given network
    * @param id the id of the network to be edited
    * @param subnetToBeCreated the subnet creation definition of Subnet.CreateSubnet
    * @return subnet created
    * @see <a href="http://sldn.softlayer.com/reference/services/softlayer_network/createSubnet" />
    */
   @POST
   @Named("Network:createSubnet")
   @Path("/SoftLayer_Network/{id}/createSubnet")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   Subnet createSubnet(@PathParam("id") long id, @WrapWith("parameters") List<Object> subnetToBeCreated);

   /**
    * removes the subnet of the given network
    * @param id id of the targeted seurity group
    * @param subnetToBeDeleted the subnet deletion definition of Subnet.DeleteSubnet
    * @reutrn boolean value true for successful deletion and false for failed deletion
    * @see <a href="http://sldn.softlayer.com/reference/services/softlayer_network/deleteSubnet" />
    */
   @POST
   @Named("Network:deleteSubnet")
   @Path("/SoftLayer_Network/{id}/deleteSubnet")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean deleteSubnet(@PathParam("id") long id, @WrapWith("parameters") List<Subnet.DeleteSubnet> subnetToBeDeleted);
}
