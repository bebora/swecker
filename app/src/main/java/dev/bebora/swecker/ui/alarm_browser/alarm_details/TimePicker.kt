package dev.bebora.swecker.ui.alarm_browser.alarm_details

import android.text.format.DateFormat
import android.widget.TimePicker
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
fun TimePicker(
    onTimeSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val selTime = remember { mutableStateOf(android.icu.util.Calendar.getInstance().time) }

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
                    text = "Select time",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            CustomTimePickerView(
                onTimeSelected = {
                    selTime.value = it
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
                        val newTime = selTime.value
                        onTimeSelected(
                            DateFormat.format("H:mm", newTime).toString(),
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
fun CustomTimePickerView(
    onTimeSelected: (Date) -> Unit,
) {
    // Adds view to Compose
    AndroidView(
        modifier = Modifier.wrapContentSize(),
        factory = { context ->
            TimePicker(context)
        },
        update = { view ->
            view.setOnTimeChangedListener { _, hour, minute ->
                onTimeSelected(
                    android.icu.util.Calendar
                        .getInstance()
                        .apply {
                            set(0, 0, 0, hour, minute)
                        }
                        .time
                )
            }
        }
    )
}
