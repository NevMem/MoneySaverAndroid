package com.nevmem.moneysaver

import org.json.JSONObject
import java.io.*

abstract class JsonLoaderHelper {
    companion object {
        fun loadJson(filename: String): JSONObject {
            val file = File("./src/test/java/com/nevmem/moneysaver/resources/$filename")
            return loadJson(FileInputStream(file))
        }

        fun loadJson(stream: InputStream): JSONObject {
            val sb = StringBuilder()
            val br = BufferedReader(InputStreamReader(stream))
            var line: String? = br.readLine()
            while (line != null) {
                sb.append(line)
                line = br.readLine()
            }
            return JSONObject(sb.toString())
        }
    }
}