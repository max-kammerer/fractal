package universe.constellation.fractal.common

import kotlin.math.sqrt

class Complex(@JvmField val real: Double, @JvmField val image: Double) {

    operator fun times(scalar: Double): Complex {
        return Complex(scalar * real, scalar * image)
    }

    operator fun div(scalar: Double): Complex {
        return Complex(real / scalar, image / scalar)
    }

    operator fun times(c: Complex): Complex {
        return Complex(real * c.real - image * c.image, real * c.image + image * c.real)
    }

    operator fun minus(c: Complex): Complex {
        return Complex(real - c.real, image - c.image)
    }

    operator fun plus(c: Complex): Complex {
        return Complex(real + c.real, image + c.image)
    }

    fun dist(): Double {
        return sqrt(real * real + image *image)
    }

    fun dist(c: Complex): Double {
        return Complex(real - c.real,  image - c.image).dist()
    }
}

