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
package org.jclouds.sshj;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Named;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.Buffer.BufferException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.OpenSSHKeyFile;
import net.schmizz.sshj.userauth.method.AuthMethod;

import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.sshj.SshjSshClient.Connection;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;

import com.jcraft.jsch.agentproxy.AgentProxy;
import com.jcraft.jsch.agentproxy.Connector;
import com.jcraft.jsch.agentproxy.Identity;
import com.jcraft.jsch.agentproxy.sshj.AuthAgent;

public class SSHClientConnection implements Connection<SSHClient> {
   private Optional<Connector> agentConnector;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      protected HostAndPort hostAndPort;
      protected LoginCredentials loginCredentials;
      protected int connectTimeout;
      protected int sessionTimeout;
      protected Optional<Connector> agentConnector;

      /**
       * @see SSHClientConnection#getHostAndPort()
       */
      public Builder hostAndPort(HostAndPort hostAndPort) {
         this.hostAndPort = hostAndPort;
         return this;
      }

      /**
       * @see SSHClientConnection#getLoginCredentials()
       */
      public Builder loginCredentials(LoginCredentials loginCredentials) {
         this.loginCredentials = loginCredentials;
         return this;
      }

      /**
       * @see SSHClientConnection#getConnectTimeout()
       */
      public Builder connectTimeout(int connectTimeout) {
         this.connectTimeout = connectTimeout;
         return this;
      }

      /**
       * @see SSHClientConnection#getConnectTimeout()
       */
      public Builder sessionTimeout(int sessionTimeout) {
         this.sessionTimeout = sessionTimeout;
         return this;
      }

      /**
       * @see SSHClientConnection#getAgentConnector()
       */
      public Builder agentConnector(Optional<Connector> agentConnector) {
         this.agentConnector = agentConnector;
         return this;
      }

      public SSHClientConnection build() {
         return new SSHClientConnection(hostAndPort, loginCredentials, connectTimeout, sessionTimeout, agentConnector);
      }

      protected Builder fromSSHClientConnection(SSHClientConnection in) {
         return hostAndPort(in.getHostAndPort()).connectTimeout(in.getConnectTimeout()).loginCredentials(
                  in.getLoginCredentials()).sessionTimeout(in.getSessionTimeout()).agentConnector(in.getAgentConnector());
      }
   }

   private SSHClientConnection(HostAndPort hostAndPort, LoginCredentials loginCredentials, int connectTimeout,
            int sessionTimeout, Optional<Connector> agentConnector) {
      this.hostAndPort = checkNotNull(hostAndPort, "hostAndPort");
      this.loginCredentials = checkNotNull(loginCredentials, "loginCredentials for %", hostAndPort);
      this.connectTimeout = connectTimeout;
      this.sessionTimeout = sessionTimeout;
      this.agentConnector = checkNotNull(agentConnector, "agentConnector for %", hostAndPort);
   }
   
   @Resource
   @Named("jclouds.ssh")
   protected Logger logger = Logger.NULL;
   
   private final HostAndPort hostAndPort;
   private final LoginCredentials loginCredentials;
   private final int connectTimeout;
   private final int sessionTimeout;

   @VisibleForTesting
   transient SSHClient ssh;

   @Override
   public void clear() {
      if (ssh != null && ssh.isConnected()) {
         try {
            ssh.disconnect();
         } catch (AssertionError e) {
            // already connected
         } catch (IOException e) {
            logger.debug("<< exception disconnecting from %s: %s", e, e.getMessage());
         }
         ssh = null;
      }
   }

   @Override
   public SSHClient create() throws Exception {
      ssh = new net.schmizz.sshj.SSHClient();
      ssh.addHostKeyVerifier(new PromiscuousVerifier());
      if (connectTimeout != 0) {
         ssh.setConnectTimeout(connectTimeout);
      }
      if (sessionTimeout != 0) {
         ssh.setTimeout(sessionTimeout);
      }
      ssh.connect(hostAndPort.getHostText(), hostAndPort.getPortOrDefault(22));
      if (loginCredentials.hasUnencryptedPrivateKey()) {
         OpenSSHKeyFile key = new OpenSSHKeyFile();
         key.init(loginCredentials.getOptionalPrivateKey().get(), null);
         ssh.authPublickey(loginCredentials.getUser(), key);
      } else if (loginCredentials.getOptionalPassword().isPresent()) {
         ssh.authPassword(loginCredentials.getUser(), loginCredentials.getOptionalPassword().get());
      } else if (agentConnector.isPresent()) {
         AgentProxy proxy = new AgentProxy(agentConnector.get());
         ssh.auth(loginCredentials.getUser(), getAuthMethods(proxy));
      }
      return ssh;
   }

   /**
    * @return host and port, where port if not present defaults to {@code 22}
    */
   public HostAndPort getHostAndPort() {
      return hostAndPort;
   }

   /**
    * 
    * @return login used in this ssh
    */
   public LoginCredentials getLoginCredentials() {
      return loginCredentials;
   }

   /**
    * 
    * @return how long to wait for the initial connection to be made
    */
   public int getConnectTimeout() {
      return connectTimeout;
   }

   /**
    * 
    * @return how long to keep the ssh open, or {@code 0} for indefinitely
    */
   public int getSessionTimeout() {
      return sessionTimeout;
   }

   /**
    *
    * @return Ssh agent connector
    */
   public Optional<Connector> getAgentConnector() {
      return agentConnector;
   }

   /**
    * 
    * @return the current ssh or {@code null} if not connected
    */
   public SSHClient getSSHClient() {
      return ssh;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      SSHClientConnection that = SSHClientConnection.class.cast(o);
      return equal(this.hostAndPort, that.hostAndPort) && equal(this.loginCredentials, that.loginCredentials)
               && equal(this.ssh, that.ssh);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(hostAndPort, loginCredentials, ssh);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("hostAndPort", hostAndPort).add("loginUser", loginCredentials.getUser())
               .add("ssh", ssh != null ? ssh.hashCode() : null).add("connectTimeout", connectTimeout).add(
                        "sessionTimeout", sessionTimeout).toString();
   }

   private static List<AuthMethod> getAuthMethods(AgentProxy agent) throws BufferException  {
      ImmutableList.Builder<AuthMethod> identities = ImmutableList.builder();
      for (Identity identity : agent.getIdentities()) {
         identities.add(new AuthAgent(agent, identity));
      }
      return identities.build();
   }

}
