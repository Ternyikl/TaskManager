package com.example.taskmanager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskmanager.ui.theme.TaskManagerTheme
import kotlin.require
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.painterResource

data class Task(
    val id: Int,
    val name: String,
    val category: String,
    val priority: Int,
    val isDone: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskManagerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TasksMainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TasksMainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val tasks = remember {
        mutableStateListOf<Task>()
    }

    var nextId: Int by remember {
        mutableStateOf(1)
    }

    var name: String by remember {
        mutableStateOf("")
    }

    var category: String by remember {
        mutableStateOf("")
    }
    var priorityText: String by remember {
        mutableStateOf("")
    }

    var nameError: Boolean by remember {
        mutableStateOf(false)
    }

    var categoryError: Boolean by remember {
        mutableStateOf(false)
    }

    var priorityError: Boolean by remember {
        mutableStateOf(false)
    }

    val filterOptions = listOf("Összes", "Aktív", "Elkészült")

    var selectedFilter: String by remember {
        mutableStateOf(filterOptions[0])
    }

    val filteredTasks = when (selectedFilter) {
        filterOptions[1] -> tasks.filter { !it.isDone }
        filterOptions[2] -> tasks.filter { it.isDone }
        else -> tasks.toList()
    }

    var editingTask: Task? by remember {
        mutableStateOf(null)
    }

    var deletingTask: Task? by remember {
        mutableStateOf(null)
    }

    val emptyMessage = when {
        tasks.isEmpty() -> "Még nincs feladatod."
        selectedFilter == filterOptions[1] -> "Nincs aktív feladat."
        selectedFilter == filterOptions[2] -> "Még nincs elkészült feladat."
        else -> ""
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Feladatkezelő",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Szervezd meg a napi teendőidet!",
                style = MaterialTheme.typography.bodyLarge
            )

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                label = { Text("Feladat neve") },
                isError = nameError,
                supportingText = {
                    if (nameError) {
                        Text("A név megadása kötelező")
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = category,
                onValueChange = {
                    category = it
                    categoryError = false
                },
                label = { Text("Kategória") },
                isError = categoryError,
                supportingText = {
                    if (categoryError) {
                        Text("A kategória megadása kötelező")
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = priorityText,
                onValueChange = {
                    if (it.length <= 1 && it.all { c -> c.isDigit() }) {
                        priorityText = it
                        priorityError = false
                    }
                },
                label = { Text("Prioritás (1–3)") },
                isError = priorityError,
                supportingText = {
                    if (priorityError) {
                        Text("Heyletelen Prioritás (1 – alacsony, 2 – közepes, 3 – magas)")
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val priority = priorityText.toIntOrNull()

                    nameError = name.isBlank()
                    categoryError = category.isBlank()
                    priorityError = if (priority == null || priority !in 1..3) true else false

                    if (nameError || categoryError || priorityError || priority == null) {
                        Toast.makeText(
                            context, "Minden mezőt meg kell megadni!\n " +
                                    "A prioritás (1 – alacsony, 2 – közepes, 3 – magas)",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val newTask = Task(
                            id = nextId,
                            name = name,
                            category = category,
                            priority = priority
                        )

                        tasks.add(newTask)
                        nextId++

                        Toast.makeText(
                            context, "Sikeres hozzáadás!",
                            Toast.LENGTH_SHORT
                        ).show()

                        name = ""
                        category = ""
                        priorityText = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Feladat hozzáadása")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { filter ->
                    if (filter == selectedFilter) {
                        Button(
                            onClick = { selectedFilter = filter },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(red = 33, green = 150, blue = 243),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(filter)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { selectedFilter = filter },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(filter)
                        }
                    }
                }
            }

            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clipboard),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = emptyMessage,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = filteredTasks, key = { task: Task -> task.id }) { task: Task ->
                        TaskCard(
                            task = task,
                            onToggleDone = {
                                val index = tasks.indexOfFirst { it.id == task.id }
                                if (index >= 0) {
                                    tasks[index] = task.copy(isDone = !task.isDone)
                                }
                            },
                            onEdit = {
                                editingTask = task
                            },
                            onDelete = {
                                deletingTask = task
                            }
                        )
                    }
                }
            }

        }
    }
    if (editingTask != null) {
        EditTaskDialog(
            task = editingTask!!,
            onDismiss = { editingTask = null },
            onSave = { updated ->
                val index = tasks.indexOfFirst { it.id == updated.id }
                if (index >= 0) {
                    tasks[index] = updated
                }
                editingTask = null
            }
        )
    }

    if (deletingTask != null) {
        DeleteTaskDialog(
            task = deletingTask!!,
            onDismiss = { deletingTask = null },
            onConfirm = { taskToDelete ->
                tasks.removeAll { it.id == taskToDelete.id }
                deletingTask = null
            }
        )
    }
}

