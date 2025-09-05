package com.example.parkingslot.mainpages.userauth

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.customresuables.buttons.ForwardButton
import com.example.parkingslot.customresuables.textfields.LabeledTextField
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.webConnect.dto.signup.SignUpRequest
import com.example.parkingslot.webConnect.dto.signup.SignUpResponse
import com.example.parkingslot.webConnect.repository.UserRepository
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SignUp(navController: NavController) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val userRepository = UserRepository()

    val isValidationSuccess by remember {
        derivedStateOf {
            password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword
        }
    }

    PageBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SignUp",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            LabeledTextField(value = name, onValueChange = { name = it }, label = "Name")
            Spacer(modifier = Modifier.height(24.dp))

            LabeledTextField(value = email, onValueChange = { email = it }, label = "Email")
            Spacer(modifier = Modifier.height(12.dp))

            LabeledTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            LabeledTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                isPassword = true,
                isError = confirmPassword.isNotEmpty() && confirmPassword != password
            )
            Spacer(modifier = Modifier.height(24.dp))

            ForwardButton(
                isEnabled = isValidationSuccess,
                onClick = {
                    userRepository.signUp(name, email, password) { result ->
                        result.onSuccess { response ->
                            val data = response.data
                            with(sharedPref.edit()) {
                                putBoolean("isLoggedIn", true)
                                putString("user_token", data.userToken)
                                putInt("user_id", data.userId)
                                putString("name", data.name)
                                apply()
                            }
                            navController.navigate(Routes.viewYourParkingAreas)
                        }
                        result.onFailure { error ->
                            Toast.makeText(context, error.message ?: "Signup failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(90.dp))

            Text(
                text = "Already have an account? LogIn",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { navController.navigate(Routes.login) },
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
