package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        System.out.println("== Cliente ==");

        try(Socket socket = new Socket("127.0.0.1", 8085)) {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            while(true) {
                System.out.println("Digite a operação que deseja fazer: ");
                System.out.println("readdir - lê todos os arquivos do diretório do servidor");
                System.out.println("create <tipo-arquivo> <nome-arquivo> - Cria um novo arquivo no servidor do tipo desejado, podendo ser" +
                        "um diretório (dir ou directory) ou um arquivo comum (file)");
                System.out.println("rename <nome-antigo> <nome-novo> - Renomeia o arquivo para o novo nome desejado");
                System.out.println("delete <nome-arquivo> - Deleta o arquivo desejado");
                Scanner teclado = new Scanner(System.in);
                dataOutputStream.writeUTF(teclado.nextLine());

                String mensagem = dataInputStream.readUTF();
                System.out.println("Servidor falou: " + mensagem);
            }
        }

    }
}
