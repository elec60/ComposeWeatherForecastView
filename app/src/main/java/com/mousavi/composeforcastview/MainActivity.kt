package com.mousavi.composeforcastview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mousavi.composeforcastview.ui.theme.ComposeForcastViewTheme
import java.util.*
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeForcastViewTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        Text(
                            modifier = Modifier.padding(24.dp),
                            text = "1 month forecast",
                            fontWeight = FontWeight.Bold
                        )
                        ForecastView()
                    }
                }
            }
        }
    }
}

private var calendar = Calendar.getInstance().apply {
    add(Calendar.DAY_OF_MONTH, -1)
}

@Composable
fun ForecastView() {

    val temps = mutableListOf<Temperature>()
    repeat(31) {
        temps += Temperature(
            max = Random.nextInt(15, 30),
            min = Random.nextInt(-10, 12)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .horizontalScroll(rememberScrollState())
    ) {

        for (i in 0 until temps.size) {
            val temp = temps[i]
            var nextTemp: Temperature? = null
            if (i < temps.size - 1) {
                nextTemp = temps[i + 1]
            }
            val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, 0, Locale.ROOT)
            ForecastDayView(
                dayOfWeek = dayOfWeek ?: "",
                date = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONDAY) + 1}",
                windSpeed = "${Random.nextInt(10, 18)}Km/h",
                width = 60.dp,
                temperature = temp,
                nextTemperature = nextTemp,
                maxTempOverAll = temps.maxOf { it.max },
                minTempOverAll = temps.minOf { it.min },
                isToday = i == 1,
                isYesterday = i == 0
            )
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }
    }
}


@Composable
fun ForecastDayView(
    dayOfWeek: String,
    date: String,
    topIcon: Painter = painterResource(id = R.drawable.ic_sunny),
    bottomIcon: Painter = painterResource(id = R.drawable.ic_moon),
    windSpeed: String,
    width: Dp,
    temperature: Temperature,
    nextTemperature: Temperature?,
    maxTempOverAll: Int,
    minTempOverAll: Int,
    isToday: Boolean = false,
    isYesterday: Boolean = false,
    lineColor: Color = Color.LightGray.copy(alpha = 0.6f),
) {
    var size by remember {
        mutableStateOf(Rect.Zero)
    }
    var y1 by remember {
        mutableStateOf(0f)
    }

    var y2 by remember {
        mutableStateOf(0f)
    }

    Box(
        modifier = Modifier
            .width(width = width)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = if (isToday) Color.LightGray.copy(alpha = .4f) else Color.Transparent,
                    shape = if (isToday) RoundedCornerShape(8.dp) else RectangleShape),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = if (isToday) "Today" else if (isYesterday) "Yesterday" else dayOfWeek,
                    fontSize = 12.sp)
                Text(text = date, fontSize = 10.sp)
            }
            Image(
                modifier = Modifier.padding(top = 14.dp),
                painter = topIcon,
                contentDescription = "",
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 30.dp)
                    .drawBehind {
                        val diff = minTempOverAll - maxTempOverAll

                        nextTemperature?.let {

                            val startOffsetForCurrentMax = Offset(size.width / 2f,
                                size.height / diff * (temperature.max - maxTempOverAll))
                            val endOffsetForNextMax = Offset(size.width * 1.5f,
                                size.height / diff * (it.max - maxTempOverAll))

                            y1 = startOffsetForCurrentMax.y

                            drawLine(
                                color = lineColor,
                                start = startOffsetForCurrentMax,
                                end = endOffsetForNextMax,
                                strokeWidth = 2.dp.toPx()
                            )

                            drawCircle(
                                color = lineColor,
                                radius = 4.dp.toPx(),
                                center = Offset(x = size.width / 2f, y = startOffsetForCurrentMax.y)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 2.dp.toPx(),
                                center = Offset(x = size.width / 2f, y = startOffsetForCurrentMax.y)
                            )

                            val startOffsetForCurrentMin = Offset(size.width / 2f,
                                size.height / diff * (temperature.min - maxTempOverAll))
                            val endOffsetForNextMin = Offset(size.width * 1.5f,
                                size.height / diff * (it.min - maxTempOverAll))


                            y2 = startOffsetForCurrentMin.y

                            drawLine(
                                color = lineColor,
                                start = startOffsetForCurrentMin,
                                end = endOffsetForNextMin,
                                strokeWidth = 2.dp.toPx()
                            )

                            drawCircle(
                                color = lineColor,
                                radius = 4.dp.toPx(),
                                center = Offset(x = size.width / 2f, y = startOffsetForCurrentMin.y)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 2.dp.toPx(),
                                center = Offset(x = size.width / 2f, y = startOffsetForCurrentMin.y)
                            )
                        } ?: run {
                            val startOffsetForCurrentMax = Offset(size.width / 2f,
                                size.height / diff * (temperature.max - maxTempOverAll))

                            y1 = startOffsetForCurrentMax.y

                            drawCircle(
                                color = lineColor,
                                radius = 4.dp.toPx(),
                                center = Offset(x = size.width / 2f, y = startOffsetForCurrentMax.y)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 2.dp.toPx(),
                                center = Offset(x = size.width / 2f, y = startOffsetForCurrentMax.y)
                            )

                            val startOffsetForCurrentMin = Offset(size.width / 2f,
                                size.height / diff * (temperature.min - maxTempOverAll))

                            y2 = startOffsetForCurrentMin.y

                            drawCircle(
                                color = lineColor,
                                radius = 4.dp.toPx(),
                                center = Offset(x = size.width / 2f, y = startOffsetForCurrentMin.y)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 2.dp.toPx(),
                                center = Offset(x = size.width / 2f, y = startOffsetForCurrentMin.y)
                            )
                        }

                    }
                    .weight(1f)
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        size = it.boundsInParent()
                    }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    modifier = Modifier
                        .offset {
                            IntOffset(0, y1.toInt() - 24.dp.roundToPx())
                        },
                    text = temperature.max.toString() + "°",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    modifier = Modifier
                        .offset {
                            IntOffset(0, y2.toInt() + 6.dp.roundToPx())
                        },
                    text = temperature.min.toString() + "°",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Image(
                modifier = Modifier.padding(bottom = 14.dp),
                painter = bottomIcon,
                contentDescription = ""
            )
            Text(
                text = windSpeed,
                modifier = Modifier.padding(bottom = 10.dp),
                fontSize = 10.sp
            )

        }

        if (isYesterday) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.LightGray.copy(alpha = 0.1f))
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun DefaultItemPreview() {
    ComposeForcastViewTheme {
        ForecastDayView(
            dayOfWeek = "Today",
            date = "10/23",
            windSpeed = "8.1km/h",
            width = 60.dp,
            temperature = Temperature(25, 4),
            nextTemperature = Temperature(28, 10),
            maxTempOverAll = 32,
            minTempOverAll = 2
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeForcastViewTheme {
        ForecastView()
    }
}