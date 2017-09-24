package br.josias

import br.josias.entidades.Servidor

fun main(args: Array<String>) {
    println("digite a porta onde você vai abrir o servidor")
    val porta = readLine()!!.toInt()
    val servidor = Servidor("localhost", porta)
    servidor.iniciarServidor()
}