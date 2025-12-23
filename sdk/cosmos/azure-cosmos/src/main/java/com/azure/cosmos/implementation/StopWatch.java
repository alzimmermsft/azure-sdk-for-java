// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.cosmos.implementation;

/**
 * A stop watch to track time between a starting time and either retrieval or stopping time.
 * <p>
 * This is a replacement for the previous usages of stop watches from both Apache Commons and Guava.
 */
public final class StopWatch {
    private State state = State.UNSTARTED;
    private long startMillis;
    private long stopMillis;

    public enum State {
        UNSTARTED,
        STARTED,
        STOPPED
    }

    /**
     * Creates a new {@link StopWatch} instance.
     */
    public StopWatch() {
    }

    /**
     * Starts the stop watch.
     *
     * @throws IllegalStateException If {@link #getState()} is anything but {@link State#UNSTARTED}.
     */
    public void start() {
        Utils.checkState(state == State.UNSTARTED,
            "Stop watch is in an invalid state for starting. Currently in %s state.", state);
        state = State.STARTED;
        startMillis = System.currentTimeMillis();
    }

    /**
     * Stops the stop watch.
     *
     * @throws IllegalStateException If {@link #getState()} is anything but {@link State#STARTED}.
     */
    public void stop() {
        Utils.checkState(state == State.STARTED,
            "Stop watch is in an invalid state for stopping. Currently in %s state.", state);
        state = State.STOPPED;
        stopMillis = System.currentTimeMillis();
    }

    /**
     * Resets the stop watch to {@link State#UNSTARTED}.
     * <p>
     * Unlike all other methods dealing with starting, stopping, and elapsed time, this method will never throw.
     */
    public void reset() {
        state = State.UNSTARTED;
    }

    /**
     * Gets the milliseconds elapsed since the start of the stop watch and either the current
     * {@link System#currentTimeMillis()} or when the stop watch was {@link #stop() stopped}.
     *
     * @return The elapsed milliseconds tracked by the stop watch.
     * @throws IllegalStateException If the {@link #getState()} is {@link State#UNSTARTED}.
     */
    public long getElapsedMillis() {
        Utils.checkState(state != State.UNSTARTED,
            "Stop watch has never been started, unable to capture elapsed milliseconds.");
        return (state == State.STOPPED) ? stopMillis - startMillis : System.currentTimeMillis() - startMillis;
    }

    /**
     * Gets the state of the stop watch.
     *
     * @return The current state of the stop watch.
     */
    public State getState() {
        return state;
    }
}
