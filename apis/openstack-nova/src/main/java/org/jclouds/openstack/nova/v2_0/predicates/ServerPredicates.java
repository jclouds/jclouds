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
package org.jclouds.openstack.nova.v2_0.predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import static org.jclouds.openstack.nova.v2_0.domain.Server.Status.ACTIVE;
import static org.jclouds.openstack.nova.v2_0.domain.Server.Status.SHUTOFF;
import static org.jclouds.util.Predicates2.retry;

import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;

import com.google.common.base.Predicate;

/**
 * This class tests to see if a Server or ServerCreated has reached a desired status. This class is most useful when
 * paired with a RetryablePredicate as in the code below. Together these classes can be used to block execution until
 * the Server or ServerCreated has reached that desired status. This is useful when your Server needs to be 100% ready
 * before you can continue with execution.
 * <p/>
 * For example, you can use the factory methods like so.
 * <p/>
 * <pre>
 * {@code
 * ServerCreated serverCreated = serverApi.create("my-server", image.getId(), flavor.getId());
 *
 * if (!ServerPredicates.awaitActive(serverApi).apply(serverCreated.getId())) {
 *     throw new TimeoutException("Timeout on server: " + serverCreated);
 * }
 * </pre>
 *
 * <pre>
 * {@code
 * if (!ServerPredicates.awaitStatus(serverApi, ACTIVE, 300, 2).apply(server.getId())) {
 *   throw new TimeoutException("Timeout on server: " + serverCreated);
 * }
 * </pre>
 */
public class ServerPredicates {
   private static final int THIRTY_MINUTES = 600 * 3;
   private static final int FIVE_SECONDS = 5;

   /**
    * Waits until a Server is ACTIVE.
    *
    * @param serverApi The ServerApi in the region where your Server resides.
    * @return Predicate that will check the status every 5 seconds for a maximum of 10 minutes.
    */
   public static Predicate<String> awaitActive(ServerApi serverApi) {
      return awaitStatus(serverApi, ACTIVE, THIRTY_MINUTES, FIVE_SECONDS);
   }

   /**
    * Waits until a Server is SHUTOFF.
    *
    * @param serverApi The ServerApi in the region where your Server resides.
    * @return Predicate that will check the status every 5 seconds for a maximum of 10 minutes.
    */
   public static Predicate<String> awaitShutoff(ServerApi serverApi) {
      return awaitStatus(serverApi, SHUTOFF, THIRTY_MINUTES, FIVE_SECONDS);
   }

   /**
    * Waits until a Server reaches Status.
    *
    * @param serverApi The ServerApi in the region where your Server resides.
    * @return Predicate that will check the status every periodInSec seconds for a maximum of maxWaitInSec minutes.
    */
   public static Predicate<String> awaitStatus(
           ServerApi serverApi, Status status, long maxWaitInSec, long periodInSec) {
      ServerStatusPredicate statusPredicate = new ServerStatusPredicate(serverApi, status);

      return retry(statusPredicate, maxWaitInSec, periodInSec, periodInSec, SECONDS);
   }

   public static class ServerStatusPredicate implements Predicate<String> {
      private final ServerApi serverApi;
      private final Status status;

      public ServerStatusPredicate(ServerApi serverApi, Status status) {
         this.serverApi = checkNotNull(serverApi, "serverApi must be defined");
         this.status = checkNotNull(status, "status must be defined");
      }

      /**
       * @return boolean Return true when the Server reaches the Status, false otherwise
       * @throws IllegalStateException if the Server associated with serverId does not exist
       */
      @Override
      public boolean apply(String serverId) {
         checkNotNull(serverId, "server must be defined");

         Server server = serverApi.get(serverId);

         if (server == null) {
            throw new IllegalStateException(String.format("Server %s not found.", serverId));
         }

         return status.equals(server.getStatus());
      }
   }
}
