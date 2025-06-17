package ua.nure.holovashenko.tidyhabit.presentation.create

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import ua.nure.holovashenko.tidyhabit.R
import ua.nure.holovashenko.tidyhabit.data.local.model.TaskCategory
import ua.nure.holovashenko.tidyhabit.presentation.main.getDisplayName
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateTaskScreen(
    viewModel: CreateTaskViewModel = hiltViewModel(),
    onTaskSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern(context.getString(R.string.date_format))
    }
    val selectedDate = remember(viewModel.dueDateMillis) {
        Instant.ofEpochMilli(viewModel.dueDateMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    var showTitleError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = context.getString(R.string.create_task),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                CreateTitleField(viewModel, showTitleError) { showTitleError = false }

                CreateDescriptionField(viewModel)

                CreateCategorySelector(viewModel)

                CreateDatePicker(viewModel, selectedDate, dateFormatter)

                Button(
                    onClick = {
                        if (viewModel.title.isBlank()) showTitleError = true
                        else viewModel.saveTask(onTaskSaved)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(context.getString(R.string.save_task))
                }

                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = context.getString(R.string.task_cancel),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateTitleField(viewModel: CreateTaskViewModel, showError: Boolean, onValid: () -> Unit) {
    OutlinedTextField(
        value = viewModel.title,
        onValueChange = {
            viewModel.title = it
            if (showError && it.isNotBlank()) onValid()
        },
        label = { Text(stringResource(R.string.task_title)) },
        modifier = Modifier.fillMaxWidth(),
        isError = showError,
        singleLine = true
    )

    if (showError) {
        Text(
            text = stringResource(R.string.task_error),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun CreateDescriptionField(viewModel: CreateTaskViewModel) {
    OutlinedTextField(
        value = viewModel.description,
        onValueChange = { viewModel.description = it },
        label = { Text(stringResource(R.string.task_description)) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CreateCategorySelector(viewModel: CreateTaskViewModel) {
    val context = LocalContext.current

    Text(
        text = stringResource(R.string.task_category_select),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TaskCategory.entries.forEach { category ->
            val isSelected = viewModel.category == category
            val bgColor by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(0.1f)
                else MaterialTheme.colorScheme.surface
            )
            val borderColor by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.category = category },
                color = bgColor,
                border = BorderStroke(1.dp, borderColor),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = if (isSelected) 2.dp else 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = category.getDisplayName(context).replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CreateDatePicker(
    viewModel: CreateTaskViewModel,
    selectedDate: LocalDate,
    dateFormatter: DateTimeFormatter
) {
    val context = LocalContext.current

    Text(
        text = stringResource(R.string.task_due_date),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = selectedDate.format(dateFormatter),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        OutlinedButton(onClick = {
            val picker = DatePickerDialog(
                context,
                { _, year, month, day ->
                    val date = LocalDate.of(year, month + 1, day)
                    viewModel.dueDateMillis = date
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                },
                selectedDate.year,
                selectedDate.monthValue - 1,
                selectedDate.dayOfMonth
            )
            picker.show()
        }) {
            Text(stringResource(R.string.task_date_change))
        }
    }
}
