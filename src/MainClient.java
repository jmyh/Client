import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MainClient {
    static Scanner in=new Scanner(System.in);
    private static String login;
    //static BufferedReader in=new BufferedReader(new InputStreamReader())
    public static void main(String[] args) {
        boolean exitSession = false;
        boolean totalExit=false;
        //cycle of programm
        while(!totalExit) {
            totalExit=false;
            exitSession=false;


            //check login
            boolean success;
            do {
                success = enterLogin();
            } while (!success);

            Client client = new Client("localhost", 45777);
            client.setLogin(login);
            //cycle of session
            while(!exitSession) {
                exitSession=false;

                displayMenu();
                //choice action
                if(in.hasNextInt()) {
                    switch (in.nextInt()) {
                        case 1: sendMessage(client);break;
                        case 2: displayHistoryOfMessages(client);break;
                        case 3: deleteRecord(client);break;
                        case 4: displayAllMessages(client);break;
                        case 5: in.nextLine();sendFile(client);break;
                        case 6: receiveFile(client);break;
                        case 7: client.exit(false);exitSession = true;break;
                        case 8: client.exit(true);exitSession = true;totalExit = true;break;
                        default: System.out.println("Неверный ввод! Повторите попытку:");
                    }
                } else  {
                    in.nextLine();
                    System.out.println("Вводимые символы должны являться целым числом!");
                }
                client.newSession();
            }
            System.out.println("Выход из текущей сессии...");
        }

        System.out.println("Выход из программы...");
    }

    private static void receiveFile(Client client) {
        System.out.println("Введите идентификатор файла, который хотите скачать:");
        if(in.hasNextInt())
            client.receiveFile(in.nextInt());
        else {
            in.nextLine();
            System.out.println("Вводимые символы должны являться целым числом!");
        }
    }

    private static void sendFile(Client client) {
        System.out.println("Введите полный путь к файлу, который вы хотите загрузить на сервер: ");
        client.sendFile(new File(in.nextLine()));
    }

    private static void displayAllMessages(Client client) {
        displaySortMenu();
        List<Message> displayList=new ArrayList<>();
        if(in.hasNextInt()) {
            int choise=in.nextInt();
            if(choise==1) {
                displayList=client.getMessages(Message.ALL_MESSAGES).stream()
                        .sorted(Comparator.comparing(Message::getLogin)).collect(Collectors.toList());

            } else if (choise==2){
                displayList=client.getMessages(Message.ALL_MESSAGES).stream()
                        .sorted(Comparator.comparing(Message::getDate)).collect(Collectors.toList());
            } else System.out.println("Неверный ввод!");

                    displayList.forEach(message -> System.out.println(message.getLogin() + ":\t" + message.getMessage() +
                            (message.isFile() ? " (file) " : " ") +message.getDate()+ " ;"));
        } else {
            in.nextLine();
            System.out.println("Вводимые символы должны являться целым числом!");
        }
    }

    private static void displayMenu() {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("1-Ввести новое сообщение;");
        System.out.println("2-Показать список своих сообщений (файлов);");
        System.out.println("3-Удалить свое сообщение;");
        System.out.println("4-Показать список сообщений всех пользователей;");
        System.out.println("5-Отправить файл на сервер;");
        System.out.println("6-Загрузить файл с сервера;");
        System.out.println("7-Выход из сессии;");
        System.out.println("8-Выход из приложения.");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    private static void displaySortMenu() {
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        System.out.println("1-Ввести сообщения (файлы) отсортированными по именам пользователей;");
        System.out.println("2-Вывести сообщения (файлы ) отсортированными по дате добавления");
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }

    private static boolean enterLogin() {
        boolean success;
        System.out.println("Введите логин:");
        in=new Scanner(System.in);
        login=in.nextLine();
        success=checkLogin(login);
        return success;
    }

    private static boolean checkLogin(String login) {
        Pattern pattern=Pattern.compile("^[a-z]{1,4}$");
        Matcher matcher=pattern.matcher(login);
        if(matcher.find()) return true;
        else {
            System.out.println("Логин должен содержать не более 4-х символов нижнего регистра латинского алфавита!");
            return false;
        }
    }

    private static void sendMessage(Client client) {
        System.out.println("Введите сообщение:");
        in=new Scanner(System.in);
        client.sendMessage(in.nextLine());
    }

    private static void deleteRecord(Client client) {
        System.out.println("Введите индентификатор удаляемого сообщения:");
        if(in.hasNextInt()) {
            client.deleteRecord(in.nextInt());
        } else {
            in.nextLine();
            System.out.println("Вводимые символы должны являться целым числом!");
        }

    }

    private static void displayHistoryOfMessages(Client client) {
        client.getMessages(Message.MY_MESSAGES)
                .forEach(message -> System.out.println(message.getId()+": "+message.getMessage()+(message.isFile()?" (file)":"")+";"));
    }
}