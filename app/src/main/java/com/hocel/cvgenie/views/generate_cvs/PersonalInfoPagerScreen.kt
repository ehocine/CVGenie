package com.hocel.cvgenie.views.generate_cvs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.hocel.cvgenie.R
import com.hocel.cvgenie.components.DatePicker
import com.hocel.cvgenie.components.InfoCVItem
import com.hocel.cvgenie.ui.theme.BackgroundColor
import com.hocel.cvgenie.ui.theme.TextColor
import com.hocel.cvgenie.ui.theme.blueText
import com.hocel.cvgenie.viewmodels.MainViewModel
import com.maxkeppeker.sheets.core.models.base.rememberSheetState

@Composable
fun PersonalInfoPagerScreen(mainViewModel: MainViewModel) {

    val imageWidth = 130.dp
    val imageHeight = 130.dp
    val imageContainerWidth = imageWidth + 8.dp
    val imageContainerHeight = imageHeight + 8.dp

    val scrollState = rememberScrollState()
    val calendarState = rememberSheetState()
    var pictureChanged by remember { mutableStateOf(false) }

    DatePicker(state = calendarState, onDateSelected = { date ->
        mainViewModel.setDateOfBirth(date)
    })

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            pictureChanged = true
            mainViewModel.setLocalImageUri(uri)
        } else {
            pictureChanged = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState)
            .background(color = MaterialTheme.colors.BackgroundColor)
    ) {

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .width(imageContainerWidth)
                    .height(imageContainerHeight),
                contentAlignment = Alignment.TopCenter
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .width(imageWidth)
                        .height(imageHeight)
                        .align(Alignment.BottomCenter)
                        .clip(CircleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(mainViewModel.localImageUri.value)
                        .crossfade(true)
                        .error(R.drawable.person)
                        .build(),
                    alignment = Alignment.BottomCenter,
                    contentDescription = "Image",
                ) {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Loading) {
                        Box(
                            modifier = Modifier.size(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colors.TextColor)
                        }
                    } else {
                        SubcomposeAsyncImageContent(
                            modifier = Modifier.clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(end = 4.dp, start = 4.dp)
                ) {
                    IconButton(
                        modifier = Modifier
                            .size(70.dp)
                            .align(Alignment.Center),
                        onClick = {
                            launcher.launch("image/*")
                        }) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            tint = blueText,
                            contentDescription = "Picture"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        InfoCVItem(
            title = "First name",
            value = mainViewModel.firstName.value,
            label = "First name",
            placeholder = "First name",
            onValueChanged = { mainViewModel.setFirstName(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoCVItem(
            title = "Last name",
            value = mainViewModel.lastName.value,
            label = "Last name",
            placeholder = "Last name",
            onValueChanged = { mainViewModel.setLastName(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoCVItem(
            title = "Date of birth",
            value = mainViewModel.dateOfBirth.value,
            label = "Date of birth",
            placeholder = "Date of birth",
            readOnly = true,
            onValueChanged = { },
            trailingIcon = {
                IconButton(onClick = {
                    calendarState.show()
                }) {
                    Icon(
                        imageVector = Icons.Default.EditCalendar,
                        contentDescription = "Calendar",
                        tint = MaterialTheme.colors.TextColor
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoCVItem(
            title = "Place of Birth",
            value = mainViewModel.placeOfBirth.value,
            label = "Place of Birth",
            placeholder = "Place of Birth",
            onValueChanged = { mainViewModel.setPlaceOfBirth(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoCVItem(
            title = "Personal address",
            value = mainViewModel.personalAddress.value,
            label = "Personal address",
            placeholder = "Personal address",
            singleLine = false,
            maxLines = 3,
            modifier = Modifier.height(100.dp),
            onValueChanged = { mainViewModel.setPersonalAddress(it) })

        Spacer(modifier = Modifier.height(16.dp))

        InfoCVItem(
            title = "Email address",
            value = mainViewModel.emailAddress.value,
            label = "Email address",
            placeholder = "Email address",
            onValueChanged = { mainViewModel.setEmailAddress(it) })

        Spacer(modifier = Modifier.height(16.dp))

        InfoCVItem(
            title = "Phone number",
            value = mainViewModel.phoneNumber.value,
            label = "Phone number",
            placeholder = "Phone number",
            onValueChanged = { mainViewModel.setPhoneNumber(it) })
        Spacer(modifier = Modifier.height(24.dp))
    }
}