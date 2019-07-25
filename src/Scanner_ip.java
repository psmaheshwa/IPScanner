import java.io.IOException;
import java.net.*;
import java.util.ArrayList;


public class Scanner_ip {
    public static ArrayList<String> Available_Devices = new ArrayList<>();

    public static ArrayList<String> ipscanner() throws UnknownHostException {

        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            InetAddress machine = socket.getLocalAddress();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(machine);
            String myip = machine.getHostName();
            if (myip.equals("127.0.0.1")) {
                System.out.println("This PC is not connected to any network!");
            } else {
                int n = networkInterface.getInterfaceAddresses().get(networkInterface.getInterfaceAddresses().size()-1).getNetworkPrefixLength();
                System.out.println("My Device IP: " + myip + "\n");
                System.out.println("Starting IP: "+"Ending IP: ");
                int ips[][] = ipcalculator(myip,n);
                int[] nt= ips[0];
                int[] br = ips[1];
//                System.out.println(nt[0]+"\t"+br[0]);
//                System.out.println(nt[1]+"\t"+br[1]);
//                System.out.println(nt[2]+"\t"+br[2]);
//                System.out.println(nt[3]+"\t"+br[3]);
                System.out.println("Search log:");
              //  nt[0]=10; nt[1]=10;nt[2]=236;nt[3]=1;
              //  br[0]=10; br[1]=10;br[2]=236;br[3]=178;
                for (int j = nt[0]; j <= br[0]; ++j){
                    for (int k = nt[1]; k <= br[1]; ++k) {
                        for (int l = nt[2]; l <= br[2]; ++l) {
                            for (int i = nt[3]; i <= br[3]; ++i) {

                              //  System.out.println(j+" "+k+" "+l+" "+i);
                                    try {
                                        InetAddress addr = InetAddress.getByName(String.format("%s.%s.%s.%s", j, k, l, i));

                                        if (addr.isReachable(500)) {
                                            System.out.println("Available: " + addr.getHostAddress());
                                            Available_Devices.add(addr.getHostAddress());
                                        }
//                                        else
//                                            System.out.println("Not available: " + addr.getHostAddress());

                                    } catch (IOException ioex) {
                                    }
                            }
                        }
                    }
                }




                // print the list of available devices
                System.out.println("\nAll Connected devices(" + Available_Devices.size() + "):");
                for (int i = 0; i < Available_Devices.size(); ++i) System.out.println(Available_Devices.get(i));
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        return Available_Devices;
    }

    private static int[][] ipcalculator(String ip, int subnet_length) {
        String str[] = new String[4];
        str = ip.split("\\.");
        int[] b = new int[32];
        b = IPcalculator.bina(str);
        int n = subnet_length;
        int[] ntwk = new int[32];
        int[] brd = new int[32];
        int t = 32 - n;

        // Obtanining network address
        for (int i = 0; i <= (31 - t); i++) {

            ntwk[i] = b[i];
            brd[i] = b[i];
        }

        // Set 32-n bits to 0
        for (int i = 31; i > (31 - t); i--) {

            ntwk[i] = 0;
        }

        // Obtaining Broadcast address
        // by setting 32-n bits to 1
        for (int i = 31; i > (31 - t); i--) {

            brd[i] = 1;
        }

        System.out.println();

        // Obtaining class of Address
        char c = IPcalculator.cls(str);
        System.out.println("Class : " + c);

        // Converting network address to decimal
        int[] nt = IPcalculator.deci(ntwk);

        // Converting broadcast address to decimal
        int[] br = IPcalculator.deci(brd);

        // Printing in dotted decimal format
        System.out.println("Network Address : " + nt[0]
                + "." + nt[1] + "." + nt[2] + "." + nt[3]);

        // Printing in dotted decimal format
        System.out.println("Broadcast Address : "
                + br[0] + "." + br[1] + "." + br[2] + "." + br[3]);
    //    String firstip = String.format("%s.%s.%s.%s",nt[0],nt[1],nt[2],nt[3]);
    //    String lastip = String.format("%s.%s.%s.%s",br[0],br[1],br[2],br[3]);
        int[][] ips = {nt,br};
        return ips;
    }
}