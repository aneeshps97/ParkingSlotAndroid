package com.example.parkingslot.Route

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.parkingslot.mainpages.MyBookings.myBookings
import com.example.parkingslot.mainpages.ParkingArea.AddSlotsToParkingArea
import com.example.parkingslot.mainpages.ParkingArea.AddUsersToParkingArea
import com.example.parkingslot.mainpages.ParkingArea.AssignSlotForUsers
import com.example.parkingslot.mainpages.ParkingArea.CreateParkingArea
import com.example.parkingslot.mainpages.ParkingArea.EditParkingArea
import com.example.parkingslot.mainpages.ParkingArea.EditSlots
import com.example.parkingslot.mainpages.ParkingArea.EditUsers
import com.example.parkingslot.mainpages.ParkingArea.ViewYourParkingAreas
import com.example.parkingslot.mainpages.ParkingArea.parkingArea
import com.example.parkingslot.mainpages.availableslot.AvailableSlot
import com.example.parkingslot.mainpages.home.HomePage
import com.example.parkingslot.mainpages.parkingTicket.ParkingTicket
import com.example.parkingslot.mainpages.releaseSlot.ReleaseSlot
import com.example.parkingslot.mainpages.transferSlot.TransferSlot
import com.example.parkingslot.mainpages.userauth.Login
import com.example.parkingslot.mainpages.userauth.SignUp
import com.example.parkingslot.sharedView.BookingViewModel
import com.example.parkingslot.webConnect.dto.booking.BookingData
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaData
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

            composable(Routes.homePage) {
                HomePage(
                    navController = navController
                )
            }

            composable(Routes.createParkingArea) {
                CreateParkingArea(
                    navController = navController
                )
            }

            composable(Routes.addSlotsToParkingArea + "/{parkingAreaId}/{parkingAreaName}/{adminId}") {
                val parkingAreaId = it.arguments?.getString("parkingAreaId")
                val parkingAreaName = it.arguments?.getString("parkingAreaName")
                val adminId = it.arguments?.getString("adminId")
                AddSlotsToParkingArea(
                    navController = navController,
                    parkingAreaId = parkingAreaId ?: "0",
                    parkingAreaName = parkingAreaName ?: "",
                    adminId = adminId ?: "0"
                )
            }
            //parking area page
            composable(Routes.parkingArea + "/{parkingAreaId}/{parkingAreaName}/{adminId}") {
                val parkingAreaId = it.arguments?.getString("parkingAreaId")
                val parkingAreaName = it.arguments?.getString("parkingAreaName")
                val adminId = it.arguments?.getString("adminId")
                parkingArea(
                    navController,
                    parkingAreaId = parkingAreaId ?: "0",
                    parkingAreaName = parkingAreaName?:"",
                    adminId = adminId?:"-1"
                )
            }

            //showing parking areas
            composable(Routes.viewYourParkingAreas) {
                val parkingAreasOfUser = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<List<ParkingAreaData>>("parkingAreasOfUser")
                    ?: emptyList()
                ViewYourParkingAreas(
                    navController = navController,
                    parkingAreas = parkingAreasOfUser
                )
            }


            composable("${Routes.editSlots}/{parkingAreaData}") { backStackEntry ->
                val json = backStackEntry.arguments?.getString("parkingAreaData")
                val parkingAreaData = Gson().fromJson(json, ParkingAreaData::class.java)
                EditSlots(
                    navController = navController,
                    parkingAreaData = parkingAreaData
                )
            }

            composable("${Routes.editUsers}/{parkingAreaData}") { backStackEntry ->
                val json = backStackEntry.arguments?.getString("parkingAreaData")
                val parkingAreaData = Gson().fromJson(json, ParkingAreaData::class.java)
                EditUsers(
                    navController = navController,
                    parkingAreaData = parkingAreaData
                )
            }







            composable(Routes.addUsersToParkingArea + "/{parkingAreaId}/{parkingAreaName}/{adminId}") {
                val parkingAreaId = it.arguments?.getString("parkingAreaId")
                val parkingAreaName = it.arguments?.getString("parkingAreaName")
                val adminId = it.arguments?.getString("adminId")
                AddUsersToParkingArea(
                    navController,
                    parkingAreaId = parkingAreaId ?: "0",
                    parkingAreaName = parkingAreaName ?: "",
                    adminId = adminId ?: "0"
                )
            }


            composable("${Routes.assignSlotForUsers}/{parkingAreaData}") { backStackEntry ->
                val json = backStackEntry.arguments?.getString("parkingAreaData")
                val parkingAreaData = Gson().fromJson(json, ParkingAreaData::class.java)
                AssignSlotForUsers(
                    navController = navController,
                    parkingAreaData = parkingAreaData
                )
            }


            //showing the current ticket

            composable(Routes.parkingTicket + "/{parkingTicketNumber}") {
                val parkingTicketNumber = it.arguments?.getString("parkingTicketNumber")
                ParkingTicket(parkingTicketNumber = parkingTicketNumber ?: "00000")
            }

            //showing available slots
            composable("${Routes.availableSlots}/{slotsJson}/{parkingAreaId}") { backStackEntry ->
                val json = backStackEntry.arguments?.getString("slotsJson")
                val freeSlots = Gson().fromJson(json, Array<BookingData>::class.java).toList()
                val parkingAreaId = backStackEntry.arguments?.getString("parkingAreaId")
                AvailableSlot(
                    bookingData = freeSlots,
                    year = year,
                    month = month,
                    navController = navController,
                    parkingAreaId = parkingAreaId
                )
            }
            // for available slot calender change button
            composable(
                route = "${Routes.availableSlots}/{year}/{month}/{slotsJson}/{parkingAreaId}",
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
                    Gson().fromJson(decodedJson, Array<BookingData>::class.java).toList()
                val parkingAreaId = backStackEntry.arguments?.getString("parkingAreaId")
                AvailableSlot(
                    year = year,
                    month = month,
                    navController = navController,
                    bookingData = slotsData,
                    parkingAreaId = parkingAreaId
                )
            }


            //for current bookings
            composable("${Routes.myBookings}/{slotsJson}/{parkingAreaId}") { backStackEntry ->
                val json = backStackEntry.arguments?.getString("slotsJson")
                val bookedSlots = Gson().fromJson(json, Array<BookingData>::class.java).toList()
                val parkingAreaId = backStackEntry.arguments?.getString("parkingAreaId")
                myBookings(
                    bookingData = bookedSlots,
                    year = year,
                    month = month,
                    navController = navController,
                    parkingAreaId = parkingAreaId
                )
            }

            //for calender forward and backward button
            composable(
                route = "${Routes.myBookings}/{year}/{month}/{slotsJson}/{parkingAreaId}",
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
                    Gson().fromJson(decodedJson, Array<BookingData>::class.java).toList()
                val parkingAreaId = backStackEntry.arguments?.getString("parkingAreaId")
                myBookings(
                    year = year,
                    month = month,
                    navController = navController,
                    bookingData = slotsData,
                    parkingAreaId = parkingAreaId
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

            composable(Routes.editParkingArea + "/{parkingAreaId}/{parkingAreaName}") {
                val parkingAreaId = it.arguments?.getString("parkingAreaId")
                val parkingAreaName = it.arguments?.getString("parkingAreaName")
                EditParkingArea(
                    parkingAreaId = parkingAreaId,
                    parkingAreaName = parkingAreaName,
                    navController = navController

                )
            }

        }
    )
}