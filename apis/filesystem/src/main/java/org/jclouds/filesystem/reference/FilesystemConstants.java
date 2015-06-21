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
package org.jclouds.filesystem.reference;

/**
 * Common constants used in filesystem provider
 */
public final class FilesystemConstants {

    /** Specify the base directory where provider starts its file operations - must exists */
    public static final String PROPERTY_BASEDIR = "jclouds.filesystem.basedir";

    /** Specify if the Content-Type of a file should be autodetected if it is not set */
    public static final String PROPERTY_AUTO_DETECT_CONTENT_TYPE = "jclouds.filesystem.auto-detect-content-type";

    private FilesystemConstants() {
        throw new AssertionError("intentionally unimplemented");
    }
}
