package com.example.nasa_googlemaps_api_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.nasa_googlemaps_api_project.satellite_images.SatelliteViewModel
import com.example.nasa_googlemaps_api_project.satellite_images.fragments.SatelliteImagesFragmentDirections
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    val viewModel by viewModel<SatelliteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupAppBar()

        satelliteNavigation()
    }

    /** All logic to setup appbar for the whole app **/
    private fun setupAppBar() {

        setSupportActionBar(findViewById(R.id.top_app_bar))

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        //Connect NavController with up navigation button in toolbar
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    /** Handles navigation on Earth Satellite Images side of app via sharedViewModel **/
    private fun satelliteNavigation() {

        //Observe only true and false statements
        viewModel.googleMapsButton.observe(this) {
            it?.let {

                when(it) {
                    true -> navController.navigate(
                            SatelliteImagesFragmentDirections.actionSatelliteImagesFragmentToMapsFragment())

                    false -> navController.navigateUp()
                }

                //sets googleMapsButton to null to allow orientation changes
                viewModel.resetButtons()
            }
        }
    }
}