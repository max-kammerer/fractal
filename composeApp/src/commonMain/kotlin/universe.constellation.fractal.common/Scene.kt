package universe.constellation.fractal.common

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import kotlin.math.min

class Scene(private val xOffset: Int, private val yOffset: Int, private val width: Float, private val height: Float, private val iterations: Int) {

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