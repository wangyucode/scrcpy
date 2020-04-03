package cn.wycode.controller

import java.io.IOException
import java.net.ServerSocket

class ClientServer() {

    fun serve(){
        val serverSocket: ServerSocket
        try {
            serverSocket = ServerSocket(PORT)
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
        println("ClientServer::started!")
        while (true) {
            try {
                val socket = serverSocket.accept()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            println("ClientServer::connected!")
        }
    }
}
