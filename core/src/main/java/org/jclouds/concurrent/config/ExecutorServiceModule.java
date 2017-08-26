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
package org.jclouds.concurrent.config;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;
import static org.jclouds.concurrent.DynamicExecutors.newScalingThreadPool;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures {@link ListeningExecutorService}.
 *
 * Note that this uses threads.
 *
 * <p>
 * This extends the underlying Future to expose a description (the task's toString) and the submission context (stack
 * trace). The submission stack trace is appended to relevant stack traces on exceptions that are returned, so the user
 * can see the logical chain of execution (in the executor, and where it was passed to the executor).
 */
@ConfiguresExecutorService
public class ExecutorServiceModule extends AbstractModule {

   private static final Method CREATE_STL;
   private static final Constructor<SimpleTimeLimiter> CONSTRUCT_STL;
   static {
      Method create = null;
      Constructor ctor = null;
      try {
         create = SimpleTimeLimiter.class.getDeclaredMethod("create", ExecutorService.class);
      } catch (NoSuchMethodException nsme) {
         try {
            ctor = SimpleTimeLimiter.class.getConstructor(ExecutorService.class);
         } catch (NoSuchMethodException nsme2) {
            throw new UnsupportedOperationException(
               "Can't find SimpleTimeLimiter creator or constructor taking ExecutorService", nsme2);
         }
      }
      CREATE_STL = create;
      CONSTRUCT_STL = ctor;
   }

   /**
    * Reflective creation of SimpleTimeLimiter to allow compatibility with Guava 23.0.
    * SimpleTimeLimiter.create(ExecutorService) was introduced in Guava 22.0 to replace
    * the SimpleTimeLimiter(ExecutorService) constructor, which was deprecated in
    * Guava 22.0 and removed in Guava 23.0. The method is public to allow test methods
    * in other packages to use it.
    * @param executorService the execution service to use when running time-limited tasks
    * @return a new instance of SimpleTimeLimiter that uses executorService
    */
   public static SimpleTimeLimiter createSimpleTimeLimiter(ExecutorService executorService) {
      try {
         if (CREATE_STL != null) {
            return (SimpleTimeLimiter) CREATE_STL.invoke(null, executorService);
         } else if (CONSTRUCT_STL != null) {
            return CONSTRUCT_STL.newInstance(executorService);
         }
         throw new UnsupportedOperationException(
            "Can't find SimpleTimeLimiter creator or constructor taking ExecutorService");
      } catch (IllegalAccessException iae) {
         throw new UnsupportedOperationException("Can't access SimpleTimeLimiter method/ctor", iae);
      } catch (InstantiationException ie) {
         throw new UnsupportedOperationException("Can't construct SimpleTimeLimiter", ie);
      } catch (InvocationTargetException ite) {
         Throwable throwable = ite.getCause();
         if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
         }
         if (throwable instanceof Error) {
            throw (Error) throwable;
         }
         throw new UnsupportedOperationException(
            "Checked exception thrown while creating SimpleTimeLimiter", throwable);
      }
   }

   static final class ShutdownExecutorOnClose implements Closeable {
      @Resource
      private Logger logger = Logger.NULL;

      private final ListeningExecutorService service;

      private ShutdownExecutorOnClose(ListeningExecutorService service) {
         this.service = service;
      }

      @Override
      public void close() throws IOException {
         List<Runnable> runnables = service.shutdownNow();
         if (!runnables.isEmpty())
            logger.warn("when shutting down executor %s, runnables outstanding: %s", service, runnables);
      }
   }

   final ListeningExecutorService userExecutorFromConstructor;

   public ExecutorServiceModule() {
      this.userExecutorFromConstructor = null;
   }

   /**
    * @deprecated {@code ioExecutor} is no longer used. This constructor will be removed in jclouds v2.
    * Use {@link #ExecutorServiceModule(ExecutorService)} instead.
    */
   @Deprecated
   public ExecutorServiceModule(@Named(PROPERTY_USER_THREADS) ExecutorService userExecutor,
         ExecutorService ioExecutor) {
      this(userExecutor);
   }

   /**
    * @deprecated {@code ioExecutor} is no longer used. This constructor will be removed in jclouds v2.
    * Use {@link #ExecutorServiceModule(ListeningExecutorService)} instead.
    */
   @Deprecated
   public ExecutorServiceModule(@Named(PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
         ListeningExecutorService ioExecutor) {
      this(userExecutor);
   }

   public ExecutorServiceModule(@Named(PROPERTY_USER_THREADS) ExecutorService userExecutor) {
      this(listeningDecorator(userExecutor));
   }

   public ExecutorServiceModule(@Named(PROPERTY_USER_THREADS) ListeningExecutorService userExecutor) {
      this.userExecutorFromConstructor = WithSubmissionTrace.wrap(userExecutor);
   }

   @Override
   protected void configure() { // NO_UCD
   }

   @Provides
   @Singleton
   final TimeLimiter timeLimiter(@Named(PROPERTY_USER_THREADS) ListeningExecutorService userExecutor) {
      return createSimpleTimeLimiter(userExecutor);
   }

   @Provides
   @Singleton
   @Named(PROPERTY_USER_THREADS)
   final ListeningExecutorService provideListeningUserExecutorService(@Named(PROPERTY_USER_THREADS) int count, Closer closer) { // NO_UCD
      if (userExecutorFromConstructor != null)
         return userExecutorFromConstructor;
      return shutdownOnClose(WithSubmissionTrace.wrap(newThreadPoolNamed("user thread %d", count)), closer);
   }

   @Provides
   @Singleton
   @Named(PROPERTY_USER_THREADS)
   final ExecutorService provideUserExecutorService(@Named(PROPERTY_USER_THREADS) ListeningExecutorService in) { // NO_UCD
      return in;
   }

   static <T extends ListeningExecutorService> T shutdownOnClose(final T service, Closer closer) {
      closer.addToClose(new ShutdownExecutorOnClose(service));
      return service;
   }

   private ListeningExecutorService newCachedThreadPoolNamed(String name) {
      return listeningDecorator(Executors.newCachedThreadPool(namedThreadFactory(name)));
   }

   private ListeningExecutorService newThreadPoolNamed(String name, int maxCount) {
      return maxCount == 0 ? newCachedThreadPoolNamed(name) : newScalingThreadPoolNamed(name, maxCount);
   }

   private ListeningExecutorService newScalingThreadPoolNamed(String name, int maxCount) {
      return listeningDecorator(newScalingThreadPool(1, maxCount, 60L * 1000, namedThreadFactory(name)));
   }

   private ThreadFactory namedThreadFactory(String name) {
      return new ThreadFactoryBuilder().setNameFormat(name).setThreadFactory(Executors.defaultThreadFactory()).build();
   }

}
