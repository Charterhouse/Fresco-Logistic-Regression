import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class LogisticRegressionSpec: Spek({

    val logistic = LogisticRegression()

    it("calculates an approximation of the Hessian matrix") {
        val matrix = Matrix(arrayOf(
                arrayOf(1.0, 2.0),
                arrayOf(3.0, 4.0),
                arrayOf(5.0, 6.0)
        ))
        val expected = Matrix(arrayOf(
                arrayOf(-8.75, -11.0),
                arrayOf(-11.0, -14.0)
        ))
        expect(logistic.hessian(matrix)).to.equal(expected)
    }

    it("calculates the Cholesky decomposition") {
        val matrix = Matrix(arrayOf(
                arrayOf(2.0, 1.0),
                arrayOf(1.0, 2.0)
        ))
        val expected = LowerTriangularMatrix(Matrix(arrayOf(
                arrayOf(Math.sqrt(2.0), 0.0),
                arrayOf(1.0/ Math.sqrt(2.0), Math.sqrt(3.0 / 2.0))
        )))
        expect(logistic.choleskyDecomposition(matrix)).to.equal(expected)
    }

    it("performs forward substitution") {
        val L = LowerTriangularMatrix(Matrix(arrayOf(
                arrayOf(1.0, 0.0, 0.0),
                arrayOf(-2.0, 1.0, 0.0),
                arrayOf(1.0, 6.0, 1.0)
        )))
        val b = Matrix(arrayOf(
                arrayOf(2.0),
                arrayOf(-1.0),
                arrayOf(4.0)
        ))
        val expected = Matrix(arrayOf(
                arrayOf(2.0),
                arrayOf(3.0),
                arrayOf(-16.0)
        ))
        val x = logistic.forwardSubstitution(L, b)
        expect(x).to.equal(expected)
    }

    it("performs back substitution") {
        val U = UpperTriangularMatrix(Matrix(arrayOf(
                arrayOf(1.0, -2.0, 1.0),
                arrayOf(0.0, 1.0, 6.0),
                arrayOf(0.0, 0.0, 1.0)
        )))
        val b = Matrix(arrayOf(
                arrayOf(4.0),
                arrayOf(-1.0),
                arrayOf(2.0)
        ))
        val expected = Matrix(arrayOf(
                arrayOf(-24.0),
                arrayOf(-13.0),
                arrayOf(2.0)
        ))
        val x = logistic.backSubstitution(U, b)
        expect(x).to.equal(expected)
    }
})
