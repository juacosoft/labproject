package com.example.labproject.ui

import androidx.navigation.NavController

object NavControllerProvider {
    var current: NavController? = null
        private set

    fun setNavController(navController: NavController) {
        this.current = navController
    }
}