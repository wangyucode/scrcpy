package cn.wycode.controller

import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.media.Media
import javafx.stage.Stage


class Launcher : Application() {
    override fun start(primaryStage: Stage) {
        val dialog = Dialog<Any>()
        dialog.title = "initialize"
        val initialTask = InitialTask()
        dialog.contentTextProperty().bind(initialTask.messageProperty())
        initialTask.valueProperty().addListener { _, _, newValue ->
            if (newValue == FINISH_VALUE) {
                dialog.dialogPane.buttonTypes.addAll(ButtonType.CLOSE)
                dialog.close()
            }
        }
        Thread(initialTask).start()
        dialog.showAndWait()

        println("Scrcpy Controller ${initialTask.deviceName} ${initialTask.videoWidth}×${initialTask.videoHeight}")
        val loader = FXMLLoader(javaClass.classLoader.getResource("main.fxml"))
        val root = loader.load<Parent>()
        val controller = loader.getController<Controller>()
        controller.client = initialTask.client
        controller.startDecoder()
        primaryStage.title = "Scrcpy Controller-${initialTask.deviceName}-${initialTask.videoWidth}×${initialTask.videoHeight}"
        primaryStage.scene = Scene(root, initialTask.videoWidth.toDouble(), initialTask.videoHeight.toDouble())
        primaryStage.setOnHidden {
            Platform.exit()
        }
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    Application.launch(Launcher::class.java, *args)
}

