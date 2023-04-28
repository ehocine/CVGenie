package com.hocel.cvgenie.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
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

    private var _jobTitle: MutableState<String> = mutableStateOf("")
    val jobTitle: State<String> = _jobTitle

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

    private var _skills: MutableState<String> = mutableStateOf("")
    val skills: State<String> = _skills

    private var _languages: MutableState<String> = mutableStateOf("")
    val languages: State<String> = _languages

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

    private var _experienceComment: MutableState<String> = mutableStateOf("")
    val experienceComment: State<String> = _experienceComment

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

    fun generateV2(context: Context, cvInfo: CV, onCvCreated: (Uri) -> Unit) {

        val pdfPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString()
        val fileName = convertTimeStampToDate(System.currentTimeMillis())
        val file = File(pdfPath, "$fileName.pdf")
        FileOutputStream(file)
        val htmlContent = StringBuilder()

        htmlContent.append(
            java.lang.String.format(
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<title>Resume</title>\n" +
                        "<meta charset=UTF-8>\n" +
                        "<link rel=\"shortcut icon\" href=https://ssl.gstatic.com/docs/documents/images/kix-favicon6.ico>\n" +
                        "<style type=text/css>body{font-family:arial,sans,sans-serif;margin:0}iframe{border:0;frameborder:0;height:100%%;width:100%%}#header,#footer{background:#f0f0f0;padding:10px 10px}#header{border-bottom:1px #ccc solid}#footer{border-top:1px #ccc solid;border-bottom:1px #ccc solid;font-size:13}#contents{margin:6px}.dash{padding:0 6px}</style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div id=contents>\n" +
                        "<style type=text/css>@import url('https://themes.googleusercontent.com/fonts/css?kit=xTOoZr6X-i3kNg7pYrzMsnEzyYBuwf3lO_Sc3Mw9RUVbV0WvE1cEyAoIq5yYZlSc');ol{margin:0;padding:0}table td,table th{padding:0}.c26{border-right-style:solid;padding:3.6pt 3.6pt 3.6pt 3.6pt;border-bottom-color:#fff;border-top-width:0;border-right-width:0;border-left-color:#fff;vertical-align:top;border-right-color:#fff;border-left-width:0;border-top-style:solid;border-left-style:solid;border-bottom-width:0;width:176.3pt;border-top-color:#fff;border-bottom-style:solid}.c4{border-right-style:solid;padding:5pt 5pt 5pt 5pt;border-bottom-color:#fff;border-top-width:0;border-right-width:0;border-left-color:#fff;vertical-align:top;border-right-color:#fff;border-left-width:0;border-top-style:solid;border-left-style:solid;border-bottom-width:0;width:327.7pt;border-top-color:#fff;border-bottom-style:solid}.c16{color:#000;font-weight:700;text-decoration:none;vertical-align:baseline;font-size:12pt;font-family:\"Raleway\";font-style:normal}.c7{color:#000;font-weight:400;text-decoration:none;vertical-align:baseline;font-size:10pt;font-family:\"Lato\";font-style:normal}.c13{color:#000;font-weight:700;text-decoration:none;vertical-align:baseline;font-size:10pt;font-family:\"Lato\";font-style:normal}.c1{color:#666;font-weight:400;text-decoration:none;vertical-align:baseline;font-size:9pt;font-family:\"Lato\";font-style:normal}.c19{color:#000;font-weight:400;text-decoration:none;vertical-align:baseline;font-size:6pt;font-family:\"Lato\";font-style:normal}.c20{color:#f2511b;font-weight:700;text-decoration:none;vertical-align:baseline;font-size:16pt;font-family:\"Raleway\";font-style:normal}.c6{padding-top:0;padding-bottom:0;line-height:1.0;text-align:left}.c32{padding-top:5pt;padding-bottom:0;line-height:1.15;text-align:left}.c0{padding-top:10pt;padding-bottom:0;line-height:1.0;text-align:left}.c22{padding-top:5pt;padding-bottom:0;line-height:1.0;text-align:left}.c10{color:#d44500;text-decoration:none;vertical-align:baseline;font-style:normal}.c2{padding-top:0;padding-bottom:0;line-height:1.15;text-align:left}.c33{padding-top:3pt;padding-bottom:0;line-height:1.0;text-align:left}.c9{padding-top:4pt;padding-bottom:0;line-height:1.15;text-align:left}.c23{border-spacing:0;border-collapse:collapse;margin:0 auto}.c30{color:#000;text-decoration:none;vertical-align:baseline;font-style:normal}.c3{padding-top:6pt;padding-bottom:0;line-height:1.15;text-align:left}.c14{padding-top:16pt;padding-bottom:0;line-height:1.15;text-align:left}.c28{padding-top:6pt;padding-bottom:0;line-height:1.0;text-align:left}.c18{font-size:9pt;font-family:\"Lato\";font-weight:400}.c24{font-size:14pt;font-family:\"Lato\";font-weight:700}.c8{font-size:10pt;font-family:\"Lato\";font-weight:400}.c5{font-size:11pt;font-family:\"Lato\";font-weight:400}.c31{background-color:#fff;max-width:504pt;padding:36pt 54pt 36pt 54pt}.c35{font-weight:700;font-size:24pt;font-family:\"Raleway\"}.c11{orphans:2;widows:2;height:11pt}.c21{height:auto}.c15{height:auto}.c27{height:auto}.c34{height:auto}.c29{height:auto}.c25{font-size:10pt}.c12{page-break-after:avoid}.c17{height:265pt}.title{padding-top:6pt;color:#000;font-weight:700;font-size:24pt;padding-bottom:0;font-family:\"Raleway\";line-height:1.0;page-break-after:avoid;orphans:2;widows:2;text-align:left}.subtitle{padding-top:3pt;color:#f2511b;font-weight:700;font-size:16pt;padding-bottom:0;font-family:\"Raleway\";line-height:1.0;page-break-after:avoid;orphans:2;widows:2;text-align:left}li{color:#000;font-size:11pt;font-family:\"Lato\"}p{margin:0;color:#000;font-size:11pt;font-family:\"Lato\"}h1{padding-top:4pt;color:#000;font-weight:700;font-size:12pt;padding-bottom:0;font-family:\"Raleway\";line-height:1.15;page-break-after:avoid;orphans:2;widows:2;text-align:left}h2{padding-top:6pt;color:#000;font-weight:700;font-size:11pt;padding-bottom:0;font-family:\"Lato\";line-height:1.15;page-break-after:avoid;orphans:2;widows:2;text-align:left}h3{padding-top:6pt;color:#666;font-size:9pt;padding-bottom:0;font-family:\"Lato\";line-height:1.15;page-break-after:avoid;orphans:2;widows:2;text-align:left}h4{padding-top:8pt;-webkit-text-decoration-skip:none;color:#666;text-decoration:underline;font-size:11pt;padding-bottom:0;line-height:1.15;page-break-after:avoid;text-decoration-skip-ink:none;font-family:\"Trebuchet MS\";orphans:2;widows:2;text-align:left}h5{padding-top:8pt;color:#666;font-size:11pt;padding-bottom:0;font-family:\"Trebuchet MS\";line-height:1.15;page-break-after:avoid;orphans:2;widows:2;text-align:left}h6{padding-top:8pt;color:#666;font-size:11pt;padding-bottom:0;font-family:\"Trebuchet MS\";line-height:1.15;page-break-after:avoid;font-style:italic;orphans:2;widows:2;text-align:left}</style>\n" +
                        "<p class=\"c2 c29\"><span class=c19></span></p>\n" +
                        "<a id=t.b7144d62fc47a2bfcf177a3c3dd72df0e868051e></a>\n" +
                        "<a id=t.0></a>\n" +
                        "<table class=c23>\n" +
                        "            <tbody>\n" +
                        "                <tr class=\"c21\">\n" +
                        "                    <td class=\"c26\" colspan=\"1\" rowspan=\"1\">\n" +
                        "                        <p class=\"c6\"><span style=\"overflow: hidden; display: inline-block; margin: 0.00px 0.00px; border: 0.00px solid #000000; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px);\"><img alt=\"\" src=\"%s\" style=\"width: 132px; height: 170px; margin-left: 0.00px; margin-top: 0.00px; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px);\"></span></p>\n" +
                        "                    </td>\n" +
                        "                    <td class=\"c4\" colspan=\"1\" rowspan=\"1\">\n" +
                        "                        <p class=\"c6 c12 title\" id=\"h.4prkjmzco10w\"><span>%s</span></p>\n" +
                        "                        <p class=\"c33 subtitle\" id=\"h.o2iwx3vdck7p\"><span class=\"c20\">%s</span></p>\n" +
                        "                        <p class=\"c6 c0\"><span class=\"c7\">%s</span></p>\n" +
                        "                        <p class=\"c6 c0\"><span class=\"c7\">%s</span></p>\n" +
                        "                        <p class=\"c0\"><span class=\"c10 c8\">%s</span></p>\n" +
                        "                        <p class=\"c6 c0\"><span class=\"c8 c10\">%s</span></p>\n" +
                        "                    </td>\n" +
                        "                </tr>",
                cvInfo.imageUri,
                "${cvInfo.firstName} ${cvInfo.lastName}",
                cvInfo.jobTitle,
                "${cvInfo.dateOfBirth} in ${cvInfo.placeOfBirth}",
                cvInfo.address,
                cvInfo.phoneNumber,
                cvInfo.email
            )
        )

        if (cvInfo.skills.isNotEmpty()) {
            htmlContent.append(
                java.lang.String.format(
                    (("\n" +
                            "                <tr class=\"c27\">\n" +
                            "                    <td class=\"c26\" colspan=\"1\" rowspan=\"1\">\n" +
                            "                        <p class=\"c6\"><span class=\"c24\">ㅡ</span></p>\n" +
                            "                        <h1 class=\"c9\" id=\"h.61e3cm1p1fln\"><span class=\"c16\">" +
                            "Skills") + "</span></h1></td>\n" +
                            "                    <td class=\"c4\" colspan=\"1\" rowspan=\"1\">\n" +
                            "                        <p class=\"c2\"><span style=\"overflow: hidden; display: inline-block; margin: 0.00px 0.00px; border: 0.00px solid #000000; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px); width: 418.00px; height: 2.67px;\"><img alt=\"\" src=\"https://lh3.googleusercontent.com/n8bZfGajkthDbPpbjeiRJ4w7rNUmj1iFxdZKCHUOVnfH9FgHVt5EBo3vOYIIoE3augYQ_DCZJUzdlStyJ5RaldVrSG36sTE0CjIot2qaiJ3YRyr2i87bt9Y9d0ngdseS9PpG0HzM\" style=\"width: 418.00px; height: 2.67px; margin-left: 0.00px; margin-top: 0.00px; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px);\" title=\"horizontal line\"></span></p>\n" +
                            "                        <p class=\"c3\"><span class=\"c7\">%s</span></p>\n" +
                            "                    </td>\n" +
                            "                </tr>"), cvInfo.skills
                )
            )
        }
        if (cvInfo.languages.isNotEmpty()) {
            htmlContent.append(
                java.lang.String.format(
                    (("\n" +
                            "                <tr class=\"c27\">\n" +
                            "                    <td class=\"c26\" colspan=\"1\" rowspan=\"1\">\n" +
                            "                        <p class=\"c6\"><span class=\"c24\">ㅡ</span></p>\n" +
                            "                        <h1 class=\"c9\" id=\"h.61e3cm1p1fln\"><span class=\"c16\">" +
                            "Languages") + "</span></h1></td>\n" +
                            "                    <td class=\"c4\" colspan=\"1\" rowspan=\"1\">\n" +
                            "                        <p class=\"c2\"><span style=\"overflow: hidden; display: inline-block; margin: 0.00px 0.00px; border: 0.00px solid #000000; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px); width: 418.00px; height: 2.67px;\"><img alt=\"\" src=\"https://lh3.googleusercontent.com/n8bZfGajkthDbPpbjeiRJ4w7rNUmj1iFxdZKCHUOVnfH9FgHVt5EBo3vOYIIoE3augYQ_DCZJUzdlStyJ5RaldVrSG36sTE0CjIot2qaiJ3YRyr2i87bt9Y9d0ngdseS9PpG0HzM\" style=\"width: 418.00px; height: 2.67px; margin-left: 0.00px; margin-top: 0.00px; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px);\" title=\"horizontal line\"></span></p>\n" +
                            "                        <p class=\"c3\"><span class=\"c7\">%s</span></p>\n" +
                            "                    </td>\n" +
                            "                </tr>"), cvInfo.languages
                )
            )
        }
        if (cvInfo.education.isNotEmpty()) {
            htmlContent.append(
                (("\n" +
                        "                <tr class=\"c15\">\n" +
                        "                    <td class=\"c26\" colspan=\"1\" rowspan=\"1\">\n" +
                        "                        <p class=\"c6\"><span class=\"c24\">ㅡ</span></p>\n" +
                        "                        <h1 class=\"c9\" id=\"h.tk538brb1kdf\"><span class=\"c16\">" +
                        "Education") + "</span></h1></td>\n" +
                        "                    <td class=\"c4\" colspan=\"1\" rowspan=\"1\">\n")
            )

            var first = true
            cvInfo.education.forEach {
                htmlContent.append(
                    java.lang.String.format(
                        ("<h2 class=\"%s\" id=\"h.u3uy0857ab2n\"><span class=\"c5\">%s </span><span class=\"c30 c5\">/ %s</span></h2>\n" +
                                "                        <h3 class=\"c2\" id=\"h.re1qtuma0rpm\"><span class=\"c1\">%s</span></h3>\n" +
                                "                        <p class=\"c32\"><span class=\"c7\">%s</span></p>\n"),
                        if (first) "c3" else "c14",
                        it.school,
                        it.field,
                        it.diploma,
                        it.yearOfDiploma
                    )
                )
                first = false
            }
            htmlContent.append(
                "</td>\n" +
                        "                </tr>"
            )
        }
        if (cvInfo.experience.isNotEmpty()) {
            htmlContent.append(
                (("\n" +
                        "                <tr class=\"c15\">\n" +
                        "                    <td class=\"c26\" colspan=\"1\" rowspan=\"1\">\n" +
                        "                        <p class=\"c6\"><span class=\"c24\">ㅡ</span></p>\n" +
                        "                        <h1 class=\"c9\" id=\"h.tk538brb1kdf\"><span class=\"c16\">" +
                        "Experience") + "</span></h1></td>\n" +
                        "                    <td class=\"c4\" colspan=\"1\" rowspan=\"1\">\n")
            )
            var first = true

            cvInfo.experience.forEach {
                htmlContent.append(
                    java.lang.String.format(
                        ("<h2 class=\"%s\" id=\"h.u3uy0857ab2n\"><span class=\"c5\">%s </span><span class=\"c30 c5\">/ %s</span></h2>\n" +
                                "                        <h3 class=\"c2\" id=\"h.re1qtuma0rpm\"><span class=\"c1\">%s</span><span class=\"c1\">%s</span></h3>\n"
                                + "                        <p class=\"c32\"><span class=\"c7\">%s</span></p>\n"
                                ),
                        if (first) "c3" else "c14",
                        it.employer,
                        it.position,
                        it.fromYear,
                        if (it.stillWorking) " - still working" else " to ${it.endYear}",
                        it.comment
                    )
                )
                first = false
            }
            htmlContent.append(
                "</td>\n" +
                        "                </tr>"
            )
        }
        htmlContent.append(
            ("</tbody>\n" +
                    "</table>\n" +
                    "<p class=\"c2 c11\"><span class=\"c30 c5\"></span></p>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>")
        )
        val webView = WebView(application)
        webView.loadDataWithBaseURL(null, htmlContent.toString(), "text/html", "utf-8", null)

        val printManager =
            context.findActivity()?.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = webView.createPrintDocumentAdapter("CVDocument")
        val jobName = "CVDocument"
        printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
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

    fun setJobTitle(jobTitle: String) {
        _jobTitle.value = jobTitle
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

    fun setSkills(skills: String) {
        _skills.value = skills
    }

    fun setLanguages(languages: String) {
        _languages.value = languages
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

    fun setExperienceComment(experienceComment: String) {
        _experienceComment.value = experienceComment
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