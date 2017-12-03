package br.josias

import java.io.InputStream
import java.io.OutputStream

/**
 * Created by alunom35 on 18/09/2017.
 */
class Feed(val inputStream: InputStream, val outputStream: OutputStream, val chat: Chat) : BaseIo, Runnable {

    override fun run() {
        var controle = true
        while (controle) {
            var msg: String? = receberPelaConexao(inputStream,chat.chaveUnica)
            if (msg != null) {
                var opms = msg.split(";")
                if(opms[0] != "privado" && opms[0] !="recusado" && opms[0] != "lista") {
                    chat.mensagensPublicas.add(opms[1])
                    println(opms[1])
                }
                else if(opms[0] == "privado") {
                    chat.usuario.mensagensPrivadas.add(opms[1])
                    println(opms[1])
                }
                else if (opms[0] == "lista"){
                    for (i in 1..opms.size - 1){
                        println(opms[i])
                    }
                }
                else if(opms[0] == "recusado") {
                    chat.mensagensPublicas.add(opms[1])
                    controle = false
                    println(opms[1])
                }
                if (opms[0] == "fim")
                    controle = false
            }else
              continue
        }
    }

}