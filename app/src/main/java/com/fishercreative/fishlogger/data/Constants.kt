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
        "Rainbow Trout",
        "Brown Trout",
        "Brook Trout",
        "Carp"
        // Add more as needed
    )

    val BAIT_TYPES = listOf(
        "Spinner Bait",
        "Minnows (Live)",
        "Plastic Grub",
        "Plastic Worm",
        "Jig",
        "Crankbait",
        "Topwater",
        "Spoon",
        "Swimbait",
        "Chatterbait",
        "Nightcrawlers",
        "Crawfish (Live)",
        "Shad",
        "Buzzbait",
        "Frog"
        // Add more as needed
    )

    val BAIT_COLORS = listOf(
        "Natural",  // For live bait
        "Black",
        "Blue",
        "Green Pumpkin",
        "Watermelon",
        "White",
        "Chartreuse",
        "Purple",
        "Red",
        "Silver",
        "Gold",
        "Yellow",
        "Brown",
        "Orange",
        "Pink"
        // Add more as needed
    )

    // Permissions
    const val LOCATION_PERMISSION_REQUEST = 1001
    
    // CSV Export
    const val CSV_MIME_TYPE = "text/csv"
    const val CSV_FILE_PREFIX = "fishlogger_"
    const val DATE_FORMAT_PATTERN = "MMddyyyy"
    const val TIME_FORMAT_PATTERN = "HHmmss"
} 