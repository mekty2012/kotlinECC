import java.math.BigInteger

@Throws(java.io.FileNotFoundException::class)
fun macCreate(encodedFileName:String, sharedSecret:BigInteger):String {
  val encodedFile = java.io.File(encodedFileName)
  if (!encodedFile.exists()) {
    throw java.io.FileNotFoundException("encodedFile : $encodedFile not found.")
  }
  encodedFile.forEachBlock(256) {array, size->
    TODO()
  }
  return "MAC$encodedFileName"
}
