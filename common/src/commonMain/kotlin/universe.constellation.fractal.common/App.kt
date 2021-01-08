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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
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
                        TextField(topY, { topY = it }, label = { Text("Top y") })
                        TextField(bottomY, { bottomY = it }, label = { Text("Bottom Y") })

                    }
                }

                Row(modifier = Modifier.padding(1.dp).weight(1.0F, true)) {
                    Column {
                        TextField(leftX, { leftX = it }, label = { Text("Left X") })
                        TextField(rightX, { rightX = it }, label = { Text("Right X") })
                    }
                }

                Button(modifier = Modifier.align(Alignment.CenterVertically), onClick = {
                    GlobalScope.launch {
                        println("xxx: " + bitmapWidth.toDouble() + " " + bitmapHeight.toDouble())
                        image = calc(
                            bitmapWidth.toDouble(),
                            bitmapHeight.toDouble(),
                            Complex(leftX.toDouble(), bottomY.toDouble()),
                            Complex(rightX.toDouble(), topY.toDouble())
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

private fun calc(renderWidth: Double, renderHeight: Double, leftBottom: Complex, rightTop: Complex): ImageBitmap {
    val workingBitmap = ImageBitmap(bitmapWidth, bitmapHeight)
    val canvas = Canvas(workingBitmap)
    canvas.drawRect(0f, 0f, workingBitmap.width.toFloat(), workingBitmap.height.toFloat(), paint = Paint().apply {
        color = backgroundColor
    })
    canvas.calc(renderWidth, renderHeight, leftBottom, rightTop)
    return workingBitmap
}

private fun Canvas.calc(renderWidth: Double, renderHeight: Double, leftBottom: Complex, rightTop: Complex) {
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

class Scene(val xOffset: Int, val yOffset: Int, val width: Float, val height: Float, val iterations: Int) {

    private val paint = Paint()

    fun draw(scope: Canvas, x: Int, y: Int, type: Int) {
        val color = when (type) {
            -1 -> Color.Black
            -2 -> {
                Color.Blue
            }
            else -> {
                val blue = (0xFF * 1.0 * (iterations - min(10 * type, iterations)) / iterations).toInt()
                //println("""$type $blue""")
                Color(red = blue, green = blue, blue = blue)
            }
        }
        val invertedY = height - (yOffset + y.toFloat())
        scope.drawRect(
            Rect(
                Offset(xOffset + x.toFloat(), invertedY),
                Offset(xOffset + x.toFloat() + 1, invertedY + 1)
            ), paint.apply { this.color = color })
    }
}

fun mandelbrot(
    leftDown: Complex,
    rightTop: Complex,
    step: Double = 0.01,
    iterations: Int = 200,
    dotConsumer: (Int, Int, Int) -> Unit,
    f: (Complex, Complex) -> Complex
) {
    var currentReal = leftDown.real
    var realIteration = 0

    val stopReal = rightTop.real
    while (currentReal <= stopReal) {
        var currentImage = leftDown.image
        val stopImage = rightTop.image
        var imageIteration = 0
        while (currentImage <= stopImage) {
            var currentValue = Complex(currentReal, currentImage)
            val point = Complex(currentReal, currentImage)
            var newValue = currentValue
            var color = 0
            for (i in 0 until iterations) {
                color = i
                newValue = f(currentValue, point)
                //if (newValue.dist(currentValue) <= epsilon) break

                if (newValue.dist() > 2) break
                currentValue = newValue
            }
            val type = when {
                newValue.dist() <= 2 -> {
                    -1
                }
                currentValue.dist().isFinite() -> {
                    color
                }
                else -> {
                    -2
                }
            }

            dotConsumer(realIteration, imageIteration, type)
            currentImage += step
            imageIteration++
        }
        currentReal += step
        realIteration++
    }
}
