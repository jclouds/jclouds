#!/bin/bash

#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Usage examples:
#
# Verify a release candidate
# $ ./verify_jclouds_rc.sh 2.0.0-rc1

set -ex

RELEASE=$1

if [ -e /sbin/md5 ]; then
    MD5SUM="/sbin/md5 -q"
else
    MD5SUM=/usr/bin/md5sum
fi

if [ -e /sbin/sha1 ]; then
    SHA1SUM=/sbin/sha1
elif [ -e /usr/bin/shasum ]; then
    SHA1SUM=/usr/bin/shasum
else
    SHA1SUM=/usr/bin/sha1sum
fi

curl http://www.apache.org/dist/jclouds/KEYS | gpg --import

svn co https://dist.apache.org/repos/dist/dev/jclouds/$RELEASE

cd $RELEASE

for tarball in `ls *.tar.gz`; do
    # Verify md5sum
    diff -wB <($MD5SUM ${tarball} | perl -pe 's/(.*?)\s.*/$1/') <(cat ${tarball}.md5);
    # Verify sha1sum
    diff -wq <($SHA1SUM ${tarball}|perl -pe 's/(.*?)\s.*/$1/') <(cat ${tarball}.sha1);
    # verify signature
    gpg --verify ${tarball}.asc ${tarball};
    # Untar
    tar -xzf ${tarball};
done

for d in jclouds jclouds-labs jclouds-labs-openstack jclouds-labs-aws jclouds-labs-google jclouds-chef jclouds-karaf jclouds-cli; do
    reldir=${d}-$(echo $RELEASE | perl -pe 's/(.*)-rc.*/$1/');
    if [ -d "${reldir}" ]; then
	cd ${reldir};
	mvn clean install;
	cd ..;
    fi
done
