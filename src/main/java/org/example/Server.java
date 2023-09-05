package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Server {

    private static String CURRENT_DIRECTORY = System.getProperty("user.dir");

    public static void main(String[] args) throws IOException {

        Path currentPath = Paths.get(CURRENT_DIRECTORY+"/file-system");
        Files.list(currentPath).forEach(path -> System.out.println(path.getFileName().toString()));

        System.out.println("== Servidor ==");

        try (ServerSocket serverSocket = new ServerSocket(8085)) {
                Socket socket = serverSocket.accept();
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                while(true) {
                    System.out.println("Cliente: "+socket.getInetAddress());

                    String[] message = dataInputStream.readUTF().split(" ");
                    String command = message[0];

                    switch (command.toLowerCase()) {
                        case "readdir": {
                            StringBuilder files = new StringBuilder();
                            Files
                                    .list(currentPath)
                                    .forEach(path -> {
                                        files.append(path.getFileName()).append(" ");
                                    });
                            dataOutputStream.writeUTF(files.toString());
                            break;
                        }
                        case "rename": {
                            String fileName = message[1];
                            String newFileName = message[2];
                            Files
                                    .list(currentPath)
                                    .filter(path -> fileName.equalsIgnoreCase(path.getFileName().toString()))
                                    .findFirst()
                                    .map(path -> {
                                        try {
                                            return Files.move(path, Path.of(currentPath+"/"+newFileName));
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });

                            dataOutputStream.writeUTF("OK");
                            break;
                        }
                        case "create": {
                            String fileType = message[1];
                            String fileName = message[2];
                            Path newPath = Path.of(currentPath+"/"+fileName);
                            if(fileType.equalsIgnoreCase("file")) {
                                Files.createFile(newPath);
                            } else if (fileType.equalsIgnoreCase("dir") ||
                                    fileType.equalsIgnoreCase("directory")){
                                Files.createDirectory(newPath);
                            } else {
                                dataOutputStream.writeUTF("Tipo de arquivo não reconhecido");
                            }
                            dataOutputStream.writeUTF("OK");
                            break;
                        }
                        case "delete": {
                            String fileName = message[1];
                            Path deletedFilePath = Path.of(currentPath+"/"+fileName);
                            Files.delete(deletedFilePath);
                            dataOutputStream.writeUTF("OK");
                            break;
                        }
                        default: {

                            dataOutputStream.writeUTF("Comando não encontrado.");
                        }
                    }
                }
        }

    }
}