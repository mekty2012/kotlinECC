import java.math.BigInteger
import java.util.Random

fun main(args:Array<String>) {
  val random = Random()
  val bitLength : Int = readLine()!!.toInt()
  val curve : ECCCurve = getECCCurve(bitLength)
  val curveSocket : java.io.File = java.io.File(curveSocketName)
  println("Prime:${curve.Prime}\na:${curve.a}\nb:${curve.b}\nGx:${curve.Gx}\nGy:${curve.Gy}")
  curveSocket.appendText("Prime:${curve.Prime}\na:${curve.a}\nb:${curve.b}\nGx:${curve.Gx}\nGy:${curve.Gy}")
  var privateKey : BigInteger
  while (true) {
    privateKey = BigInteger(bitLength, random)
    println("privateKey try : ${privateKey}")
    if (privateKey.compareTo(curve.Prime) < 0) {
      break
    }
  }
  val alicePoint : ECCPoint = curve.G.scalarMultiple(privateKey)
  val alicePointSocket : java.io.File = java.io.File(alicePointSocketName)
  alicePointSocket.appendText("isO:${if (alicePoint.isO) 0 else 1}\nx:${alicePoint.x}\ny:${alicePoint.y}")
  println("isO:${if (alicePoint.isO) 0 else 1}\nx:${alicePoint.x}\ny:${alicePoint.y}")
  var bobPoint : ECCPoint
  var bobCount = 0
  read@while (true) {
    try {
      val bobPointSocket : java.io.File = java.io.File(bobPointSocketName)
      val bobPointData : List<BigInteger?> = deserializePoint(bobPointSocket.readLines())
      bobPoint = ECCPoint(bobPointData[1], bobPointData[2], curve)
      break@read
    } catch (e : java.io.FileNotFoundException) {
      Thread.sleep(10)
      println("bob Try : ${bobCount++}")
      continue@read
    }
  }
  val resultPoint = bobPoint.scalarMultiple(privateKey)
  val sharedSecret = bobPoint.x
  println(sharedSecret)
}


