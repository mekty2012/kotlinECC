import java.math.BigInteger
import java.security.SecureRandom

val _random = SecureRandom()

const val debug = true
const val encodingCurveSocketName = "encodingCurve"
const val encodingAlicePointSocketName = "encodingAlicePoint"
const val encodingBobPointSocketName = "encodingBobPoint"
const val plainText = "PlainText.txt"
const val cipherText = "EncodedPlainText.txt"
const val decodedText = "DecodedPlainText.txt"
const val macCurveSocketName = "macCurve"
const val macAlicePointSocketName = "macAlicePoint"
const val macBobPointSocketName = "macBobPoint"
const val macName = "MACEncodedPlainText.txt"


val SmallPrimes = listOf("2","3","5","7","11","13","17","19","23","29","31","37","41",
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

fun legendreSymbol(n:BigInteger, p:BigInteger):BigInteger {
  return if (n.mod(p) == BigInteger.ZERO) {
    BigInteger.ZERO
  }
  else {
    if (n.modPow((p - BigInteger.ONE) shr 1, p) == BigInteger.ONE) BigInteger.ONE else BigInteger.ONE.negate()
  }
}

fun isPseudoPrime(n:BigInteger, certainty:Int):Boolean {
  for (p in SmallPrimes) {
    if (n.mod(BigInteger(p)) == BigInteger.ZERO) {return false}
  }
  for (i in 0 until certainty) {
    val randChecker = BigInteger.valueOf(_random.nextLong())
    if (!randChecker.modPow(randChecker - BigInteger.ONE, randChecker) == BigInteger.ONE) {return false}
  }
  return true
}

fun sqrt(n:BigInteger):BigInteger {
  var div = BigInteger.ZERO.setBit(n.bitLength() / 2)
  var div2 = div
  while (true) {
    val y = (div + (n / div)) shr 1
    if (y == div || y == div2)
      return y
    div2 = div
    div = y
  }
}

fun deserializeCurve(text:List<String>):List<BigInteger> {
  assert(text.size == 5)
  val regex = Regex("[A-Za-z]+:([0-9a-f]+)")
  return text.map{BigInteger(regex.find(it)!!.groupValues[1], 16)}
}

fun deserializePoint(text:List<String>):List<BigInteger?> {
  assert(text.size == 3)
  val regex = Regex("[A-Za-z]+:([0-9a-f]+)")
  if (text[0] == "isO:0") {
    return listOf(BigInteger.valueOf(0), null, null)
  }
  return text.map{BigInteger(regex.find(it)!!.groupValues[1], 16)}
}

fun debugPrintln(text:Any?) {
  if (debug) {println(text)}
}

fun sharedECCPointSender(curveFileName:String, alicePointFileName:String, bobPointFileName:String, bitLength:Int, random:SecureRandom):ECCPoint {
  // Requires called with sharedECCPointReceiver in different thread.
  val curve = getECCCurve(bitLength, random)
  val curveFile = java.io.File(curveFileName)
  curveFile.appendText(""+
    "modular:${curve.prime.toString(16)}\n" +
    "a:${curve.a.toString(16)}\n" +
    "b:${curve.b.toString(16)}\n" +
    "generatorXCoordinate:${curve.generatorXCoordinate.toString(16)}\n" +
    "generatorYCoordinate:${curve.generatorYCoordinate.toString(16)}")
  var privateKey : BigInteger
  while (true) {
    privateKey = BigInteger(bitLength, random)
    if (privateKey < curve.prime) {break}
  }
  val alicePoint = curve.generator.scalarMultiple(privateKey)
  val alicePointFile = java.io.File(alicePointFileName)
  alicePointFile.appendText(""+
    "isO:${if (alicePoint.isO) 0 else 1}\n" +
    "x:${alicePoint.x?.toString(16)}\n" +
    "y:${alicePoint.y?.toString(16)}")
  val bobPointFile = java.io.File(bobPointFileName)
  val bobPoint : ECCPoint
  while (true) {
    if (bobPointFile.exists()) {
      val bobPointData = deserializePoint(bobPointFile.readLines())
      bobPoint = ECCPoint(bobPointData[1], bobPointData[2], curve)
      break
    }
    else {
      Thread.sleep(10)
    }
  }
  return bobPoint.scalarMultiple(privateKey)
}

fun sharedECCPointReceiver(curveFileName:String, alicePointFileName:String, bobPointFileName:String, bitLength:Int, random:SecureRandom):ECCPoint {
  // Requires called with sharedECCPointSender in different thread.
  val curveFile = java.io.File(curveFileName)
  val curve : ECCCurve
  while (true) {
    if (curveFile.exists()) {
      val curveData = deserializeCurve(curveFile.readLines())
      curve = ECCCurve(curveData[0], curveData[1], curveData[2], curveData[3], curveData[4])
      break
    }
    else {
      Thread.sleep(10)
    }
  }
  val privateKey = BigInteger(bitLength, random)
  val bobPointFile = java.io.File(bobPointFileName)
  val bobPoint = curve.generator.scalarMultiple(privateKey)
  bobPointFile.appendText(""+
    "isO:${if (bobPoint.isO) 0 else 1}\n" +
    "x:${bobPoint.x?.toString(16)}\n" +
    "y:${bobPoint.y?.toString(16)}")
  val alicePointFile = java.io.File(alicePointFileName)
  val alicePoint:ECCPoint
  while (true) {
    if (alicePointFile.exists()) {
      val alicePointData = deserializePoint(alicePointFile.readLines())
      alicePoint = ECCPoint(alicePointData[1], alicePointData[2], curve)
      break
    }
    else {
      Thread.sleep(10)
    }
  }
  return alicePoint.scalarMultiple(privateKey)
}

fun discreteDivision(dividend:BigInteger, divisor:BigInteger, prime:BigInteger):BigInteger {
  val inv = divisor.modPow(prime - BigInteger.valueOf(2L), prime)
  return inv * dividend
}

fun chineseRemainderDecomposition(n:BigInteger):Int {
  // given BigInteger n, find smallest i such that p1 * p2 * ... pi >= n
  var result = BigInteger.ONE
  var i = 0
  while (n > result) {
    result *= BigInteger.valueOf(SmallPrimes[i].toLong())
    i++
  }
  return i
}

fun chineseRemainderComposition(mod:List<BigInteger>):BigInteger {
  // given list of m = mod[i] (mod p[i]), return original m (mod p[0] * ... p[n])
  TODO()
}
