// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.cosmos.implementation.query.metrics;

import com.azure.cosmos.implementation.StopWatch;

public class SchedulingStopwatch {
    private final StopWatch turnaroundTimeStopwatch;
    private final StopWatch responseTimeStopwatch;
    private final StopWatch runTimeStopwatch;
    private long numPreemptions;
    private boolean responded;

    public SchedulingStopwatch() {
        this.turnaroundTimeStopwatch = new StopWatch();
        this.responseTimeStopwatch = new StopWatch();
        this.runTimeStopwatch = new StopWatch();
    }

    public SchedulingTimeSpan getElapsedTime() {
        return new SchedulingTimeSpan(this.turnaroundTimeStopwatch.getElapsedMillis(),
            this.responseTimeStopwatch.getElapsedMillis(), this.runTimeStopwatch.getElapsedMillis(),
            this.turnaroundTimeStopwatch.getElapsedMillis() - this.runTimeStopwatch.getElapsedMillis(),
            this.numPreemptions);
    }

    /**
     * Tells the SchedulingStopwatch know that the process is in a state where it is ready to be worked on,
     * which in turn starts the stopwatch for response time and turnaround time.
     */
    public void ready() {
        startStopWatch(this.turnaroundTimeStopwatch);
        startStopWatch(this.responseTimeStopwatch);
    }

    public void start() {
        synchronized (this.runTimeStopwatch) {
            if (this.runTimeStopwatch.getState() == StopWatch.State.STARTED) {
                return;
            }
            if (!this.responded) {
                // This is the first time the process got a response, so the response time stopwatch needs to stop.
                stopStopWatch(this.responseTimeStopwatch);
                this.responded = true;
            }
            this.runTimeStopwatch.reset();
            this.runTimeStopwatch.start();
        }
    }

    public void stop() {
        synchronized (this.runTimeStopwatch) {
            if (this.runTimeStopwatch.getState() != StopWatch.State.STARTED) {
                return;
            }
            this.runTimeStopwatch.stop();
            this.numPreemptions++;
        }
    }

    public void terminate() {
        stopStopWatch(this.turnaroundTimeStopwatch);
        stopStopWatch(this.responseTimeStopwatch);
    }

    private void startStopWatch(StopWatch stopwatch) {
        synchronized (stopwatch) {
            if (stopwatch.getState() == StopWatch.State.STARTED) {
                return; // idempotent start
            }
            stopwatch.start();
        }
    }

    private void stopStopWatch(StopWatch stopwatch) {
        synchronized (stopwatch) {
            if (stopwatch.getState() != StopWatch.State.STARTED) {
                return; // idempotent stop
            }
            stopwatch.stop();
        }
    }
}
