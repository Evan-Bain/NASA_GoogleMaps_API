package com.example.nasa_googlemaps_api_project

import android.app.Application
import com.example.nasa_googlemaps_api_project.home.HomeViewModel
import com.example.nasa_googlemaps_api_project.home.data.ImageOfDayRepository
import com.example.nasa_googlemaps_api_project.home.data.room.ImageOfDayDatabase
import com.example.nasa_googlemaps_api_project.home.network.ImageOfDayApiClient
import com.example.nasa_googlemaps_api_project.home.network.ImageOfDayApiInterface
import com.example.nasa_googlemaps_api_project.satellite_images.SatelliteViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(koinModules())
        }
    }

    /** Returns list of all Koin modules needed for app **/
    private fun koinModules(): List<Module> {
        val homeModule = module {

            //ViewModel for HomeFragment
            viewModel { HomeViewModel(get()) }

            single { ImageOfDayRepository(get(), get(), Dispatchers.Default) }
            single { ImageOfDayApiClient.createApi() }
            single { ImageOfDayDatabase.getDatabase(this@App).imageOfDayDao() }
        }

        val satelliteSharedModule = module {

            //ViewModel shared between fragments associated with ViewPagerFragment
            viewModel { SatelliteViewModel() }
        }

        return listOf(homeModule, satelliteSharedModule)
    }
}