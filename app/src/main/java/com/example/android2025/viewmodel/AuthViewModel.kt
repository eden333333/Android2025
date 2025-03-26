import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android2025.data.local.AppDatabase
import com.example.android2025.data.local.UserEntity
import com.example.android2025.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val postDao = AppDatabase.getDatabase(application).postDao()
    private val repository = AuthRepository(application,userDao,postDao)


    val user: LiveData<UserEntity?> = repository.getUser()

    private val _logoutComplete = MutableLiveData<Boolean>()
    val logoutComplete: LiveData<Boolean> = _logoutComplete

    private val _errorRegister = MutableLiveData<String>()
    val errorRegister: LiveData<String> = _errorRegister

    private val _errorLogin = MutableLiveData<String>()
    val errorLogin: LiveData<String> = _errorLogin

    private val _errorUpdateUser = MutableLiveData<String>()
    val errorUpdateUser: LiveData<String> = _errorUpdateUser

    // Registration function
    fun register(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            val photoUrl = imageUri?.let { uri ->
                repository.uploadImageToCloudinary(uri)
            }
            val result = repository.register(email, password, username, firstName, lastName, photoUrl)
            result.onSuccess {
                Log.d("AuthViewModel", "Registration successful: ${it.uid}")
            }.onFailure {
                _errorRegister.value = it.message
                Log.e("AuthViewModel", "Registration failed: ${it.message}")

            }
        }
    }

    // Login function
    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            result.onSuccess {
                Log.d("AuthViewModel", "Login successful: ${it.uid}")
            }.onFailure {
                _errorLogin.value = it.message
            }
        }
    }
    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _logoutComplete.value = true

        }

    }
    fun updateUser(
        username: String,
        firstName: String,
        lastName: String,
        imageUri: Uri?,
        currentPhotoUrl: String?,
        onSuccess: (String?) -> Unit = {}
    ) {
        viewModelScope.launch {
            val photoUrl = if (imageUri != null) {
                repository.uploadImageToCloudinary(imageUri)
            } else {
                currentPhotoUrl            }
            val result = repository.updateUser(username, firstName, lastName, photoUrl)
            result.onSuccess {
                Log.d("AuthViewModel", "User updated successfully")
                onSuccess(photoUrl)
            }.onFailure {
                _errorUpdateUser.value = it.message
                Log.e("AuthViewModel", "User update failed: ${it.message}")
            }
        }
    }
}

