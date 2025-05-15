package com.fishercreative.fishlogger.data

object Constants {
    val COMMON_FISH_SPECIES = listOf(
        "Largemouth Bass",
        "Smallmouth Bass",
        "Spotted Bass",
        "Bluegill",
        "Channel Catfish",
        "Blue Catfish",
        "Flathead Catfish",
        "Crappie",
        "Walleye",
        "Northern Pike",
        "Muskie",
        "Yellow Perch",
        "Rainbow Trout",
        "Brown Trout",
        "Brook Trout",
        "Lake Trout",
        "Carp",
        "White Bass",
        "Striped Bass",
        "Hybrid Striped Bass"
    )

    val BAIT_TYPES = listOf(
        "Plastic Worm",
        "Jig",
        "Crankbait",
        "Spinnerbait",
        "Topwater",
        "Minnows",
        "Leeches",
        "Stink Bait",
        "Cut Bait",
        "Corn",
        "Worms",
        "Swimbait",
        "Chatterbait",
        "Dropshot",
        "Texas Rig",
        "Carolina Rig",
        "Ned Rig",
        "Tube",
        "Grub"
    )

    val BAIT_COLORS = listOf(
        "Black",
        "Blue",
        "Green",
        "Red",
        "Purple",
        "White",
        "Yellow",
        "Orange",
        "Brown",
        "Pink",
        "Chartreuse",
        "Natural (Live)",
        "Salt & Pepper",
        "Watermelon"
    )

    // Permissions
    const val LOCATION_PERMISSION_REQUEST = 1001
    
    // CSV Export
    const val CSV_MIME_TYPE = "text/csv"
    const val CSV_FILE_PREFIX = "fishlogger_"
    const val DATE_FORMAT_PATTERN = "MMddyyyy"
    const val TIME_FORMAT_PATTERN = "HHmmss"
} 