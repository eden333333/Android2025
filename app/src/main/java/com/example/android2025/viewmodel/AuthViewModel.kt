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
    private val repository = AuthRepository(application,userDao)

    val user: LiveData<UserEntity?> = repository.getUser()

    private val _errorRegister = MutableLiveData<String>()
    val errorRegister: LiveData<String> = _errorRegister

    private val _errorLogin = MutableLiveData<String>()
    val errorLogin: LiveData<String> = _errorLogin

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
        }

    }
}
