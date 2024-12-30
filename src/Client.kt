import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

    fun main(args: Array<String>) {
        val request = args[0]
        var result = ""
        // Определяем номер порта, на котором нас ожидает сервер для ответа
        val portNumber = 1777
        println("Client is started with parameters $request")

        // Открыть сокет (Socket) для обращения к локальному компьютеру
        // Сервер мы будем запускать на этом же компьютере
        // Это специальный класс для сетевого взаимодействия c клиентской стороны
        val socket = Socket("127.0.0.1", portNumber)
        // Поток для чтения символов из сокета
        val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
        // Поток для записи символов в сокет
        val printWriter = PrintWriter(socket.getOutputStream(), true)

        // Отправляем запрос
        printWriter.println(request)

        // Входим в цикл чтения
        while (true) {
            /*
            Читает строку текста.
            Строка считается завершенной любым из следующих символов:
            перевод строки ('\n'),
            возврат каретки ('\r'),
            возврат каретки, за которым сразу следует перевод строки,
            или достижение конца файла (EOF).
             */
            val line: String = bufferedReader.readLine()
            // Если пришел ответ “END”, то заканчиваем цикл
            if (line == "END") {
                break
            }
            // результат в возвращаемую переменную
            println(line)
//            result = line

            // Завершаем сеанс
            printWriter.println("END")
        }

        //закрываем
        bufferedReader.close()
        printWriter.close()
        socket.close()

        println("Client is finished")
    }
