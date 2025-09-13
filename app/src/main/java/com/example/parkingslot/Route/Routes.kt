package com.example.parkingslot.Route

object Routes {
    var parkingArea = "ParkingArea"
    var availableSlots = "availableSlots"
    var parkingTicket = "ParkingTicket"
    var myBookings = "myBookings"

    var login = "Login"
    var signup= "SignUp"


    @Deprecated(
        message = "No longer used. Use viewYourParkingAreas instead.",
        replaceWith = ReplaceWith("viewYourParkingAreas")
    )
    var homePage = "HomePage"


    var createParkingArea = "CreateParkingArea"

    var addSlotsToParkingArea = "AddSlotsToParkingArea"

    var addUsersToParkingArea = "AddUsersToParkingArea"

    var assignSlotForUsers = "AssignSlotForUsers"

    var autoAssignSlots = "AutoAssignSlots"

    var viewYourParkingAreas = "ViewYourParkingAreas"

    var editParkingArea = "EditParkingArea"

    var editSlots = "EditSlots"

    var editUsers = "EditUsers"

    var editBooking ="EditBooking"

}