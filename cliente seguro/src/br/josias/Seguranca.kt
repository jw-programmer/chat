package br.josias

import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.security.PublicKey
import javax.crypto.Cipher
import javax.crypto.SecretKey

object Seguranca{

    fun trocaChave(chave: SecretKey, saida: OutputStream, entrada: InputStream) {

        val transferenciaObjeto = ObjectOutputStream(saida)
        val recebimentoObjeto = ObjectInputStream(entrada)

        val publicaServidor = recebimentoObjeto.readObject() as PublicKey

        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicaServidor)
        val cripto = cipher.doFinal(chave.encoded)
        transferenciaObjeto.writeObject(cripto)




    }

    fun criptografar(textoLimpo: ByteArray, chave: SecretKey): ByteArray {
        val enigma = Cipher.getInstance("DES/ECB/PKCS5Padding")
        enigma.init(Cipher.ENCRYPT_MODE, chave)
       return enigma.doFinal(textoLimpo)
    }

    fun descriptografar(textoCifrado: ByteArray, chave: SecretKey): ByteArray {
        val enigma = Cipher.getInstance("DES/ECB/NoPadding")
        enigma.init(Cipher.DECRYPT_MODE, chave)
        return enigma.doFinal(textoCifrado)
    }
}