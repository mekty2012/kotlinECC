import java.math.BigInteger
import java.util.Random
class ECCCurveFactory {
  fun getECCCurve(bitLength:Int) : ECCCurve {
    val random = new Random()
    while (true) {
      val prime = BigInteger(bitLength, 14, random) // probable prime with probability 1 - 1 / 2^14 > 0.9999
      val a = BigInteger(bitLength, random)
      val b = BigInteger(bitLength, random)
      var gx : BigInteger
      var gy : BigInteger
      while (true) { // loop for suitable x
        gx = BigInteger(bitLength, random)
        
      }
    }
  }
}
