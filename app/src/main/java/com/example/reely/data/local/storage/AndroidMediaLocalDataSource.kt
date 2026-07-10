package com.example.reely.data.local.storage

import android.content.Context
import android.net.Uri
import com.example.reely.data.local.LocalMediaDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class AndroidLocalMediaDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : LocalMediaDataSource {

    override suspend fun copyImageToAppStorage(
        sourceUri: String,
        directoryName: String
    ): String = withContext(Dispatchers.IO) {
        val uri = Uri.parse(sourceUri)

        val directory = File(context.filesDir, directoryName)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val destinationFile = File(
            directory,
            "image_${UUID.randomUUID()}.jpg"
        )

        context.contentResolver.openInputStream(uri)?.use { input ->
            destinationFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: error("Cannot open image input stream")

        destinationFile.absolutePath
    }

    override suspend fun deleteFile(path: String?) = withContext(Dispatchers.IO) {
        if (path.isNullOrBlank()) return@withContext

        val file = File(path)

        val isAppFile = file.absolutePath.startsWith(context.filesDir.absolutePath)

        if (file.exists() && isAppFile) {
            file.delete()
        }
    }
}