package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.VideoEngine
import com.example.data.model.VideoProject
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VideoGalleryView(
    projects: List<VideoProject>,
    onSelectProject: (VideoProject) -> Unit,
    onDeleteProject: (Long) -> Unit,
    onNewProject: () -> Unit,
    modifier: Modifier = Modifier
) {
    var filterEngine by remember { mutableStateOf<VideoEngine?>(null) }
    val filteredList = remember(projects, filterEngine) {
        if (filterEngine == null) projects else projects.filter { it.engine == filterEngine }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBgDark)
            .padding(horizontal = 16.dp)
            .testTag("gallery_view_container")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Video Studio Gallery",
                    color = SleekTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${projects.size} Complete Video Renders (40-50s)",
                    color = SleekTextSecondary,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = onNewProject,
                colors = ButtonDefaults.buttonColors(containerColor = SleekBluePrimary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create New", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Engine Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterEngine == null,
                onClick = { filterEngine = null },
                label = { Text("All Renders (${projects.size})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SleekBluePrimary,
                    selectedLabelColor = Color.White,
                    containerColor = SleekCard,
                    labelColor = SleekTextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = filterEngine == null,
                    borderColor = SleekBorder,
                    selectedBorderColor = SleekBluePrimary
                )
            )

            VideoEngine.values().take(3).forEach { eng ->
                FilterChip(
                    selected = filterEngine == eng,
                    onClick = { filterEngine = if (filterEngine == eng) null else eng },
                    label = { Text(eng.displayName, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = eng.badgeColor,
                        selectedLabelColor = Color.White,
                        containerColor = SleekCard,
                        labelColor = SleekTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = filterEngine == eng,
                        borderColor = SleekBorder,
                        selectedBorderColor = eng.badgeColor
                    )
                )
            }
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MovieCreation,
                        contentDescription = null,
                        tint = SleekTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No videos generated yet",
                        color = SleekTextSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Enter any prompt to generate a 40-50s video with Sora, Zero V3, or Nano V2!",
                        color = SleekTextMuted,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredList, key = { it.id }) { project ->
                    GalleryVideoCard(
                        project = project,
                        onSelect = { onSelectProject(project) },
                        onDelete = { onDeleteProject(project.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun GalleryVideoCard(
    project: VideoProject,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val drawableRes = when (project.engine) {
        VideoEngine.ZERO_V3 -> R.drawable.img_zero_demo
        VideoEngine.NANO_V2 -> R.drawable.img_nano_demo
        else -> R.drawable.img_sora_demo
    }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.US) }
    val formattedDate = remember(project.createdAt) {
        dateFormatter.format(Date(project.createdAt))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SleekBorder, RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .testTag("gallery_card_${project.id}"),
        colors = CardDefaults.cardColors(containerColor = SleekCardSecondary)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Thumbnail Image with Overlay Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Image(
                    painter = painterResource(id = drawableRes),
                    contentDescription = project.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Play Button Center Overlay
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SleekBluePrimary.copy(alpha = 0.9f))
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play video",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Top Left Engine Badge
                Surface(
                    color = project.engine.badgeColor.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = project.engine.displayName,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                // Top Right Duration Badge
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "${project.targetDurationSeconds}s",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Project Details Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = project.title,
                        color = SleekTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = SleekTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = project.prompt,
                    color = SleekTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = MaleVoiceDeep,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "${project.maleVoice.speakerName} (Male)",
                            color = MaleVoiceDeep,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = formattedDate,
                        color = SleekTextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
