/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.chef.domain;

import java.net.URI;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Cookbook definition as returned by the Chef server >= 0.10.8.
 * 
 * @author Ignasi Barrera
 */
public class CookbookDefinition {
    
    private URI url;
    private Set<Version> versions = Sets.newLinkedHashSet();
    
    // only for deserialization
    CookbookDefinition() {

    }
    
    public CookbookDefinition(URI url, Set<Version> versions) {
        this.url = url;
        this.versions = versions;
    }
    
    public URI getUrl() {
        return url;
    }

    public Set<Version> getVersions() {
        return versions;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((versions == null) ? 0 : versions.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CookbookDefinition other = (CookbookDefinition) obj;
        if (url == null)
        {
            if (other.url != null)
                return false;
        }
        else if (!url.equals(other.url))
            return false;
        if (versions == null)
        {
            if (other.versions != null)
                return false;
        }
        else if (!versions.equals(other.versions))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CookbookDefinition [url=" + url + ", versions=" + versions + "]";
    }


    public static class Version {
        private URI url;
        private String version;
        
        // only for deserialization
        Version() {

        }

        public Version(URI url, String version) {
            this.url = url;
            this.version = version;
        }

        public URI getUrl() {
            return url;
        }

        public String getVersion() {
            return version;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((url == null) ? 0 : url.hashCode());
            result = prime * result + ((version == null) ? 0 : version.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Version other = (Version) obj;
            if (url == null)
            {
                if (other.url != null)
                    return false;
            }
            else if (!url.equals(other.url))
                return false;
            if (version == null)
            {
                if (other.version != null)
                    return false;
            }
            else if (!version.equals(other.version))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "Version [url=" + url + ", version=" + version + "]";
        }
    }
}
