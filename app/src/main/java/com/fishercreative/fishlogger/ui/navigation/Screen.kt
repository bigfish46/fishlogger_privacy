package com.fishercreative.fishlogger.ui.navigation

sealed class Screen(val route: String) {
    object NewCatch : Screen("new_catch")
    object LoggedCatches : Screen("logged_catches")
    object EditCatch : Screen("edit_catch/{catchId}") {
        fun createRoute(catchId: String) = "edit_catch/$catchId"
    }
} 