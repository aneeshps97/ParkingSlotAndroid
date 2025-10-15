package com.example.parkingslot.mainpages.parkingTicket

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parkingslot.mainpages.background.PageBackground
import kotlin.math.cos
import kotlin.math.sin
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import com.example.parkingslot.encryption.SimpleCrypto
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

@Composable
fun ParkingTicket(
    parkingTicketNumber: String? = "0000",
    ticketLine1: String? = "LuLu",
    ticketLine2: String? = "Tech park",
    issuedDate:String?="",
    parkingAreaName:String?="",
    userName:String?="",
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp * .80f
    val screenHeight = configuration.screenHeightDp.dp * .80f
    val circleSize = if (screenWidth < screenHeight) screenWidth else screenHeight
    var flipped by remember { mutableStateOf(false) }
    val rotationY by animateFloatAsState(targetValue = if (flipped) 180f else 0f)
    PageBackground() {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .background(color = Color(0xFF289984), shape = CircleShape)
                    .clip(CircleShape)
                    .graphicsLayer { this.rotationY = rotationY }
                    .clickable { flipped = !flipped },
                contentAlignment = Alignment.Center
            ) {
                if (rotationY <= 90f) {
                    val text = "BIKE PARKING PERMIT"
                    val textRadiusOffset: Float = 120f
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val centerX = canvasWidth / 2f
                        val centerY = canvasHeight / 2f

                        val radius = (canvasWidth / 2f) - textRadiusOffset
                        val paint = Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 70f
                            textAlign = Paint.Align.CENTER
                            isAntiAlias = true
                            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

                        }

                        val angleStep = 180f / text.length

                        for (i in text.indices) {
                            val angle =
                                Math.toRadians((i * angleStep - 90).toDouble()) // start from top
                            val x = centerX + radius * cos(angle).toFloat()
                            val y = centerY + radius * sin(angle).toFloat()

                            drawContext.canvas.nativeCanvas.apply {
                                save()
                                rotate(i * angleStep, x, y)
                                drawText(text[i].toString(), x, y, paint)
                                restore()
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(circleSize * 0.60f)
                            .background(color = Color.White, shape = CircleShape)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = ticketLine1.toString(),
                                color = Color.Blue,
                                fontFamily = FontFamily.Cursive,
                                fontWeight = FontWeight.Bold,
                                fontSize = 50.sp
                            )
                            Text(
                                text = ticketLine2.toString(),
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Slot: $parkingTicketNumber",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    }
                }else{
                    QrCodeImage(
                        data = createData(
                            ticketNumber = parkingTicketNumber.toString(),
                            date = issuedDate.toString(),
                            parkingAreaName = parkingAreaName.toString(),
                            userName = userName.toString()
                        ),
                        modifier = Modifier.size(200.dp)
                    )
                }
            }

        }
    }

}

fun createData(ticketNumber:String, date: String, parkingAreaName: String, userName:String):String{
 val authkey = "XYZ123SECRET"
    val ticket = TicketData(
        ticketNumber = ticketNumber,
        date = date,
        parkingAreaName = parkingAreaName,
        userName = userName,
        authKey = authkey
    )
    val gson = Gson()
    val jsonString = gson.toJson(ticket)
    return SimpleCrypto.encrypt(jsonString)

}

@Composable
fun QrCodeImage(
    data: String,
    modifier: Modifier = Modifier,
    size: Int = 512
) {
    val bitmap = remember(data, size) {
        generateQrCodeBitmap(data, size)
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "QR Code",
        modifier = modifier
    )
}

// Helper function to generate Bitmap
fun generateQrCodeBitmap(data: String, size: Int = 512): Bitmap {
    val bitMatrix: BitMatrix = MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, size, size)
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bmp
}


