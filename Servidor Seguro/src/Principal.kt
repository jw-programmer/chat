import br.josias.Servidor

fun main(args: Array<String>) {
    println("digite a porta onde você vai abrir o servidor")
    val porta = readLine()!!.toInt()
    val servidor = Servidor("localhost", porta)
    servidor.iniciarServidor()
}