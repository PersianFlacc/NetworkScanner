import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String NetAddress, Answer;
        short HostAddress, PartAddress, HostAccessible;
        int FirstIndex, LastIndex, TIME;
        boolean Error = false, ShowError;
        boolean UserAnswer;

        Scanner sc = new Scanner(System.in);
        PortScanWorker[] PortScanWorkers = new PortScanWorker[256];
        Thread[] PortThreads = new Thread[256];

        System.out.println("Hello! It's your pocket Network Scanner!");

        do{
            System.out.println("Please enter IP address in format x.x.x to scan addresses from x.x.x.0 to x.x.x.255 (or enter 'esc' to quit): ");
            Answer = sc.nextLine();
            sc.reset();

            if (Answer.equalsIgnoreCase("esc")) {
                break;
            }

            if (Answer.length()<=5) {
                System.out.println("Your address too short!");
                continue;
            }

            FirstIndex = 0;

            for (int i = 1; i<=3; i++) {

                if (i==3) {
                    LastIndex = (short) Answer.length();
                }else{
                    LastIndex = (short) Answer.indexOf("." + FirstIndex);
                }

                if (!(LastIndex > FirstIndex)) {
                    System.out.println("Your address missing a point!");
                    Error = true;
                    break;
                }

                try {
                    PartAddress = Short.parseShort(Answer.substring(FirstIndex, LastIndex));
                } catch (NumberFormatException exception) {
                    System.out.printf("Invalid address!");
                    Error = true;
                    break;
                }

                FirstIndex = (short) (LastIndex + 1);
            }
            if(Error) {
                continue;
            }

            NetAddress = Answer;
            TIME = InputTimeOut(1000);
            ShowError = AnswerYN ("You want show connection errors (y/n)? ");

            for (HostAddress = 0; HostAddress <= 255; HostAddress++) {
                PortScanWorkers[HostAddress] = new PortScanWorker(NetAddress, HostAddress, TIME, ShowError);
                PortThreads[HostAddress] = new Thread(PortScanWorkers[HostAddress], NetAddress + "." + HostAddress);
                PortThreads[HostAddress].start();
            }

            HostAccessible = 0;
            for (HostAddress = 0; HostAddress <= 255; HostAddress++) {
                try {
                    PortThreads[HostAddress].join();
                }catch (InterruptedException exception) {
                    throw new RuntimeException(exception);
                }
                if (PortScanWorkers[HostAddress].isRealizable()){
                    System.out.printf("%s is realizable!\n", NetAddress + "." + HostAddress);
                    HostAccessible++;
                }
            }
            System.out.printf("%d hosts is realizable", HostAccessible);

            if (HostAccessible>0) {
                do {
                    UserAnswer = AnswerYN("You want a port scan on realizable adresses (y/n)? ");
                    if (UserAnswer) {
                        TIME = InputTimeOut(100);
                        ShowError = AnswerYN ("You want show connection errors (y/n)? ");

                        for (HostAddress = 0; HostAddress <=255; HostAddress++) {
                            if (PortScanWorkers[HostAddress].isRealizable()){
                                System.out.printf("Find for realizable ports... %s \n", NetAddress + "." + HostAddress);

                                new PortScanWorker(NetAddress, HostAddress, TIME, ShowError, true).run();

                                HostAccessible--;
                                if (HostAccessible>0) {
                                    UserAnswer = AnswerYN ("You want continue (y/n)?");
                                    if (!UserAnswer) break;
                                }
                            }
                        }
                        if (!UserAnswer) break;
                    } else break;
                } while (true);
            }
        } while (true);

        System.out.println("Pocket Network Scanner is finished her work!");
    }

    static boolean AnswerYN (String Message) {
        do {
            String Answer;
            final Scanner sc = new Scanner(System.in);
            System.out.println(Message);
            Answer = sc.nextLine();
            sc.reset();
            if (Answer.equalsIgnoreCase("y")){
                return true;
            } else if (Answer.equalsIgnoreCase("n")){
                return false;
            } else {
                System.out.println("Error! Repeat your input!");
            }
        } while (true);
    }

    static int InputTimeOut (int RecTimeOut) {
        int TIME;
        final Scanner sc = new Scanner(System.in);

        do {
            System.out.printf("Please Specify the timeout value in ms from 0 to 2 147 483 647 (%d ms is recommended): ", RecTimeOut);
            try {
                TIME = sc.nextInt();
                sc.nextLine();
                sc.reset();
            } catch (Exception exception) {
                TIME = -1;
            }

            if (TIME<0){
                System.out.println("Error! Repeat your input!");
            } else {
                return TIME;
            }
        } while (true);
    }
}