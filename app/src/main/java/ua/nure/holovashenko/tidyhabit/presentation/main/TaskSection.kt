package ua.nure.holovashenko.tidyhabit.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ua.nure.holovashenko.tidyhabit.data.local.model.Task

@Composable
fun TaskSection(
    title: String,
    tasks: List<Task>,
    onComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
    cardColor: Color = MaterialTheme.colorScheme.surface // 👈 новий параметр
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = titleColor
        )
        if (tasks.isEmpty()) {
            Text("No tasks in this section.", style = MaterialTheme.typography.bodyMedium)
        } else {
            tasks.forEach { task ->
                TaskCard(
                    task = task,
                    onComplete = onComplete,
                    onDelete = onDelete,
                    cardColor = cardColor
                )
            }
        }
    }
}
