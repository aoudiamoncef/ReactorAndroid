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
package com.aoudiamoncef.reactor.android.testutil;


import java.util.concurrent.TimeUnit;

import reactor.core.Disposable;
import reactor.core.scheduler.Scheduler;

public final class EmptyScheduler implements Scheduler {
    @Override
    public Disposable schedule(final Runnable task) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Disposable schedule(final Runnable task, final long delay, final TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Disposable schedulePeriodically(final Runnable task, final long initialDelay, final long period, final TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long now(final TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Worker createWorker() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDisposed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException();
    }
}
