package universe.constellation.fractal.common

fun mandelbrot(
    leftDown: ComplexNumber,
    rightTop: ComplexNumber,
    step: Double = 0.01,
    iterations: Int = 200,
    dotConsumer: (Int, Int, Int) -> Unit,
    f: (ComplexNumber, ComplexNumber) -> ComplexNumber
) {
    var currentReal = leftDown.real
    var realIteration = 0

    val stopReal = rightTop.real
    while (currentReal <= stopReal) {
        var currentImage = leftDown.image
        val stopImage = rightTop.image
        var imageIteration = 0
        while (currentImage <= stopImage) {
            var currentValue = ComplexNumber(currentReal, currentImage)
            val point = ComplexNumber(currentReal, currentImage)
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
