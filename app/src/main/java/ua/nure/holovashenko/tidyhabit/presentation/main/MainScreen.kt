package ua.nure.holovashenko.tidyhabit.presentation.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import ua.nure.holovashenko.tidyhabit.R
import ua.nure.holovashenko.tidyhabit.data.local.model.Task
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

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

    val overdueTasks = tasks.filter {
        Instant.ofEpochMilli(it.dueDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .isBefore(today)
    }

    val todayTasks = tasks.filter {
        Instant.ofEpochMilli(it.dueDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .isEqual(today)
    }

    val upcomingTasks = tasks.filter {
        Instant.ofEpochMilli(it.dueDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .isAfter(today)
    }

    val groupedUpcomingTasks: Map<LocalDate, List<Task>> = upcomingTasks.groupBy {
        Instant.ofEpochMilli(it.dueDate).atZone(ZoneId.systemDefault()).toLocalDate()
    }.toSortedMap()

    LaunchedEffect(Unit) {
        viewModel.loadTasks()
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

            // Інформація про користувача
            user?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Header
                        Text(
                            text = context.getString(R.string.welcome, it.name),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Level
                        Text(
                            text = context.getString(R.string.user_level, it.level),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // XP Progress
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LinearProgressIndicator(
                                progress = it.xp / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Text(
                                text = context.getString(R.string.user_xp, it.xp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Streak
                        Text(
                            text = context.resources.getQuantityString(
                                R.plurals.user_streak,
                                it.streak,
                                it.streak
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        // Logout button aligned right
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { viewModel.logout(onLoggedOut = onLogout) }
                            ) {
                                Text(
                                    text = context.getString(R.string.logout),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }

            // Overdue Tasks
            if (overdueTasks.isNotEmpty()) {
                TaskSection(
                    title = context.getString(R.string.overdue_section_title),
                    tasks = overdueTasks,
                    onComplete = viewModel::completeTask,
                    onDelete = viewModel::deleteTask,
                    cardColor = MaterialTheme.colorScheme.errorContainer
                )
            }

            // Today Tasks
            if (todayTasks.isNotEmpty()) {
                TaskSection(
                    title = context.getString(R.string.today_section_title),
                    tasks = todayTasks,
                    onComplete = viewModel::completeTask,
                    onDelete = viewModel::deleteTask,
                    cardColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            }

            // Upcoming Tasks
            if (upcomingTasks.isNotEmpty()) {
                groupedUpcomingTasks.forEach { (date, tasksForDate) ->
                    TaskSection(
                        title = date.format(java.time.format.DateTimeFormatter.ofPattern(context.getString(R.string.date_format))),
                        tasks = tasksForDate,
                        onComplete = viewModel::completeTask,
                        onDelete = viewModel::deleteTask,
                        cardColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                }
            }
        }
    }
}
