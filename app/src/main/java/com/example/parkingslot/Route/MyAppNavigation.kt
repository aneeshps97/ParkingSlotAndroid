package com.example.parkingslot.Route

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.parkingslot.mainpages.parkingTicket.ParkingTicket
import com.example.parkingslot.mainpages.MyBookings.myBookings
import com.example.parkingslot.mainpages.ParkingArea.AddSlotsToParkingArea
import com.example.parkingslot.mainpages.ParkingArea.AddUsersToParkingArea
import com.example.parkingslot.mainpages.ParkingArea.AssignSlotForUsers
import com.example.parkingslot.mainpages.ParkingArea.CreateParkingArea
import com.example.parkingslot.mainpages.ParkingArea.ViewYourParkingAreas
import com.example.parkingslot.mainpages.availableslot.AvailableSlot
import com.example.parkingslot.mainpages.home.HomePage
import com.example.parkingslot.mainpages.releaseSlot.ReleaseSlot
import com.example.parkingslot.sharedView.BookingViewModel
import com.example.parkingslot.mainpages.transferSlot.TransferSlot
import com.example.parkingslot.mainpages.userauth.Login
import com.example.parkingslot.mainpages.userauth.SignUp
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
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

            composable(Routes.homePage){
                HomePage(
                    navController = navController
                )
            }

            composable(Routes.createParkingArea){
                CreateParkingArea(
                    navController = navController
                )
            }

            composable(Routes.addSlotsToParkingArea + "/{parkingAreaId}/{parkingAreaName}"){
                val parkingAreaId = it.arguments?.getString("parkingAreaId")
                val parkingAreaName = it.arguments?.getString("parkingAreaName")
                AddSlotsToParkingArea(
                    navController = navController,
                    parkingAreaId = parkingAreaId?:"0",
                    parkingAreaName = parkingAreaName?:""
                )
            }

            //wecome page
            composable(Routes.welcomePage+"/{parkingAreaId}") {
                val parkingAreaId =it.arguments?.getString("parkingAreaId")
                welcomePage(
                    navController,
                    bookingViewModel = bookingViewModel,
                    parkingAreaId =parkingAreaId?:"0"
                )
            }

            //showing available slots
            composable("${Routes.viewYourParkingAreas}/{parkingAreasOfUser}") { backStackEntry->
                val json = backStackEntry.arguments?.getString("parkingAreasOfUser")
                val parkingAreasOfUser = Gson().fromJson(json, Array<ParkingAreaResponse>::class.java).toList()
                ViewYourParkingAreas(
                    navController = navController,
                    parkingAreas = parkingAreasOfUser
                )
            }

            composable(Routes.addUsersToParkingArea+"/{parkingAreaId}") {
                val parkingAreaId = it.arguments?.getString("parkingAreaId")
                AddUsersToParkingArea(
                    navController,
                    parkingAreaId = parkingAreaId?:"0"
                )
            }

            composable(Routes.assignSlotForUsers+"/{parkingAreaId}") {
                val parkingAreaId = it.arguments?.getString("parkingAreaId")
                AssignSlotForUsers(
                    navController,
                    parkingAreaId = parkingAreaId?:"0"
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