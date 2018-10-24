package adunkrul.ECC

import java.math.BigInteger

val _Random = java.util.Random()
val Primes = listOf("2","3","5","7","11","13","17","19","23","29","31","37","41",
"43","47","53","59","61","67","71","73","79","83","89","97","101","103","107",
"109","113","127","131","137","139","149","151","157","163","167","173","179",
"181","191","193","197","199","211","223","227","229","233","239","241","251",
"257","263","269","271","277","281","283","293","307","311","313","317","331",
"337","347","349","353","359","367","373","379","383","389","397","401","409",
"419","421","431","433","439","443","449","457","461","463","467","479","487",
"491","499","503","509","521","523","541","547","557","563","569","571","577",
"587","593","599","601","607","613","617","619","631","641","643","647","653",
"659","661","673","677","683","691","701","709","719","727","733","739","743",
"751","757","761","769","773","787","797","809","811","821","823","827","829",
"839","853","857","859","863","877","881","883","887","907","911","919","929",
"937","941","947","953","967","971","977","983","991","997")

data class ECCCurve(val Prime : BigInteger, val a : BigInteger, val b : BigInteger, val Gx : BigInteger, val Gy : BigInteger) {
  val G = ECCPoint(Gx, Gy, this)
  val nt : BigInteger
  val n : BigInteger
  val h : BigInteger
  init {
    var nttemp = BigInteger.ZERO
    var i = BigInteger.ZERO
    while (i.compareTo(Prime) <= 0) {
      val right = i.modPow(3, Prime).add((a.multiply(i)).mod(Prime)).add(b).mod(Prime)
      nttemp = nttemp.add(legendereSymbol(right, Prime).add(BigInteger.ONE))
      i = i.add(BigInteger.ONE)
    }
    nt = nttemp
    n = discreteLogarithm(ECCPoint(null, null, this))
    h = nt.divide(n)
    if (!h.equals(BigInteger.ONE)) {
      // log warning
    }
  }
  fun discreteLogarithm(point : ECCPoint):BigInteger {
    if (Prime.bitLength <= 100) {return simpleDiscreteLogarithm(ECCPoint)}
    else if (Prime.bitLength <= 1000) {return bsgsDiscreteLogarithm(ECCPoint)}
    else {return randomWalkDiscreteLogarithm(ECCPoint)}
  }
  fun simpleDiscreteLogarithm(m:ECCPoint):BigInteger { // do not use on large numbers
    var mult = G
    val n = BigInteger.ONE
    while (!mult.equals(m)) {
      mult = mult.add(G)
      n = n.add(BigInteger.ONE)
      if (n.compareTo(Prime) >= 0) {
        throw ECCException("Point not on residue of G")
      }
    }
    return n
  }
  fun bsgsDiscreteLogarithm(m:ECCPoint):BigInteger { // do not use on large numbers
    val pointSet = mutableMapOf<ECCPoint, BigInteger>()
    var i = BigInteger.ONE
    var sq = sqrt(n)
    var point = G
    while (i.compareTo(sq) <= 0) { // baby step
      pointSet.put(point to i)
      point = point.add(G)
      i = i.add(BigInteger.ONE)
    }

    if (pointSet.contains(m)) {
      return pointSet.getOrElse(m, {BigInteger.ZERO}) // if baby step contains our result, then return index
    }

    val Gprime = G.scalarMultiple(sq).negate()
    var point = Gprime.add(m)

    i = BigInteger.ONE
    while (i.compareTo(sq) <= 0) {
      if (pointSet.contains(point)) {
        return e.add(pointSet.getOrElse(point, {BigInteger.ZERO}))
      }
      i = i.add(BigInteger.ONE)
      point = point.add(Gprime)
    }
    throw ECCException("Point Not on Field of G")
    return BigInteger.ZERO
  }
  fun randomWalkDiscreteLogarithm(m:ECCPoint):BigInteger { // recommended
    throw NotImplementedError()
    return BigInteger.ZERO
  }
}

fun legendereSymbol(n:BigInteger, p:BigInteger):BigInteger {
  if ((n.mod(p)).equals(BigIneger.ZERO)) {return BigInteger.ZERO}
  else {
    return if (n.modPow((p.subtract(BigIneger.ONE)).shiftRight(1)).equals(BigIneger.ONE)) BigInteger.ONE else BigInteger.ONE.negate()
  }
}
fun isPseudoPrime(n:BigInteger, certainty:Int):Boolean {
  for (p in Primes) {
    if ((n.mod(BigInteger(p))).equals(0)) {return false}
  }
  for (i in 0 until certainty) {
    var randChecker = BigInteger.valueOf(_Random.nextLong(java.lang.Long.MAX_VALUE-10000))
    if (!randChecker.modPow(p-1, p).equals(1)) {return false}
  }
  return true
}
