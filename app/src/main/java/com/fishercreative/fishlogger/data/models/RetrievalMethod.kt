package com.fishercreative.fishlogger.data.models

enum class RetrievalMethod {
    CASTING,
    JIGGING,
    DRIFTING,
    TROLLING,
    SLIP_BOBBER,
    BOTTOM_BOUNCING,
    POWER_FISHING,
    DRIFT_FISHING,
    BACK_TROLLING,
    SPOT_LOCK_JIGGING,
    CASTING_CRANKBAITS,
    FLY_FISHING,
    SHORE_FISHING,
    ICE_FISHING,
    OTHER;

    override fun toString(): String {
        return name.replace("_", " ").split(" ")
            .joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } }
    }
} 