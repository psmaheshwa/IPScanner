import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UIForm extends JFrame{
    private JFrame frame;
    private JPanel panel;
    private JTextField fromTextField;
    private JTextField toTextField;
    private JButton startButton;
    private JLabel deviceip;
    private JTextField getnoteip;
    private JButton notify;
    private JTable table1;
    private JButton shutdownButton;
    private JTextField shutdownip;
    private boolean notifyloop = false;
    private boolean scanloop = false;
    private Thread runner = null;
    private int connected = 0;
    private int count = 0;
    private String recipent;
    private String email;
    private String password;


    private DefaultTableModel model = new DefaultTableModel() {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
            return false;
        }
    };


    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private static boolean validate(final String ip) {
        return PATTERN.matcher(ip).matches();
    }

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
                   Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    private void scanner(){
        System.out.println("Search log:");
        table1.setModel(model);
        model.addColumn("IP Address");
        model.addColumn("Status");
        table1.setAutoCreateRowSorter(true);
        table1.setShowHorizontalLines(false);
        table1.setShowVerticalLines(false);


        String startingip = fromTextField.getText();
        String endingip = toTextField.getText();
        System.out.println(startingip+""+endingip);

        String[] nt = startingip.split("\\.");
        String[] br= endingip.split("\\.");

        if(validate(startingip) && validate(endingip)){
            for (int j = Integer.parseInt(nt[0]); j <= Integer.parseInt(br[0]); ++j) {
                for (int k = Integer.parseInt(nt[1]); k <= Integer.parseInt(br[1]); ++k) {
                    for (int l = Integer.parseInt(nt[2]); l <= Integer.parseInt(br[2]); ++l) {
                        for (int i = Integer.parseInt(nt[3]); i <= Integer.parseInt(br[3]); ++i) {
                                try {
                                    InetAddress addr = InetAddress.getByName(String.format("%s.%s.%s.%s", j, k, l, i));

                                    if (addr.isReachable(1000)) {
                                            if (addr.getHostAddress()==String.format("%s.%s.%s.%s", j, k, l, i)){
                                                model.addRow(new Object[]{addr.getHostAddress(), "UP"});

                                            }else
                                                model.addRow(new Object[]{addr.getHostAddress(), addr.getHostName()});


                                        ++count;
                                        System.out.println("Active : " + addr.getHostAddress());
                                        ++connected;
                                    } else {
                                        model.addRow(new Object[]{ addr.getHostAddress(), "Down"});
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
    private void start() {
        if (runner == null)
        {
            runner = new Thread(this::scanner);
            runner.start();
        }
    }

    private void stop() {
        runner.stop();
        JOptionPane.showMessageDialog(frame, "No.of.Device Scanned : "+count+"\nNo.of.Device Connected : "+connected,
                "Result",
                JOptionPane.INFORMATION_MESSAGE);
        connected = count =0;
        runner = null;
    }



   UIForm(){
        Shutdown power = new Shutdown();
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
            String myip = machine.getHostAddress();
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
                        JFrame notifier = new JFrame("email Credentials");
                        recipent = JOptionPane.showInputDialog(frame, "Enter Recipient email!");
                        email = JOptionPane.showInputDialog(frame, "Enter your email ");
                        password = JOptionPane.showInputDialog(frame, "Enter your password!");
                        if (validateEmail(email) && validateEmail(recipent)){
                            if(!notifyloop){
                                notify.setText("Stop");
                                notifyloop = true;
                            }else{
                                notifyloop = false;
                                notify.setText("Notify");
                                JOptionPane.showMessageDialog(frame, wntip + " disconnected!",
                                        "Notifier",
                                        JOptionPane.WARNING_MESSAGE);
                            }
                        }else{
                            JOptionPane.showMessageDialog(frame,"invalid email!",
                                    "Notifier",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                        try {
                            InetAddress addr = InetAddress.getByName(wntip);
                            new SwingWorker(){

                                @Override
                                protected Object doInBackground() throws Exception {
                                    while (notifyloop) {
                                        if (addr.isReachable(500)) {
                                        } else{
                                            if(!(email == null || password == null || recipent == null)){
                                                mailAlert.sendMail(wntip,email,password,recipent);
                                                notify.setText("Notify");
                                                break;
                                            }
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
                shutdownButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        String shutip = shutdownip.getText();
                        if(validate(shutip)){
                            new SwingWorker(){

                                @Override
                                protected Object doInBackground() throws Exception {
                                    power.down(shutip);
                                    JOptionPane.showMessageDialog(frame, shutip + " Machine Successfully shutdown!",
                                            "Power Off",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    return null;
                                }

                            }.execute();
                        }else  JOptionPane.showMessageDialog(frame, shutip + " not a valid IP!",
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