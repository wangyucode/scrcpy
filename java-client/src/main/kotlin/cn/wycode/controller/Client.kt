package cn.wycode.controller

import java.io.IOException
import java.io.InputStream
import java.net.Socket
import java.nio.charset.StandardCharsets

class Client {

    lateinit var videoSocket: Socket
    lateinit var videoInputStream: InputStream
    lateinit var controlSocket: Socket

    fun connect(): Boolean {
        return try {
            videoSocket = Socket("localhost", PORT)
            println("ClientServer::videoSocket connected!")
            controlSocket = Socket("localhost", PORT)
            println("ClientServer::controlSocket connected!")
            videoInputStream = videoSocket.getInputStream()
            val signal = videoInputStream.read()
            println("ClientServer::signal->$signal")
            return signal == 0
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun readDeviceName(): String {
        val buffer = ByteArray(64)
        videoInputStream.read(buffer)
        return buffer.toString(StandardCharsets.UTF_8).trim(0.toChar())
    }

    fun readVideoWidth(): Int {
        return videoInputStream.read().shl(8).or(videoInputStream.read())
    }

    fun readVideoHeight(): Int {
        return videoInputStream.read().shl(8).or(videoInputStream.read())
    }
}

