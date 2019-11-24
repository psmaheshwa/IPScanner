

class Scanner_ip {

    static int[][] ipcalculator(String ip, int subnet_length) {
        String[] str = ip.split("\\.");
        int[] b = IPcalculator.bina(str);
        int[] ntwk = new int[32];
        int[] brd = new int[32];
        int t = 32 - subnet_length;

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


        // Obtaining class of Address

        // Converting network address to decimal
        int[] nt = IPcalculator.deci(ntwk);

        // Converting broadcast address to decimal
        int[] br = IPcalculator.deci(brd);

        // Printing in dotted decimal format

        // Printing in dotted decimal format

        return new int[][]{nt,br};
    }
}