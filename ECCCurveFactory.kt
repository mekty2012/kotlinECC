import java.math.BigInteger
import java.security.SecureRandom
fun getECCCurve(bitLength:Int, random:SecureRandom) : ECCCurve {
  while (true) {
    var prime : BigInteger
    while (true) {
      prime = BigInteger(bitLength, 14, random)
      val case : BigInteger = BigInteger.valueOf(3L) and prime
      if (case == BigInteger.valueOf(3L)) {
        break
      }
    } // probable modular with probability 1 - 1 / 2^14 > 0.9999, p != 1 (mod 4) to be easy to make quadratic residue
    println("modular Selected : $prime")
    var a : BigInteger
    var b : BigInteger
    while (true) {
      a = BigInteger(bitLength, random).mod(prime)
      b = BigInteger(bitLength, random).mod(prime)
      // assert 4 a^3 + 27 b^2 != 0 (mod p), otherwise it makes curve supersingular, making discrete logarithm easier
      if ((a.modPow(BigInteger.valueOf(3L), prime) * BigInteger.valueOf(4L) + b.modPow(BigInteger.valueOf(2L), prime) * BigInteger.valueOf(27L)).mod(prime) != BigInteger.ZERO) {
        break
      }
    }
    println("a, b Selected : $a, $b")
    var gx : BigInteger
    val gy : BigInteger
    while (true) { // loop for suitable x
      gx = BigInteger(bitLength, random).mod(prime)
      if (gx == BigInteger.ZERO) {continue}
      val right = (gx.modPow(BigInteger.valueOf(3L), prime) + gx * a + b).mod(prime)
      if (legendreSymbol(right, prime) == BigInteger.ONE) {
        // search for some gx s.t. (gx, gy) exists two. (equivalent to legendre symbol value == 1)
        // calculate gy = 'right'^{(modular + 1) / 4}, quadratic residue.
        gy = right.modPow((prime + BigInteger.ONE) shr 2, prime)
        break
      }
    }
    // TODO : yet not well made curve, co-factor h = 1 needed. (point generator be primitive root of E(F_p))
    println("gx, gy selected : ($gx, $gy)")
    return ECCCurve(prime, a, b, gx, gy)
  }
}

fun getComplexMultiplicationECCCUrve(bitLength:Int, random:SecureRandom) : ECCCurve {
  TODO()
}
