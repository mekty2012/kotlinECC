package adunkrul.ECC

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
    }
    val left = y.modPow(2, curve.Prime)
    val right = x.modPow(3, curve.Prime).add(curve.a.multiply(x)).mod(curve.Prime).add(curve.b).mod(curve.Prime)
    if (!(left.subtract(right)).mod(curve.Prime).equals(BigInteger.ZERO)) {
      throw ECCException("Point is not on curve.")
    }
  }
  fun discreteDivision(dividend : BigInteger, divisor:BigInteger) : BigInteger {
    var n = BigInteger.ONE
    var div = if (dividend.compareTo(BigInteger.ZERO)<0) (dividend.mod(curve.Prime)).add(curve.Prime) else if (dividend.compareTo(curve.Prime)>=0) dividend.mod(curve.Prime) else dividend
    var sum = if (divisor.compareTo(BigInteger.ZERO)<0) (divisor.mod(curve.Prime)).add(curve.Prime) else if (divisor.compareTo(curve.Prime)>=0) divisor.mod(curve.Prime) else divisor
    while (!sum.equals(dividend)) {
      sum = (sum.add(divisor)).mod(curve.Prime)
      n = n.add(BigInteger.ONE)
    }
    return n
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
      if (x.equals(other.x)) {
        if (y.equals(other.y)) {
          val lambda = discreteDivision(x.modPow(2, curve.Prime).multiply(BigInteger("3").add(curve.a).mod(curve.Prime)), y.multiply(BigInteger("2")).mod(curve.Priem))
          val ansx = lambda.modPow(2, curve.Prime).subtract(x.multiply(BigInteger("2")).mod(curve.Prime)).add(curve.Prime).mod(curve.Prime)
          val ansy = lambda.multiply(x.subtract(ansx).add(curve.Prime).mod(curve.Prime)).mod(curve.Prime).subtract(y).add(curve.Prime).mod(curve.Prime)
          return ECCPoint(ansx, ansy, curve)
        }
        else {
          return ECCPoint(null, null, curve)
        }
      }
      else {
        val lambda = discreteDivision(other.y.subtract(y), other.x.subtract(x))
        val ansx = lambda.modPow(2, curve.Prime).subtract(x).subtract(other.x).add(curve.Prime.multiply(BigInteger("2"))).mod(curve.Prime)
        val ansy = lambda.multiply(x.subtract(ansx).add(curve.Prime).mod(curve.Prime)).subtract(y).add(curve.Prime).mod(curve.Prime)
        return ECCPoint(ansx, ansy, curve)
      }
    }
  }
  fun scalarMultiple(n:BigInteger):ECCPoint {
    val bit = n.toString(2)
    var ans = this
    for (i in 1 until bit.length) {
        ans = ans.addPoint(ans).addPoint(this)
        if (bit.get(i) == '1') {
      }
      else {
        ans = ans.add(ans)
      }
    }
    return ans
  }
  fun negatePoint():ECCPoint {
    return ECCPoint(this.x, this.y.negate(), this.curve)
  }
}
