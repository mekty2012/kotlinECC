import java.math.BigInteger

const val curveSocketName = "curve"
const val alicePointSocketName = "alicePoint"
const val bobPointSocketName = "bobPoint"

fun legendereSymbol(n:BigInteger, p:BigInteger):BigInteger {
  if ((n.mod(p)).equals(BigInteger.ZERO)) {return BigInteger.ZERO}
  else {
    return if (n.modPow((p.subtract(BigInteger.ONE)).shiftRight(1), p).equals(BigInteger.ONE)) BigInteger.ONE else BigInteger.ONE.negate()
  }
}
fun isPseudoPrime(n:BigInteger, certainty:Int):Boolean {
  for (p in Primes) {
    if ((n.mod(BigInteger(p))).equals(BigInteger.ZERO)) {return false}
  }
  for (i in 0 until certainty) {
    val randChecker = BigInteger.valueOf(_Random.nextLong())
    if (!randChecker.modPow(randChecker.subtract(BigInteger.ONE), randChecker).equals(BigInteger.ONE)) {return false}
  }
  return true
}
fun sqrt(n:BigInteger):BigInteger {
  var div = BigInteger.ZERO.setBit(n.bitLength() / 2)
  var div2 = div
  while (true) {
    val y = div.add(n.divide(div)).shiftRight(1)
    if (y.equals(div) || y.equals(div2))
      return y
    div2 = div
    div = y
  }
}

fun deserializeCurve(text:List<String>):List<BigInteger> {
  assert(text.size == 5)
  val regex = Regex("[A-Za-z]+:([0-9]+)")
  val ans = text.map{it->BigInteger(regex.find(it)!!.groupValues[1])}
  return ans
}

fun deserializePoint(text:List<String>):List<BigInteger?> {
  assert(text.size == 3)
  val regex = Regex("[A-Za-z]+:([0-9]+)")
  if (text[0] == "isO:0") {
    return listOf(BigInteger.valueOf(0), null, null)
  }
  val ans = text.map{it->BigInteger(regex.find(it)!!.groupValues[1])}
  return ans
}
