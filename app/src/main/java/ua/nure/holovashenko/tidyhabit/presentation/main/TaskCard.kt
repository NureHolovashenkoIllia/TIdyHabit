package ua.nure.holovashenko.tidyhabit.presentation.main

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ua.nure.holovashenko.tidyhabit.data.local.model.Task
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import ua.nure.holovashenko.tidyhabit.R
import ua.nure.holovashenko.tidyhabit.data.local.model.TaskCategory

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskCard(
    task: Task,
    onComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    cardColor: Color = MaterialTheme.colorScheme.surface
) {
    val dismissState = rememberDismissState()
    val context = LocalContext.current

    LaunchedEffect(dismissState.currentValue) {
        if (
            dismissState.currentValue == DismissValue.DismissedToStart &&
            !task.isCompleted
        ) {
            onComplete(task)
            dismissState.snapTo(DismissValue.Default)
        } else {
            dismissState.snapTo(DismissValue.Default)
        }
    }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 4.dp)
                    .background(
                        color = if (task.isCompleted)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.large
                    ),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (!task.isCompleted) {
                    Text(
                        text = context.getString(R.string.complete_task),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(end = 24.dp)
                    )
                }
            }
        },
        dismissContent = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (task.isCompleted)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        cardColor
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Заголовок
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Опис
                    task.description?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Категорія
                    Text(
                        text = context.getString(R.string.task_category, task.category.getDisplayName(context)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Кнопки
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (!task.isCompleted) {
                            TextButton(onClick = { onComplete(task) }) {
                                Text(context.getString(R.string.complete_task))
                            }
                        }

                        TextButton(onClick = { onDelete(task) }) {
                            Text(context.getString(R.string.delete_task))
                        }
                    }
                }
            }
        }
    )
}

fun TaskCategory.getDisplayName(context: Context): String {
    return when (this) {
        TaskCategory.DISHES -> context.getString(R.string.category_dishes)
        TaskCategory.VACUUM -> context.getString(R.string.category_vacuum)
        TaskCategory.LAUNDRY -> context.getString(R.string.category_laundry)
        TaskCategory.OTHER -> context.getString(R.string.category_other)
    }
}
