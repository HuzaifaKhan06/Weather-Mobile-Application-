package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

//7c7f8c984a503c452da2f39e54fc9d34
class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("islamabad")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        // Parameters


        val response = retrofit.getWeatherData(cityName, "de90592425feeb76292eb436301a0939", "metric")
        Log.d("TAG", "Request URL: ${response.request().url()}")
        response.enqueue(object : Callback<weatherApp>{
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
                Log.d("TAG", "onResponse called")
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity =responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunset= responseBody.sys.sunset.toLong()
                    val sealevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxTemp= responseBody.main.temp_max
                    val minTemp= responseBody.main.temp_min

                 binding.temp.text="$temperature ℃"
                    binding.weather.text = condition
                    binding.maxtemp.text = "Max Temp: $maxTemp ℃"
                    binding.mintemp.text = "Min Temp: $minTemp ℃"
                    binding.humidity.text = "$humidity %"
                    binding.wind.text = "$windSpeed M/s"
                    binding.sunrise.text= "${time(sunRise)}"
                    binding.sunset.text= "${time(sunset)}"
                    binding.sea.text = "$sealevel hPa"
                    binding.condition.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                        binding.date.text= date()
                        binding.cityname.text="$cityName"
                // Log.d("TAG", "onResponse: $temperature")
                    changeImagesAccordingToWeatherCondition(condition)
                }
            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {

            }

        })

    }

    private fun changeImagesAccordingToWeatherCondition(conditions:String) {
        //Log.d("WeatherCondition", "Received condition: $conditions")
        when(conditions){

            "Sunny"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background_one)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Clear Sky"->{
                binding.root.setBackgroundResource(R.drawable.clear_sky_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Haze","Overcast","Mist","Fog","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.fog_foggy_backfround)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Clouds"->{
                binding.root.setBackgroundResource(R.drawable.clouds_background_one)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Partly Clouds"->{
                binding.root.setBackgroundResource(R.drawable.clouds_background_two)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drizzle"->{
                binding.root.setBackgroundResource(R.drawable.light_rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Overcast","Moderate Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.heavy_rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            "Moderate Snow"->{
                binding.root.setBackgroundResource(R.drawable.snow_background_one)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            "Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.heavy_snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            "Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.blizard_background_image)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }

        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf =SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp: Long): String {
        val sdf =SimpleDateFormat("HH mm", Locale.getDefault())
        return sdf.format((Date(timestamp*100)))
    }

    fun dayName(timestamp:Long): String{
        val sdf =SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}