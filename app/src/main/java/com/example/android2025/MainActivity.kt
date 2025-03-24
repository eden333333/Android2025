package com.example.android2025

import AuthViewModel
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI

class MainActivity :  AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var authViewModel: AuthViewModel
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var imagePickerCallback: ((Uri?) -> Unit)? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        /** Set up the toolbar **/
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        /** Initialize the NavHostFragment and NavController **/

        // first find the Fragment container and cast it to NavHostFragment type
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // then get the NavController from the NavHostFragment
        navController = navHostFragment.navController

        // Connect the toolbar with navController
        NavigationUI.setupActionBarWithNavController(this, navController)

        // listen for changes in the navigation destination.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // checking if the current destination is the Login or Register fragment.
            if (destination.id == R.id.loginFragment || destination.id == R.id.registerFragment) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }
        }

        /** Initialize  ViewModel for authentication */
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)


        /** Register the global image picker launcher */
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imagePickerCallback?.invoke(uri)
        }

    }

    /** Inflate the toolbar menu */
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
                navController.navigate(R.id.weatherSearchFragment)
                return true
            }
            R.id.nav_profile -> {
                navController.navigate(R.id.profileFragment)
                return true
            }
            R.id.nav_logout -> {
                // Trigger logout in ViewModel
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

    // Public method to allow fragments to launch the image picker
    fun launchImagePicker(callback: (Uri?) -> Unit) {
        imagePickerCallback = callback
        imagePickerLauncher.launch("image/*")
    }
}
