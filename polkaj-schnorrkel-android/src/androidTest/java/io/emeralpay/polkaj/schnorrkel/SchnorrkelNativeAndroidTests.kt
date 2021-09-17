package io.emeralpay.polkaj.schnorrkel

import io.emeraldpay.polkaj.schnorrkel.Schnorrkel
import io.emeraldpay.polkaj.schnorrkel.SchnorrkelException
import io.emeraldpay.polkaj.schnorrkel.SchnorrkelNative
import org.apache.commons.codec.binary.Hex
import org.junit.Test
import java.math.BigInteger
import java.security.SecureRandom
import kotlin.test.*

class SchnorrkelNativeAndroidTests {

    private fun ByteArray.encodeHex() : String = String(Hex.encodeHex(this))
    private fun String.decodeHex() : ByteArray = Hex.decodeHex(toCharArray())

    private val schnorrkel = SchnorrkelNative()
    private val key1 = Schnorrkel.KeyPair(
        "46ebddef8cd9bb167dc30878d7113b7e168e6f0646beffd77d69d39bad76b47a".decodeHex(),
        "28b0ae221c6bb06856b287f60d7ea0d98552ea5a16db16956849aa371db3eb51fd190cce74df356432b410bd64682309d6dedb27c76845daf388557cbac3ca34".decodeHex()
    )

    @Test
    fun canSign(){
        val result = schnorrkel.sign("".toByteArray(), key1)
        assertNotNull(result)
        assertEquals(64, result.size)
    }

    @Test
    fun throwsErrorOnShortSk(){
        assertFailsWith<SchnorrkelException>("SecretKey must be 64 bytes in length"){
            schnorrkel.sign("".toByteArray(),
                Schnorrkel.KeyPair(
                    "46ebddef8cd9bb167dc30878d7113b7e168e6f0646beffd77d69d39bad76b47a".decodeHex(),
                    "28b0".decodeHex()
                )
            )
        }
    }

    @Test
    fun signatureIsValid(){
        val msg = "hello".toByteArray()
        val sig = schnorrkel.sign(msg, key1)
        assertTrue {
            schnorrkel.verify(sig, msg, key1)
        }
    }

    @Test
    fun modifiedSignatureIsInvalid(){
        val msg = "hello".toByteArray()
        val sig = schnorrkel.sign(msg, key1)
        assertTrue {
            schnorrkel.verify(sig, msg, key1)
        }
        assertFalse {
            sig[0] = (sig[0] + 1).toByte()
            schnorrkel.verify(sig, msg, key1)
        }
    }

    @Test
    fun differentSignatureIsInvalid(){
        val msg = "hello".toByteArray()
        val sig1 = schnorrkel.sign(msg, key1)
        val sig2 = schnorrkel.sign("hello2".toByteArray(), key1)
        assertTrue {
            schnorrkel.verify(sig1, msg, key1)
        }
        assertFalse {
            schnorrkel.verify(sig2, msg, key1)
        }
    }

    @Test
    fun throwsErrorOnInvalidSignature(){
        val msg = "hello".toByteArray()
        assertFailsWith<SchnorrkelException> {
            schnorrkel.verify("00112233".decodeHex(), msg, key1)
        }
    }

    @Test
    fun throwsErrorOnInvalidPubkey(){
        val msg = "hello".toByteArray()
        val sig = schnorrkel.sign(msg, key1)
        assertFailsWith<SchnorrkelException> {
            schnorrkel.verify(sig, msg, Schnorrkel.PublicKey("11223344".decodeHex()))
        }
    }

    @Test
    fun generatesWorkingKey(){
        val random = SecureRandom()
        val msg = "hello".toByteArray()
        val keypair = schnorrkel.generateKeyPair(random)

        assertNotNull(keypair)
        assertEquals(Schnorrkel.PUBLIC_KEY_LENGTH, keypair.publicKey.size)
        assertEquals(Schnorrkel.SECRET_KEY_LENGTH, keypair.secretKey.size)
        assertNotEquals(BigInteger.ZERO, BigInteger(1, keypair.publicKey))
        assertNotEquals(BigInteger.ZERO, BigInteger(1, keypair.secretKey))

        val sig = schnorrkel.sign(msg, keypair)
        assertTrue {
            schnorrkel.verify(sig, msg, keypair)
        }
    }

    @Test
    fun generatesKeyFromDefaultSecureRandom(){
        val keypair = schnorrkel.generateKeyPair()
        assertNotNull(keypair)
        assertEquals(Schnorrkel.PUBLIC_KEY_LENGTH, keypair.publicKey.size)
        assertEquals(Schnorrkel.SECRET_KEY_LENGTH, keypair.secretKey.size)
        assertNotEquals(BigInteger.ZERO, BigInteger(1, keypair.publicKey))
        assertNotEquals(BigInteger.ZERO, BigInteger(1, keypair.secretKey))
    }

    @Test
    fun generatesFromSeed(){
        val keypair = schnorrkel.generateKeyPairFromSeed("fac7959dbfe72f052e5a0c3c8d6530f202b02fd8f9f5ca3580ec8deb7797479e".decodeHex())
        assertNotNull(keypair)
        assertEquals(Schnorrkel.PUBLIC_KEY_LENGTH, keypair.publicKey.size)
        assertEquals(Schnorrkel.SECRET_KEY_LENGTH, keypair.secretKey.size)
        assertNotEquals(BigInteger.ZERO, BigInteger(1, keypair.publicKey))
        assertNotEquals(BigInteger.ZERO, BigInteger(1, keypair.secretKey))
        assertEquals("46ebddef8cd9bb167dc30878d7113b7e168e6f0646beffd77d69d39bad76b47a", keypair.publicKey.encodeHex())
    }

    @Test
    fun deriveKey(){
        val seed = "fac7959dbfe72f052e5a0c3c8d6530f202b02fd8f9f5ca3580ec8deb7797479e".decodeHex()
        val cc = Schnorrkel.ChainCode.from("14416c696365".decodeHex()) // Alice
        val base = schnorrkel.generateKeyPairFromSeed(seed)
        val keypair = schnorrkel.deriveKeyPair(base, cc)
        assertEquals("d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d", keypair.publicKey.encodeHex())
    }

    @Test
    fun deriveKeySoft(){
        val seed = "fac7959dbfe72f052e5a0c3c8d6530f202b02fd8f9f5ca3580ec8deb7797479e".decodeHex()
        val cc = Schnorrkel.ChainCode("0c666f6f00000000000000000000000000000000000000000000000000000000".decodeHex())
        val base = schnorrkel.generateKeyPairFromSeed(seed)
        val keypair = schnorrkel.deriveKeyPairSoft(base, cc)
        assertEquals("40b9675df90efa6069ff623b0fdfcf706cd47ca7452a5056c7ad58194d23440a", keypair.publicKey.encodeHex())
    }

    @Test
    fun deriveSoftPublicKey(){
        val pub = "46ebddef8cd9bb167dc30878d7113b7e168e6f0646beffd77d69d39bad76b47a".decodeHex()
        val cc = Schnorrkel.ChainCode.from("0c666f6f00000000000000000000000000000000000000000000000000000000".decodeHex())
        val softKey = schnorrkel.derivePublicKeySoft(Schnorrkel.PublicKey(pub), cc)
        assertEquals("40b9675df90efa6069ff623b0fdfcf706cd47ca7452a5056c7ad58194d23440a", softKey.publicKey.encodeHex())
    }

}