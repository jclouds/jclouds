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
package org.jclouds.http.internal;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;
import static org.jclouds.http.HttpUtils.releasePayload;
import static org.jclouds.io.Payloads.newInputStreamPayload;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.rest.internal.BaseHttpApiMetadata;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

@Test(groups = "unit", testName = "BaseHttpCommandExecutorServiceTest")
public class BaseHttpCommandExecutorServiceTest {

   public void testStreamIsClosedWhenRetrying() throws IOException {
      MockInputStream in = new MockInputStream(2); // Input stream that produces 2 bytes
      HttpResponse response = HttpResponse.builder().payload(newInputStreamPayload(in)).build();
      response.getPayload().getContentMetadata().setContentLength(1l);
      HttpCommand command = mockHttpCommand();

      DelegatingRetryHandler retryHandler = EasyMock.createMock(DelegatingRetryHandler.class);
      DelegatingErrorHandler errorHandler = EasyMock.createMock(DelegatingErrorHandler.class);

      expect(retryHandler.shouldRetryRequest(command, response)).andReturn(true);
      replay(retryHandler, errorHandler);

      // Verify the stream is open. This consumes one byte.
      assertEquals(response.getPayload().openStream().available(), 2);
      assertEquals(response.getPayload().openStream().read(), 1);

      BaseHttpCommandExecutorService<?> service = mockHttpCommandExecutorService(retryHandler, errorHandler);
      assertTrue(service.shouldContinue(command, response));

      verify(retryHandler, errorHandler);

      // Verify that the response stream is closed and consumed
      assertFalse(in.isOpen);
      assertTrue(response.getPayload().openStream() == in); // The service shouldn't have changed it
      assertEquals(response.getPayload().openStream().available(), 0);
      assertEquals(response.getPayload().openStream().read(), -1);
   }

   public void testStreamIsClosedWhenNotRetrying() throws IOException {
      MockInputStream in = new MockInputStream(2); // Input stream that produces 2 bytes
      HttpResponse response = HttpResponse.builder().payload(newInputStreamPayload(in)).build();
      response.getPayload().getContentMetadata().setContentLength(1l);
      HttpCommand command = mockHttpCommand();

      DelegatingRetryHandler retryHandler = EasyMock.createMock(DelegatingRetryHandler.class);
      DelegatingErrorHandler errorHandler = EasyMock.createMock(DelegatingErrorHandler.class);

      errorHandler.handleError(command, response);
      expectLastCall();
      expect(retryHandler.shouldRetryRequest(command, response)).andReturn(false);
      replay(retryHandler, errorHandler);

      // Verify the stream is open. This consumes one byte.
      assertEquals(response.getPayload().openStream().available(), 2);
      assertEquals(response.getPayload().openStream().read(), 1);

      BaseHttpCommandExecutorService<?> service = mockHttpCommandExecutorService(retryHandler, errorHandler);
      assertFalse(service.shouldContinue(command, response));

      verify(retryHandler, errorHandler);

      // Verify that the response stream is closed
      assertFalse(in.isOpen);
      assertTrue(response.getPayload().openStream() == in);
      assertEquals(response.getPayload().openStream().available(), 0);
      assertEquals(response.getPayload().openStream().read(), -1);
   }

   public void testStreamIsClosedAndBufferedInTheErrorHandlerWhenNotRetrying() throws IOException {
      MockInputStream in = new MockInputStream(2); // Input stream that produces 2 bytes
      HttpResponse response = HttpResponse.builder().payload(newInputStreamPayload(in)).build();
      response.getPayload().getContentMetadata().setContentLength(1l);
      HttpCommand command = mockHttpCommand();

      DelegatingRetryHandler retryHandler = EasyMock.createMock(DelegatingRetryHandler.class);
      DelegatingErrorHandler errorHandler = EasyMock.createMock(DelegatingErrorHandler.class);

      errorHandler.handleError(command, response);
      expectLastCall().andAnswer(new IAnswer<Void>() {
         @Override
         public Void answer() throws Throwable {
            // This error handler will close the original stream and buffer it into memory
            HttpResponse response = (HttpResponse) getCurrentArguments()[1];
            closeClientButKeepContentStream(response);
            return null;
         }
      });

      expect(retryHandler.shouldRetryRequest(command, response)).andReturn(false);
      replay(retryHandler, errorHandler);

      // Verify the stream is open. This consumes one byte.
      assertEquals(response.getPayload().openStream().available(), 2);
      assertEquals(response.getPayload().openStream().read(), 1);

      BaseHttpCommandExecutorService<?> service = mockHttpCommandExecutorService(retryHandler, errorHandler);
      assertFalse(service.shouldContinue(command, response));

      verify(retryHandler, errorHandler);

      // Verify that the original response stream is closed and consumed
      assertFalse(in.isOpen);
      assertEquals(in.available(), 0);
      assertEquals(in.read(), -1);

      // Verify that the buffered stream is now repeatable and we can read the bytes that still have not
      // been consumed from the original stream
      assertTrue(response.getPayload().isRepeatable());
      assertEquals(response.getPayload().openStream().available(), 1);
      assertEquals(response.getPayload().openStream().read(), 0);
   }

