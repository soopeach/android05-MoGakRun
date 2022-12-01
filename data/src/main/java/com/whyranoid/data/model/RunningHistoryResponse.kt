package com.whyranoid.data.model

import com.whyranoid.domain.model.RunningHistory

data class RunningHistoryResponse(
    val historyId: String = "",
    val startedAt: Long = 0L,
    val finishedAt: Long = 0L,
    val totalRunningTime: Int = 0,
    val pace: Double = 0.0,
    val totalDistance: Double = 0.0
)

fun RunningHistory.toRunningHistoryResponse() =
    RunningHistoryResponse(
        historyId = historyId,
        startedAt = startedAt,
        finishedAt = finishedAt,
        totalRunningTime = totalRunningTime,
        pace = pace,
        totalDistance = totalDistance
    )
