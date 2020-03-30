package com.example.ravenous.utils

import android.app.Application
import java.io.File

class FileHelper {
    companion object {
        fun readTextFile(app: Application, fileName: String): String? {
            val file = File(app.cacheDir, fileName)
            return if (file.exists()) file.readText() else null
        }

        fun saveTextToFile(app: Application, fileName: String, json: String?) {
            val file = File(app.cacheDir, fileName)
            file.writeText(json ?: "", Charsets.UTF_8)
        }
    }
}