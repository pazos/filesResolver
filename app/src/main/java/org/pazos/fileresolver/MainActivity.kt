package org.pazos.fileresolver

/* Sample activity that handles action.MAIN and action.VIEW intents.
 *
 * It tries to resolve content from local file providers, if the content served is a local file.
 * If the file is resolved then enable a button to open that file with another activities.
 */

import java.io.File

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var filePath: String? = null
    private var kindOfIntent: String? = null
    private var shouldStartActivity: Boolean = false

    private lateinit var userMessage: TextView
    private lateinit var openWithButton: Button
    private lateinit var filePathInfo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userMessage = findViewById(R.id.filePath)
        openWithButton = findViewById(R.id.startActivityButton)
        openWithButton.isEnabled = false
    }

    override fun onPause() {
        super.onPause()
        filePath = null
        kindOfIntent = null
        shouldStartActivity = false
        openWithButton.isEnabled = false
    }

    override fun onResume() {
        super.onResume()
        kindOfIntent = intent.scheme
        when (kindOfIntent) {
            "file" -> {
                filePath = intent.data?.path
            }
            "content" -> {
                intent.data?.let { fileProviderUri ->
                    val fd = this.contentResolver.openFileDescriptor(fileProviderUri, "r")
                    filePath = FileUtils.getFileDescriptorPath(fd)
                }
                // ?: TODO: show how to copy the stream to a rw path and get the file from there if filePath is still null
            }
            else -> {
                filePath = null
            }
        }
        // check if filePath is a readable file and keep info
        filePathInfo = filePath?.let { inputPath ->
            val inputFile: File? = File(inputPath)
            inputFile?.let { realFile ->
                if (realFile.canRead()) {
                    // file resolved: read permission GRANTED
                    shouldStartActivity = true
                    "file $realFile resolved from $kindOfIntent uri"
                } else {
                    shouldStartActivity = true
                    // file resolved: read permission DENIED
                    "file $realFile resolved from $kindOfIntent uri, no read permission"
                }
                // file not resolved: NO_FILE,
            } ?: "unable to resolve intent, is this a local file?"
        // file not resolved: NO_INTENT
        } ?: "activity called without intent"

        // update message with gathered info
        userMessage.text = filePathInfo

        // open w/ other activities handling and uri with file scheme, using original mime
        if (shouldStartActivity) {
            startActivityButton.setOnClickListener {
                val fileView = Intent(Intent.ACTION_VIEW)
                fileView.setDataAndType(Uri.fromFile(File(filePath!!)), intent.type!!)
                try {
                    startActivity(fileView)
                } catch (ignore: ActivityNotFoundException) {

                }
            }
            startActivityButton.isEnabled = true
        }
    }
}
