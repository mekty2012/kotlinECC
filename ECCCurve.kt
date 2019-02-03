import java.math.BigInteger

class ECCCurve(val prime : BigInteger, val a : BigInteger, val b : BigInteger, val generatorXCoordinate : BigInteger, val generatorYCoordinate : BigInteger) {
  val generator = ECCPoint(generatorXCoordinate, generatorYCoordinate, this)
  private var pointCount = BigInteger.ZERO
  private var generatorOrder = BigInteger.ZERO
  private var cofactor = BigInteger.ZERO
  init {
    pointCount = TODO()
    generatorOrder = discreteLogarithm(generator, ECCPoint(null, null, this)) // thinking_face
    cofactor = pointCount.divide(generatorOrder)
  }
  fun getPointCount() : BigInteger = pointCount
  fun getGeneratorOrder() : BigInteger = generatorOrder
  fun getCofactor() : BigInteger = cofactor
}

fun countCurvePoint(prime:BigInteger, a:BigInteger, b:BigInteger) : BigInteger {
  /*
   * We use notion of frobenius endomorphism, division polynomial.
   */
  return BigInteger.ZERO
}
