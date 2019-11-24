import java.util.*;
import java.lang.Math;

class IPcalculator {

    // Converts IP address to the binary form
    static int[] bina(String[] str) {
        int a, b, c, d, i, rem;
        a = b = c = d = 1;
        Stack<Integer> st = new Stack<>();

        // Separate each number of the IP address
        if (str != null) {
            a = Integer.parseInt(str[0]);
            b = Integer.parseInt(str[1]);
            c = Integer.parseInt(str[2]);
            d = Integer.parseInt(str[3]);
        }

        // convert first number to binary
        for (i = 0; i <= 7; i++) {
            rem = a % 2;
            st.push(rem);
            a = a / 2;
        }

        // Obtain First octet
        int[] re = new int[32];
        for (i = 0; i <= 7; i++) {
            re[i] = st.pop();
        }

        // convert second number to binary
        for (i = 8; i <= 15; i++) {
            rem = b % 2;
            st.push(rem);
            b = b / 2;
        }

        // Obtain Second octet
        for (i = 8; i <= 15; i++) {
            re[i] = st.pop();
        }

        // convert Third number to binary
        for (i = 16; i <= 23; i++) {
            rem = c % 2;
            st.push(rem);
            c = c / 2;
        }

        // Obtain Third octet
        for (i = 16; i <= 23; i++) {
            re[i] = st.pop();
        }

        // convert fourth number to binary
        for (i = 24; i <= 31; i++) {
            rem = d % 2;
            st.push(rem);
            d = d / 2;
        }

        // Obtain Fourth octet
        for (i = 24; i <= 31; i++) {
            re[i] = st.pop();
        }

        return (re);
    }

    // Converts IP address
    // from binary to decimal form
    static int[] deci(int[] bi) {

        int[] arr = new int[4];
        int a, b, c, d, i, j;
        a = b = c = d = 0;
        j = 7;

        for (i = 0; i < 8; i++) {

            a = a + (int) (Math.pow(2, j)) * bi[i];
            j--;
        }

        j = 7;
        for (i = 8; i < 16; i++) {

            b = b + bi[i] * (int) (Math.pow(2, j));
            j--;
        }

        j = 7;
        for (i = 16; i < 24; i++) {

            c = c + bi[i] * (int) (Math.pow(2, j));
            j--;
        }

        j = 7;
        for (i = 24; i < 32; i++) {

            d = d + bi[i] * (int) (Math.pow(2, j));
            j--;
        }

        arr[0] = a;
        arr[1] = b;
        arr[2] = c;
        arr[3] = d;
        return arr;
    }

}