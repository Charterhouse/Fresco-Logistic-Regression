package plain

class LogisticRegression {
    fun hessian(matrix: MatrixType): MatrixType {
        return -0.25 * (matrix.transpose() * matrix)
    }

    fun choleskyDecomposition(matrix: MatrixType): LowerTriangularMatrix {
        val d = matrix.numberOfRows
        val a = Array(d, {
            row -> Array(d, { column -> matrix[row, column] })
        })
        for (j in 0 until d) {
            for (k in 0 until j) {
                for (i in j until d) {
                    a[i][j] -= a[i][k] * a[j][k]
                }
            }
            a[j][j] = Math.sqrt(a[j][j])
            for (k in j + 1 until d) {
                a[k][j] /= a[j][j]
            }
        }
        return LowerTriangularMatrix(Matrix(*a))
    }

    /**
     * Calculates the solution for the equation Lx=b,
     * using forward substitution.
     *
     * See also https://en.wikipedia.org/wiki/Triangular_matrix#Algorithm
     */
    fun forwardSubstitution(L: LowerTriangularMatrix, b: Vector):
            Vector {
        val n = b.size
        val y = DoubleArray(n, { 0.0 })
        for (i in 0 until n) {
            y[i] = b[i]
            for (j in 0 until i) {
                y[i] -= L[i, j] * y[j]
            }
            y[i] /= L[i, i]
        }
        return Vector(*y)
    }

    /**
     * Calculates the solution for the equation Ux=b,
     * using back substitution.
     *
     * See also https://en.wikipedia.org/wiki/Triangular_matrix#Algorithm
     */
    fun backSubstitution(U: UpperTriangularMatrix, b: Vector):
            Vector {
        val n = b.size
        val x = DoubleArray(n, { 0.0 })
        for (i in (0 until n).reversed()) {
            x[i] = b[i]
            for (j in i+1 until n) {
                x[i] -= U[i, j] * x[j]
            }
            x[i] /= U[i, i]
        }
        return Vector(*x)
    }

    fun likelihood(v1: Vector, v2: Vector): Double {
        val exponential = exp(-(v1 * v2)[0])
        return 1.0 / (1.0 + exponential)
    }

    fun logLikelihoodPrime(
            x: MatrixType, y: Vector, beta: Vector): Vector {
        val result = DoubleArray(beta.size, { 0.0 })
        for (k in 0 until beta.size) {
            for (i in 0 until x.numberOfRows) {
                result[k] += (
                        y[i] - likelihood(x.row(i), beta)
                        ) * x[i,k]
            }
        }
        return Vector(*result)
    }

    fun updateLearnedModel(L: LowerTriangularMatrix, beta: Vector, l: Vector):
            Vector {
        val y = forwardSubstitution(L, l)
        val r = backSubstitution(L.transpose(), y)
        return beta + r
    }

    fun fitLogisticModel(Xs: Array<MatrixType>, Ys: Array<Vector>,
                         lambda: Double = 0.0,
                         numberOfIterations: Int = 10): Vector {
        var H: MatrixType? = null
        for (X in Xs) {
            val localH = hessian(X)
            if (H == null) {
                H = localH
            } else {
                H += localH
            }
        }

        if (H == null) {
            throw IllegalArgumentException("input must not be empty")
        }

        val I = IdentityMatrix(H.numberOfColumns)
        H -= lambda * I

        val L = choleskyDecomposition(-1.0 * H)

        var beta = Vector(*DoubleArray(H.numberOfColumns, { 0.0 }))
        for (i in 0 until numberOfIterations) {
            var lprime: Vector? = null
            for (party in 0 until Xs.size) {
                val X = Xs[party]
                val Y = Ys[party]
                val localLPrime = logLikelihoodPrime(X, Y, beta)
                if (lprime == null) {
                    lprime = localLPrime
                } else {
                    lprime += localLPrime
                }
            }

            if (lprime == null) {
                throw IllegalArgumentException("does not happen")
            }

            lprime -= (lambda * beta)
            beta = updateLearnedModel(L, beta, lprime)
        }
        return beta
    }
}
