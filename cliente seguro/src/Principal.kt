import br.josias.Chat
import br.josias.Usuario

fun main(args: Array<String>) {
    println("digite o seu nome de usuario:")
    val user = readLine()
    val usuario = Usuario(user!!)
    println("digite o endereço ip do servidor:")
    val ip = readLine()
    println("digite a porta onde o servidor estar aberto:")
    val porta = readLine()!!.toInt()
    val sala = Chat(ip!!,porta, usuario)
    sala.iniciarChat()

}