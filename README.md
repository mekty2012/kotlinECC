# kotlinECC
Elliptic Curve Cryptography

## ECCCurve.kt
Elliptic Curve
compile/test passed
## ECCPoint.kt
Point Elliptic Curve
compile/test passed
## ECCSender.kt
ECC sharedsecret sender
compile/test passed
checked for high number (bit length 512)
## ECCReceiver.kt
ECC sharedsecret receiver
compile/test passed
checked for high number (bit length 512)
## ECCUtils.kt
util functions that other classes use.
compile/test passed

## TODO()
### ECCEncoder.kt
based on shared secret, encrypt plaintext.
### ECCDecoder.kt
based on shared secret, decrypt encrypted text.
### ECCInteruptor.kt 
try to
1. get shared secret
2. modify encrypted text and not detected
3. pretend as alice/eve
