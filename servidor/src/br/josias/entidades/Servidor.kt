package br.josias.entidades

import br.josias.interfaces.Observavel
import br.josias.interfaces.Sujeito
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

/**
 * Created by alunom35 on 18/09/2017.
 */
class Servidor(val ip: String, val porta: Int) : Sujeito {
    override var status: String = ""

    override var observaveis: MutableList<Observavel> = LinkedList()

    fun atualizarStatus(valor: String){
        status = valor
        atualizarObservaveis()
    }

    override fun adicinarObservavel(novo: Observavel):Boolean {
        if(novo !in observaveis){
            observaveis.add(novo)
            return true
        }else
            return false
    }

    override fun removerObservavel(sai: Observavel):Boolean {
        if (sai in observaveis){
            observaveis.remove(sai)
            return true
        }else
            return false
    }

    fun pesquisarObservavel(chave: String): Observavel?{
        for(ob in observaveis){
            if(ob.id == chave){
                return ob
            }
        }
        return null
    }

    override fun atualizarObservaveis(){
        observaveis.forEach { it.atualizar(status) }
    }

    fun fecharConexao(conexao: ConexaoUsuario){
        if(conexao.socket.isClosed)
            removerObservavel(conexao)
    }

    fun usuarios():String{
        var lista: StringBuffer = StringBuffer("")
        for (ob in observaveis){
            lista.append(";"+ob.id)
        }
        return lista.toString()
    }

    fun validarAcesso(novaConexao: Socket){
        var bid = ByteArray(1024)
        novaConexao.getInputStream().read(bid)
        val primeiraLinha = bid.toString(Charsets.UTF_8)
        val matriz = primeiraLinha.split(";")
        val id = matriz[1]
        for(ob in observaveis){
            if (ob.id == id){
              val erro = "recusado;Desculpe. Já existe usuario com esse nome. Por favor, desconecte e escolha outro"
              val berro = erro.toByteArray(Charsets.UTF_8)
              novaConexao.getOutputStream().write(berro)
              novaConexao.getOutputStream().flush()
              novaConexao.close()
              return Unit
            }
        }
        val conexao = ConexaoUsuario(novaConexao, this, id)
        val t = Thread(conexao)
        adicinarObservavel(conexao)
        atualizarStatus(primeiraLinha)
        t.start()

    }


    fun iniciarServidor() {
        val serverSoket: ServerSocket
        serverSoket = ServerSocket(porta)
        println("Servidor ${ip} aberto na porta ${porta}")

        while (true){
            var cliente = serverSoket.accept()
            validarAcesso(cliente)

        }

    }

}