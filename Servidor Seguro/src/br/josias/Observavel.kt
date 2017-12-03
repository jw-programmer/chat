package br.josias

interface Observavel {

    val id: String

    fun atualizar(arg: String)
}