/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aoudiamoncef.reactor.android.plugins;

import com.aoudiamoncef.reactor.android.testutil.EmptyScheduler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public final class ReactorAndroidPluginsTest {
    @Before @After
    public void setUpAndTearDown() {
        ReactorAndroidPlugins.reset();
    }

    @Test
    public void mainThreadHandlerCalled() {
        final AtomicReference<Scheduler> schedulerRef = new AtomicReference<>();
        final Scheduler newScheduler = new EmptyScheduler();
        ReactorAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> {
            schedulerRef.set(scheduler);
            return newScheduler;
        });

        Scheduler scheduler = new EmptyScheduler();
        Scheduler actual = ReactorAndroidPlugins.onMainThreadScheduler(scheduler);
        assertSame(newScheduler, actual);
        assertSame(scheduler, schedulerRef.get());
    }

    @Test
    public void resetClearsMainThreadHandler() {
        ReactorAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> {
            throw new AssertionError();
        });
        ReactorAndroidPlugins.reset();

        Scheduler scheduler = new EmptyScheduler();
        Scheduler actual = ReactorAndroidPlugins.onMainThreadScheduler(scheduler);
        assertSame(scheduler, actual);
    }

    @Test
    public void initMainThreadHandlerCalled() {
        final AtomicReference<Callable<Scheduler>> schedulerRef = new AtomicReference<>();
        final Scheduler newScheduler = new EmptyScheduler();
        ReactorAndroidPlugins
                .setInitMainThreadSchedulerHandler(scheduler -> {
                    schedulerRef.set(scheduler);
                    return newScheduler;
                });

        Callable<Scheduler> scheduler = () -> {
            throw new AssertionError();
        };
        Scheduler actual = ReactorAndroidPlugins.initMainThreadScheduler(scheduler);
        assertSame(newScheduler, actual);
        assertSame(scheduler, schedulerRef.get());
    }

    @Test
    public void resetClearsInitMainThreadHandler() throws Exception {
        ReactorAndroidPlugins
                .setInitMainThreadSchedulerHandler(scheduler -> {
                    throw new AssertionError();
                });

        final Scheduler scheduler = new EmptyScheduler();
        Callable<Scheduler> schedulerCallable = () -> scheduler;

        ReactorAndroidPlugins.reset();

        Scheduler actual = ReactorAndroidPlugins.initMainThreadScheduler(schedulerCallable);
        assertSame(schedulerCallable.call(), actual);
    }

    @Test
    public void defaultMainThreadSchedulerIsInitializedLazily() {
        Function<Callable<Scheduler>, Scheduler> safeOverride = scheduler -> new EmptyScheduler();
        Callable<Scheduler> unsafeDefault = () -> {
            throw new AssertionError();
        };

       ReactorAndroidPlugins.setInitMainThreadSchedulerHandler(safeOverride);
       ReactorAndroidPlugins.initMainThreadScheduler(unsafeDefault);
    }

    @Test
    public void overrideInitMainSchedulerThrowsWhenSchedulerCallableIsNull() {
        try {
            ReactorAndroidPlugins.initMainThreadScheduler(null);
            fail();
        } catch (NullPointerException e) {
            assertEquals("scheduler == null", e.getMessage());
        }
    }

    @Test
    public void overrideInitMainSchedulerThrowsWhenSchedulerCallableReturnsNull() {
        Callable<Scheduler> nullResultCallable = () -> null;

        try {
            ReactorAndroidPlugins.initMainThreadScheduler(nullResultCallable);
            fail();
        } catch (NullPointerException e) {
            assertEquals("Scheduler Callable returned null", e.getMessage());
        }
    }

    @Test
    public void getInitMainThreadSchedulerHandlerReturnsHandler() {
        Function<Callable<Scheduler>, Scheduler> handler = schedulerCallable -> Schedulers.fromExecutor(Executors.newFixedThreadPool(2), true);
        ReactorAndroidPlugins.setInitMainThreadSchedulerHandler(handler);
        assertSame(handler, ReactorAndroidPlugins.getInitMainThreadSchedulerHandler());
    }

    @Test
    public void getMainThreadSchedulerHandlerReturnsHandler() {
        Function<Scheduler, Scheduler> handler = scheduler -> Schedulers.fromExecutor(Executors.newFixedThreadPool(2), true);
        ReactorAndroidPlugins.setMainThreadSchedulerHandler(handler);
        assertSame(handler, ReactorAndroidPlugins.getOnMainThreadSchedulerHandler());
    }

    @Test
    public void getInitMainThreadSchedulerHandlerReturnsNullIfNotSet() {
        ReactorAndroidPlugins.reset();
        assertNull(ReactorAndroidPlugins.getInitMainThreadSchedulerHandler());
    }

    @Test
    public void getMainThreadSchedulerHandlerReturnsNullIfNotSet() {
        ReactorAndroidPlugins.reset();
        assertNull(ReactorAndroidPlugins.getOnMainThreadSchedulerHandler());
    }
}
