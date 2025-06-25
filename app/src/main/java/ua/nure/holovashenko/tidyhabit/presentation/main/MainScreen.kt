package ua.nure.holovashenko.tidyhabit.presentation.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import ua.nure.holovashenko.tidyhabit.R
import ua.nure.holovashenko.tidyhabit.data.local.model.Task
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onCreateTaskClick: () -> Unit,
    onLogout: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    val user by viewModel.user.collectAsState()
    val context = LocalContext.current
    val today = LocalDate.now()

    val groupedTasks = remember(tasks) { groupTasksByDate(tasks, today) }

    LaunchedEffect(Unit) {
        viewModel.onStart()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateTaskClick) {
                Icon(Icons.Default.Add, contentDescription = context.getString(R.string.add_task))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            user?.let {
                UserCard(user = it, onLogout = { viewModel.logout(onLogout) })
            }

            val noTasks = groupedTasks.overdue.isEmpty() &&
                    groupedTasks.today.isEmpty() &&
                    groupedTasks.upcoming.values.all { it.isEmpty() }

            if (noTasks) {
                Text(
                    text = context.getString(R.string.no_tasks_available),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 32.dp)
                )
            } else {
                TaskSection(
                    title = context.getString(R.string.overdue_section_title),
                    tasks = groupedTasks.overdue,
                    cardColor = MaterialTheme.colorScheme.errorContainer,
                    onComplete = viewModel::completeTask,
                    onDelete = viewModel::deleteTask
                )

                TaskSection(
                    title = context.getString(R.string.today_section_title),
                    tasks = groupedTasks.today,
                    cardColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    onComplete = viewModel::completeTask,
                    onDelete = viewModel::deleteTask
                )

                groupedTasks.upcoming.forEach { (date, tasksForDate) ->
                    TaskSection(
                        title = date.format(DateTimeFormatter.ofPattern(context.getString(R.string.date_format))),
                        tasks = tasksForDate,
                        cardColor = MaterialTheme.colorScheme.surfaceContainer,
                        onComplete = viewModel::completeTask,
                        onDelete = viewModel::deleteTask
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun groupTasksByDate(
    tasks: List<Task>,
    today: LocalDate
): GroupedTasks {
    val overdue = tasks.filter {
        Instant.ofEpochMilli(it.dueDate).atZone(ZoneId.systemDefault()).toLocalDate().isBefore(today)
    }

    val todayTasks = tasks.filter {
        Instant.ofEpochMilli(it.dueDate).atZone(ZoneId.systemDefault()).toLocalDate().isEqual(today)
    }

    val upcoming = tasks.filter {
        Instant.ofEpochMilli(it.dueDate).atZone(ZoneId.systemDefault()).toLocalDate().isAfter(today)
    }.groupBy {
        Instant.ofEpochMilli(it.dueDate).atZone(ZoneId.systemDefault()).toLocalDate()
    }.toSortedMap()

    return GroupedTasks(overdue, todayTasks, upcoming)
}

private data class GroupedTasks(
    val overdue: List<Task>,
    val today: List<Task>,
    val upcoming: Map<LocalDate, List<Task>>
)
