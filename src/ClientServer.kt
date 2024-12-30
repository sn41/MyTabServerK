import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

/*
Запустить сервер.
Убедитесь, что он вывел надпись “Waiting for a connection on 1777”
Прервать работу сервера можно принудительно.

Waiting for a connection on 1777
Server returns: Было получено сообщение: Тестовая строка для передачи
Server returns: Было получено сообщение: bye
Waiting for a connection on 1777
...

Запустить клиент.
Программа должна послать/принять сообщения и закончить свою работу.

Client is started
Server returns: Тестовая строка для передачи


*/
object MyServer {
    @JvmStatic
    fun main(args: Array<String>) {
        // Определяем номер порта, который будет "слушать" сервер
        val port = 1777

        try {
            // Открыть серверный сокет (ServerSocket)
            val servSocket = ServerSocket(port)

            // Входим в бесконечный цикл - ожидаем соединения
            while (true) {
                println("Waiting for a connection on $port")

                // Получив соединение начинаем работать с сокетом
                val fromClientSocket = servSocket.accept()

                // Работаем с потоками ввода-вывода,
                // используем блок try(){} - catch автоматически завершающиий потоки ввода-вывода
                // при выходе из блока.
                // Потоки ввода-вывода должны наследовать интерфейс AutoCloseable
                try {
                    fromClientSocket.use { localSocket ->
                        PrintWriter(localSocket.getOutputStream(), true).use { printWriter ->
                            BufferedReader(
                                InputStreamReader(localSocket.getInputStream())
                            ).use { bufferedReader ->

                                // Читаем сообщения от клиента
                                var str: String
                                while ((bufferedReader.readLine().also { str = it }) != null) {
                                    // Печатаем сообщение
                                    println("The message: $str")

                                    // Ожидаем сообщение от клиента с содержанием "bye" для прекращения цикла обмена.
                                    if (str != "bye") {
                                        // Посылаем клиенту ответ
                                        str = "Server returns: Было получено сообщение:$str"
                                        printWriter.println(str)
                                    } else {
                                        // Завершаем цикл обмена.
                                        // Отправляем клиенту сообщение окончания сеанса "bye".
                                        printWriter.println("bye")
                                        // Завершаем цикл
                                        break
                                    }
                                }
                            }
                        }
                    }
                } catch (ex: IOException) {
                    //Вывод трассировки ошибки в поток вывода консоли System.out.
                    ex.printStackTrace(System.out)
                }
            }
        } catch (ex: IOException) {
            //Вывод трассировки ошибки в поток вывода консоли System.out.
            ex.printStackTrace(System.out)
        }
    }
}


internal object MyClient {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        // Определяем номер порта, на котором нас ожидает сервер для ответа
        val portNumber = 1777
        // Подготавливаем строку для запроса - просто строка
        var str = "Тестовая строка для передачи"

        // Пишем, что стартовали клиент
        println("Client is started")

        // Открыть сокет (Socket) для обращения к локальному компьютеру
        // Сервер мы будем запускать на этом же компьютере
        // Это специальный класс для сетевого взаимодействия c клиентской стороны
        val socket = Socket("127.0.0.1", portNumber)

        // Создать поток для чтения символов из сокета
        // Для этого надо открыть поток сокета - socket.getInputStream()
        // Потом преобразовать его в поток символов - new InputStreamReader
        // И уже потом сделать его читателем строк - BufferedReader
        val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))

        // Создать поток для записи символов в сокет
        val printWriter = PrintWriter(socket.getOutputStream(), true)

        // Отправляем тестовую строку в сокет
        printWriter.println(str)

        // Входим в цикл чтения, что нам ответил сервер
        while ((bufferedReader.readLine().also { str = it }) != null) {
            // Если пришел ответ “bye”, то заканчиваем цикл
            if (str == "bye") {
                break
            }
            // Печатаем ответ от сервера на консоль для проверки
            println(str)
            // Посылаем ему "bye" для окончания "разговора"
            printWriter.println("bye")
        }

        //закрываем
        bufferedReader.close()
        printWriter.close()
        socket.close()

        println("Client is finished")
    }
}