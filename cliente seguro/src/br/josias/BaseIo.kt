package br.josias

import java.io.InputStream
import java.io.OutputStream
import java.net.SocketException
import javax.crypto.SecretKey

interface BaseIo {
    fun enviarPelaCoenxao(saida: OutputStream, msg: String, chave: SecretKey) {
        val bmsg = msg.toByteArray(Charsets.UTF_8)
        val criptmsg = Seguranca.criptografar(bmsg,chave)
        saida.write(criptmsg)
        saida.flush()
    }


    fun receberPelaConexao(entrada: InputStream,chave: SecretKey): String {
        try {
            val buffer = ByteArray(1024)
            val size = entrada.read(buffer)
            val criptmsg = buffer.copyOf(size)
            val bmsg = Seguranca.descriptografar(criptmsg,chave)
            val msg = bmsg.toString(Charsets.UTF_8)
            return msg
        } catch (e: NegativeArraySizeException) {
            return "fim;conexao encerrada"
        } catch (e: SocketException) {
            return "fim;conexao encerrada"
        }
    }
}
