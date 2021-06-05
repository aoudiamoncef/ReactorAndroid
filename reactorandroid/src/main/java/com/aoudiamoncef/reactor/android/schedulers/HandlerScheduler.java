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
package com.aoudiamoncef.reactor.android.schedulers;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.Exceptions;
import reactor.core.publisher.Hooks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

final class HandlerScheduler implements Scheduler {
    private final Handler handler;
    private final boolean async;

    HandlerScheduler(Handler handler, boolean async) {
        this.handler = handler;
        this.async = async;
    }

    @Override
    public Disposable schedule(final Runnable task) {
        return null;
    }

    @Override
    @SuppressLint("NewApi") // Async will only be true when the API is available to call.
    public Disposable schedule(Runnable run, long delay, TimeUnit unit) {
        Objects.requireNonNull(run, "run == null");
        Objects.requireNonNull(unit, "unit == null");

        run = Schedulers.onSchedule(run);
        ScheduledRunnable scheduled = new ScheduledRunnable(handler, run);
        Message message = Message.obtain(handler, scheduled);
        if (async) {
            message.setAsynchronous(true);
        }
        handler.sendMessageDelayed(message, unit.toMillis(delay));
        return scheduled;
    }

    @Override
    public Disposable schedulePeriodically(final Runnable task, final long initialDelay, final long period, final TimeUnit unit) {
        return null;
    }

    @Override
    public long now(final TimeUnit unit) {
        return 0;
    }

    @Override
    public Worker createWorker() {
        return new HandlerWorker(handler, async);
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    @Override
    public void start() {

    }

    private static final class HandlerWorker implements Worker {
        private final Handler handler;
        private final boolean async;

        private volatile boolean disposed;

        HandlerWorker(Handler handler, boolean async) {
            this.handler = handler;
            this.async = async;
        }

        @Override
        public Disposable schedule(final Runnable task) {
            return null;
        }

        @Override
        @SuppressLint("NewApi") // Async will only be true when the API is available to call.
        public Disposable schedule(Runnable run, long delay, TimeUnit unit) {
            if (run == null) throw new NullPointerException("run == null");
            if (unit == null) throw new NullPointerException("unit == null");

            if (disposed) {
                return Disposables.disposed();
            }

            run = Schedulers.onSchedule(run);

            ScheduledRunnable scheduled = new ScheduledRunnable(handler, run);

            Message message = Message.obtain(handler, scheduled);
            message.obj = this; // Used as token for batch disposal of this worker's runnables.

            if (async) {
                message.setAsynchronous(true);
            }

            handler.sendMessageDelayed(message, unit.toMillis(delay));

            // Re-check disposed state for removing in case we were racing a call to dispose().
            if (disposed) {
                handler.removeCallbacks(scheduled);
                return Disposables.disposed();
            }

            return scheduled;
        }

        @Override
        public Disposable schedulePeriodically(final Runnable task, final long initialDelay, final long period, final TimeUnit unit) {
            return null;
        }

        @Override
        public void dispose() {
            disposed = true;
            handler.removeCallbacksAndMessages(this /* token */);
        }

        @Override
        public boolean isDisposed() {
            return disposed;
        }
    }

    private static final class ScheduledRunnable implements Runnable, Disposable {
        private final Handler handler;
        private final Runnable delegate;

        private volatile boolean disposed; // Tracked solely for isDisposed().

        ScheduledRunnable(Handler handler, Runnable delegate) {
            this.handler = handler;
            this.delegate = delegate;
        }

        @Override
        public void run() {
            try {
                delegate.run();
            } catch (Throwable t) {
                Exceptions.throwIfFatal(t);
                Hooks.onNextError((throwable, o) -> throwable);
//                RxJavaPlugins.onError(t);
            }
        }

        @Override
        public void dispose() {
            handler.removeCallbacks(this);
            disposed = true;
        }

        @Override
        public boolean isDisposed() {
            return disposed;
        }
    }
}
