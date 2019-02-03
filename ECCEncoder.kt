import java.math.BigInteger
import kotlin.experimental.xor

fun encode(plainFileName:String,
           sharedSecretList:Array<BigInteger>,
           encodedFileName:String,
           bitLength:Int,
           random:java.security.SecureRandom) {
  val plainFile = java.io.File(plainFileName)
  val encodedFile = java.io.File(encodedFileName)
  var i = 0
  plainFile.forEachBlock(bitLength / 8) { buf, size ->
    val newBuffer = Array(bitLength / 8) {j -> if (i < size) buf[j] else random.nextInt().toByte()}
    val sharedSecret = sharedSecretList[i].toByteArray()
    val encodedBuffer = Array(bitLength / 8) {j->newBuffer[j].xor(sharedSecret[j])}
    encodedFile.appendBytes(encodedBuffer.toByteArray())
    i++
  }
}

fun decode(encodedFileName:String,
           sharedSecretList:Array<BigInteger>,
           bitLength:Int,
           decodedFileName:String) {
  val encodedFile = java.io.File(encodedFileName)
  val decodedFile = java.io.File(decodedFileName)
  var i = 0
  encodedFile.forEachBlock(bitLength / 8) {buf, size ->
    val newBuffer = Array(bitLength / 8) {j-> if (j < size) buf[j] else 0}
    val sharedSecret = sharedSecretList[i].toByteArray()
    val decodedBuffer = Array(bitLength / 8) {j->newBuffer[i].xor(sharedSecret[j])}
    decodedFile.appendBytes(decodedBuffer.toByteArray())
    i++
  }
}
