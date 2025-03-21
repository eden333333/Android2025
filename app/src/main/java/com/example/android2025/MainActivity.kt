package com.example.android2025

import AuthViewModel
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.android2025.ui.theme.Android2025Theme

class MainActivity :  AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Set up the toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize the NavHostFragment and NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Initialize  ViewModel for authentication
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

    }
    // Inflate the toolbar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_nav_menu, menu)
        return true
    }

    // Handle menu item clicks using the NavController
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                navController.navigate(R.id.homeFragment)
                return true
            }
            R.id.nav_weather -> {
                navController.navigate(R.id.weatherFragment)
                return true
            }
            R.id.nav_profile -> {
                navController.navigate(R.id.profileFragment)
                return true
            }
            R.id.nav_logout -> {
                // Trigger logout in your ViewModel
                authViewModel.logout()
                // Navigate back to LoginFragment after logout
                navController.navigate(R.id.loginFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Handle Up navigation
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
