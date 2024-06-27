package universe.constellation.fractal.common

import kotlin.math.sqrt

class ComplexNumber(@JvmField val real: Double, @JvmField val image: Double) {

    operator fun times(scalar: Double): ComplexNumber {
        return ComplexNumber(scalar * real, scalar * image)
    }

    operator fun div(scalar: Double): ComplexNumber {
        return ComplexNumber(real / scalar, image / scalar)
    }

    operator fun times(c: ComplexNumber): ComplexNumber {
        return ComplexNumber(real * c.real - image * c.image, real * c.image + image * c.real)
    }

    operator fun minus(c: ComplexNumber): ComplexNumber {
        return ComplexNumber(real - c.real, image - c.image)
    }

    operator fun plus(c: ComplexNumber): ComplexNumber {
        return ComplexNumber(real + c.real, image + c.image)
    }

    fun dist(): Double {
        return sqrt(real * real + image *image)
    }

    fun dist(c: ComplexNumber): Double {
        return ComplexNumber(real - c.real,  image - c.image).dist()
    }
}

