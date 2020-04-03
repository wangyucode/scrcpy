package cn.wycode.controller

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.stage.Stage


class Launcher : Application() {
    override fun start(primaryStage: Stage) {
        val dialog = Dialog<Any>()
        val initialTask = InitialTask()
        dialog.contentTextProperty().bind(initialTask.messageProperty())
        initialTask.valueProperty().addListener { _, oldValue, newValue ->
            println("$oldValue,$newValue")
            if (newValue == FINISH_VALUE) {
                dialog.dialogPane.buttonTypes.addAll(ButtonType.CLOSE)
                dialog.close()
            }
        }
        Thread(initialTask).start()
        dialog.showAndWait()

        val root = FXMLLoader.load<Parent>(javaClass.classLoader.getResource("main.fxml"))
        primaryStage.title = "Hello World"
        primaryStage.scene = Scene(root, 300.0, 275.0)
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    Application.launch(Launcher::class.java, *args)
}

