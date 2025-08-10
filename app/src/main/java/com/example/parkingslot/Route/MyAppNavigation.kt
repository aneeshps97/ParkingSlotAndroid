package com.example.parkingslot.Route

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.parkingslot.parkingTicket.ParkingTicket
import com.example.parkingslot.MyBookings.myBookings
import com.example.parkingslot.availableSloats.AvailableSlot
import com.example.parkingslot.releaseSlot.ReleaseSlot
import com.example.parkingslot.sharedView.BookingViewModel
import com.example.parkingslot.transferSlot.TransferSlot
import com.example.parkingslot.userauth.Login
import com.example.parkingslot.userauth.SignUp
import com.example.parkingslot.webConnect.requestresponse.BookingResponse
import com.example.parkingslot.welcome.welcomePage
import com.google.gson.Gson
import java.net.URLDecoder
import java.util.Calendar

@Composable
fun MyAppNavigation() {
    val navController = rememberNavController();
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val bookingViewModel: BookingViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = Routes.login, builder = {

            //for login
            composable(Routes.login) {
                Login(
                    navController = navController
                )
            }

            //sign up
            composable(Routes.signup) {
                SignUp(
                    navController = navController
                )
            }

            //wecome page
            composable(Routes.welcomePage) {
                welcomePage(
                    navController,
                    bookingViewModel = bookingViewModel
                )
            }

            //showing the current ticket

            composable(Routes.parkingTicket + "/{parkingTicketNumber}") {
                val parkingTicketNumber = it.arguments?.getString("parkingTicketNumber")
                ParkingTicket(parkingTicketNumber = parkingTicketNumber ?: "00000")
            }

            //showing available slots
            composable("${Routes.availableSlots}/{slotsJson}") { backStackEntry->
                val json = backStackEntry.arguments?.getString("slotsJson")
                val freeSlots = Gson().fromJson(json, Array<BookingResponse>::class.java).toList()
                AvailableSlot(
                    bookingData = freeSlots,
                    year = year,
                    month = month,
                    navController = navController
                )
            }
            // for available slot calender change button
            composable(
                route = "${Routes.availableSlots}/{year}/{month}/{slotsJson}",
                arguments = listOf(
                    navArgument("year") { type = NavType.IntType },
                    navArgument("month") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val year = backStackEntry.arguments?.getInt("year") ?: 0
                val month = backStackEntry.arguments?.getInt("month") ?: 0
                val json = backStackEntry.arguments?.getString("slotsJson")
                val decodedJson = URLDecoder.decode(json, "UTF-8")
                val slotsData =
                    Gson().fromJson(decodedJson, Array<BookingResponse>::class.java).toList()
                AvailableSlot(
                    year = year,
                    month = month,
                    navController = navController,
                    bookingData = slotsData
                )
            }


            //for current bookings
            composable("${Routes.myBookings}/{slotsJson}") { backStackEntry ->
                val json = backStackEntry.arguments?.getString("slotsJson")
                val bookedSlots = Gson().fromJson(json, Array<BookingResponse>::class.java).toList()
                myBookings(
                    bookingData = bookedSlots,
                    year = year,
                    month = month,
                    navController = navController
                )
            }

            //for calender forward and backward button
            composable(
                route = "${Routes.myBookings}/{year}/{month}/{slotsJson}",
                arguments = listOf(
                    navArgument("year") { type = NavType.IntType },
                    navArgument("month") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val year = backStackEntry.arguments?.getInt("year") ?: 0
                val month = backStackEntry.arguments?.getInt("month") ?: 0
                val json = backStackEntry.arguments?.getString("slotsJson")
                val decodedJson = URLDecoder.decode(json, "UTF-8")
                val slotsData =
                    Gson().fromJson(decodedJson, Array<BookingResponse>::class.java).toList()
                myBookings(
                    year = year,
                    month = month,
                    navController = navController,
                    bookingData = slotsData
                )

            }

            // for the popup to transfer the slot
            composable(Routes.transferSlot) {
                TransferSlot(
                    navController = navController
                )
            }

            //for releasing the slot
            composable(Routes.releaseSlot) {
                ReleaseSlot(
                    navController = navController
                )
            }

        }
    )
}