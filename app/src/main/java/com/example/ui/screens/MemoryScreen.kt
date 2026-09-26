package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MemoryItem
import com.example.ui.components.HudCard
import com.example.ui.theme.ArcCyan
import com.example.ui.theme.ArcCyanDark
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.DarkVoid
import com.example.ui.theme.PlasmaBlue
import com.example.ui.theme.ReactorMint
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun MemoryScreen(
    memories: List<MemoryItem>,
    onAddMemory: (String, String, String, Boolean) -> Unit,
    onUpdateMemory: (MemoryItem) -> Unit,
    onDeleteMemory: (MemoryItem) -> Unit,
    onClearAllMemories: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingMemory by remember { mutableStateOf<MemoryItem?>(null) }
    var showClearConfirmation by remember { mutableStateOf(false) }

    val categories = listOf("ALL", "PROFILE", "GOAL", "INTEREST", "PREFERENCE", "PROJECT", "SCHOOL")

    val filteredMemories = if (selectedCategory == "ALL") {
        memories
    } else {
        memories.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .testTag("memory_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp)
        ) {
            // Header summary
            item {
                HudCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("memory_header_card"),
                    borderColor = ArcCyan.copy(alpha = 0.4f)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = ArcCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "USER-APPROVED MEMORY VAULT",
                                    color = ArcCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                            }

                            if (memories.isNotEmpty()) {
                                IconButton(
                                    onClick = { showClearConfirmation = true },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("clear_all_memories_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteSweep,
                                        contentDescription = "Clear All Memories",
                                        tint = CriticalRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "JARIDIT stores context you explicitly confirm. These memories are injected into your assistant's neural prompts so it never forgets your projects, preferences, or goals.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ArcCyanDark)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${memories.size} MEMORIES STORED",
                                    color = ArcCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Category filter chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) ArcCyan else SurfaceCard)
                                .border(
                                    1.dp,
                                    if (isSelected) ArcCyan else SurfaceBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                                .testTag("category_filter_$category")
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) DarkVoid else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Memories list
            if (filteredMemories.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No memories in this category",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredMemories, key = { it.id }) { item ->
                    MemoryCard(
                        memory = item,
                        onEdit = { editingMemory = item },
                        onDelete = { onDeleteMemory(item) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // Add Memory FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = ArcCyan,
            contentColor = DarkVoid,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_memory_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Memory")
        }

        // Add Dialog
        if (showAddDialog) {
            MemoryFormDialog(
                title = "Record New Memory",
                initialTitle = "",
                initialDetail = "",
                initialCategory = "FACT",
                initialSensitive = false,
                onDismiss = { showAddDialog = false },
                onSave = { title, detail, category, isSensitive ->
                    onAddMemory(title, detail, category, isSensitive)
                    showAddDialog = false
                }
            )
        }

        // Edit Dialog
        editingMemory?.let { memory ->
            MemoryFormDialog(
                title = "Edit Memory",
                initialTitle = memory.title,
                initialDetail = memory.detail,
                initialCategory = memory.category,
                initialSensitive = memory.isSensitive,
                onDismiss = { editingMemory = null },
                onSave = { title, detail, category, isSensitive ->
                    onUpdateMemory(
                        memory.copy(
                            title = title,
                            detail = detail,
                            category = category,
                            isSensitive = isSensitive
                        )
                    )
                    editingMemory = null
                }
            )
        }

        // Clear All Confirmation Dialog
        if (showClearConfirmation) {
            AlertDialog(
                onDismissRequest = { showClearConfirmation = false },
                title = {
                    Text(
                        "Purge Neural Memory Vault?",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "This will permanently erase all saved context, interests, and preferences that JARIDIT remembers about you.",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onClearAllMemories()
                            showClearConfirmation = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CriticalRed)
                    ) {
                        Text("Purge All", color = DarkVoid, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirmation = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = DarkCharcoal,
                modifier = Modifier.testTag("confirm_clear_memories_dialog")
            )
        }
    }
}

@Composable
private fun MemoryCard(
    memory: MemoryItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
            .testTag("memory_card_${memory.id}")
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (memory.category.uppercase()) {
                                    "GOAL" -> ReactorMint.copy(alpha = 0.2f)
                                    "PROJECT" -> ArcCyan.copy(alpha = 0.2f)
                                    "PROFILE" -> PlasmaBlue.copy(alpha = 0.2f)
                                    else -> SurfaceCardElevated
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = memory.category.uppercase(),
                            color = when (memory.category.uppercase()) {
                                "GOAL" -> ReactorMint
                                "PROJECT" -> ArcCyan
                                "PROFILE" -> PlasmaBlue
                                else -> TextSecondary
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (memory.isSensitive) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Confidential",
                            tint = ArcCyan,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = TextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = CriticalRed.copy(alpha = 0.8f),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = memory.title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = memory.detail,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun MemoryFormDialog(
    title: String,
    initialTitle: String,
    initialDetail: String,
    initialCategory: String,
    initialSensitive: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Boolean) -> Unit
) {
    var memTitle by remember { mutableStateOf(initialTitle) }
    var detail by remember { mutableStateOf(initialDetail) }
    var category by remember { mutableStateOf(initialCategory) }
    var isSensitive by remember { mutableStateOf(initialSensitive) }

    val categories = listOf("PROFILE", "GOAL", "INTEREST", "PREFERENCE", "PROJECT", "SCHOOL", "FACT")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = memTitle,
                    onValueChange = { memTitle = it },
                    label = { Text("Memory Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArcCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("memory_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = detail,
                    onValueChange = { detail = it },
                    label = { Text("Details & Context") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArcCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("memory_detail_input")
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Category:",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (category == cat) ArcCyan else SurfaceCardElevated)
                                .clickable { category = cat }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (category == cat) DarkVoid else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Mark Confidential",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Switch(
                        checked = isSensitive,
                        onCheckedChange = { isSensitive = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ArcCyan,
                            checkedTrackColor = ArcCyanDark
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (memTitle.isNotBlank() && detail.isNotBlank()) {
                        onSave(memTitle, detail, category, isSensitive)
                    }
                },
                enabled = memTitle.isNotBlank() && detail.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ArcCyan, contentColor = DarkVoid),
                modifier = Modifier.testTag("save_memory_btn")
            ) {
                Text("Save to Vault", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkCharcoal,
        modifier = Modifier.testTag("memory_form_dialog")
    )
}
