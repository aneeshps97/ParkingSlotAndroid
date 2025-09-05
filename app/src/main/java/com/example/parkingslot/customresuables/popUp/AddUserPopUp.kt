package com.example.parkingslot.customresuables.popUp
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.customresuables.confirm.ConfirmPopUp
import com.example.parkingslot.customresuables.textfields.InputTextFieldWithButton
import com.example.parkingslot.mainpages.ParkingArea.handleFindingUserByEmail
import com.example.parkingslot.webConnect.dto.slot.Slot
import com.example.parkingslot.webConnect.dto.slot.SlotData
import com.example.parkingslot.webConnect.dto.user.User
import com.example.parkingslot.webConnect.dto.user.UserData
import com.example.parkingslot.webConnect.dto.user.UserResponse
import com.example.parkingslot.webConnect.repository.UserRepository
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AddUserPopUp(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    navController: NavController,
    onUserAdded: (User) -> Unit // Callback when a user is selected
) {
    val context:Context = LocalContext.current
    val showConfirmationDialog = remember { mutableStateOf(false) }
    val user = remember { mutableStateOf<User?>(null) }
    var userRepository = UserRepository()
    ConfirmPopUp(
        text1 = user.value?.name ?: "",
        text2 = "sure you want to add this user?",
        showDialog = showConfirmationDialog.value,
        onDismiss = { showConfirmationDialog.value = false },
        onConfirm = {
            user.value?.let { safeUser ->
                Toast.makeText(context, "Confirm clicked: ${safeUser.name}", Toast.LENGTH_SHORT).show()
                onUserAdded(safeUser)
            }
            showConfirmationDialog.value = false
        }

    )

    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                var email by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp), // Limit height for scroll
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Title
                    Text("Add User", style = MaterialTheme.typography.titleMedium)

                    // Text box with initial value
                    InputTextFieldWithButton(email, onValueChange = { email = it }, {
                        handleFindingUserByEmailForEditingUser(context,email,showConfirmationDialog,user,userRepository)
                    },"Email")
                    // Cancel button
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel", color = Color.Black)
                    }
                }
            }
        }
    }
}

fun handleFindingUserByEmailForEditingUser(
    context: Context,
    email: String,
    showConfirmationDialog: MutableState<Boolean>,
    userState: MutableState<User?>,
    repository: UserRepository
) {
    repository.findUserByEmail(email) { result ->
        result.onSuccess { response ->
            val data = response.data
            val user = User(
                id = data?.userId,
                name = data?.name.orEmpty()
            )
            userState.value = user
            showConfirmationDialog.value = true
        }

        result.onFailure { error ->
            Toast.makeText(
                context,
                error.message ?: "Failed to fetch user",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
