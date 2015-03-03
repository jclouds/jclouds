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
package org.jclouds.json;

import static com.google.common.io.BaseEncoding.base16;
import static com.google.common.primitives.Bytes.asList;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.config.GsonModule;
import org.jclouds.json.config.GsonModule.DefaultExclusionStrategy;
import org.testng.annotations.Test;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

@Test
public class JsonTest {
   private Json json = Guice.createInjector(new GsonModule()).getInstance(Json.class);

   private static class ObjectNoDefaultConstructor {
      private final String stringValue;
      private final int intValue;

      public ObjectNoDefaultConstructor(String stringValue, int intValue) {
         this.stringValue = stringValue;
         this.intValue = intValue;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + intValue;
         result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
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
         ObjectNoDefaultConstructor other = (ObjectNoDefaultConstructor) obj;
         if (intValue != other.intValue)
            return false;
         if (stringValue == null) {
            if (other.stringValue != null)
               return false;
         } else if (!stringValue.equals(other.stringValue))
            return false;
         return true;
      }
   }

   public void testObjectNoDefaultConstructor() {
      ObjectNoDefaultConstructor obj = new ObjectNoDefaultConstructor("foo", 1);
      assertEquals(json.toJson(obj), "{\"stringValue\":\"foo\",\"intValue\":1}");
      ObjectNoDefaultConstructor obj2 = json.fromJson(json.toJson(obj), ObjectNoDefaultConstructor.class);
      assertEquals(obj2, obj);
      assertEquals(json.toJson(obj2), json.toJson(obj));
   }

   static class ExcludeStringValue implements DefaultExclusionStrategy {
      public boolean shouldSkipClass(Class<?> clazz) {
        return false;
      }

      public boolean shouldSkipField(FieldAttributes f) {
        return f.getName().equals("stringValue");
      }
   }

   public void testExcluder() {
      Json excluder = Guice.createInjector(new GsonModule(), new AbstractModule() {
         protected void configure() {
            bind(DefaultExclusionStrategy.class).to(ExcludeStringValue.class);
         }
      }).getInstance(Json.class);
      ObjectNoDefaultConstructor obj = new ObjectNoDefaultConstructor("foo", 1);
      assertEquals(excluder.toJson(obj), "{\"intValue\":1}");
   }

   private static class EnumInside {
      private static enum Test {
         FOO, BAR;
      }

      private Test enumValue;
   }

   private static class ByteList {
      List<Byte> checksum;
   }

   public void testByteList() {
      ByteList bl = new ByteList();
      bl.checksum = asList(base16().lowerCase().decode("1dda05ed139664f1f89b9dec482b77c0"));
      assertEquals(json.toJson(bl), "{\"checksum\":\"1dda05ed139664f1f89b9dec482b77c0\"}");
      assertEquals(json.fromJson(json.toJson(bl), ByteList.class).checksum, bl.checksum);
   }

   public void testPropertiesSerializesDefaults() {
      Properties props = new Properties();
      props.put("string", "string");
      props.put("number", "1");
      props.put("boolean", "true");
      assertEquals(json.toJson(props), "{\"string\":\"string\",\"boolean\":\"true\",\"number\":\"1\"}");
      Properties props3 = new Properties(props);
      assertEquals(json.toJson(props3), "{\"string\":\"string\",\"boolean\":\"true\",\"number\":\"1\"}");
      Properties props2 = json.fromJson(json.toJson(props), Properties.class);
      assertEquals(props2, props);
      assertEquals(json.toJson(props2), json.toJson(props));
   }

   public void testMapStringObjectWithAllValidValuesOneDeep() {
      Map<String, Object> map = ImmutableMap.<String, Object>builder()
         .put("string", "string")
         .put("map", ImmutableMap.of("key", "value"))
         .put("list", ImmutableList.of("key", "value"))
         .put("boolean", true)
         .put("number", 1.0)
         .build();
      assertEquals(json.toJson(map),
               "{\"string\":\"string\",\"map\":{\"key\":\"value\"},\"list\":[\"key\",\"value\"],\"boolean\":true,\"number\":1.0}");
      Map<String, Object> map2 = json.fromJson(json.toJson(map), new TypeLiteral<Map<String, Object>>() {
      }.getType());
      assertEquals(map2, map);
      assertEquals(json.toJson(map2), json.toJson(map));
   }

   public void testMapStringObjectWithNumericalKeysConvertToStrings() {
      Map<String, Object> map = ImmutableMap.<String, Object> of("map", ImmutableMap.of(1, "value"));
      assertEquals(json.toJson(map), "{\"map\":{\"1\":\"value\"}}");
      Map<String, Object> map2 = json.fromJson(json.toJson(map), new TypeLiteral<Map<String, Object>>() {
      }.getType());
      // note conversion.. ensures valid
      assertEquals(map2, ImmutableMap.<String, Object> of("map", ImmutableMap.of("1", "value")));
      assertEquals(json.toJson(map2), json.toJson(map));
   }

