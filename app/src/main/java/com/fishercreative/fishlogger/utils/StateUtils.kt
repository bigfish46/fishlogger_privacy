package com.fishercreative.fishlogger.utils

object StateUtils {
    private val stateMap = mapOf(
        "AL" to "Alabama",
        "AK" to "Alaska",
        "AZ" to "Arizona",
        "AR" to "Arkansas",
        "CA" to "California",
        "CO" to "Colorado",
        "CT" to "Connecticut",
        "DE" to "Delaware",
        "FL" to "Florida",
        "GA" to "Georgia",
        "HI" to "Hawaii",
        "ID" to "Idaho",
        "IL" to "Illinois",
        "IN" to "Indiana",
        "IA" to "Iowa",
        "KS" to "Kansas",
        "KY" to "Kentucky",
        "LA" to "Louisiana",
        "ME" to "Maine",
        "MD" to "Maryland",
        "MA" to "Massachusetts",
        "MI" to "Michigan",
        "MN" to "Minnesota",
        "MS" to "Mississippi",
        "MO" to "Missouri",
        "MT" to "Montana",
        "NE" to "Nebraska",
        "NV" to "Nevada",
        "NH" to "New Hampshire",
        "NJ" to "New Jersey",
        "NM" to "New Mexico",
        "NY" to "New York",
        "NC" to "North Carolina",
        "ND" to "North Dakota",
        "OH" to "Ohio",
        "OK" to "Oklahoma",
        "OR" to "Oregon",
        "PA" to "Pennsylvania",
        "RI" to "Rhode Island",
        "SC" to "South Carolina",
        "SD" to "South Dakota",
        "TN" to "Tennessee",
        "TX" to "Texas",
        "UT" to "Utah",
        "VT" to "Vermont",
        "VA" to "Virginia",
        "WA" to "Washington",
        "WV" to "West Virginia",
        "WI" to "Wisconsin",
        "WY" to "Wyoming",
        "DC" to "District of Columbia"
    )

    private val reverseStateMap = stateMap.entries.associate { (abbr, name) -> name.uppercase() to abbr }

    fun getFullStateName(abbreviation: String): String {
        return stateMap[abbreviation.uppercase()] ?: abbreviation
    }

    fun getStateAbbreviation(fullName: String): String? {
        return if (fullName.length == 2) {
            if (stateMap.containsKey(fullName.uppercase())) fullName.uppercase() else null
        } else {
            reverseStateMap[fullName.uppercase()]
        }
    }

    fun isValidStateInput(input: String): Boolean {
        return if (input.length == 2) {
            stateMap.containsKey(input.uppercase())
        } else {
            reverseStateMap.containsKey(input.uppercase())
        }
    }

    fun formatCityState(city: String, state: String): String {
        val formattedState = getStateAbbreviation(state) ?: state
        return if (city.isNotBlank() && state.isNotBlank()) {
            "$city, $formattedState"
        } else if (city.isNotBlank()) {
            city
        } else {
            formattedState
        }
    }
} 