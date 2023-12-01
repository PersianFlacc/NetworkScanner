import java.awt.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PortScanWorker implements Runnable{
    private final int TIME;
    private final String IpAddress;
    private final boolean Error, PortScan;
    private boolean Realizable;

    public PortScanWorker(String NetAddress, short HostAddress, int TIME, boolean error) {
        this.IpAddress = NetAddress + "." + HostAddress;
        this.TIME = TIME;
        this.Error = error;
        this.PortScan = false;
    }

    public PortScanWorker(String NetAddress, short HostAddress, int TIME, boolean error, boolean portScan) {
        this.IpAddress = NetAddress + "." + HostAddress;
        this.TIME = TIME;
        this.Error = error;
        this.PortScan = false;
    }

    public boolean isRealizable() {
        return Realizable;
    }

    public void setRealizable(boolean realizable) {
        Realizable = realizable;
    }

    @Override
    public void run() {
        if(!PortScan) {
            try {
                InetAddress IA = InetAddress.getByName(IpAddress);
                setRealizable(IA.isReachable(TIME));
            } catch (Exception exception) {
                setRealizable(false);
                if(Error) System.out.printf("Your IP Address %s has error: %s\n", IpAddress, exception.getMessage());
            }
        }else{
            int portsOpen = 0;
            for(int Port=0; Port<=65535; Port++){
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(IpAddress, Port), TIME);
                    portsOpen++;
                    System.out.printf("%s port is realizable!\n", IpAddress + ":" + Port);
                } catch (Exception exception) {
                    if(Error) System.out.printf("ERROR! Your IP %s has error %s\n", IpAddress + ":" + Port, exception.getMessage());
                }
                if (Port!=0 && (Port%10000==0 || Port==65535)) {
                    System.out.printf("Scanning complete! Your port %d\n", Port);
                }
            }
            System.out.printf("Your %d ports is realizable!\n", portsOpen);
        }
    }
}