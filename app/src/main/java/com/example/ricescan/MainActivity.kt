package com.example.ricescan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.ricescan.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomBar = findViewById<android.view.View>(R.id.bottomBar)
        setupBottomBar(bottomBar, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomBar.visibility = if (
                destination.id == R.id.homeFragment ||
                destination.id == R.id.cameraFragment
            ) {
                android.view.View.GONE
            } else {
                android.view.View.VISIBLE
            }
        }
    }

    private fun setupBottomBar(bottomBar: android.view.View, navController: NavController) {
        bottomBar.findViewById<android.view.View>(R.id.nav_home)?.setOnClickListener {
            navController.popBackStack(R.id.homeFragment, false)
        }
        bottomBar.findViewById<android.view.View>(R.id.nav_plants)?.setOnClickListener {
            navController.navigate(R.id.myPlantsFragment)
        }
        bottomBar.findViewById<android.view.View>(R.id.nav_camera)?.setOnClickListener {
            navController.navigate(R.id.cameraFragment)
        }
        bottomBar.findViewById<android.view.View>(R.id.nav_reminder)?.setOnClickListener {
            navController.navigate(R.id.comingSoonFragment)
        }
        bottomBar.findViewById<android.view.View>(R.id.nav_weather)?.setOnClickListener {
            navController.navigate(R.id.comingSoonFragment)
        }
    }
}
