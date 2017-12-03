package br.josias

import java.io.OutputStream
import java.net.Socket
import java.util.*
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Created by alunom35 on 18/09/2017.
 */
class Chat(val ipServidor: String, val portaServidor: Int, val usuario: Usuario) : BaseIo {
    var mensagensPublicas: MutableList<String>

    val chaveUnica: SecretKey = KeyGenerator.getInstance("DES").generateKey()

    init {
        mensagensPublicas = LinkedList()
    }

    fun deslogar(entrada: String, saida: OutputStream): Boolean {
        val msgLogg = "logoff;${usuario};publica;escape"
        enviarPelaCoenxao(saida, msgLogg, chaveUnica)
        return false
    }

    fun mensagemPublica(entrada: String, saida: OutputStream) {
        val msgLogg = "mensagem;${usuario};publica;${entrada};escape"
        enviarPelaCoenxao(saida, msgLogg, chaveUnica)
    }

    fun mensagemPrivada(entrada: String, saida: OutputStream) {
        println("digite o usuario que vai receber a mensagem")
        val destinatario = readLine()
        println("digite a sua mensagem")
        val mensagem = readLine()
        val msg = "mensagem;${usuario};privado;${mensagem};${destinatario};escape"
        enviarPelaCoenxao(saida, msg, chaveUnica)
    }

    fun listaUsuario(msg: String, saida: OutputStream) {
        val msg = "listarUsuario;${usuario};privado;escape"
        enviarPelaCoenxao(saida, msg, chaveUnica)
    }

    fun iniciarChat() {
        val conexao: Socket = Socket(ipServidor, portaServidor)
        Seguranca.trocaChave(chaveUnica, conexao.getOutputStream(), conexao.getInputStream())
        val saida = conexao.getOutputStream()
        var teste = "novo;${usuario};publica;escape"
        enviarPelaCoenxao(saida, teste, chaveUnica)
        val feed = Thread(Feed(conexao.getInputStream(), saida, this))
        feed.start()
        var controle = true
        while (controle) {
            if (feed.isAlive) {
                println("digite a mensagem(digite 'logoff' para desconectar ou 'privado' para uma mensagem particular." +
                        "Para ver os participantes conectados digite 'lista'):")
                var msg = readLine()
                when (msg) {
                    "logoff" -> {
                        controle = deslogar(msg, saida)
                    }
                    "privado" -> mensagemPrivada(msg, saida)
                    "lista" -> listaUsuario(msg, saida)
                    else -> mensagemPublica(msg!!, saida)
                }
            }
        }
        conexao.close()
    }


}