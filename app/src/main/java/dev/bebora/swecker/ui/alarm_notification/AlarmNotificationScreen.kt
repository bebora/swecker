package dev.bebora.swecker.ui.alarm_notification

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.AlarmOff
import androidx.compose.material.icons.outlined.Snooze

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AlarmNotificationFullScreen(
    modifier: Modifier = Modifier,
    alarmName: String,
    time: String,
    onAlarmDismiss: () -> Unit,
    onAlarmSnooze: () -> Unit
) {

    var isDragging by remember {
        mutableStateOf(false)
    }

    var isSnoozeSelected by remember {
        mutableStateOf(false)
    }

    var isStopSelected by remember {
        mutableStateOf(false)
    }

    val delay = 600
    val duration = 1200
    val infiniteTransition = rememberInfiniteTransition()


    val buttonSize = 80.dp
    val buttonDistance = 16.dp

    val dragOffset = remember { mutableStateOf(0f) }


    //animations
    val animatedScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.02f, animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                delayMillis = delay,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val blackToWhite by infiniteTransition.animateColor(
        initialValue = Color.Black,
        targetValue = Color.White,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(
                durationMillis = duration,
                delayMillis = delay,
                easing = LinearOutSlowInEasing

            ),
            repeatMode = RepeatMode.Restart
        ),

        )

    val animatedExternalCircleRadius by infiniteTransition.animateValue(
        initialValue = 80.dp,
        targetValue = 300.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                delayMillis = delay,
                easing = LinearOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )


    Column(
        modifier = modifier
            .fillMaxSize(1f)
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.1f))
        Text(
            text = time,
            color = Color.White,
            fontWeight = FontWeight(700),
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = alarmName, color = Color.White, style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.weight(0.9f))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(100.dp)
                .width(300.dp)
        ) {


            if (!isDragging) {
                Box(
                    modifier = modifier
                        .size(animatedExternalCircleRadius)
                        .clip(shape = CircleShape)
                        .border(BorderStroke(4.dp, blackToWhite), shape = CircleShape)
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(1f)
            ) {

                //Snooze
                AnimatedButton(
                    icon = Icons.Outlined.Snooze,
                    animatedScale = animatedScale,
                    isAnimated = isStopSelected,
                    dragOffset = -dragOffset.value,
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer
                )

                Spacer(modifier = Modifier.width(16.dp))

                //detect drag from center
                Box(modifier = Modifier.pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (isSnoozeSelected) {
                                onAlarmSnooze()
                            } else if (isStopSelected) {
                                onAlarmDismiss()
                            }
                            dragOffset.value = 0f
                            isDragging = false
                        },
                        onDragStart = {
                            isDragging = true
                        }
                    ) { _, dragAmount ->
                        val originalX = dragOffset.value
                        val newValue = (originalX + dragAmount).coerceIn(
                            -(buttonSize + buttonDistance).value,
                            (buttonSize + buttonDistance).value
                        )
                        if (newValue >= 60f) {
                            isStopSelected = true
                        } else if (newValue <= -60f) {
                            isSnoozeSelected = true
                        }
                        dragOffset.value = newValue
                    }
                }) {
                    if (!isDragging) {
                        Box(
                            modifier = modifier
                                .size(buttonSize)
                                .scale(animatedScale)
                                .clip(shape = CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Alarm,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )

                        }
                    } else {
                        Spacer(modifier = Modifier.size(buttonSize))
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                //AlarmStop
                AnimatedButton(
                    icon = Icons.Outlined.AlarmOff,
                    animatedScale = animatedScale,
                    isAnimated = isStopSelected,
                    dragOffset = dragOffset.value,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer
                )


            }
            if (isDragging) {
                //draggable circle
                Box(
                    modifier = modifier
                        .size(84.dp)
                        .offset(x = dragOffset.value.dp)
                        .clip(shape = CircleShape)
                        .border(BorderStroke(4.dp, Color.White), shape = CircleShape),
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }

}


@Composable
fun AnimatedButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    animatedScale: Float,
    isAnimated: Boolean,
    dragOffset: Float,
    backgroundColor: Color
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .scale(
                if (isAnimated) {
                    animatedScale
                } else {
                    1.0f
                }
            )
            .clip(shape = CircleShape)
            .background(
                backgroundColor.copy(
                    alpha = (dragOffset / 96).coerceIn(
                        0f,
                        1f
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = Color.White
        )

    }
}

@Preview(showBackground = true)
@Composable
fun AlarmNotificationPreview() {
    AlarmNotificationFullScreen(
        alarmName = "Test name",
        time = "12:30",
        onAlarmDismiss = { /*TODO*/ }) {

    }
}
