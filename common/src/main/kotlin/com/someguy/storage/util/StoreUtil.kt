package com.someguy.storage.util

import com.cobblemon.mod.common.util.fromJson
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.*
import java.nio.file.Path
import java.util.*

object StoreUtil
{
    fun getFile(savePath: Path,dirName: String,subDirName: String?): File
    {
        val file: File = if (subDirName != null) {
            savePath.resolve("$dirName/$subDirName/").toFile()
        } else {
            savePath.resolve("$dirName/").toFile()
        }
        return file
    }

    /**
     * @throws [IOException](1) from [File.createNewFile]
     * @throws [FileNotFoundException](2) from [FileReader] constructor
     * @return - target [JsonObject] if found
     * - null if [Exception] thrown from [Gson.fromJson]
     */
    fun getJsonOrCreateFile(file: File,gson: Gson): JsonObject?
    {
        try {
            file.parentFile.mkdirs()
            file.createNewFile()
        } catch (e: IOException) {
            throw IOException(e)
        }

        val br: BufferedReader
        try {
            br = BufferedReader(FileReader(file))
        } catch (e: FileNotFoundException) {
            throw RuntimeException(e)
        }

        try {
            val json = gson.fromJson<JsonObject>(br)
            br.close()
            return json
        } catch (e: Exception){
            return null
        }
    }

    fun getUuidKey(root: Path, keyDir: File,name: String, key: String,gson: Gson): UUID
    {
        val keyPath = root.resolve(getFileStringJson(keyDir,name))
        val file = keyPath.toFile()

        val json = getJsonOrCreateFile(file,gson)

        return if (json != null && json.has(key)) {
            UUID.fromString(json.get(key).toString().filterNot { it == '"' })
        } else if (json != null) {
            writeNewUuid(file,key,json)
        } else {
            writeNewUuid(file,key,JsonObject())
        }
    }

    private fun writeNewUuid(file: File,key: String,json: JsonObject): UUID
    {
        val pw: PrintWriter
        try {
            pw = PrintWriter(file)
        } catch (e: FileNotFoundException) {
            throw RuntimeException(e)
        }

        val id = UUID.randomUUID()
        json.addProperty(key,id.toString())
        pw.write(json.toString())
        pw.flush()
        pw.close()
        return id
    }

    private fun getFileStringJson(rootFile: File, name: String): String
    {
        return getFileString(rootFile,name) + ".json"
    }

    private fun getFileString(rootFile: File, name: String): String
    {
        var fileString = rootFile.toString()
        if (!fileString.endsWith("/")) {
            fileString += "/"
        }
        fileString += (name)
        return fileString
    }

    // https://www.baeldung.com/kotlin/convert-camel-case-snake-case
    @Suppress("MemberVisibilityCanBePrivate","unused")
    fun String.snakeToCamelCase(): String
    {
        val pattern = "_[a-z]".toRegex()
        return replace(pattern) { it.value.last().uppercase() }
    }
    @Suppress("MemberVisibilityCanBePrivate")
    fun String.snakeToKebabCase(): String
    {
        return this.replace('_','-')
    }

}