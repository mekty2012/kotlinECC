import java.math.BigInteger

@Throws(java.io.FileNotFoundException::class)
fun macCheck(encodedFileName:String, macFileName:String, sharedSecret:BigInteger):Boolean {
  val encodedFile = java.io.File(encodedFileName)
  val macFile = java.io.File(macFileName)
  if (!encodedFile.exists()) {
    throw java.io.FileNotFoundException("encodedFile : $encodedFile not found.")
  }
  if (!macFile.exists()) {
    throw java.io.FileNotFoundException("macFileName : $macFileName not found.")
  }
  
  return true
}
