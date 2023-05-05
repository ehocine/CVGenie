package android.print

import android.content.Context
import android.os.Handler
import android.os.ParcelFileDescriptor
import android.print.PrintAttributes.Resolution
import android.print.PrintDocumentAdapter.LayoutResultCallback
import android.print.PrintDocumentAdapter.WriteResultCallback
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import com.hocel.cvgenie.utils.convertTimeStampToDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File

class PdfConverter private constructor() {
    private var mContext: Context? = null
    private var mHtmlString: String? = null
    private var mPdfFile: File? = null
    private var mPdfPrintAttrs: PrintAttributes? = null
    private var mIsCurrentlyConverting = false
    private var mWebView: WebView? = null

    private fun exportPdf(onExportFinished: () -> Unit) {
        val handler = Handler(mContext!!.mainLooper)
        handler.post {
            mWebView = WebView(mContext!!)
            mWebView!!.loadDataWithBaseURL(
                null,
                mHtmlString!!,
                "text/html",
                "utf-8",
                null
            )
            mWebView!!.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    val documentAdapter =
                        mWebView!!.createPrintDocumentAdapter(convertTimeStampToDate(System.currentTimeMillis()))
                    documentAdapter.onLayout(
                        null,
                        pdfPrintAttrs, null, object : LayoutResultCallback() {}, null
                    )
                    documentAdapter.onWrite(arrayOf<PageRange>(PageRange.ALL_PAGES),
                        outputFileDescriptor, null, object : WriteResultCallback() {
                            override fun onWriteFinished(pages: Array<PageRange>) {
                                destroy()
                                Log.d(TAG, "Export finished")
                                onExportFinished()
                            }
                        }
                    )
                }
            }
        }
    }

    var pdfPrintAttrs: PrintAttributes?
        get() = if (mPdfPrintAttrs != null) mPdfPrintAttrs else defaultPrintAttrs
        set(printAttrs) {
            mPdfPrintAttrs = printAttrs
        }

    fun convert(
        context: Context?,
        htmlString: String?,
        file: File?,
        onExportFinished: () -> Unit
    ) {
        requireNotNull(context) { "context can't be null" }
        requireNotNull(htmlString) { "htmlString can't be null" }
        requireNotNull(file) { "file can't be null" }
        if (mIsCurrentlyConverting) return
        mContext = context
        mHtmlString = htmlString
        mPdfFile = file
        mIsCurrentlyConverting = true
        exportPdf(onExportFinished = { onExportFinished() })
    }

    private val outputFileDescriptor: ParcelFileDescriptor?
        get() {
            try {
                mPdfFile!!.createNewFile()
                return ParcelFileDescriptor.open(
                    mPdfFile,
                    ParcelFileDescriptor.MODE_TRUNCATE or ParcelFileDescriptor.MODE_READ_WRITE
                )
            } catch (e: Exception) {
                Log.d(TAG, "Failed to open ParcelFileDescriptor", e)
            }
            return null
        }
    private val defaultPrintAttrs: PrintAttributes
        get() = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(Resolution("RESOLUTION_ID", "RESOLUTION_ID", 600, 600))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

    private fun destroy() {
        mContext = null
        mHtmlString = null
        mPdfFile = null
        mPdfPrintAttrs = null
        mIsCurrentlyConverting = false
        mWebView = null
    }

    companion object {
        private const val TAG = "PdfConverter"
        private var sInstance: PdfConverter? = null

        @get:Synchronized
        val instance: PdfConverter?
            get() {
                if (sInstance == null) sInstance = PdfConverter()
                return sInstance
            }
    }
}