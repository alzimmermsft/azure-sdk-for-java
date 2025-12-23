// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.changeFeedMetrics

import com.azure.cosmos.spark.NormalizedRange

import scala.collection.mutable

/**
 * A custom type that maintains tracking change feed range to partition indexes.
 * <p>
 * Previously, the locations where this type is used leveraged Guava's BiMap, a much more complicated type that tracks
 * a [[Map]] and its inverse with many restrictions on how uniqueness is managed for keys and values.
 * Where both keys and values needed to be unique.
 * <p>
 * This implementation differs as the value portion was always a monotonically incremented
 * [[java.util.concurrent.atomic.AtomicLong]], which  would never repeat values.
 */
class ChangeFeedPartitionIndexMap {
    private val feedToIndex = mutable.HashMap[NormalizedRange, Long]()
    def getOrUpdateFeedRangeIndex(feedRange: NormalizedRange, op: => Long): Long = {
        this.synchronized {
            feedToIndex.getOrElseUpdate(feedRange, op)
        }
    }

    def getFeedRangeByIndex(index: Long): NormalizedRange = {
        this.synchronized {
            feedToIndex.find(p => p._2 == index).get._1
        }
    }
}
