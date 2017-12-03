package br.josias

import java.util.*

/**
 * Created by alunom35 on 18/09/2017.
 */
class Usuario(private val nome: String) {
    var mensagensPrivadas: MutableList<String>

    init {
        mensagensPrivadas = LinkedList()
    }

    override fun toString(): String {
        return nome
    }
}