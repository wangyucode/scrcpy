package cn.wycode.controller

import javafx.concurrent.Task
import java.nio.file.Paths

const val DEVICE_SERVER_PATH = "/data/local/tmp/scrcpy-server.jar"
const val SERVER_FILE_NAME = "scrcpy-server"
const val FINISH_VALUE = 1

class InitialTask : Task<Int>() {

    override fun call(): Int {
        var result = 0
        updateMessage("pushing")
        if (pushService()) {
            updateMessage("push success!")
            result++
        }
        return result
    }

    private fun pushService(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val serverFile = Paths.get(javaClass.classLoader.getResource(SERVER_FILE_NAME)!!.toURI()).toFile()
            val process = runtime.exec("adb push ${serverFile.absolutePath} $DEVICE_SERVER_PATH")
            val result = process.inputStream.bufferedReader().readText()
            println(result)
            return result.contains(serverFile.length().toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}