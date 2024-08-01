package com.sg8.storage.util

import com.cobblemon.mod.common.util.fromJson
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.*
import java.nio.file.Path
import java.util.*

object StoreUtil {

    fun getFile(savePath: Path, dirName: String, subDirName: String?): File {
        val file: File = if (subDirName != null) {
            savePath.resolve("$dirName/$subDirName/").toFile()
        } else {
            savePath.resolve("$dirName/").toFile()
        }
        return file
    }

    private fun getJsonOrCreateFile(file: File, gson: Gson): JsonObject? {
        try {
            file.parentFile.mkdirs()
            file.createNewFile()
        } catch (e: IOException) {
            throw IOException(e)
        }

        val reader: BufferedReader
        try {
            reader = BufferedReader(FileReader(file))
        } catch (e: FileNotFoundException) {
            throw RuntimeException(e)
        }

        try {
            val json = gson.fromJson<JsonObject>(reader)
            reader.close()
            return json
        } catch (_: Exception){
            return null
        }
    }

    fun getUuidKey(root: Path, keyDir: File, name: String, key: String, gson: Gson): UUID {
        val keyPath = root.resolve(getFileStringJson(rootFile = keyDir, name = name))
        val file = keyPath.toFile()
        val json = getJsonOrCreateFile(file = file, gson = gson)

        return when {
            json != null && json.has(key) -> {
                UUID.fromString(json.get(key).toString().filterNot { it == '"' })
            }
            json != null -> writeNewUuid(file = file, key = key, json = json)
            else -> writeNewUuid(file = file, key = key, json = JsonObject())
        }
    }

    private fun writeNewUuid(file: File, key: String, json: JsonObject): UUID {
        val writer: PrintWriter
        try {
            writer = PrintWriter(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            throw Exception(e)
        }

        val uuid = UUID.randomUUID()
        json.addProperty(key, (uuid.toString()))
        writer.write(json.toString())
        writer.flush()
        writer.close()
        return uuid
    }

    private fun getFileStringJson(rootFile: File, name: String): String {
        return (getFileString(rootFile = rootFile, name = name) + ".json")
    }

    private fun getFileString(rootFile: File, name: String): String {
        var fileString = rootFile.toString()
        if (!fileString.endsWith(('/'))) {
            fileString += '/'
        }
        return (fileString + name)
    }

}
