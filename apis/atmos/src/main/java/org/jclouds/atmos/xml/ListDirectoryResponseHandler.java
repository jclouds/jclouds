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
package org.jclouds.atmos.xml;

import java.util.Set;

import org.jclouds.atmos.domain.DirectoryEntry;
import org.jclouds.atmos.domain.FileType;
import org.jclouds.http.functions.ParseSax;

import com.google.common.collect.Sets;

/**
 * Parses an object list
 * <p/>
 * 
 * @see <a href="https://community.emc.com/community/labs/atmos_online" />
 */
public class ListDirectoryResponseHandler extends ParseSax.HandlerWithResult<Set<DirectoryEntry>> {

   private Set<DirectoryEntry> entries = Sets.newLinkedHashSet();
   private String currentObjectId;
   private FileType currentType;
   private String currentFileName;
   private long currentSize;

   // metadata parsing
   private String currentName;

   private StringBuilder currentText = new StringBuilder();

   public Set<DirectoryEntry> getResult() {
      return entries;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("ObjectID")) {
         currentObjectId = currentText.toString().trim();
      } else if (qName.equals("FileType")) {
         currentType = FileType.fromValue(currentText.toString().trim());
      } else if (qName.equals("Filename")) {
         currentFileName = currentText.toString().trim();
         if (currentFileName.equals(""))
            currentFileName = null;
      } else if (qName.equals("Name")) {
         currentName = currentText.toString().trim();
      } else if (qName.equals("Value")) {
         if (currentName.equals("size")) {
            currentSize = Long.parseLong(currentText.toString().trim());
         }
      } else if (qName.equals("DirectoryEntry")) {
         entries.add(new DirectoryEntry(currentObjectId, currentType, currentFileName, currentSize));
      }
      currentText.setLength(0);
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
