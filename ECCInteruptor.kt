import java.math.BigInteger

fun main() {
  val curveSocket = java.io.File(encodingCurveSocketName)
  val curveParam : List<BigInteger>
  while (true) {
    if (curveSocket.exists()) {
      val ansList : List<String> = curveSocket.readLines()
      curveParam = deserializeCurve(ansList)
      break
    }
    else {
      Thread.sleep(10)
    }
  }
  val curve = ECCCurve(curveParam[0], curveParam[1], curveParam[2], curveParam[3], curveParam[4])
  val alicePointSocket = java.io.File(encodingAlicePointSocketName)
  val bobPointSocket = java.io.File(encodingBobPointSocketName)
  val alicePointData : Pair<BigInteger?, BigInteger?>
  val bobPointData : Pair<BigInteger?, BigInteger?>
  while (true) {
    if (alicePointSocket.exists() && bobPointSocket.exists()) {
      val aliceList : List<BigInteger?> = deserializePoint(alicePointSocket.readLines())
      val bobList : List<BigInteger?> = deserializePoint(bobPointSocket.readLines())
      alicePointData = Pair(aliceList[1], aliceList[2])
      bobPointData = Pair(bobList[1], bobList[2])
      break
    }
    else {
      Thread.sleep(10)
    }
  }
  val start = System.currentTimeMillis()
  val alicePoint = ECCPoint(alicePointData.first, alicePointData.second, curve)
  val bobPoint = ECCPoint(bobPointData.first, bobPointData.second, curve)
  val originPoint = curve.generator
  val log = discreteLogarithm(originPoint, alicePoint)
  val resultPoint = bobPoint.scalarMultiple(log)
  val takenSecret = resultPoint.x
  println("Secret : $takenSecret")
  val diff = System.currentTimeMillis() - start
  println(String.format("Time : %d (ms)", diff))
}

fun discreteLogarithm(origin : ECCPoint, point : ECCPoint):BigInteger {
  if (origin.curve != point.curve) {
    throw ECCException("Point not on same curve.")
  }
  return when {
    origin.curve.prime.bitLength() <= 10 -> simpleDiscreteLogarithm(origin, point)
    origin.curve.prime.bitLength() <= 20 -> bsgsDiscreteLogarithm(origin, point)
    else -> {
      var answer : BigInteger? = null
      val setList = listOf(listOf(0,1,2), listOf(0,2,1), listOf(1,0,2), listOf(1,2,0), listOf(2,0,1), listOf(2,1,0))
      for (i in 0..5) {
        answer = rhoDiscreteLogarithm(origin, point, setList[i])
        if (answer != null) {break}
      }
      if (answer == null) {throw ECCException("Rho Discrete Logarithm Failed")}
      answer
    }
  }
}
@Throws(ECCException::class)
fun simpleDiscreteLogarithm(origin:ECCPoint, point:ECCPoint):BigInteger { // do not use on large numbers
  var mult = origin
  var n = BigInteger.ONE
  while (mult != point) {
    mult = mult.addPoint(origin)
    n = n.add(BigInteger.ONE)
    if (n >= origin.curve.getGeneratorOrder()) {
      throw ECCException("Point not on field of generator")
    }
  }
  return n
}
@Throws(ECCException::class)
fun bsgsDiscreteLogarithm(origin:ECCPoint, point:ECCPoint):BigInteger { // do not use on large numbers
  val pointSet = mutableMapOf<ECCPoint, BigInteger>()
  var i = BigInteger.ONE
  val sq = sqrt(origin.curve.getGeneratorOrder())
  var babyPoint = origin
  while (i <= sq) { // baby step
    pointSet[babyPoint] = i
    babyPoint = babyPoint.addPoint(origin)
    i = i.add(BigInteger.ONE)
  }
  
  if (pointSet.contains(point)) {
    return pointSet.getOrDefault(point, BigInteger.ZERO) // if baby step contains our result, then return index
  }
  
  val gPrime = origin.scalarMultiple(sq).negatePoint()
  var giantPoint = gPrime.addPoint(point)
  
  i = BigInteger.ONE
  while (i <= sq) {
    if (pointSet.contains(giantPoint)) {
      return i.add(pointSet.getOrDefault(giantPoint, BigInteger.ZERO))
    }
    i = i.add(BigInteger.ONE)
    giantPoint = giantPoint.addPoint(gPrime)
  }
  throw ECCException("Point Not on field of generator")
}

fun rhoDiscreteLogarithm(origin : ECCPoint, point : ECCPoint, setNum:List<Int>):BigInteger? {
  assert(setNum.size == 3)
  assert(0 in setNum && 1 in setNum && 2 in setNum)
  var i = BigInteger.ONE
  var pointI = ECCPoint(null, null, origin.curve)
  var alphaI = BigInteger.ZERO
  var betaI = BigInteger.ZERO
  var pointTwoI = ECCPoint(null, null, origin.curve)
  var alphaTwoI = BigInteger.ZERO
  var betaTwoI = BigInteger.ZERO
  while (true) {
    val (tempPoint, tempAlpha, tempBeta) = nextStep(pointI, alphaI, betaI, origin, point, setNum)
    pointI = tempPoint
    alphaI = tempAlpha
    betaI = tempBeta
    val (tempTwoPoint, tempTwoAlpha, tempTwoBeta) = nextStep(pointTwoI, alphaTwoI, betaTwoI, origin, point, setNum)
    val (tempTwoTwoPoint, tempTwoTwoAlpha, tempTwoTwoBeta) = nextStep(tempTwoPoint, tempTwoAlpha, tempTwoBeta, origin, point, setNum)
    pointTwoI = tempTwoTwoPoint
    alphaTwoI = tempTwoTwoAlpha
    betaTwoI = tempTwoTwoBeta
    if (pointI.x == pointTwoI.x && pointI.y == pointTwoI.y) {
      val r = betaI.subtract(betaTwoI).mod(origin.curve.prime) // origin^alphaI point^betaI = origin^alphaTwoI point^betaTwoI
      if (r == BigInteger.ZERO) {
        return null
      }
      return discreteDivision(alphaTwoI.subtract(alphaI).mod(origin.curve.prime), r, origin.curve.getPointCount())
    }
    else {
      i = i.add(BigInteger.ONE)
    }
  }
}

fun setNumber(point:ECCPoint):Int {
  return point.x?.mod(BigInteger.valueOf(3L))?.toInt() ?: 0
}

fun nextStep(point:ECCPoint, alphaNum:BigInteger, betaNum:BigInteger, alpha:ECCPoint, beta:ECCPoint, set:List<Int>):Triple<ECCPoint, BigInteger, BigInteger> {
  return when (set[setNumber(point)]) {
    0-> {
      Triple(point.addPoint(beta), alphaNum, betaNum.add(BigInteger.ONE))
    }
    1-> {
      Triple(point.addPoint(point), alphaNum.multiply(BigInteger.valueOf(2L)), betaNum.multiply(BigInteger.valueOf(2L)))
    }
    else -> {
      Triple(point.addPoint(alpha), alphaNum.add(BigInteger.ONE), betaNum)
    }
  }
}
