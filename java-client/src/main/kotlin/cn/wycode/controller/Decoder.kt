package cn.wycode.controller

import javafx.concurrent.Task
import javafx.scene.image.Image
import org.bytedeco.ffmpeg.avcodec.AVPacket
import org.bytedeco.ffmpeg.avutil.AVDictionary
import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264
import org.bytedeco.ffmpeg.global.avutil.AV_NOPTS_VALUE
import java.io.InputStream
import java.nio.ByteBuffer

class Decoder(private val inputStream: InputStream) : Task<Image>() {

    override fun call(): Image {

        val avCodec = avcodec.avcodec_find_decoder(AV_CODEC_ID_H264)
        val avCodecContext = avcodec.avcodec_alloc_context3(avCodec)
        val parser = avcodec.avcodec_open2(avCodecContext, avCodec, AVDictionary(null))
        val avPacket = AVPacket()
        readPacket(avPacket)
        return Image("https://www.baidu.com/img/bd_logo1.png")
    }

    private fun readPacket(avPacket: AVPacket) {
        // The video stream contains raw packets, without time information. When we
        // record, we retrieve the timestamps separately, from a "meta" header
        // added by the server before each raw packet.
        //
        // The "meta" header length is 12 bytes:
        // [. . . . . . . .|. . . .]. . . . . . . . . . . . . . . ...
        //  <-------------> <-----> <-----------------------------...
        //        PTS        packet        raw packet
        //                    size
        //
        // It is followed by <packet_size> bytes containing the packet/frame.
        val headerBuffer = ByteArray(12)
        inputStream.read(headerBuffer)
        var pts = ByteBuffer.wrap(headerBuffer).getLong()
        val size = ByteBuffer.wrap(headerBuffer, 8, 4).getInt()
        println("pts-->$pts")
        println("size-->$size")
        if (pts == -1L) pts = AV_NOPTS_VALUE
        avcodec.av_new_packet(avPacket, size)
        val packetBuffer = ByteArray(size)
        inputStream.read(packetBuffer)
        avPacket.data().put(packetBuffer, 0, size)
        avPacket.pts(pts)
    }

}