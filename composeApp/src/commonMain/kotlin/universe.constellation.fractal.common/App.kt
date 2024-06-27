package universe.constellation.fractal.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.system.measureTimeMillis

private var counter = 0
private var bitmapWidth = 600
private var bitmapHeight = 600
private val backgroundColor = Color.LightGray
private lateinit var scene: Scene
private var epsilon = 0.00001

@Composable
fun App() {
    var image by mutableStateOf(ImageBitmap(bitmapWidth, bitmapHeight, ImageBitmapConfig.Argb8888))
    MaterialTheme {
        var leftX by remember { mutableStateOf("-2.0") }
        var topY by remember { mutableStateOf("2.0") }
        var rightX by remember { mutableStateOf("2.0") }
        var bottomY by remember { mutableStateOf("-2.0") }
        Column {
            Row(modifier = Modifier.padding(2.dp).align(Alignment.CenterHorizontally)) {

                Row(modifier = Modifier.padding(1.dp).weight(1.0F, true)) {
                    Column {
                        TextField(topY, { topY = it }, label = { Text("Top Y") })
                        TextField(bottomY, { bottomY = it }, label = { Text("Bottom Y") })

                    }
                }

                Row(modifier = Modifier.padding(1.dp).weight(1.0F, true)) {
                    Column {
                        TextField(leftX, { leftX = it }, label = { Text("Left X") })
                        TextField(rightX, { rightX = it }, label = { Text("Right X") })
                    }
                }

                Button(modifier = Modifier.align(alignment = Alignment.CenterVertically), onClick = {
                    GlobalScope.launch {
                        println("info: " + bitmapWidth.toDouble() + " " + bitmapHeight.toDouble())
                        image = calc(
                            bitmapWidth.toDouble(),
                            bitmapHeight.toDouble(),
                            ComplexNumber(leftX.toDouble(), bottomY.toDouble()),
                            ComplexNumber(rightX.toDouble(), topY.toDouble())
                        )
                    }

                }) {
                    Text("Refresh")
                }
            }


            Canvas(
                modifier = Modifier.fillMaxSize().onSizeChanged { bitmapWidth = it.width; bitmapHeight = it.height }
                    .pointerInput(Unit) {
                        forEachGesture {
                            awaitPointerEventScope {
                                val down = awaitFirstDown()
                                println(down.position)

                            }
                        }

                    }) {

                //clean
                drawRect(backgroundColor, topLeft = Offset(0f, 0f), size = Size(this.size.width, this.size.height))
                translate(-((image.width - size.width) / 2), -((image.height - size.height) / 2)) {
                    drawImage(image)
                }
                println("Redraw event" + counter++)
            }
        }
    }
}

private fun calc(renderWidth: Double, renderHeight: Double, leftBottom: ComplexNumber, rightTop: ComplexNumber): ImageBitmap {
    val workingBitmap = ImageBitmap(bitmapWidth, bitmapHeight)
    val canvas = Canvas(workingBitmap)
    canvas.drawRect(0f, 0f, workingBitmap.width.toFloat(), workingBitmap.height.toFloat(), paint = Paint().apply {
        color = backgroundColor
    })
    canvas.calc(renderWidth, renderHeight, leftBottom, rightTop)
    return workingBitmap
}

private fun Canvas.calc(renderWidth: Double, renderHeight: Double, leftBottom: ComplexNumber, rightTop: ComplexNumber) {
    println("render $renderWidth $renderHeight")
    val dist = rightTop - leftBottom

    val step = max(dist.real / renderWidth, dist.image / renderHeight)
    val xOffset = (renderWidth - dist.real / step) / 2
    val yOffset = (renderHeight - dist.image / step) / 2

    val iterations = 200
    //TODO
    scene = Scene(xOffset.toInt(), yOffset.toInt(), 0F, renderHeight.toFloat(), iterations)
    println("time = " + measureTimeMillis {
        mandelbrot(
            leftBottom,
            rightTop,
            step = step,
            iterations = iterations,
            dotConsumer = { x, y, type ->
                scene.draw(this, x, y, type)
            }) { z, c ->
            z * z + c
        }
    })
}