package br.josias.entidades

import java.io.InputStream
import java.io.OutputStream
import java.net.SocketException
import java.nio.charset.Charset

interface BaseIo {
    fun enviarPelaCoenxao(saida: OutputStream, msg: String) {
        val bmsg = msg.toByteArray(Charsets.UTF_8)
        saida.write(bmsg)
        saida.flush()
    }

    fun receberPelaConexao(entrada: InputStream): String {
        try {
            val buffer = ByteArray(1024)
            val size = entrada.read(buffer)
            val bmsg = buffer.copyOf(size)
            val msg = bmsg.toString(Charsets.UTF_8)
            return msg
        } catch (e: NegativeArraySizeException) {
            return "fim;conexao encerrada"
        } catch (e: SocketException) {
            return "fim;conexao encerrada"
        }
    }
}
