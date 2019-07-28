import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.net.*;

public class GUIForm {
    JFrame f;
    private JPanel panel;
    private JTable table;
    private JLabel ipfrom;
    private static int connected = 0;


    GUIForm() throws UnknownHostException {
        int count = 0;
        f = new JFrame("IP Scanner");
        ipfrom.setText("IP Range");
        ipfrom.setBounds(50, 50, 100, 30);
        f.add(ipfrom);
        //*****************TABLE**************************//
        DefaultTableModel model = new DefaultTableModel() {
            public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };
        String column[] = {"ID", "ADDRESS", "STATUS"};
        model.setColumnIdentifiers(column);
        table = new JTable();
        table.setModel(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        f.add(scroll);
        //************************************************//
        f.setSize(900, 500);
        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        //***********************IP FETCHER*******************//
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            InetAddress machine = socket.getLocalAddress();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(machine);
            String myip = machine.getHostName();
            if (myip.equals("127.0.0.1")) {
                System.out.println("This PC is not connected to any network!");
            } else {
                int n = networkInterface.getInterfaceAddresses().get(networkInterface.getInterfaceAddresses().size() - 1).getNetworkPrefixLength();
                System.out.println("My Device IP: " + myip + "\n");
                int ips[][] = Scanner_ip.ipcalculator(myip, n);
                int[] nt = ips[0];
                int[] br = ips[1];
                System.out.println("Search log:");
                for (int j = nt[0]; j <= br[0]; ++j) {
                    for (int k = nt[1]; k <= br[1]; ++k) {
                        for (int l = nt[2]; l <= br[2]; ++l) {
                            for (int i = nt[3]; i <= br[3]; ++i) {
                                try {
                                    InetAddress addr = InetAddress.getByName(String.format("%s.%s.%s.%s", j, k, l, i));

                                    if (addr.isReachable(50)) {
                                        model.addRow(new Object[]{++count, addr.getHostAddress(), "UP"});
                                        System.out.println("Active : " + addr.getHostAddress());
                                        ++connected;
                                    } else {
                                        model.addRow(new Object[]{++count, addr.getHostAddress(), "DOWN"});
                                        System.out.println("Inactive : " + addr.getHostAddress());
                                    }
                                } catch (IOException ioex) {
                                }
                            }
                        }
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

                try {
            new GUIForm();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

}
