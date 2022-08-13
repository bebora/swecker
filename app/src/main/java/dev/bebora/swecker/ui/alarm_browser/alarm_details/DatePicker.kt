package dev.bebora.swecker.ui.alarm_browser.alarm_details

import android.icu.util.Calendar
import android.text.format.DateFormat
import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.util.*

@Composable
fun DatePicker(
    minDate: Long? = null,
    maxDate: Long? = null,
    onDateSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val selDate = remember { mutableStateOf(Calendar.getInstance().time) }

    Dialog(onDismissRequest = { onDismissRequest() }, properties = DialogProperties()) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(size = 16.dp)
                )
        ) {
            Column(
                Modifier
                    .defaultMinSize(minHeight = 72.dp)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select date",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.size(24.dp))

                Text(
                    text = DateFormat.format("MMM d, yyyy", selDate.value).toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.size(16.dp))
            }

            CustomCalendarView(
                minDate,
                maxDate,
                onDateSelected = {
                    selDate.value = it
                }
            )

            Spacer(modifier = Modifier.size(8.dp))

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp, end = 16.dp)
            ) {
                Button(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.textButtonColors(),
                ) {
                    Text(
                        text = "Cancel",
                    )
                }

                Button(
                    onClick = {
                        val newDate = selDate.value
                        onDateSelected(
                            // This makes sure date is not out of range
                            DateFormat.format(
                                "EEE, MMM d", Date(
                                    maxOf(
                                        minOf(maxDate ?: Long.MAX_VALUE, newDate.time),
                                        minDate ?: Long.MIN_VALUE
                                    )
                                )
                            ).toString(),
                        )
                        onDismissRequest()
                    },
                    colors = ButtonDefaults.textButtonColors(),
                ) {
                    Text(
                        text = "OK",
                    )
                }

            }
        }
    }
}


@Composable
fun CustomCalendarView(
    minDate: Long? = null,
    maxDate: Long? = null,
    onDateSelected: (Date) -> Unit
) {
    // Adds view to Compose
    AndroidView(
        modifier = Modifier.wrapContentSize(),
        factory = { context ->
            CalendarView(context)
        },
        update = { view ->
            if (minDate != null)
                view.minDate = minDate
            if (maxDate != null)
                view.maxDate = maxDate

            view.setOnDateChangeListener { _, year, month, dayOfMonth ->
                onDateSelected(
                    Calendar
                        .getInstance()
                        .apply {
                            set(year, month, dayOfMonth)
                        }
                        .time
                )
            }
        }
    )
}

fun getDaysOfWeekDisplayNames(loc: Locale): List<String> {
    val calendar = java.util.Calendar.getInstance()

    var dayOfWeek = calendar.firstDayOfWeek
    val result = mutableListOf<String>()

    var i = 7
    while (i > 0) {
        if (dayOfWeek > 7) {
            dayOfWeek = 1
        }
        calendar.set(java.util.Calendar.DAY_OF_WEEK, dayOfWeek)
        result.add(
            calendar.getDisplayName(
                java.util.Calendar.DAY_OF_WEEK,
                java.util.Calendar.SHORT,
                loc
            )!!
        )
        dayOfWeek++
        i--
    }
    return result
}
