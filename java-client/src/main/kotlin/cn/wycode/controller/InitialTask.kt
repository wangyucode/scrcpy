package cn.wycode.controller

import javafx.concurrent.Task
import java.nio.file.Paths

const val DEVICE_SERVER_PATH = "/data/local/tmp/scrcpy-server.jar"
const val SERVER_FILE_NAME = "scrcpy-server"
const val FINISH_VALUE = 4
const val PORT = 15940
const val SOCKET_NAME = "scrcpy"
const val SERVER_APP = "com.genymobile.scrcpy.Server"
const val SERVER_VERSION = "1.12.1"
const val MAX_SIZE = "1024"
const val BIT_RATE = "4M"
const val MAX_FPS = "24"
const val TUNNEL_FORWARD = "false"
const val CORP = "-" // 1224:1440:0:0   # 1224x1440 at offset (0,0)
const val SEND_FRAME_META = "true" //// always send frame meta (packet boundaries + timestamp)
const val CONTROL = "true"

class InitialTask : Task<Int>() {

    private val runtime = Runtime.getRuntime()

    override fun call(): Int {
        set(0)
        updateMessage("push service")
        if (!pushService()) {
            return get()
        }
        updateMessage("push success!")
        set(1)

        updateMessage("reverse tunnel")
        if (!reverseTunnel()) {
            return get()
        }
        updateMessage("reversing success!")
        set(2)

        updateMessage("listen to:$PORT")
        Thread(Runnable {
            ClientServer().serve()
        }).start()
        set(3)

        updateMessage("execute service")
        if (!executeService()) {
            return get()
        }
        updateMessage("service started")
        set(4)

        return get()
    }

    private fun executeService(): Boolean {
        try {
            val process = runtime.exec("adb shell CLASSPATH=$PORT localabstract:$SOCKET_NAME")
            val result = process.inputStream.bufferedReader().readText()
            println(result)
            return PORT.toString() == result || "" == result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun reverseTunnel(): Boolean {
        try {
            val process = runtime.exec("adb reverse tcp:$DEVICE_SERVER_PATH app_process / $SERVER_APP $SERVER_VERSION $MAX_SIZE $BIT_RATE $MAX_FPS $TUNNEL_FORWARD $CORP $SEND_FRAME_META $CONTROL")
            val result = process.inputStream.bufferedReader().readText()
            println(result)
            return true
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