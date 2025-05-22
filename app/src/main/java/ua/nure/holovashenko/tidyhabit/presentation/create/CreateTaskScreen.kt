package ua.nure.holovashenko.tidyhabit.presentation.create

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CreateTaskScreen(onTaskSaved: () -> Unit) {
    Column {
        Text("Create Task Screen")
        Button(onClick = onTaskSaved) {
            Text("Save Task")
        }
    }
}
