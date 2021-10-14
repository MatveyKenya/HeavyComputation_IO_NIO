package ru.matveykenya;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Рассчитываем число Фибоначчи итеративным методом
 * с Blocking IO - действие всего 1 и мы только ждем результата 1 вычисления
 * поэтому нет смысла использовать NIO
 */

public class Main {

    public static void main(String[] args) {

        Thread server = new Thread(Main::server);
        Thread client = new Thread(Main::client);

        server.start();
        client.start();
    }

    static void server() {
        // Занимаем порт, определяя серверный сокет
        try{
            ServerSocket servSocket = new ServerSocket(23444);
            while (true) {
                //  Ждем подключения клиента и получаем потоки для дальнейшей работы
                try (Socket socket = servSocket.accept();
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        // Выход если от клиента получили end
                        if (line.equals("end")) {
                            return;
                        }
                        //вычисляем и пишем ответ
                        out.println(fibonacci(Integer.parseInt(line)));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long fibonacci(int n) {
        long f0 = 0, f1 = 1, f2 = 1;
        if (n <= 0) return f0;
        if (n == 1) return f1;
        for (int i = 1; i < n; i++) {
            f2 = f0 + f1;
            f0 = f1;
            f1 = f2;
        }
        return f2;
    }

    static void client() {
        try (// Определяем сокет сервера
            Socket socket = new Socket("127.0.0.1", 23444);
            // Получаем входящий и исходящий потоки информации
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            Scanner scanner = new Scanner(System.in)) {
            String msg;
            while (true) {
                System.out.println("Введите номер числа в последовательности Фибоначчи или end для завершения: ");
                msg = scanner.nextLine();
                out.println(msg);
                if ("end".equals(msg)) {
                    break;
                }
                System.out.println("SERVER: Число Фибоначчи --- " + in.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}