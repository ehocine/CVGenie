package com.hocel.cvgenie.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hocel.cvgenie.data.CV
import com.hocel.cvgenie.data.Education
import com.hocel.cvgenie.data.Experience
import com.hocel.cvgenie.ui.theme.BackgroundColor
import com.hocel.cvgenie.ui.theme.CardColor
import com.hocel.cvgenie.ui.theme.RedColor
import com.hocel.cvgenie.ui.theme.TextColor
import com.hocel.cvgenie.utils.convertTimeStampToDateAndTime
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun CVDocumentItem(
    CVDocument: CV,
    showImage: Boolean = true,
    onItemClicked: (CVDocument: CV) -> Unit,
    enableDeleteAction: Boolean = false,
    deleteScannedText: (CVDocument: CV) -> Unit
) {
    val pdfState = rememberVerticalPdfReaderState(
        resource = ResourceType.Remote(CVDocument.cvUrl),
        isZoomEnable = true
    )

    val delete = SwipeAction(
        onSwipe = {
            deleteScannedText(CVDocument)
        },
        icon = {
            Icon(
                modifier = Modifier.padding(16.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete icon",
                tint = Color.White
            )
        },
        background = RedColor
    )
    SwipeableActionsBox(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp)),
        swipeThreshold = 120.dp,
        endActions = if (enableDeleteAction) listOf(delete) else listOf()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = {
                    onItemClicked(CVDocument)
                }),
            elevation = 0.dp,
            backgroundColor = MaterialTheme.colors.CardColor
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 12.dp, bottom = 12.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showImage) {
                    Box(
                        modifier = Modifier
                            .height(100.dp)
                            .width(75.dp)
                            .weight(0.18f, fill = false)
                            .clip(RoundedCornerShape(12.dp)),
                    ) {
                        VerticalPDFReader(
                            state = pdfState,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = MaterialTheme.colors.BackgroundColor)
                        )
                        when (pdfState.isLoaded) {
                            false -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = MaterialTheme.colors.TextColor)
                                }
                            }
                            else -> Unit
                        }
                    }
                }

                Spacer(modifier = Modifier.width(5.dp))

                Column(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 12.dp)
                        .align(Alignment.CenterVertically)
                        .weight(0.6f)
                ) {

                    Text(
                        text = "${CVDocument.firstName} ${CVDocument.lastName}",
                        color = MaterialTheme.colors.TextColor,
                        style = MaterialTheme.typography.subtitle2,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        Text(
                            text = "CV created on",
                            color = MaterialTheme.colors.TextColor,
                            style = MaterialTheme.typography.subtitle2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .wrapContentSize(Alignment.BottomStart)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                tint = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.7f) else Color.Black.copy(
                                    alpha = 0.7f
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = convertTimeStampToDateAndTime(CVDocument.generatedTime),
                                modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                                color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.7f) else Color.Black.copy(
                                    alpha = 0.7f
                                ),
                                style = MaterialTheme.typography.subtitle2
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun EducationInfoItem(
    educationInfo: Education,
    onItemClicked: (educationInfo: Education) -> Unit,
    enableDeleteAction: Boolean = false,
    deleteEducationalInfo: (educationInfo: Education) -> Unit
) {
    val delete = SwipeAction(
        onSwipe = {
            deleteEducationalInfo(educationInfo)
        },
        icon = {
            Icon(
                modifier = Modifier.padding(16.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete icon",
                tint = Color.White
            )
        },
        background = RedColor
    )
    SwipeableActionsBox(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp)),
        swipeThreshold = 120.dp,
        endActions = if (enableDeleteAction) listOf(delete) else listOf()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = {
                    onItemClicked(educationInfo)
                }),
            elevation = 0.dp,
            backgroundColor = MaterialTheme.colors.CardColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 8.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "School: ${educationInfo.school}",
                    color = MaterialTheme.colors.TextColor,
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Field: ${educationInfo.field}",
                    color = MaterialTheme.colors.TextColor,
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Diploma: ${educationInfo.diploma}",
                    color = MaterialTheme.colors.TextColor,
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Year: ${educationInfo.yearOfDiploma}",
                    color = MaterialTheme.colors.TextColor,
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
fun ExperienceInfoItem(
    experienceInfo: Experience,
    onItemClicked: (experienceInfo: Experience) -> Unit,
    enableDeleteAction: Boolean = false,
    deleteEducationalInfo: (experienceInfo: Experience) -> Unit
) {
    val delete = SwipeAction(
        onSwipe = {
            deleteEducationalInfo(experienceInfo)
        },
        icon = {
            Icon(
                modifier = Modifier.padding(16.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete icon",
                tint = Color.White
            )
        },
        background = RedColor
    )
    SwipeableActionsBox(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp)),
        swipeThreshold = 120.dp,
        endActions = if (enableDeleteAction) listOf(delete) else listOf()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = {
                    onItemClicked(experienceInfo)
                }),
            elevation = 0.dp,
            backgroundColor = MaterialTheme.colors.CardColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 8.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Employer: ${experienceInfo.employer}",
                    color = MaterialTheme.colors.TextColor,
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Position: ${experienceInfo.position}",
                    color = MaterialTheme.colors.TextColor,
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Year of starting: ${experienceInfo.fromYear}",
                    color = MaterialTheme.colors.TextColor,
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (experienceInfo.stillWorking) "Still working" else "Year of ending: ${experienceInfo.endYear}",
                    color = MaterialTheme.colors.TextColor,
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}