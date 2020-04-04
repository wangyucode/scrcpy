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
const val BIT_RATE = "4000000"
const val MAX_FPS = "24"
const val TUNNEL_FORWARD = "true"
const val CORP = "-" // 1224:1440:0:0   # 1224x1440 at offset (0,0)
const val SEND_FRAME_META = "true" //// always send frame meta (packet boundaries + timestamp)
const val CONTROL = "true"

class InitialTask : Task<Int>() {

    private val runtime = Runtime.getRuntime()
    lateinit var deviceName: String
    lateinit var client: Client
    var videoWidth = 1080
    var videoHeight = 1920

    override fun call(): Int {
        updateValue(0)
        updateMessage("push service")
        if (!pushService()) return get()
        updateMessage("push success!")
        updateValue(1)

        updateMessage("enable tunnel")
        if (!enableTunnel()) return get()
        updateMessage("reversing success!")
        updateValue(2)

        updateMessage("execute service")
        Thread(Runnable {
            executeService()
        }).start()

        Thread.sleep(1000)

        updateMessage("connect service")
        client = Client()
        if (!client.connect()) return get()
        updateMessage("connected")
        updateValue(3)

        if (!readDeviceInfo(client)) return get()
        updateValue(4)
        return get()
    }


    private fun readDeviceInfo(client: Client): Boolean {
        deviceName = client.readDeviceName()
        println("InitialTask::deviceName->$deviceName")
        videoWidth = client.readVideoWidth()
        println("InitialTask::videoWidth->$videoWidth")
        videoHeight = client.readVideoHeight()
        println("InitialTask::videoHeight->$videoHeight")
        return true
    }


    private fun executeService(): Boolean {
        try {
            val command = "adb shell CLASSPATH=$DEVICE_SERVER_PATH app_process / $SERVER_APP $SERVER_VERSION $MAX_SIZE $BIT_RATE $MAX_FPS $TUNNEL_FORWARD $CORP $SEND_FRAME_META $CONTROL"
            println(command)
            val process = runtime.exec(command)
            val error = process.errorStream.bufferedReader().readText()
            val result = process.inputStream.bufferedReader().readText()
            println(result)
            println(error)
            return !result.contains("ERROR")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun enableTunnel(): Boolean {
        try {
            val command = "adb forward tcp:$PORT localabstract:$SOCKET_NAME"
            println(command)
            val process = runtime.exec(command)
            val error = process.errorStream.bufferedReader().readText()
            val result = process.inputStream.bufferedReader().readText()
            println(result)
            println(error)
            return result.contains(PORT.toString()) || "" == result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun pushService(): Boolean {
        try {
            val serverFile = Paths.get(javaClass.classLoader.getResource(SERVER_FILE_NAME)!!.toURI()).toFile()
            val command = "adb push ${serverFile.absolutePath} $DEVICE_SERVER_PATH"
            println(command)
            val process = runtime.exec(command)
            val result = process.inputStream.bufferedReader().readText()
            println(result)
            return result.contains(serverFile.length().toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}