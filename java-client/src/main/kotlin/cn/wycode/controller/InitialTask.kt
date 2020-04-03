package cn.wycode.controller

import javafx.concurrent.Task
import java.nio.file.Paths

const val DEVICE_SERVER_PATH = "/data/local/tmp/scrcpy-server.jar"
const val SERVER_FILE_NAME = "scrcpy-server"
const val FINISH_VALUE = 2
const val PORT = 15940
const val SOCKET_NAME = "scrcpy"

class InitialTask : Task<Int>() {

    private val runtime = Runtime.getRuntime()

    override fun call(): Int {
        var result = 0
        updateMessage("pushing server")
        if (!pushService()) {
            return result;
        }
        updateMessage("push success!")
        result++

        updateMessage("reversing tunnel")
        if (!reverseTunnel()) {
            return result
        }
        updateMessage("reversing success!")
        result++

        return result
    }

    private fun reverseTunnel(): Boolean {
        try {
            val process = runtime.exec("adb reverse tcp:$PORT localabstract:$SOCKET_NAME")
            val result = process.inputStream.bufferedReader().readText()
            println(result)
            return PORT.toString() == result || "" == result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun pushService(): Boolean {
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