fun priorityLabel(priority: Int): String = when (priority) {
    1 -> "Alacsony prioritás"
    2 -> "Közepes prioritás"
    else -> "Magas prioritás"
}

fun priorityColor(priority: Int): Color = when (priority) {
    1 -> Color(red = 76, green = 175, blue = 80)
    2 -> Color(red = 255, green = 160, blue = 0)
    else -> Color(red = 229, green = 57, blue = 53)
}

@Composable
fun TaskAvatar(priority: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(priorityColor(priority), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = priority.toString(),
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TaskCard(
    task: Task,
    onToggleDone: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                TaskAvatar(priority = task.priority)

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = task.category,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = priorityLabel(task.priority),
                        style = MaterialTheme.typography.bodyMedium,
                        color = priorityColor(task.priority)
                    )
                    Text(
                        text = if (task.isDone) "Állapot: Elkészült" else "Állapot: Aktív",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (task.isDone) FontWeight.Bold else FontWeight.Normal
                    )
                }

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Szerkesztés")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Törlés")
                }
            }

            Spacer(modifier = Modifier.padding(top = 8.dp))

            if (task.isDone) {
                OutlinedButton(
                    onClick = onToggleDone,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Újra aktív")
                }
            } else {
                Button(
                    onClick = onToggleDone,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Elkészült")
                }
            }
        }
    }
}

@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    val context = LocalContext.current

    var name: String by remember {
        mutableStateOf(task.name)
    }

    var category: String by remember {
        mutableStateOf(task.category)
    }

    var priorityText: String by remember {
        mutableStateOf(task.priority.toString())
    }

    var nameError: Boolean by remember {
        mutableStateOf(false)
    }

    var categoryError: Boolean by remember {
        mutableStateOf(false)
    }

    var priorityError: Boolean by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Feladat szerkesztése") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text("Feladat neve") },
                    isError = nameError,
                    supportingText = {
                        if (nameError) {
                            Text("A név megadása kötelező")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = {
                        category = it
                        categoryError = false
                    },
                    label = { Text("Kategória") },
                    isError = categoryError,
                    supportingText = {
                        if (categoryError) {
                            Text("A kategória megadása kötelező")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = priorityText,
                    onValueChange = {
                        if (it.length <= 1 && it.all { c -> c.isDigit() }) {
                            priorityText = it
                            priorityError = false
                        }
                    },
                    label = { Text("Prioritás (1–3)") },
                    isError = priorityError,
                    supportingText = {
                        if (priorityError) {
                            Text("Helytelen prioritás (1 – alacsony, 2 – közepes, 3 – magas)")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val priority = priorityText.toIntOrNull()

                    nameError = name.isBlank()
                    categoryError = category.isBlank()
                    priorityError = priority == null || priority !in 1..3

                    if (nameError || categoryError || priorityError || priority == null) {
                        Toast.makeText(
                            context, "Minden mezőt meg kell adni!\n " +
                                    "A prioritás (1 – alacsony, 2 – közepes, 3 – magas)",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        onSave(
                            task.copy(
                                name = name,
                                category = category,
                                priority = priority
                            )
                        )
                    }
                }
            ) {
                Text("Mentés")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Mégse")
            }
        }
    )
}

@Composable
fun DeleteTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onConfirm: (Task) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Feladat törlése") },
        text = {
            Text("Biztosan törölni szeretnéd az „${task.name}” feladatot?")
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(task) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Törlés")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Mégse")
            }
        }
    )
}