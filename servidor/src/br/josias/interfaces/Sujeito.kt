package br.josias.interfaces

/**
 * Created by alunom35 on 18/09/2017.
 */
interface Sujeito {
    var status: String
    var observaveis: MutableList<Observavel>

    fun adicinarObservavel(novo: Observavel):Boolean

    fun removerObservavel(sai: Observavel):Boolean

    fun atualizarObservaveis()
}

