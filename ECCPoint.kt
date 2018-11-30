import java.math.BigInteger

data class ECCPoint(val x : BigInteger?, val y : BigInteger?, val curve : ECCCurve) {
  val isO : Boolean
  init {
    if (x == null && y == null) {
      isO = true
    }
    else if (x == null || y == null) {
      throw ECCException("Point coordinates must be both null or both non-null.")
    }
    else {
      isO = false
      val left = y!!.modPow(BigInteger.valueOf(2L), curve.Prime)
      val right = x!!.modPow(BigInteger.valueOf(3L), curve.Prime).add(curve.a.multiply(x!!)).mod(curve.Prime).add(curve.b).mod(curve.Prime)
      if (!(left.subtract(right).mod(curve.Prime).equals(BigInteger.ZERO))) {
        throw ECCException("Point is not on curve.\nCurve:y^2=x^3+${curve.a}x+${curve.b} (mod ${curve.Prime}\nPoint:(${this.x}, ${this.y})")
      }
    }
  }
  private fun discreteDivision(dividend : BigInteger, divisor:BigInteger) : BigInteger {
    // returns dividend / divisor (mod curve.Prime)
    val inv = divisor.modPow(curve.Prime.subtract(BigInteger.valueOf(2L)), curve.Prime)
    return inv.multiply(dividend)
  }
  fun addPointCoordinate(other:ECCPoint) : Pair<BigInteger?, BigInteger?> {
    if (curve != other.curve) {
      throw ECCException("Point on different curve.")
    }
    if (isO) {
      return Pair(other.x, other.y)
    }
    else if (other.isO) {
      return Pair(x, y)
    }
    else {
      if (x!!.equals(other.x!!)) {
        if (y!!.equals(other.y!!.negate())) {
          return Pair(null, null)
        }
        else if (y!!.mod(curve.Prime).equals(BigInteger.ZERO)) {
          return Pair(null, null)
        }
        else {
          val lambda = discreteDivision(x!!.modPow(BigInteger.valueOf(2L), curve.Prime).multiply(BigInteger.valueOf(3L)).add(curve.a).mod(curve.Prime), y!!.multiply(BigInteger.valueOf(2L)).mod(curve.Prime))
          val ansx = lambda.modPow(BigInteger.valueOf(2L), curve.Prime).subtract(x!!.multiply(BigInteger.valueOf(2L)).mod(curve.Prime)).mod(curve.Prime)
          val ansy = lambda.multiply(x!!.subtract(ansx).mod(curve.Prime)).subtract(y!!).mod(curve.Prime)
          return Pair(ansx, ansy)
        }
      }
      else {
        val lambda = discreteDivision(other.y!!.subtract(y!!), other.x!!.subtract(x!!))
        val ansx = lambda.modPow(BigInteger.valueOf(2L), curve.Prime).subtract(x!!).subtract(other.x!!).add(curve.Prime.multiply(BigInteger.valueOf(2L))).mod(curve.Prime)
        val ansy = lambda.multiply(x!!.subtract(ansx).mod(curve.Prime)).subtract(y!!).mod(curve.Prime)
        return Pair(ansx, ansy)
      }
    }
  }
  fun addPoint(other : ECCPoint) : ECCPoint {
    if (curve != other.curve) {
      throw ECCException("Point on different curve.")
    }
    if (isO) {
      return other
    }
    else if (other.isO) {
      return this
    }
    else {
      val coordinate = this.addPointCoordinate(other)
      return ECCPoint(coordinate.first, coordinate.second, this.curve)
    }
  }
  fun scalarMultiple(n:BigInteger):ECCPoint {
    val bit = n.toString(2)
    var ans = this
    for (i in 1 until bit.length) {
      if (bit.get(i) == '1') {
        ans = ans.addPoint(ans).addPoint(this)
      }
      else {
        ans = ans.addPoint(ans)
      }
    }
    return ans
  }
  fun negatePoint():ECCPoint {
    return ECCPoint(this.x, this.y?.negate(), this.curve)
  }
}
