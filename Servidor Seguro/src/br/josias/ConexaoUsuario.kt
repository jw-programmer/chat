package br.josias

import java.net.Socket
import javax.crypto.Cipher
import javax.crypto.SecretKey

/**
 * Created by alunom35 on 18/09/2017.
 */
class ConexaoUsuario(val socket: Socket, val servidor: Servidor, override val id: String, val chave: SecretKey) : Runnable, Observavel {

    fun criptografar(msg: ByteArray, chave: SecretKey): ByteArray {
        val enigma = Cipher.getInstance("DES/ECB/PKCS5Padding")
        enigma.init(Cipher.ENCRYPT_MODE, chave)
        return enigma.doFinal(msg)
    }

    fun descriptografar(msg: ByteArray, chave: SecretKey): ByteArray {
        val enigma = Cipher.getInstance("DES/ECB/NoPadding")
        enigma.init(Cipher.DECRYPT_MODE, chave)
        return enigma.doFinal(msg)
    }

    fun receberPelaConexao(): String {
        try {
            val sin = socket.getInputStream()
            val buffer = ByteArray(1024)
            val size = sin.read(buffer)
            val criptmsg = buffer.copyOf(size)
            val bmsg = descriptografar(criptmsg, chave)
            val msg = bmsg.toString(Charsets.UTF_8)
            return msg
        } catch (e: NegativeArraySizeException) {
            return "cliente quitou"
        }
    }

    fun enviarPelaConexao(msg: String) {
        val sou = socket.getOutputStream()
        val bMsg = msg.toByteArray()
        val cripmsg = criptografar(bMsg, chave)
        sou.write(cripmsg)
        sou.flush()
    }

    fun msgNovoUsuario(parans: List<String>) {
        println("chego em novo usuario")
        val msg = "novo;${parans[1]};escape"
        println(msg)
        enviarPelaConexao(msg)
    }

    fun atualizarMsg(parans: List<String>) {
        if (parans[2] == "privado") {
            if (parans[4] == id) {
                val msg = "privado;Apenas para você!!! Usuario ${parans[1]} diz: ${parans[3]};escape"
                println(msg)
                enviarPelaConexao(msg)
            }
        } else {
            val msg = "publico;Usuario ${parans[1]} diz: ${parans[3]};escape"
            println(msg)
            enviarPelaConexao(msg)
        }
    }

    fun msgUsuarioLogoff(parans: List<String>) {
        val msg = "logoff;Usuario ${parans[1]} estar desconectando;escape"
        println(msg)
        enviarPelaConexao(msg)

    }

    private fun listaUsuario(parans: List<String>) {
        if (parans[2] == "privado") {
            if (parans[1] == id) {
                val msg = "lista${servidor.usuarios()};escape"
                enviarPelaConexao(msg)
            }
        }

    }

    override fun atualizar(arg: String) {
        //o protocolo têm a seguinte configuração
        //tipo;nomeUsuario;privacidade;mensagem;[recepetor se for privada]
        var parans = arg.split(";")
        val tipo = parans[0]
        when (tipo) {
            "novo" -> msgNovoUsuario(parans)
            "mensagem" -> atualizarMsg(parans)
            "logoff" -> msgUsuarioLogoff(parans)
            "listarUsuario" -> listaUsuario(parans)
        }
    }


    override fun run() {
        var controle = true
        while (controle) {
            var msg: String? = receberPelaConexao()
            if (msg != null) {
                servidor.atualizarStatus(msg)
                if (msg.split(';')[0] == "logoff")
                    controle = false
            }
        }

        socket.close()
        servidor.fecharConexao(this)

    }

}