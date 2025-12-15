/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Portions Copyright (c) Microsoft Corporation
 */

package com.azure.cosmos.implementation.apachecommons.lang.time;


import com.azure.cosmos.implementation.apachecommons.lang.StringUtils;
import com.azure.cosmos.implementation.apachecommons.lang.Validate;

public class DurationFormatUtils {
    /**
     * <p>DurationFormatUtils instances should NOT be constructed in standard programming.</p>
     *
     * <p>This constructor is public to permit tools that require a JavaBean instance
     * to operate.</p>
     */
    public DurationFormatUtils() {
        super();
    }

    /**
     * <p>Formats the time gap as a string.</p>
     *
     * <p>The format used is ISO 8601-like: {@code HH:mm:ss.SSS}.</p>
     *
     * @param durationMillis  the duration to format
     * @return the formatted duration, not null
     * @throws IllegalArgumentException if durationMillis is negative
     */
    public static String formatDurationHMS(final long durationMillis) {
        Validate.inclusiveBetween(0, Long.MAX_VALUE, durationMillis, "durationMillis must not be negative");

        long milliseconds = durationMillis;

        long hours = milliseconds / DateUtils.MILLIS_PER_HOUR;
        milliseconds -= (hours * DateUtils.MILLIS_PER_HOUR);

        long minutes = milliseconds / DateUtils.MILLIS_PER_MINUTE;
        milliseconds -= (minutes * DateUtils.MILLIS_PER_MINUTE);

        long seconds = milliseconds / DateUtils.MILLIS_PER_SECOND;
        milliseconds -= (seconds * DateUtils.MILLIS_PER_SECOND);

        return paddedValue(hours, 2) + ":" + paddedValue(minutes, 2) + ":" + paddedValue(seconds, 2) + "."
            + paddedValue(milliseconds, 3);
    }

    /**
     * <p>Converts a {@code long} to a {@code String} with optional
     * zero padding.</p>
     *
     * @param value the value to convert
     * @param count the size to pad to (ignored if {@code padWithZeros} is false)
     * @return the string result
     */
    private static String paddedValue(final long value, final int count) {
        final String longString = Long.toString(value);
        return StringUtils.leftPad(longString, count, '0');
    }

    private static class DateUtils {
        /**
         * Number of milliseconds in a standard second.
         */
        public static final long MILLIS_PER_SECOND = 1000;
        /**
         * Number of milliseconds in a standard minute.
         */
        public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
        /**
         * Number of milliseconds in a standard hour.
         */
        public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    }
}
