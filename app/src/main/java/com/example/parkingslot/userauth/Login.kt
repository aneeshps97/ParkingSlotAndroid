package com.example.parkingslot.userauth

import android.content.Context
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
import com.example.parkingslot.background.PageBackground
import com.example.parkingslot.buttons.ForwardButton
import com.example.parkingslot.webConnect.requestresponse.LoginRequest
import com.example.parkingslot.webConnect.requestresponse.LoginResponse
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Login(navController: NavController) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val isLoggedIn =sharedPref.getBoolean("isLoggedIn", false)

    // Navigate immediately if already logged in
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            /*navController.navigate(Routes.welcomePage)*/
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

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            ForwardButton {
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
    sharedPref: android.content.SharedPreferences,
    context: Context
) {
    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
    val loginRequest = LoginRequest(email = email, password = password)
    api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
            if (response.body()!=null) {
                // Save login info
                with(sharedPref.edit()) {
                    putBoolean("isLoggedIn", true)
                    putString("user_token", response.body()?.userToken)
                    putInt("user_id", response.body()?.id ?: 0)
                    putString("name",response.body()?.name)
                    apply()
                }
                /*navController.navigate(Routes.welcomePage)*/
                navController.navigate(Routes.homePage)
            } else {
                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}
