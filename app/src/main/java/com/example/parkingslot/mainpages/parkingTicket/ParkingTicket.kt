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

@Composable
fun ParkingTicket(parkingTicketNumber: String? = "0000", modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp * .80f
    val screenHeight = configuration.screenHeightDp.dp * .80f
    val circleSize = if (screenWidth < screenHeight) screenWidth else screenHeight
    PageBackground() {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .background(color = Color(0xFF289984), shape = CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
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
                            text = "LuLu",
                            color = Color.Blue,
                            fontFamily = FontFamily.Cursive,
                            fontWeight = FontWeight.Bold,
                            fontSize = 50.sp
                        )
                        Text(
                            text = "Tech park",
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
            }
        }
    }

}


