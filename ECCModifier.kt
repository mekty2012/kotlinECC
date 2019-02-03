fun main() {
  val encodedFile = java.io.File(cipherText)
  val random = java.security.SecureRandom()
  while (!encodedFile.exists()) {
    Thread.sleep(10)
  }
  val length = encodedFile.length()
  encodedFile.delete()
  for (i in 0 until (length / 256)) {
    if (i != length / 256 - 1) {
      encodedFile.writeBytes(ByteArray(256) {random.nextInt().toByte()})
    }
    else {
      encodedFile.writeBytes(ByteArray((length % 256).toInt()) {random.nextInt().toByte()})
    }
  }
}
