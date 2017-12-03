package br.josias

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.security.KeyPairGenerator
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * Created by alunom35 on 18/09/2017.
 */
class Servidor(val ip: String, val porta: Int) : Sujeito {
    override var status: String = ""

    override var observaveis: MutableList<Observavel> = LinkedList()

    fun atualizarStatus(valor: String) {
        status = valor
        atualizarObservaveis()
    }

    override fun adicinarObservavel(novo: Observavel): Boolean {
        if (novo !in observaveis) {
            observaveis.add(novo)
            return true
        } else
            return false
    }

    override fun removerObservavel(sai: Observavel): Boolean {
        if (sai in observaveis) {
            observaveis.remove(sai)
            return true
        } else
            return false
    }

    fun pesquisarObservavel(chave: String): Observavel? {
        for (ob in observaveis) {
            if (ob.id == chave) {
                return ob
            }
        }
        return null
    }

    override fun atualizarObservaveis() {
        observaveis.forEach { it.atualizar(status) }
    }

    fun fecharConexao(conexao: ConexaoUsuario) {
        if (conexao.socket.isClosed)
            removerObservavel(conexao)
    }

    fun usuarios(): String {
        var lista: StringBuffer = StringBuffer("")
        for (ob in observaveis) {
            lista.append(";" + ob.id)
        }
        return lista.toString()
    }

    fun validarAcesso(novaConexao: Socket, chave: SecretKey) {
        var bid = ByteArray(2048)
        novaConexao.getInputStream().read(bid)
        val cripbid = bid.copyOf(bid.size)

        var enigma = Cipher.getInstance("DES/ECB/NoPadding")
        enigma.init(Cipher.DECRYPT_MODE, chave)
        val cript = enigma.doFinal(cripbid)
        val primeiraLinha = cript.toString(Charsets.UTF_8)
        println(primeiraLinha)
        val matriz = primeiraLinha.split(";")
        val id = matriz[1]
        println(id)
        for (ob in observaveis) {
            if (ob.id == id) {
                val erro = "recusado;Desculpe. Já existe usuario com esse nome. Por favor, desconecte e escolha outro"
                val berro = erro.toByteArray(Charsets.UTF_8)
                enigma = Cipher.getInstance("DES/ECB/PKCS5Padding")
                enigma.init(Cipher.ENCRYPT_MODE, chave)
                val cripberro = enigma.doFinal(berro)
                novaConexao.getOutputStream().write(cripberro)
                novaConexao.getOutputStream().flush()
                novaConexao.close()
                return Unit
            }
        }

        val conexao = ConexaoUsuario(novaConexao, this, id, chave)
        val t = Thread(conexao)
        adicinarObservavel(conexao)
        atualizarStatus(primeiraLinha)
        t.start()

    }

    fun trocaChave(cliente: Socket): SecretKey {
        val cypher = Cipher.getInstance("RSA")
        val geradorDeChave = KeyPairGenerator.getInstance("RSA")
        geradorDeChave.initialize(2084)
        val chaves = geradorDeChave.genKeyPair()
        val chavePublica = chaves.public
        val chavePrivada = chaves.private

        val sendKey = ObjectOutputStream(cliente.getOutputStream())
        val reciveKey = ObjectInputStream(cliente.getInputStream())

        sendKey.writeObject(chavePublica)

        val chaveCriptografada = reciveKey.readObject() as ByteArray
        cypher.init(Cipher.DECRYPT_MODE, chavePrivada)
        val chave = cypher.doFinal(chaveCriptografada)
        val chaveUnica = SecretKeySpec(chave, 0, chave.size, "DES")
        return chaveUnica
    }


    fun iniciarServidor() {
        val serverSoket: ServerSocket
        serverSoket = ServerSocket(porta)
        println("Servidor ${ip} aberto na porta ${porta}")

        while (true) {
            var cliente = serverSoket.accept()
            var chave = trocaChave(cliente)
            validarAcesso(cliente, chave)

        }

    }

}