import java.math.BigInteger

fun main(args:Array<String>) {
  
  val encodingBitLength = args[1].toInt()
  val encodingCurveSocket = java.io.File(encodingCurveSocketName)
  val encodingAlicePointSocket = java.io.File(encodingAlicePointSocketName)
  val encodingBobPointSocket = java.io.File(encodingBobPointSocketName)
  
  val encodingCurveNameList = encodingCurveSocket.readLines()
  val encodingAlicePointNameList = encodingAlicePointSocket.readLines()
  val encodingBobPointNameList = encodingBobPointSocket.readLines()
  
  val sharedSecrets = Array<BigInteger>(encodingCurveNameList.size) {BigInteger.ZERO}
  
  val threads = Array(encodingCurveNameList.size) {
    Thread {
      val result = sharedECCPointReceiver(encodingCurveNameList[it], encodingAlicePointNameList[it], encodingBobPointNameList[it], encodingBitLength, java.security.SecureRandom())
      sharedSecrets[it] = result.x ?: BigInteger.ZERO
    }
  }
  for (thread in threads) {
    thread.start()
  }
  for (thread in threads) {
    thread.join()
  }
  
  Thread.sleep(1000) // need to check whether encoding ended or not
  decode(cipherText, sharedSecrets, encodingBitLength, decodedText)
  
  val macBitLength = args[2].toInt()
  
  val macCurveSocket = java.io.File(macCurveSocketName)
  val macAlicePointSocket = java.io.File(macAlicePointSocketName)
  val macBobPointSocket = java.io.File(macBobPointSocketName)
  
  val macCurveNameList = macCurveSocket.readLines()
  val macAlicePointNameList = macAlicePointSocket.readLines()
  val macBobPointNameList = macBobPointSocket.readLines()

  
}
