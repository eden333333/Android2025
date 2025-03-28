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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.findNavController
import com.example.android2025.viewmodel.PostViewModel

class MainActivity :  AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var authViewModel: AuthViewModel
    private lateinit var postViewModel: PostViewModel
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var imagePickerCallback: ((Uri?) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
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

        // Toolbar visibility
        navController.addOnDestinationChangedListener { _, destination, _ ->

            // Hide the toolbar in the login and register fragments
            if (destination.id == R.id.loginFragment || destination.id == R.id.registerFragment) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }

            // Disable back button in top-level destinations
            val topLevelDestinations = setOf(
                R.id.homeFragment,
                R.id.weatherSearchFragment,
                R.id.profileFragment,
                R.id.userPostsFragment
            )
            val isTopLevel = destination.id in topLevelDestinations
            supportActionBar?.setDisplayHomeAsUpEnabled(!isTopLevel)
        }


        /** Register the global image picker launcher */
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imagePickerCallback?.invoke(uri)
        }

        /** Initialize ViewModels **/
        // Initialize ViewModel for posts
        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]
        // Initialize ViewModel for authentication
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Observe the user LiveData
        authViewModel.user.observe(this) { user ->
            if (user != null &&
                (( navController.currentDestination?.id == R.id.loginFragment ) || ( navController.currentDestination?.id == R.id.registerFragment ))) {
                navController.navigate(R.id.homeFragment)
                postViewModel.refreshPosts()
            }
        }

        authViewModel.logoutSuccess.observe(this) { success ->
            if (success == true) {
                navController.navigate(R.id.loginFragment)
                authViewModel.resetLogoutSuccess()
            }
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
            R.id.nav_user_posts -> {
                navController.navigate(R.id.userPostsFragment)
                return true
            }
            R.id.nav_logout -> {
                authViewModel.logout()
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
