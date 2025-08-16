package com.example.parkingslot.mainpages.userauth

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.customresuables.buttons.ForwardButton
import com.example.parkingslot.customresuables.textfields.LabeledTextField
import com.example.parkingslot.webConnect.dto.login.LoginRequest
import com.example.parkingslot.webConnect.dto.login.LoginResponse
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Login(navController: NavController) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(Routes.homePage)
        }
    }

    LoginScreen(
        onLoginClick = { email, password ->
            handleLogin(email, password, navController, sharedPref, context)
        },
        onSignUpClick = {
            navController.navigate(Routes.signup)
        }
    )
}

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isValidationSuccess by remember {
        derivedStateOf {
            email.isNotEmpty() && password.isNotEmpty()
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
            Text("Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LabeledTextField(value = email, onValueChange = { email = it }, label = "Email")
            Spacer(modifier = Modifier.height(12.dp))

            LabeledTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            ForwardButton(isEnabled = isValidationSuccess) {
                onLoginClick(email, password)
            }

            Spacer(modifier = Modifier.height(90.dp))

            Text(
                text = "Need an account? Sign Up",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onSignUpClick() },
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


private fun handleLogin(
    email: String,
    password: String,
    navController: NavController,
    sharedPref: SharedPreferences,
    context: Context
) {
    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
    val loginRequest = LoginRequest(email = email, password = password)

    api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
            val responseBody = response.body()
            val data = responseBody?.data
            if (responseBody?.status == 0 && data != null) {
                with(sharedPref.edit()) {
                    putBoolean("isLoggedIn", true)
                    putString("user_token", data.userToken)
                    putInt("user_id", data.id ?: 0)
                    putString("name", data.name)
                    apply()
                }
                navController.navigate(Routes.homePage)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody?.let { JSONObject(it).getString("message") }
                Toast.makeText(context, errorMessage ?: "Login failed", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}
