import java.math.BigInteger
import java.util.Random

fun main(args:Array<String>) {
  val random = Random()
  var ans : List<String>
  var curveCount = 0
  readCurve@while (true) {
    try {
      val curveSocket : java.io.File = java.io.File(curveSocketName)
      ans = curveSocket.readLines()
      break@readCurve
    } catch (e : java.io.FileNotFoundException) {
      Thread.sleep(10)
      println("curve Try:${curveCount++}")
      continue@readCurve
    }
  }
  val curveParam : List<BigInteger> = deserializeCurve(ans)
  println("Prime:${curveParam[0]}\na:${curveParam[1]}\nb:${curveParam[2]}\nGx:${curveParam[3]}\nGy:${curveParam[4]}")
  val curve : ECCCurve = ECCCurve(curveParam[0], curveParam[1], curveParam[2], curveParam[3], curveParam[4])
  var privateKey : BigInteger
  while (true) {
    privateKey = BigInteger(curve.Prime.bitLength(), random)
    println("privateKey try : ${privateKey}")
    if (privateKey.compareTo(curve.Prime) < 0) {
      break
    }
  }

  val bobPoint : ECCPoint = curve.G.scalarMultiple(privateKey)
  val bobPointSocket : java.io.File = java.io.File(bobPointSocketName)
  bobPointSocket.appendText("isO:${if (bobPoint.isO) 0 else 1}\nx:${bobPoint.x}\ny:${bobPoint.y}")
  println("isO:${if (bobPoint.isO) 0 else 1}\nx:${bobPoint.x}\ny:${bobPoint.y}")
  var alicePoint : ECCPoint
  var aliceCount = 0
  readalice@while (true) {
    try {
      val alicePointSocket : java.io.File = java.io.File(alicePointSocketName)
      val alicePointData : List<BigInteger?> = deserializePoint(alicePointSocket.readLines())
      alicePoint = ECCPoint(alicePointData[1], alicePointData[2], curve)
      break@readalice
    } catch (e : java.io.FileNotFoundException) {
      Thread.sleep(10)
      println("alice try : ${aliceCount++}")
      continue@readalice
    }
  }
  val resultPoint = alicePoint.scalarMultiple(privateKey)
  val sharedSecret = bobPoint.x
  println(sharedSecret)
}