   public void testMapStringObjectWithBooleanKeysConvertToStrings() {
      Map<String, Object> map = ImmutableMap.<String, Object> of("map", ImmutableMap.of(true, "value"));
      assertEquals(json.toJson(map), "{\"map\":{\"true\":\"value\"}}");
      Map<String, Object> map2 = json.fromJson(json.toJson(map), new TypeLiteral<Map<String, Object>>() {
      }.getType());
      // note conversion.. ensures valid
      assertEquals(map2, ImmutableMap.<String, Object> of("map", ImmutableMap.of("true", "value")));
      assertEquals(json.toJson(map2), json.toJson(map));
   }

   public void testDeserializeEnum() {
      assertEquals(json.fromJson("{enumValue : \"FOO\"}", EnumInside.class).enumValue, EnumInside.Test.FOO);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testDeserializeEnumWhenBadValue() {
      assertEquals(json.fromJson("{enumValue : \"s\"}", EnumInside.class).enumValue, EnumInside.Test.FOO);
   }

   private static class EnumInsideWithParser {
      private static enum Test {
         FOO, BAR, UNRECOGNIZED;

         @SuppressWarnings("unused")
         public static Test fromValue(String state) {
            try {
               return valueOf(state);
            } catch (IllegalArgumentException e) {
               return UNRECOGNIZED;
            }
         }
      }

      private Test enumValue;
   }

   public void testDeserializeEnumWithParser() {
      assertEquals(json.fromJson("{enumValue : \"FOO\"}", EnumInsideWithParser.class).enumValue,
               EnumInsideWithParser.Test.FOO);
   }

   public void testDeserializeEnumWithParserAndBadValue() {
      assertEquals(json.fromJson("{enumValue : \"sd\"}", EnumInsideWithParser.class).enumValue,
               EnumInsideWithParser.Test.UNRECOGNIZED);
   }

   @AutoValue
   abstract static class SerializedNamesType {
      abstract String id();

      @Nullable abstract Map<String, String> volumes();

      @SerializedNames({ "Id", "Volumes" })
      private static SerializedNamesType create(String id, Map<String, String> volumes) {
         return new AutoValue_JsonTest_SerializedNamesType(id, volumes);
      }
   }

   public void autoValueSerializedNames() {
      Json json = Guice.createInjector(new GsonModule()).getInstance(Json.class);

      SerializedNamesType resource = SerializedNamesType.create("1234", Collections.<String, String>emptyMap());
      String spinalJson = "{\"Id\":\"1234\",\"Volumes\":{}}";

      assertEquals(json.toJson(resource), spinalJson);
      assertEquals(json.fromJson(spinalJson, SerializedNamesType.class), resource);
   }

   public void autoValueWithBuilder() {
      Json json = Guice.createInjector(new GsonModule()).getInstance(Json.class);

      SerializedNamesWithBuilder resource = SerializedNamesWithBuilder.builder().id("1234").number(2).build();
      String spinalJson = "{\"custom_id_name\":\"1234\",\"number\":2}";

      assertEquals(json.toJson(resource), spinalJson);
      assertEquals(json.fromJson(spinalJson, SerializedNamesWithBuilder.class), resource);
   }

   public void autoValueWithBuilderNested() {
      Json json = Guice.createInjector(new GsonModule()).getInstance(Json.class);

      SerializedNamesNestedWithBuilder resource = SerializedNamesNestedWithBuilder.builder().id("1234")
            .snwb(ImmutableList.of(SerializedNamesWithBuilder.builder().id("5678").number(1).build())).build();

      String spinalJson = "{\"nested_id_name\":\"1234\",\"serialized_name_with_builder\":[{\"custom_id_name\":\"5678\",\"number\":1}]}";

      assertEquals(json.toJson(resource), spinalJson);
      assertEquals(json.fromJson(spinalJson, SerializedNamesNestedWithBuilder.class), resource);
   }

   public void autoValueWithBuilderMissingNested() {
      Json json = Guice.createInjector(new GsonModule()).getInstance(Json.class);

      SerializedNamesNestedWithBuilder resource = SerializedNamesNestedWithBuilder.builder().id("1234")
            .snwb(ImmutableList.<SerializedNamesWithBuilder>of()).build();

      String spinalJson = "{\"nested_id_name\":\"1234\",\"serialized_name_with_builder\":[]}";

      assertEquals(json.toJson(resource), spinalJson);
      assertEquals(json.fromJson(spinalJson, SerializedNamesNestedWithBuilder.class), resource);
   }

   @AutoValue
   abstract static class SerializedNamesNestedWithBuilder {
      public abstract String getId();
      @Nullable
      public abstract List<SerializedNamesWithBuilder> getSnwb();

      public static Builder builder() {
         return new AutoValue_JsonTest_SerializedNamesNestedWithBuilder.Builder();
      }
      public abstract Builder toBuilder();

      @AutoValue.Builder
      public interface Builder {
         Builder id(String id);
         Builder snwb(List<SerializedNamesWithBuilder> snwb);
         SerializedNamesNestedWithBuilder build();
      }

      @SerializedNames({"nested_id_name", "serialized_name_with_builder"})
      private static SerializedNamesNestedWithBuilder create(String id, List<SerializedNamesWithBuilder> snwb) {
         return builder().id(id).snwb(snwb).build();
      }
   }

   @AutoValue
   abstract static class SerializedNamesWithBuilder {
      public abstract String getId();
      public abstract int getNumber();

      public static Builder builder() {
         return new AutoValue_JsonTest_SerializedNamesWithBuilder.Builder();
      }
      public abstract Builder toBuilder();

      @AutoValue.Builder
      public interface Builder {
         Builder id(String id);
         Builder number(int number);
         SerializedNamesWithBuilder build();
      }

      @SerializedNames({"custom_id_name", "number"})
      private static SerializedNamesWithBuilder create(String id, int number) {
         return builder().id(id).number(number).build();
      }
   }

   @AutoValue
   abstract static class SerializedNamesTooFewType {
      abstract String id();

      @Nullable abstract Map<String, String> volumes();

      @SerializedNames("Id") // TODO: check things like this with error-prone, not runtime!
      private static SerializedNamesTooFewType create(String id, Map<String, String> volumes) {
         return new AutoValue_JsonTest_SerializedNamesTooFewType(id, volumes);
      }
   }

   /** Common problem. Someone adds a parameter, but forgets to add it to the names list. */
   @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "Incorrect number .*")
   public void autoValueSerializedNames_tooFew() {
      Json json = Guice.createInjector(new GsonModule()).getInstance(Json.class);
      json.toJson(SerializedNamesTooFewType.create("1234", null));
   }

