import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
    private Socket socket;
    private Socket fileSocket;
    private String login;
    private Scanner scanner;
    private PrintWriter printWriter;

    private int port;
    private String ip;

    Client(String ip, int port) {
        try{
            this.port=port;
            this.ip=ip;
            socket=new Socket(InetAddress.getByName(ip),port);
            scanner=new Scanner(socket.getInputStream());
            printWriter=new PrintWriter(socket.getOutputStream(),true);

            fileSocket=new Socket(InetAddress.getByName(ip),port+1);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newSession() {
        try{
            socket=new Socket(InetAddress.getByName(ip),port);
            scanner=new Scanner(socket.getInputStream());
            printWriter=new PrintWriter(socket.getOutputStream(),true);

            fileSocket=new Socket(InetAddress.getByName(ip),port+1);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLogin (String login) {this.login=login;}

    public void sendMessage(String message) {
        printWriter.println(new Message(login,message));
    }

    public List<Message> getMessages(String command) {
        List<Message> messages=new ArrayList<>();
        printWriter.println(new Message(login,command));
        while(true) {
            Message message=new Message(scanner.nextLine());
            if(message.getMessage().equals(Message.END)) break;
            messages.add(message);
        }
        return messages;
    }

    public void deleteRecord(int index) {
        printWriter.println(new Message(login,Message.DELETE,index));
        System.out.println(scanner.nextLine());
    }

    public void sendFile(File file) {
        if(file.exists()) {
            try (BufferedOutputStream fileOS= new BufferedOutputStream(fileSocket.getOutputStream())){
                printWriter.println(new Message(login,Message.SEND_FILE));
                printWriter.println(new Message(login, file.getName()));
                //read file from disk
                byte[] buffer = Files.readAllBytes(file.toPath());
                //send file to server

                fileOS.write(buffer,0,buffer.length);
                fileOS.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else System.out.println("Файл не существует: "+file);

    }

    public void exit(boolean totalExit) {

        printWriter.println(new Message(login,Message.EXIT));
        printWriter.close();
        scanner.close();
        try {
            if (totalExit) {
                socket.close();
                fileSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveFile(int id) {
        Message message;
        printWriter.println(new Message(login,Message.GET_FILE,id));
        String str=scanner.nextLine();
        if(!(message=new Message(str)).getMessage().equals(Message.ERROR)) {
            String name = message.getMessage();
            try (ByteArrayOutputStream buf = new ByteArrayOutputStream();
                 FileOutputStream fos = new FileOutputStream(name);
                 BufferedInputStream fileIS = new BufferedInputStream(fileSocket.getInputStream())) {
                int result = fileIS.read();
                while (result != -1) {
                    buf.write((byte) result);
                    result = fileIS.read();
                }
                buf.writeTo(fos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println((new Message(scanner.nextLine())).getMessage());
        }
    }
}
