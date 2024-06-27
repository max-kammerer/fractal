import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import universe.constellation.fractal.common.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "fractal",
    ) {
        App()
    }
}