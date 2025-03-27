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

    //observers
    val user: LiveData<UserEntity?> = repository.getUser()

    private val _logoutSuccess = MutableLiveData<Boolean?>()
    val logoutSuccess: LiveData<Boolean?> get() = _logoutSuccess

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

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
        _loading.value = true
        viewModelScope.launch {
            val photoUrl = imageUri?.let { uri ->
                repository.uploadImageToCloudinary(uri)
            }
            val result = repository.register(email, password, username, firstName, lastName, photoUrl)
            result.onSuccess {
                _loading.value = false
                Log.d("AuthViewModel", "Registration successful: ${it.uid}")
            }.onFailure {
                _loading.value = false
                _errorRegister.value = it.message
                Log.e("AuthViewModel", "Registration failed: ${it.message}")

            }
        }
    }

    // Login function
    fun login(email: String, password: String) {
        _loading.value = true
        viewModelScope.launch {
            val result = repository.login(email, password)
            result.onSuccess {
                _loading.value = false
                Log.d("AuthViewModel", "Login successful: ${it.uid}")
            }.onFailure {
                _loading.value = false
                _errorLogin.value = it.message
            }
        }
    }
    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _logoutSuccess.postValue(true)
        }
    }

    fun resetLogoutSuccess() {
        _logoutSuccess.value = null
    }

    fun updateUser(
        username: String,
        firstName: String,
        lastName: String,
        imageUri: Uri?,
        currentPhotoUrl: String?,
        onSuccess: (String?) -> Unit = {}
    ) {
        _loading.value = true
        viewModelScope.launch {
            val photoUrl = if (imageUri != null) {
                repository.uploadImageToCloudinary(imageUri)
            } else {
                currentPhotoUrl            }
            val result = repository.updateUser(username, firstName, lastName, photoUrl)
            result.onSuccess {
                Log.d("AuthViewModel", "User updated successfully")
                _loading.value = false
                onSuccess(photoUrl)
            }.onFailure {
                _errorUpdateUser.value = it.message
                _loading.value = false
                Log.e("AuthViewModel", "User update failed: ${it.message}")
            }
        }
    }
}

