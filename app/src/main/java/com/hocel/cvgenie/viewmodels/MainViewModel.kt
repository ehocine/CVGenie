package com.hocel.cvgenie.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hocel.cvgenie.data.CV
import com.hocel.cvgenie.data.Education
import com.hocel.cvgenie.data.Experience
import com.hocel.cvgenie.data.User
import com.hocel.cvgenie.navigation.Screens
import com.hocel.cvgenie.utils.*
import com.hocel.cvgenie.utils.Constants.FIRESTORE_USERS_DATABASE
import com.hocel.cvgenie.utils.Constants.LIST_OF_CVs
import com.hocel.cvgenie.utils.Constants.auth
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private var _userInfo: MutableStateFlow<User> = MutableStateFlow(User())
    var userInfo = _userInfo.asStateFlow()

    private var _gettingUserDataState = MutableStateFlow(LoadingState.IDLE)
    var gettingUserDataState: StateFlow<LoadingState> = _gettingUserDataState

    private var _sheetStateContent: MutableStateFlow<SheetContentState> =
        MutableStateFlow(SheetContentState.ADD)
    val sheetStateContent: StateFlow<SheetContentState> = _sheetStateContent

    private var _localImageUri: MutableState<Uri> = mutableStateOf(Uri.parse(""))
    val localImageUri: State<Uri> = _localImageUri

    private var _firstName: MutableState<String> = mutableStateOf("")
    val firstName: State<String> = _firstName

    private var _lastName: MutableState<String> = mutableStateOf("")
    val lastName: State<String> = _lastName

    private var _dateOfBirth: MutableState<String> = mutableStateOf("")
    val dateOfBirth: State<String> = _dateOfBirth

    private var _placeOfBirth: MutableState<String> = mutableStateOf("")
    val placeOfBirth: State<String> = _placeOfBirth

    private var _emailAddress: MutableState<String> = mutableStateOf("")
    val emailAddress: State<String> = _emailAddress

    private var _personalAddress: MutableState<String> = mutableStateOf("")
    val personalAddress: State<String> = _personalAddress

    private var _phoneNumber: MutableState<String> = mutableStateOf("")
    val phoneNumber: State<String> = _phoneNumber

    private var _educationList = mutableStateListOf<Education>()
    val educationList: SnapshotStateList<Education> = _educationList

    private var _school: MutableState<String> = mutableStateOf("")
    val school: State<String> = _school

    private var _field: MutableState<String> = mutableStateOf("")
    val field: State<String> = _field

    private var _diploma: MutableState<String> = mutableStateOf("")
    val diploma: State<String> = _diploma

    private var _yearOfDiploma: MutableState<String> = mutableStateOf("")
    val yearOfDiploma: State<String> = _yearOfDiploma

    private var _experienceList = mutableStateListOf<Experience>()
    val experienceList: SnapshotStateList<Experience> = _experienceList

    private var _employer: MutableState<String> = mutableStateOf("")
    val employer: State<String> = _employer

    private var _position: MutableState<String> = mutableStateOf("")
    val position: State<String> = _position

    private var _yearOfStart: MutableState<String> = mutableStateOf("")
    val yearOfStart: State<String> = _yearOfStart

    private var _stillWorking: MutableState<Boolean> = mutableStateOf(false)
    val stillWorking: State<Boolean> = _stillWorking

    private var _yearOfEnding: MutableState<String> = mutableStateOf("")
    val yearOfEnding: State<String> = _yearOfEnding

    private var _selectedCVDocument: MutableState<CV> = mutableStateOf(CV())
    val selectedCVDocument: State<CV> = _selectedCVDocument

    private var _creatingCVState = MutableStateFlow(LoadingState.IDLE)
    var creatingCVState: StateFlow<LoadingState> = _creatingCVState

    private var _cvDocumentAction: MutableState<CVDocumentAction> =
        mutableStateOf(CVDocumentAction.NONE)
    val cvDocumentAction: State<CVDocumentAction> = _cvDocumentAction

    private var _selectedEducationItem: MutableState<Education> = mutableStateOf(Education())
    val selectedEducationItem: State<Education> = _selectedEducationItem

    private var _selectedExperienceItem: MutableState<Experience> = mutableStateOf(Experience())
    val selectedExperienceItem: State<Experience> = _selectedExperienceItem

    fun getUserInfo(context: Context) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser
        val data = currentUser?.let { db.collection(FIRESTORE_USERS_DATABASE).document(it.uid) }
        if (hasInternetConnection(context)) {
            if (currentUser != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        _gettingUserDataState.emit(LoadingState.LOADING)
                        data?.addSnapshotListener { value, error ->
                            if (error != null) {
                                return@addSnapshotListener
                            }
                            if (value != null && value.exists()) {
                                _userInfo.value =
                                    value.toObject(User::class.java) ?: User()
                            } else {
                                "An error occurred".toast(context, Toast.LENGTH_SHORT)
                            }
                        }
                        _gettingUserDataState.emit(LoadingState.LOADED)
                    } catch (e: Exception) {
                        _gettingUserDataState.emit(LoadingState.ERROR)
                        withContext(Dispatchers.Main) {
                            "An error occurred".toast(context, Toast.LENGTH_SHORT)
                        }
                    }
                }
            }
        } else {
            "Device is not connected to the internet".toast(context, Toast.LENGTH_SHORT)
        }
    }

    fun addOrRemoveCVDocument(
        context: Context,
        action: AddOrRemoveAction,
        CVDocument: CV,
        onAddSuccess: () -> Unit,
        onRemoveSuccess: () -> Unit
    ) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser
        val data = currentUser?.let { db.collection(FIRESTORE_USERS_DATABASE).document(it.uid) }
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    when (action) {
                        AddOrRemoveAction.ADD -> {
                            data?.update(
                                LIST_OF_CVs,
                                FieldValue.arrayUnion(CVDocument)
                            )?.addOnSuccessListener {
                                onAddSuccess()

                            }?.addOnFailureListener {
                                "Something went wrong: $it".toast(context, Toast.LENGTH_SHORT)
                            }
                        }
                        AddOrRemoveAction.REMOVE -> {
                            data?.update(
                                LIST_OF_CVs,
                                FieldValue.arrayRemove(CVDocument)
                            )?.addOnSuccessListener {
                                onRemoveSuccess()
                            }?.addOnFailureListener {
                                "Something went wrong: $it".toast(context, Toast.LENGTH_SHORT)
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        withContext(Dispatchers.Main) {
                            "An error occurred".toast(context, Toast.LENGTH_SHORT)
                        }
                    }
                }
            }
        } else {
            "Device is not connected to the internet".toast(context, Toast.LENGTH_SHORT)
        }
    }

    fun generateCVDocument(cvInfo: CV, onCvCreated: (fileUri: Uri) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _creatingCVState.emit(LoadingState.LOADING)
            try {
                val pdfPath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString()
                val fileName = convertTimeStampToDate(System.currentTimeMillis())
                val file = File(pdfPath, "$fileName.pdf")
                FileOutputStream(file)

                val writer = PdfWriter(file)
                val pdfDocument = PdfDocument(writer)
                val document = Document(pdfDocument)

                pdfDocument.defaultPageSize

                val cvTitle: Paragraph = Paragraph("Curriculum Vitae").setBold().setFontSize(18F)
                    .setTextAlignment(TextAlignment.CENTER)

                val personalInfo: Paragraph =
                    Paragraph("Personal information").setBold().setFontSize(16F)
                        .setTextAlignment(TextAlignment.LEFT)

                val personalInfoTableWidth = floatArrayOf(140f, 140f, 140f)
                val personalInfoTable = Table(personalInfoTableWidth)
                personalInfoTable.setHorizontalAlignment(HorizontalAlignment.CENTER)

                val bitmap =
                    application.contentResolver.openInputStream(_localImageUri.value)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                val imageStream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, imageStream)
                val bitmapData = imageStream.toByteArray()
                val image = Image(ImageDataFactory.create(bitmapData))
                image.setHorizontalAlignment(HorizontalAlignment.RIGHT)
                image.setHeight(100f)
                image.setWidth(80f)

                personalInfoTable.addCell(
                    Cell().add(Paragraph("First name:")).setBorder(Border.NO_BORDER)
                )
                personalInfoTable.addCell(
                    Cell().add(Paragraph(cvInfo.firstName)).setBorder(Border.NO_BORDER)
                )
                personalInfoTable.addCell(Cell(6, 1).add(image).setBorder(Border.NO_BORDER))

                personalInfoTable.addCell(
                    Cell().add(Paragraph("Last name:")).setBorder(Border.NO_BORDER)
                )
                personalInfoTable.addCell(
                    Cell().add(Paragraph(cvInfo.lastName)).setBorder(Border.NO_BORDER)
                )

                personalInfoTable.addCell(
                    Cell().add(Paragraph("Date and place of birth:")).setBorder(Border.NO_BORDER)
                )
                personalInfoTable.addCell(
                    Cell().add(Paragraph("${cvInfo.dateOfBirth} in ${cvInfo.placeOfBirth}"))
                        .setBorder(Border.NO_BORDER)
                )

                personalInfoTable.addCell(
                    Cell().add(Paragraph("Phone number:")).setBorder(Border.NO_BORDER)
                )
                personalInfoTable.addCell(
                    Cell().add(Paragraph(cvInfo.phoneNumber)).setBorder(Border.NO_BORDER)
                )

                personalInfoTable.addCell(
                    Cell().add(Paragraph("Email:")).setBorder(Border.NO_BORDER)
                )
                personalInfoTable.addCell(
                    Cell().add(Paragraph(cvInfo.email)).setBorder(Border.NO_BORDER)
                )

                personalInfoTable.addCell(
                    Cell().add(Paragraph("Address:")).setBorder(Border.NO_BORDER)
                )
                personalInfoTable.addCell(
                    Cell().add(Paragraph(cvInfo.address)).setBorder(Border.NO_BORDER)
                )

                val educationInfo: Paragraph =
                    Paragraph("Education information").setBold().setFontSize(16F)
                        .setTextAlignment(TextAlignment.LEFT)

                val educationInfoTableWidth = floatArrayOf(300f)
                val educationInfoTable = Table(educationInfoTableWidth)
                educationInfoTable.setHorizontalAlignment(HorizontalAlignment.LEFT)

                cvInfo.education.forEach {
                    educationInfoTable.addCell(
                        Cell().add(Paragraph("• ${it.diploma} in ${it.field}, ${it.yearOfDiploma}, ${it.school}."))
                            .setBorder(Border.NO_BORDER)
                    )
                }

                val experienceInfo: Paragraph =
                    Paragraph("Experience information").setBold().setFontSize(16F)
                        .setTextAlignment(TextAlignment.LEFT)

                val experienceInfoTableWidth = floatArrayOf(300f)
                val experienceInfoTable = Table(experienceInfoTableWidth)
                experienceInfoTable.setHorizontalAlignment(HorizontalAlignment.LEFT)

                cvInfo.experience.forEach {
                    experienceInfoTable.addCell(
                        Cell().add(Paragraph("• ${it.position} at ${it.employer}, from ${it.fromYear} - ${if (it.stillWorking) "still working" else "to ${it.endYear}"}"))
                            .setBorder(Border.NO_BORDER)
                    )
                }

                document.add(cvTitle)
                document.add(Paragraph("\n"))
                document.add(personalInfo)
                document.add(Paragraph("\n"))
                document.add(personalInfoTable)
                document.add(Paragraph("\n"))
                if (cvInfo.education.isNotEmpty()) document.add(educationInfo)
                document.add(Paragraph("\n"))
                document.add(educationInfoTable)
                document.add(Paragraph("\n"))
                if (cvInfo.experience.isNotEmpty()) document.add(experienceInfo)
                document.add(Paragraph("\n"))
                document.add(experienceInfoTable)

                document.close()

                val uri: Uri = Uri.fromFile(file)
                onCvCreated(uri)
                withContext(Dispatchers.Main) {
                    "Your CV has been created".toast(application, Toast.LENGTH_SHORT)
                }
                _creatingCVState.emit(LoadingState.LOADED)
            } catch (e: Exception) {
                _creatingCVState.emit(LoadingState.ERROR)
            }
        }
    }

    fun setSheetStateContent(sheetStateContent: SheetContentState) {
        _sheetStateContent.value = sheetStateContent
    }

    fun setLocalImageUri(imageUri: Uri?) {
        if (imageUri != null) {
            _localImageUri.value = imageUri
        }
    }

    fun setFirstName(firstName: String) {
        _firstName.value = firstName
    }

    fun setLastName(lastName: String) {
        _lastName.value = lastName
    }

    fun setDateOfBirth(dateOfBirth: LocalDate) {
        _dateOfBirth.value = dateOfBirth.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
    }

    fun setPlaceOfBirth(placeOfBirth: String) {
        _placeOfBirth.value = placeOfBirth
    }

    fun setEmailAddress(emailAddress: String) {
        _emailAddress.value = emailAddress
    }

    fun setPersonalAddress(personalAddress: String) {
        _personalAddress.value = personalAddress
    }

    fun setPhoneNumber(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
    }

    fun addEducationItem(education: Education) {
        _educationList.add(education)
    }

    fun updateEducationItem(index: Int, education: Education) {
        _educationList[index] = education
    }

    fun removeEducationItem(education: Education) {
        _educationList.remove(education)
    }

    fun setSchool(school: String) {
        _school.value = school
    }

    fun setField(field: String) {
        _field.value = field
    }

    fun setDiploma(diploma: String) {
        _diploma.value = diploma
    }

    fun setYearOfDiploma(year: String) {
        _yearOfDiploma.value = year
    }

    fun addExperienceItem(experience: Experience) {
        _experienceList.add(experience)
    }

    fun updateExperienceItem(index: Int, experience: Experience) {
        _experienceList[index] = experience
    }

    fun removeExperienceItem(experience: Experience) {
        _experienceList.remove(experience)
    }

    fun setEmployer(employer: String) {
        _employer.value = employer
    }

    fun setPosition(position: String) {
        _position.value = position
    }

    fun setYearOfStarting(yearOfStarting: String) {
        _yearOfStart.value = yearOfStarting
    }

    fun setStillWorking(stillWorking: Boolean) {
        _stillWorking.value = stillWorking
    }

    fun setYearOfEnding(yearOfEnding: String) {
        _yearOfEnding.value = yearOfEnding
    }

    fun resetCVFields() {
        resetEducationFields()
        resetExperienceFields()
        _firstName.value = ""
        _lastName.value = ""
        _localImageUri.value = Uri.parse("")
        _dateOfBirth.value = ""
        _placeOfBirth.value = ""
        _emailAddress.value = ""
        _personalAddress.value = ""
        _phoneNumber.value = ""
        _educationList.clear()
        _experienceList.clear()
    }

    fun resetEducationFields() {
        _school.value = ""
        _field.value = ""
        _diploma.value = ""
        _yearOfDiploma.value = ""
    }

    fun resetExperienceFields() {
        _employer.value = ""
        _position.value = ""
        _yearOfStart.value = ""
        _stillWorking.value = false
        _yearOfEnding.value = ""
    }

    fun setCVDocumentToEdit(selectedCV: CV) {
        _firstName.value = selectedCV.firstName
        _lastName.value = selectedCV.lastName
        _dateOfBirth.value = selectedCV.dateOfBirth
        _placeOfBirth.value = selectedCV.placeOfBirth
        _emailAddress.value = selectedCV.email
        _phoneNumber.value = selectedCV.phoneNumber
        _personalAddress.value = selectedCV.address
        _localImageUri.value = Uri.parse("")
        selectedCV.education.forEach {
            _educationList.add(it)
        }
        selectedCV.experience.forEach {
            _experienceList.add(it)
        }
    }

    fun selectCVDocument(CVDocument: CV) {
        _selectedCVDocument.value = CVDocument
    }

    fun setSelectedEducationItem(education: Education) {
        setSchool(education.school)
        setField(education.field)
        setDiploma(education.diploma)
        setYearOfDiploma(education.yearOfDiploma)
        _selectedEducationItem.value = education
    }

    fun setSelectedExperienceItem(experience: Experience) {
        setEmployer(experience.employer)
        setPosition(experience.position)
        setYearOfStarting(experience.fromYear)
        setStillWorking(experience.stillWorking)
        setYearOfEnding(experience.endYear)
        _selectedExperienceItem.value = experience
    }

    fun setCreatingCVState(state: LoadingState) {
        viewModelScope.launch {
            _creatingCVState.emit(state)
        }
    }

    fun setCVDocumentAction(action: CVDocumentAction) {
        _cvDocumentAction.value = action
    }

    fun signOut(
        context: Context,
        navController: NavController
    ) {
        try {
            auth.signOut()
            "Successfully signed out".toast(context, Toast.LENGTH_SHORT)
            navController.navigate(Screens.Login.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }

            }
        } catch (e: Exception) {
            "An error occurred".toast(context, Toast.LENGTH_SHORT)
        }
    }
}