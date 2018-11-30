import java.math.BigInteger
import java.util.Random
fun getECCCurve(bitLength:Int) : ECCCurve {
  val random = Random()
  while (true) {
    var prime : BigInteger
    while (true) {
      prime = BigInteger(bitLength, 14, random)
      val case : BigInteger = BigInteger.valueOf(3L).and(prime)
      if (case.equals(BigInteger.valueOf(3L))) {
        break
      }
    } // probable prime with probability 1 - 1 / 2^14 > 0.9999, p != 1 (mod 8) to be easy to make quadratic residue
    println("Prime Selected : $prime")
    var a : BigInteger
    var b : BigInteger
    while (true) {
      a = BigInteger(bitLength, random).mod(prime)
      b = BigInteger(bitLength, random).mod(prime)
      // assert 4 a^3 + 27 b^2 != 0 (mod p), otherwise it makes curve supersingular, making discrete logarithm easier
      if (!a.modPow(BigInteger.valueOf(3L), prime).multiply(BigInteger.valueOf(4L)).add(b.modPow(BigInteger.valueOf(2L), prime).multiply(BigInteger.valueOf(27L))).mod(prime).equals(BigInteger.ONE)) {
        break
      }
    }
    println("a, b Selected : $a, $b")
    var gx : BigInteger
    var gy : BigInteger
    while (true) { // loop for suitable x
      gx = BigInteger(bitLength, random).mod(prime)
      if (gx.equals(BigInteger.ZERO)) {continue}
      val right = gx.modPow(BigInteger.valueOf(3L), prime).add(gx.multiply(a)).add(b).mod(prime)
      if (legendereSymbol(right, prime).equals(BigInteger.ONE)) {
        // search for some gx s.t. (gx, gy) exists two. (equivalent to legendere symbol value == 1)
        // calculate gy = 'right'^{(prime + 1) / 4}
        gy = right.modPow(prime.add(BigInteger.ONE).shiftRight(2), prime)
        break
      }
    }
    println("gx, gy selected : ($gx, $gy)")
    val curve = ECCCurve(prime, a, b, gx, gy)
    return curve
  }
}

class ECCCurveFactory(val bitList:List<Int>) {
  val curveQueue = mutableListOf<ECCCurve>()
  
}
