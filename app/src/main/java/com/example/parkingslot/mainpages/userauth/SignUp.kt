package com.example.parkingslot.mainpages.userauth

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.customresuables.buttons.ForwardButton
import com.example.parkingslot.customresuables.textfields.LabeledTextField
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.webConnect.dto.login.LoginResponse
import com.example.parkingslot.webConnect.dto.signup.SignUpRequest
import com.example.parkingslot.webConnect.dto.signup.SignUpResponse
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
            Text("SignUp", style = MaterialTheme.typography.headlineMedium)
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

            ForwardButton(isEnabled = isValidationSuccess,onClick={ handleSignUp(context, sharedPref, navController, name, email, password)})
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

fun handleSignUp(
    context: Context,
    sharedPref: SharedPreferences,
    navController: NavController,
    name: String,
    email: String,
    password: String
) {
    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
    val signUpRequest = SignUpRequest(name = name, email = email, password = password)

    api.signUp(signUpRequest).enqueue(object : Callback<SignUpResponse> {
        override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
            val responseBody = response.body()
            val data = responseBody?.data
            if (responseBody?.status == 0 && data != null) {
                with(sharedPref.edit()) {
                    putBoolean("isLoggedIn", true)
                    putString("user_token", data.userToken)
                    putInt("user_id", data.userId ?: 0)
                    putString("name", data.name)
                    apply()
                }
                navController.navigate(Routes.homePage)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody?.let { JSONObject(it).getString("message") }
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}

