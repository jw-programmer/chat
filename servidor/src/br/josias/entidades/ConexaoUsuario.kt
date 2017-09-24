package br.josias.entidades

import br.josias.interfaces.Observavel
import java.net.Socket
import java.nio.charset.Charset

/**
 * Created by alunom35 on 18/09/2017.
 */
class ConexaoUsuario(val socket: Socket, val servidor: Servidor,override val id:String) : Runnable, Observavel {

    fun receberPelaConexao(): String {
        try{
            val sin = socket.getInputStream()
            val buffer = ByteArray(1024)
            val size = sin.read(buffer)
            val bmsg = buffer.copyOf(size)
            val msg = bmsg.toString(Charsets.UTF_8)
            return msg
        }catch (e: NegativeArraySizeException){
            return "cliente quitou"
        }
    }

    fun enviarPelaConexao(msg: String) {
        val sou = socket.getOutputStream()
        val bMsg = msg.toByteArray()
        sou.write(bMsg)
        sou.flush()
    }

    fun msgNovoUsuario(parans: List<String>) {
        println("chego em novo usuario")
        val msg = "novo;${parans[1]};escape"
        println(msg)
        enviarPelaConexao(msg)
    }

    fun atualizarMsg(parans: List<String>) {
        if(parans[2] == "privado"){
            if(parans[4] == id){
                val msg = "privado;Apenas para você!!! Usuario ${parans[1]} diz: ${parans[3]}"
                println(msg)
                enviarPelaConexao(msg)
            }
        }else {
            val msg = "publico;Usuario ${parans[1]} diz: ${parans[3]}"
            println(msg)
            enviarPelaConexao(msg)
        }
    }

    fun msgUsuarioLogoff(parans: List<String>) {
        val msg = "logoff;Usuario ${parans[1]} estar desconectando"
        println(msg)
        enviarPelaConexao(msg)

    }

    private fun listaUsuario(parans: List<String>) {
        val msg = "lista${servidor.usuarios()}"
        enviarPelaConexao(msg)
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
                if(msg.split(';')[0] == "logoff")
                    controle = false
            }
        }

        socket.close()
        servidor.fecharConexao(this)

    }

}