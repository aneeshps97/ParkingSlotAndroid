package com.example.parkingslot.calender

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parkingslot.webConnect.requestresponse.BookingResponse
import com.google.gson.Gson
import java.net.URLEncoder
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun Calender(modifier: Modifier = Modifier, bookingData: List<BookingResponse> = emptyList<BookingResponse>(), year: Int, month: Int, navController: NavController, onClick: (String) -> Unit, calenderRef: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val firstDayOfMonth = LocalDate.of(year, month, 1)
        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
        val dayOfWeekOffset = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0
        val gson = Gson()
        val jsonString = gson.toJson(bookingData)
        val encodedJson = URLEncoder.encode(jsonString, "UTF-8")
        Column {

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                var currentMonth = month
                var currentYear = year

                MonthNavigator(
                    year = currentYear,
                    month = currentMonth,
                    onPrev = {
                        if (currentMonth == 1) {
                            currentMonth = 12
                            currentYear -= 1
                        } else {
                            currentMonth -= 1
                        }
                        navController.navigate("${calenderRef}/$currentYear/$currentMonth/$encodedJson")
                    },
                    onNext = {
                        if (currentMonth == 12) {
                            currentMonth = 1
                            currentYear += 1
                        } else {
                            currentMonth += 1
                        }
                        navController.navigate("${calenderRef}/$currentYear/$currentMonth/$encodedJson")
                    }
                )

            }
            Row {
                daysOfWeek.forEach {
                    Text(
                        text = it,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            val totalCells = daysInMonth + dayOfWeekOffset
            val weeks = (totalCells / 7) + if (totalCells % 7 > 0) 1 else 0
            var day = 1

            repeat(weeks) {
                Row {
                    for (i in 0..6) {
                        val cellIndex = it * 7 + i
                        if (cellIndex < dayOfWeekOffset || day > daysInMonth) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            )
                        } else {
                            val date:String = "$year-$month-$day"
                            val isMatch =isDateMatching(bookingData,date)
                            Text(
                                text = day.toString(),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .let { mod ->
                                        if (isMatch) mod.clickable {
                                            onClick(normalizeDate(date))
                                        } else mod
                                    },
                                textAlign = TextAlign.Center,
                                color = if (isMatch) Color.Green else Color.Unspecified // or your default color
                            )

                            day++
                        }
                    }
                }
            }
        }
    }

}


fun isDateMatching(slots: List<BookingResponse>, inputDate: String): Boolean {
    for (slot in slots) {
        val inputDateNormalized = normalizeDate(inputDate)
        val matches = slot.date == inputDateNormalized
        if (matches) {
            return true
        }
    }
    return false
}


fun normalizeDate(inputDateStr: String): String {
    val formatterInput = DateTimeFormatter.ofPattern("yyyy-M-d")
    val formatterOutput = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val date = LocalDate.parse(inputDateStr, formatterInput)
    return date.format(formatterOutput)
}


@Composable
fun MonthNavigator(year: Int, month: Int, onPrev: () -> Unit, onNext: () -> Unit) {
    val monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onPrev) {
            Text("<", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$monthName $year",
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(onClick = onNext) {
            Text(">", fontSize = 20.sp)
        }
    }
}