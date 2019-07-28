

public class Scanner_ip {

    static int[][] ipcalculator(String ip, int subnet_length) {
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
        int[][] ips = {nt,br};
        return ips;
    }
}