   public void testCloseStreamCanBeCalledMoreThanOnce() throws IOException {
      MockInputStream in = new MockInputStream(2); // Input stream that produces 2 bytes
      HttpResponse response = HttpResponse.builder().payload(newInputStreamPayload(in)).build();
      response.getPayload().getContentMetadata().setContentLength(1l);
      HttpCommand command = mockHttpCommand();

      DelegatingRetryHandler retryHandler = EasyMock.createMock(DelegatingRetryHandler.class);
      DelegatingErrorHandler errorHandler = EasyMock.createMock(DelegatingErrorHandler.class);

      errorHandler.handleError(command, response);
      expectLastCall().andAnswer(new IAnswer<Void>() {
         @Override
         public Void answer() throws Throwable {
            // This error handler will close the original stream
            HttpResponse response = (HttpResponse) getCurrentArguments()[1];
            releasePayload(response);
            return null;
         }
      });

      expect(retryHandler.shouldRetryRequest(command, response)).andReturn(false);
      replay(retryHandler, errorHandler);

      // Verify the stream is open. This consumes one byte.
      assertEquals(response.getPayload().openStream().available(), 2);
      assertEquals(response.getPayload().openStream().read(), 1);

      BaseHttpCommandExecutorService<?> service = mockHttpCommandExecutorService(retryHandler, errorHandler);
      assertFalse(service.shouldContinue(command, response));

      verify(retryHandler, errorHandler);

      // Verify that the response stream is closed and consumed
      assertFalse(in.isOpen);
      assertEquals(in.closeCount, 2); // The stream has been closed twice, but the IOException should not propagated
      assertTrue(response.getPayload().openStream() == in); // The service shouldn't have changed it
      assertEquals(response.getPayload().openStream().available(), 0);
      assertEquals(response.getPayload().openStream().read(), -1);
   }

   public void testDoNotRetryPostOnException() throws IOException {
      helperRetryOnlyIdempotent("POST");
   }

   public void testRetryGetOnException() throws IOException {
      helperRetryOnlyIdempotent("GET");
   }

   private void helperRetryOnlyIdempotent(String method) throws IOException {
      final IOException error = new IOException("test exception");
      HttpRequestFilter throwingFilter = new HttpRequestFilter() {
         @Override
         public HttpRequest filter(HttpRequest request) throws HttpException {
            throw new HttpException(error);
         }
      };
      HttpCommand command = new HttpCommand(HttpRequest.builder().endpoint("http://localhost").method(method).filter(throwingFilter).build());

      IOExceptionRetryHandler ioRetryHandler = EasyMock.createMock(IOExceptionRetryHandler.class);

      if ("GET".equals(method)) {
         expect(ioRetryHandler.shouldRetryRequest(command, error)).andReturn(true);
         expect(ioRetryHandler.shouldRetryRequest(command, error)).andReturn(false);
      }
      replay(ioRetryHandler);

      BaseHttpCommandExecutorService<?> service = mockHttpCommandExecutorService(ioRetryHandler);
      try {
         service.invoke(command);
         fail("Expected to fail due to throwing filter");
      } catch (Exception e) {}

      verify(ioRetryHandler);
   }

   private HttpCommand mockHttpCommand() {
      return new HttpCommand(HttpRequest.builder().endpoint("http://localhost").method("mock").build());
   }
   
   private BaseHttpCommandExecutorService<?> mockHttpCommandExecutorService(final DelegatingRetryHandler retryHandler,
         final DelegatingErrorHandler errorHandler) {
      Injector injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), BaseHttpApiMetadata.defaultProperties());
            bind(DelegatingRetryHandler.class).toInstance(retryHandler);
            bind(DelegatingErrorHandler.class).toInstance(errorHandler);
            bind(BaseHttpCommandExecutorService.class).to(MockHttpCommandExecutorService.class);
         }
      });

      return injector.getInstance(BaseHttpCommandExecutorService.class);
   }

   private BaseHttpCommandExecutorService<?> mockHttpCommandExecutorService(final IOExceptionRetryHandler ioRetryHandler) {
      Injector injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), BaseHttpApiMetadata.defaultProperties());
            bind(IOExceptionRetryHandler.class).toInstance(ioRetryHandler);
            bind(BaseHttpCommandExecutorService.class).to(MockHttpCommandExecutorService.class);
         }
      });

      return injector.getInstance(BaseHttpCommandExecutorService.class);
   }

   private static class MockInputStream extends InputStream {
      boolean isOpen = true;
      int count;
      int closeCount = 0;

      public MockInputStream(int count) {
         this.count = count;
      }

      @Override
      public void close() throws IOException {
         this.closeCount++;
         if (!this.isOpen) {
            throw new IOException("The stream is already closed");
         }
         this.isOpen = false;
      }

      @Override
      public int read() throws IOException {
         if (this.isOpen)
            return (count > 0) ? --count : -1;
         else
            return -1;
      }

      @Override
      public int available() throws IOException {
         if (this.isOpen)
            return count;
         else
            return 0;
      }

   }

   private static class MockHttpCommandExecutorService extends BaseHttpCommandExecutorService<Object> {

      @Inject
      MockHttpCommandExecutorService(HttpUtils utils, ContentMetadataCodec contentMetadataCodec,
            DelegatingRetryHandler retryHandler, IOExceptionRetryHandler ioRetryHandler,
            DelegatingErrorHandler errorHandler, HttpWire wire) {
         super(utils, contentMetadataCodec, retryHandler, ioRetryHandler, errorHandler, wire);
      }

      @Override
      protected Object convert(HttpRequest request) throws IOException, InterruptedException {
         return null;
      }

      @Override
      protected HttpResponse invoke(Object nativeRequest) throws IOException, InterruptedException {
         return null;
      }

      @Override
      protected void cleanup(Object nativeRequest) {

      }

   }

}
