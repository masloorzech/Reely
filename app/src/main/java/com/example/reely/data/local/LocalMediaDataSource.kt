package com.example.reely.data.local

interface LocalMediaDataSource {
    suspend fun copyImageToAppStorage(sourceUri: String, directoryName: String): String
    suspend fun deleteFile(path: String?)
}