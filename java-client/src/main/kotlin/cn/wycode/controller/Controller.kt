package cn.wycode.controller

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.net.URL
import java.util.*


class Controller : Initializable {

    lateinit var client: Client

    @FXML
    lateinit var image : ImageView

    override fun initialize(location: URL?, resources: ResourceBundle?) {

    }

    fun startDecoder() {
        val decoder = Decoder(client.videoInputStream)
        Thread(decoder).start()
        image.imageProperty().bind(decoder.valueProperty())
    }


}
