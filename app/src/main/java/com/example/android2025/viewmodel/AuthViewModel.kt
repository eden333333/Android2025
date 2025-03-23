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

    private val _user = MutableLiveData<UserEntity?>()
    val user: LiveData<UserEntity?> = _user // expose user data to the UI

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

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
            val result = repository.register(email, password, username, firstName, lastName,imageUri)
            result.onSuccess {
                Log.d("AuthViewModel", "Registration successful: ${it.uid}")
                _user.value = it
            }.onFailure {
                _error.value = it.message
                Log.e("AuthViewModel", "Registration failed: ${it.message}")

            }
        }
    }

    // Login function
    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            result.onSuccess {
                _user.value = it

            }.onFailure {
                _error.value = it.message

            }
        }
    }
    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _user.value = null

        }

    }
}
