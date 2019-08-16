import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.regex.Pattern;

public class UIForm extends JFrame{
    JFrame frame;
    private JPanel panel;
    private JTextField fromTextField;
    private JTextField toTextField;
    private JButton startButton;
    private JTable table;
    private JLabel deviceip;
    private JTextField getnoteip;
    private JButton notify;
    private JLabel enteriplabel;
    private JLabel deviceiplabel;
    private JLabel iprangelable;
    private JLabel tolabel;
    private JTable table1;
    private boolean notifyloop = false;
    private boolean scanloop = false;
    private Thread runner = null;
    private String[][] data;


    private DefaultTableModel model = new DefaultTableModel() {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
            return false;
        }
    };


    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static boolean validate(final String ip) {
        return PATTERN.matcher(ip).matches();
    }


    void scanner(){
        System.out.println("Search log:");
        table1.setModel(model);
        model.addColumn("IP Address");
        model.addColumn("Status");
        table1.setAutoCreateRowSorter(true);


        String startingip = fromTextField.getText();
        String endingip = toTextField.getText();
        System.out.println(startingip+""+endingip);

        String[] nt = startingip.split("\\.");
        String[] br= endingip.split("\\.");

        if(validate(startingip) && validate(endingip)){
            int connected = 0;
            int count = 0;
            for (int j = Integer.parseInt(nt[0]); j <= Integer.parseInt(br[0]); ++j) {
                for (int k = Integer.parseInt(nt[1]); k <= Integer.parseInt(br[1]); ++k) {
                    for (int l = Integer.parseInt(nt[2]); l <= Integer.parseInt(br[2]); ++l) {
                        for (int i = Integer.parseInt(nt[3])+1; i <= Integer.parseInt(br[3]); ++i) {
                                try {
                                    InetAddress addr = InetAddress.getByName(String.format("%s.%s.%s.%s", j, k, l, i));

                                    if (addr.isReachable(50)) {
                                        model.addRow(new Object[]{addr.getHostAddress(), "UP"});
                                        ++count;
                                        System.out.println("Active : " + addr.getHostAddress());
                                        ++connected;
                                    } else {
                                        model.addRow(new Object[]{ addr.getHostAddress(), "DOWN"});
                                        ++count;
                                        System.out.println("Inactive : " + addr.getHostAddress());
                                    }
                                } catch (IOException ioex) {
                                }
                        }
                    }
                }
            }
            JOptionPane.showMessageDialog(frame, "No.of.Device Scanned : "+count+"\nNo.of.Device Connected : "+connected,
                    "Result",
                    JOptionPane.INFORMATION_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(frame," not a valid IP!",
                    "Wrong input",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    public void start() {
        if (runner == null)
        {
            runner = new Thread(this::scanner);
            runner.start();
        }
    }

    public void stop() {
        runner.stop();
        runner = null;
    }



   UIForm(){
        MailAlert mailAlert = new MailAlert();
        this.setTitle("IP Scanner");
        this.setSize(900, 500);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setContentPane(panel);
        this.pack();
        //************************************************//




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
                int[][] ips = Scanner_ip.ipcalculator(myip, n);
                int[] nt = ips[0];
                int[] br = ips[1];
                deviceip.setText(myip);
                toTextField.setText(br[0]+"."+br[1]+"."+br[2]+"."+br[3]);
                fromTextField.setText(nt[0]+"."+nt[1]+"."+nt[2]+"."+nt[3]);
                fromTextField.setEditable(true);
                toTextField.setEditable(true);

                startButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if(!scanloop){
                            scanloop = true;
                            startButton.setText("Stop");
                            start();
                        }else {
                            scanloop = false;
                            startButton.setText("Restart");
                            model = new DefaultTableModel() {
                                public boolean isCellEditable(int rowIndex, int mColIndex) {
                                    return false;
                                }
                            };
                            stop();
                        }
                    }
                });

            notify.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    String wntip = getnoteip.getText();
                    if(validate(wntip)) {
                        if(!notifyloop){
                            notify.setText("Stop");
                            notifyloop = true;
                        }else{
                            notifyloop = false;
                            notify.setText("Notify");
                        }

                        try {
                            InetAddress addr = InetAddress.getByName(wntip);
                            new SwingWorker(){

                                @Override
                                protected Object doInBackground() throws Exception {
                                    while (notifyloop) {
                                        if (addr.isReachable(100)) {
                                        } else{
                                            JOptionPane.showMessageDialog(frame, wntip + " disconnected!",
                                                    "Notifier",
                                                    JOptionPane.WARNING_MESSAGE);
                                            mailAlert.sendMail(wntip);
                                            notify.setText("Notify");
                                            break;
                                        }
                                    }
                                    return null;
                                }
                            }.execute();
                        } catch (IOException ioex) {
                        }
                    }else
                        JOptionPane.showMessageDialog(frame, wntip + " not a valid IP!",
                                "Wrong input",
                                JOptionPane.ERROR_MESSAGE);
                }

            });

            }

        }
        catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
