package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProjectItem
import com.example.data.ProjectMilestone
import com.example.ui.JariditViewModel
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
fun ProjectsScreen(
    viewModel: JariditViewModel,
    projects: List<ProjectItem>,
    onConsultProject: (ProjectItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var expandedProjectId by remember { mutableStateOf<Long?>(projects.firstOrNull()?.id) }
    var addingMilestoneForProject by remember { mutableStateOf<ProjectItem?>(null) }
    var editingNotesProject by remember { mutableStateOf<ProjectItem?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .testTag("projects_screen")
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
                        .testTag("projects_header_card"),
                    borderColor = ArcCyan.copy(alpha = 0.4f)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                tint = ArcCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "STARK LABS PROTOCOL // PROJECTS",
                                color = ArcCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Track your technical inventions, engineering goals, and research milestones. JARIDIT integrates project status into chat context.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Project items
            items(projects, key = { it.id }) { project ->
                val milestonesFlow = remember(project.id) { viewModel.getMilestonesForProject(project.id) }
                val milestones by milestonesFlow.collectAsState(initial = emptyList())
                val isExpanded = expandedProjectId == project.id

                ProjectCard(
                    project = project,
                    milestones = milestones,
                    isExpanded = isExpanded,
                    onToggleExpand = {
                        expandedProjectId = if (isExpanded) null else project.id
                    },
                    onToggleMilestone = { milestone ->
                        viewModel.toggleMilestone(milestone, milestones, project)
                    },
                    onDeleteMilestone = { milestone ->
                        viewModel.deleteMilestone(milestone)
                    },
                    onAddMilestoneClick = {
                        addingMilestoneForProject = project
                    },
                    onEditNotesClick = {
                        editingNotesProject = project
                    },
                    onConsultClick = {
                        onConsultProject(project)
                    },
                    onDeleteProject = {
                        viewModel.deleteProject(project)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // New Project FAB
        FloatingActionButton(
            onClick = { showNewProjectDialog = true },
            containerColor = ArcCyan,
            contentColor = DarkVoid,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_project_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Project")
        }

        // Add Project Dialog
        if (showNewProjectDialog) {
            NewProjectDialog(
                onDismiss = { showNewProjectDialog = false },
                onConfirm = { title, desc, cat ->
                    viewModel.addProject(title, desc, cat)
                    showNewProjectDialog = false
                }
            )
        }

        // Add Milestone Dialog
        addingMilestoneForProject?.let { proj ->
            AddMilestoneDialog(
                projectName = proj.title,
                onDismiss = { addingMilestoneForProject = null },
                onConfirm = { title ->
                    viewModel.addMilestone(proj.id, title)
                    addingMilestoneForProject = null
                }
            )
        }

        // Edit Notes Dialog
        editingNotesProject?.let { proj ->
            EditProjectNotesDialog(
                initialNotes = proj.notes,
                onDismiss = { editingNotesProject = null },
                onSave = { updatedNotes ->
                    viewModel.updateProject(proj.copy(notes = updatedNotes))
                    editingNotesProject = null
                }
            )
        }
    }
}

@Composable
private fun ProjectCard(
    project: ProjectItem,
    milestones: List<ProjectMilestone>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleMilestone: (ProjectMilestone) -> Unit,
    onDeleteMilestone: (ProjectMilestone) -> Unit,
    onAddMilestoneClick: () -> Unit,
    onEditNotesClick: () -> Unit,
    onConsultClick: () -> Unit,
    onDeleteProject: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, if (isExpanded) ArcCyan.copy(alpha = 0.5f) else SurfaceBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
            .testTag("project_card_${project.id}")
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
                            .background(ArcCyanDark)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = project.category.uppercase(),
                            color = ArcCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "STATUS: ${project.status}",
                        color = ReactorMint,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onConsultClick,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("consult_project_btn_${project.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Consult JARIDIT",
                            tint = ArcCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleExpand,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = project.title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = project.description,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            // Progress Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "PROGRESSION",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${project.progress}%",
                    color = ArcCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { project.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ArcCyan,
                trackColor = SurfaceCardElevated,
                strokeCap = StrokeCap.Round
            )

            // Expanded Details: Milestones & Notes
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(SurfaceBorder)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Milestones Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "MILESTONES & PHASES",
                            color = ArcCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        TextButton(
                            onClick = onAddMilestoneClick,
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = ArcCyan)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Add Step", fontSize = 11.sp, color = ArcCyan)
                        }
                    }

                    if (milestones.isEmpty()) {
                        Text(
                            text = "No milestones yet. Tap 'Add Step' to create your roadmap.",
                            color = TextTertiary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        milestones.forEach { milestone ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceCardElevated)
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onToggleMilestone(milestone) }
                                ) {
                                    Icon(
                                        imageVector = if (milestone.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (milestone.isCompleted) ReactorMint else TextTertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = milestone.title,
                                        color = if (milestone.isCompleted) TextSecondary else TextPrimary,
                                        fontSize = 13.sp
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteMilestone(milestone) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove",
                                        tint = TextTertiary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    // Notes Section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "RESEARCH LOG & NOTES",
                            color = PlasmaBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        IconButton(
                            onClick = onEditNotesClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Notes", tint = PlasmaBlue, modifier = Modifier.size(14.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCharcoal)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = project.notes.ifBlank { "No log entries yet. Tap the edit pencil to document your findings." },
                            color = if (project.notes.isBlank()) TextTertiary else TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = onDeleteProject) {
                            Text("Delete Project", color = CriticalRed, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("ENGINEERING") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Initiate New Project", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Project Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArcCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Scope & Scientific Focus") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArcCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text("Category:", color = TextSecondary, fontSize = 12.sp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    listOf("ENGINEERING", "SCIENCE", "AI", "SCHOOL").forEach { cat ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (category == cat) ArcCyan else SurfaceCardElevated)
                                .clickable { category = cat }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) onConfirm(title, description, category)
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ArcCyan, contentColor = DarkVoid)
            ) {
                Text("Create Project", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkCharcoal
    )
}

@Composable
private fun AddMilestoneDialog(
    projectName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var stepTitle by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Phase Milestone", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text("Project: $projectName", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = stepTitle,
                    onValueChange = { stepTitle = it },
                    label = { Text("Milestone / Sub-Goal") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArcCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (stepTitle.isNotBlank()) onConfirm(stepTitle) },
                enabled = stepTitle.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ArcCyan, contentColor = DarkVoid)
            ) {
                Text("Add Step", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkCharcoal
    )
}

@Composable
private fun EditProjectNotesDialog(
    initialNotes: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var notesText by remember { mutableStateOf(initialNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Research Log", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                label = { Text("Technical Notes & Experiments") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ArcCyan,
                    unfocusedBorderColor = SurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                minLines = 4,
                maxLines = 8,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(notesText) },
                colors = ButtonDefaults.buttonColors(containerColor = ArcCyan, contentColor = DarkVoid)
            ) {
                Text("Save Notes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkCharcoal
    )
}
