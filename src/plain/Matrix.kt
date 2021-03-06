package plain

abstract class MatrixType {
    abstract val numberOfColumns: Int
    abstract val numberOfRows: Int

    abstract operator fun get(row: Int, column: Int): Double

    fun row(index: Int): Vector {
        val row = DoubleArray(numberOfColumns, { 0.0 })
        for (column in 0 until numberOfColumns) {
            row[column] = get(index, column)
        }
        return Vector(*row)
    }

    open fun transpose(): MatrixType {
        return TransposedMatrix(this)
    }

    operator fun times(other: MatrixType): MatrixType {
        assertMultiplicationCompatibility(this, other)
        val rows = numberOfRows
        val columns = other.numberOfColumns
        val multiplied = Array(rows, { Array(columns, { 0.0 }) })
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                for (i in 0 until numberOfColumns) {
                    multiplied[row][column] +=
                            this[row, i] * other[i, column]
                }
            }
        }

        return Matrix(*multiplied)
    }

    private fun assertMultiplicationCompatibility(a: MatrixType, b: MatrixType) {
        val compatible = a.numberOfColumns == b.numberOfRows
        if (!compatible) {
            throw IllegalArgumentException("these matrices cannot be multiplied")
        }
    }

    open operator fun times(vector: Vector): Vector {
        return (this * vector.transpose()).transpose().row(0)
    }

    open operator fun times(scalar: Double): MatrixType {
        return MultipliedMatrix(this, scalar)
    }

    operator fun minus(other: MatrixType): MatrixType {
        assertOfSameShape(this, other)
        return SubtractedMatrix(this, other)
    }

    operator fun plus(other: MatrixType): MatrixType {
        assertOfSameShape(this, other)
        return AddedMatrix(this, other)
    }

    private fun assertOfSameShape(a: MatrixType, b: MatrixType) {
        val sameShape = a.numberOfRows == b.numberOfRows &&
                a.numberOfColumns == b.numberOfColumns
        if (!sameShape) {
            throw IllegalArgumentException("these matrices are not of the same shape")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is MatrixType) return false
        if (numberOfRows != other.numberOfRows) return false
        if (numberOfColumns != other.numberOfColumns) return false
        for (row in 0 until numberOfRows) {
            @Suppress("LoopToCallChain")
            for (column in 0 until numberOfColumns) {
                if (this[row, column] != other[row, column]) {
                    return false
                }
            }
        }
        return true
    }

    fun isCloseTo(other: MatrixType, delta: Double): Boolean {
        assertOfSameShape(this, other)
        for (row in 0 until numberOfRows) {
            for (column in 0 until numberOfColumns) {
                if (Math.abs(this[row, column] - other[row, column]) > delta) {
                    return false
                }
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var result = numberOfColumns
        result = 31 * result + numberOfRows
        return result
    }

    override fun toString(): String {
        var result = ""
        for (row in 0 until numberOfRows) {
            if (row != 0) {
                result += "\n"
            }
            for (column in 0 until numberOfColumns) {
                if (column != 0) {
                    result += ", "
                }
                result += this[row, column]
            }
        }
        return result
    }
}

class Matrix(vararg val elements: Array<Double>): MatrixType() {
    override val numberOfColumns: Int
    override val numberOfRows: Int

    init {
        var numberOfColumns: Int? = null
        elements.forEach { element ->
            if (numberOfColumns == null) {
                numberOfColumns = element.size
            }
            if (numberOfColumns != element.size) {
                throw IllegalArgumentException("Rows are not of equal size")
            }
        }
        this.numberOfColumns = numberOfColumns ?: 0
        this.numberOfRows = elements.size
    }

    override operator fun get(row: Int, column: Int): Double {
        return elements[row][column]
    }
}

class TransposedMatrix(val matrix: MatrixType): MatrixType() {
    override val numberOfRows: Int
        get() = matrix.numberOfColumns
    override val numberOfColumns: Int
        get() = matrix.numberOfRows
    override operator fun get(row: Int, column: Int): Double {
        return matrix[column, row]
    }
}

class MultipliedMatrix(val matrix: MatrixType, val scalar: Double): MatrixType() {
    override val numberOfRows: Int
        get() = matrix.numberOfRows
    override val numberOfColumns: Int
        get() = matrix.numberOfColumns
    override operator fun get(row: Int, column: Int): Double {
        return matrix[row, column] * scalar
    }
}

class LowerTriangularMatrix(val matrix: MatrixType): MatrixType() {
    override val numberOfRows: Int
        get() = matrix.numberOfRows
    override val numberOfColumns: Int
        get() = matrix.numberOfColumns
    override operator fun get(row: Int, column: Int): Double {
        if (column > row) {
            return 0.0
        } else {
            return matrix[row, column]
        }
    }
    override fun transpose(): UpperTriangularMatrix {
        return UpperTriangularMatrix(super.transpose())
    }
}

class UpperTriangularMatrix(val matrix: MatrixType): MatrixType() {
    override val numberOfRows: Int
        get() = matrix.numberOfRows
    override val numberOfColumns: Int
        get() = matrix.numberOfColumns
    override operator fun get(row: Int, column: Int): Double {
        if (column < row) {
            return 0.0
        } else {
            return matrix[row, column]
        }
    }

    override fun transpose(): LowerTriangularMatrix {
        return LowerTriangularMatrix(super.transpose())
    }
}

class SubtractedMatrix(val left: MatrixType, val right: MatrixType): MatrixType() {
    override val numberOfRows: Int
        get() = left.numberOfRows
    override val numberOfColumns: Int
        get() = left.numberOfColumns
    override operator fun get(row: Int, column: Int): Double {
        return left[row, column] - right[row, column]
    }
}

class AddedMatrix(val left: MatrixType, val right: MatrixType): MatrixType() {
    override val numberOfRows: Int
        get() = left.numberOfRows
    override val numberOfColumns: Int
        get() = left.numberOfColumns
    override operator fun get(row: Int, column: Int): Double {
        return left[row, column] + right[row, column]
    }
}

class IdentityMatrix(size: Int): MatrixType() {
    override val numberOfColumns: Int = size
    override val numberOfRows: Int = size
    override operator fun get(row: Int, column: Int): Double {
        if (row == column) {
            return 1.0
        } else {
            return 0.0
        }
    }
}

operator fun Double.times(matrix: MatrixType): MatrixType {
    return matrix * this
}

fun matrixFromVectors(vararg vectors: Vector): MatrixType {
    val rows = vectors.size
    val columns = vectors[0].size
    val result = Array(rows, { Array(columns, { 0.0 }) })
    for (row in 0 until rows) {
        for (column in 0 until columns) {
            result[row][column] = vectors[row].get(column)
        }
    }
    return Matrix(*result)
}
