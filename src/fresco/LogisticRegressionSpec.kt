package fresco

import com.winterbe.expekt.expect
import fresco.dsl.KnownFixedPoint
import fresco.dsl.evaluate
import fresco.dsl.matrices.Matrix
import fresco.dsl.matrices.Vector
import fresco.dsl.matrices.times
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class LogisticRegressionSpec: Spek({
    val logistic = LogisticRegression()

    it("updates learned model using previous value and first derivative") {
        val X = Matrix(
                arrayOf(KnownFixedPoint(1.0), KnownFixedPoint(2.0)),
                arrayOf(KnownFixedPoint(3.0), KnownFixedPoint(4.0))
        )
        val H = logistic.hessian(X)
        val L = logistic.choleskyDecomposition(-1.0 * H)
        val l = Vector(7.0, 8.0)
        var beta = Vector(5.0, 6.0)

        beta = logistic.updateLearnedModel(L, beta, l)

        val result = evaluate(beta)
        val expected = plain.Vector(33.0, -12.0)
        expect(result.isCloseTo(expected, 0.01)).to.be.`true`
    }

    it("computes best beta for logistic regression with non-zero lambda") {
        val intercept = 1.65707
        val beta_hp = 0.00968555
        val beta_wt = -1.17481

        val Xs = arrayOf(X1, X2)
        val Ys = arrayOf(am1, am2)

        val beta = logistic.fitLogisticModel(
                Xs, Ys, lambda = 1.0, numberOfIterations = 4
        )

        val result = evaluate(beta)
        val expected = plain.Vector(beta_hp, beta_wt, intercept)
        println("Result: $result, expected: $expected")
        expect(result.isCloseTo(expected, 0.01)).to.be.`true`
    }
})

val hp1 = plain.Vector(
        110.0, 110.0, 93.0, 110.0, 175.0, 105.0, 245.0, 62.0,
        95.0, 123.0, 123.0, 180.0, 180.0, 180.0, 205.0, 215.0
)
val hp2 = plain.Vector(
        230.0, 66.0, 52.0, 65.0, 97.0, 150.0, 150.0, 245.0,
        175.0, 66.0, 91.0, 113.0, 264.0, 175.0, 335.0, 109.0
)
val wt1 = plain.Vector(
        2.62, 2.875, 2.32, 3.215, 3.44, 3.46, 3.57, 3.19,
        3.15, 3.44, 3.44, 4.07, 3.73, 3.78, 5.25, 5.424
)
val wt2 = plain.Vector(
        5.345, 2.2, 1.615, 1.835, 2.465, 3.52, 3.435, 3.84,
        3.845, 1.935, 2.14, 1.513, 3.17, 2.77, 3.57, 2.78
)
val am1 = plain.Vector(
        1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
)
val am2 = plain.Vector(
        0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0
)

val ones1 = plain.Vector(*DoubleArray(hp1.size, { 1.0 }))
val ones2 = plain.Vector(*DoubleArray(hp2.size, { 1.0 }))

val X1 = plain.matrixFromVectors(hp1, wt1, ones1).transpose()
val X2 = plain.matrixFromVectors(hp2, wt2, ones2).transpose()