   public void autoValueSerializedNames_nullValueInJson() {
      Json json = Guice.createInjector(new GsonModule()).getInstance(Json.class);

      assertEquals(json.fromJson("{\"Id\":\"1234\",\"Volumes\":null}", SerializedNamesType.class),
            SerializedNamesType.create("1234", null));
   }

   @AutoValue
   abstract static class NestedSerializedNamesType {
      abstract SerializedNamesType item();
      abstract List<SerializedNamesType> items();

      @SerializedNames({ "Item", "Items" })
      private static NestedSerializedNamesType create(SerializedNamesType item, List<SerializedNamesType> items) {
         return new AutoValue_JsonTest_NestedSerializedNamesType(item, items);
      }
   }

   private final NestedSerializedNamesType nested = NestedSerializedNamesType
         .create(SerializedNamesType.create("1234", Collections.<String, String>emptyMap()),
               Arrays.asList(SerializedNamesType.create("5678", ImmutableMap.of("Foo", "Bar"))));

   public void autoValueSerializedNames_nestedType() {
      Json json = Guice.createInjector(new GsonModule()).getInstance(Json.class);

      String spinalJson = "{\"Item\":{\"Id\":\"1234\",\"Volumes\":{}},\"Items\":[{\"Id\":\"5678\",\"Volumes\":{\"Foo\":\"Bar\"}}]}";

      assertEquals(json.toJson(nested), spinalJson);
      assertEquals(json.fromJson(spinalJson, NestedSerializedNamesType.class), nested);
   }

   public void autoValueSerializedNames_overriddenTypeAdapterFactory() {
      Json json = Guice.createInjector(new GsonModule(), new AbstractModule() {
         @Override protected void configure() {
         }

         @Provides public Set<TypeAdapterFactory> typeAdapterFactories() {
            return ImmutableSet.<TypeAdapterFactory>of(new NestedSerializedNamesTypeAdapterFactory());
         }
      }).getInstance(Json.class);

      assertEquals(json.toJson(nested), "{\"id\":\"1234\",\"count\":1}");
      assertEquals(json.fromJson("{\"id\":\"1234\",\"count\":1}", NestedSerializedNamesType.class), nested);
   }

   private class NestedSerializedNamesTypeAdapterFactory extends TypeAdapter<NestedSerializedNamesType>
         implements TypeAdapterFactory {

      @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         if (!(NestedSerializedNamesType.class.isAssignableFrom(typeToken.getRawType()))) {
            return null;
         }
         return (TypeAdapter<T>) this;
      }

      @Override public void write(JsonWriter out, NestedSerializedNamesType value) throws IOException {
         out.beginObject();
         out.name("id").value(value.item().id());
         out.name("count").value(value.items().size());
         out.endObject();
      }

      @Override public NestedSerializedNamesType read(JsonReader in) throws IOException {
         in.beginObject();
         in.nextName();
         in.nextString();
         in.nextName();
         in.nextInt();
         in.endObject();
         return nested;
      }
   }
}
