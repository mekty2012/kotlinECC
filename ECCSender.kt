import java.math.BigInteger

fun main(args:Array<String>) {
  val random = java.security.SecureRandom()
  val plainFile = java.io.File(plainText)
  val fileSize = plainFile.length()
  val encodingBitLength = args[1].toInt()
  
  val encodingCurveSocket = java.io.File(encodingCurveSocketName)
  val encodingAlicePointSocket = java.io.File(encodingAlicePointSocketName)
  val encodingBobPointSocket = java.io.File(encodingBobPointSocketName)
  
  val encodingCurveNameList = mutableListOf<String>()
  val encodingAlicePointNameList = mutableListOf<String>()
  val encodingBobPointNameList = mutableListOf<String>()
  for (i in 0 .. (fileSize / encodingBitLength)) {
    val curveRandomString = getRandomString(20, random)
    encodingCurveSocket.appendText(curveRandomString + "\n")
    encodingCurveNameList.add(curveRandomString)
    val alicePointRandomString = getRandomString(20, random)
    encodingAlicePointSocket.appendText(alicePointRandomString + "\n")
    encodingAlicePointNameList.add(alicePointRandomString)
    val bobPointRandomString = getRandomString(20, random)
    encodingBobPointSocket.appendText(bobPointRandomString + "\n")
    encodingBobPointNameList.add(bobPointRandomString)
  }
  
  val sharedSecrets = Array<BigInteger>(encodingCurveNameList.size) {BigInteger.ZERO}
  val threads = Array(encodingCurveNameList.size) {
    Thread {
      val result = sharedECCPointSender(encodingCurveNameList[it], encodingAlicePointNameList[it], encodingBobPointNameList[it], encodingBitLength, java.security.SecureRandom())
      sharedSecrets[it] = result.x ?: BigInteger.ZERO
    }
  }
  for (thread in threads) {
    thread.start()
  }
  for (thread in threads) {
    thread.join()
  }
  
  encode(plainText, sharedSecrets, cipherText, encodingBitLength, random)
  
  val macBitLength = args[2].toInt()
  
  val macCurveSocket = java.io.File(macCurveSocketName)
  val macAlicePointSocket = java.io.File(macAlicePointSocketName)
  val macBobPointSocket = java.io.File(macBobPointSocketName)
  
  val macCurveNameList = mutableListOf<String>()
  val macAlicePointNameList = mutableListOf<String>()
  val macBobPointNameList = mutableListOf<String>()
  for (i in 0 .. (fileSize / macBitLength)) {
    val curveRandomString = getRandomString(20, random)
    macCurveSocket.appendText(curveRandomString + "\n")
    macCurveNameList.add(curveRandomString)
    val alicePointRandomString = getRandomString(20, random)
    macAlicePointSocket.appendText(alicePointRandomString + "\n")
    macAlicePointNameList.add(alicePointRandomString)
    val bobPointRandomString = getRandomString(20, random)
    macBobPointSocket.appendText(bobPointRandomString + "\n")
    macBobPointNameList.add(bobPointRandomString)
  }
  
  
}

fun getRandomString(size:Int, random:java.security.SecureRandom):String {
  return (Array(size) {'a' + random.nextInt(26)}).joinToString("")
}

