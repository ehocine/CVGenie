package com.hocel.cvgenie.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private var _gettingData = MutableStateFlow(LoadingState.IDLE)
    var gettingData: StateFlow<LoadingState> = _gettingData

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
    val selectedCVDocumentModel: State<CV> = _selectedCVDocument

    fun getUserInfo(context: Context) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser
        val data = currentUser?.let { db.collection(FIRESTORE_USERS_DATABASE).document(it.uid) }
        if (hasInternetConnection(context)) {
            if (currentUser != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        _gettingData.emit(LoadingState.LOADING)
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
                        _gettingData.emit(LoadingState.LOADED)
                    } catch (e: Exception) {
                        _gettingData.emit(LoadingState.ERROR)
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

    fun addOrRemoveScannedText(
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

    fun generateCVDocument(cvInfo: CV) {

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

    fun setStillWorking(stillworking: Boolean) {
        _stillWorking.value = stillworking
    }

    fun setYearOfEnding(yearOfEnding: String) {
        _yearOfEnding.value = yearOfEnding
    }

    fun resetCVFields() {
        resetEducationFields()
        resetExperienceFields()
        _firstName.value = ""
        _lastName.value = ""
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

    fun selectCVDocument(CVDocument: CV) {
        _selectedCVDocument.value = CVDocument
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