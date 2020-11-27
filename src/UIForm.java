import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
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
    private JTable table1;
    private JTextField shutdownip;
    private JLabel deviceiplabel;
    private JLabel enteriplabel;
    private JLabel iprangelable;
    private JLabel tolabel;
    private boolean notifyloop = false;
    private boolean scanloop = false;
    private Thread runner = null;
    private int connected = 0;
    private int count = 0;
    private String recipent;
    private String email;
    private String password;
    String tableName;

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

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    private void scanner(){
        table1.setModel(model);
        model.addColumn("IP Address");
        model.addColumn("MAC");
        model.addColumn("Status");
        table1.setAutoCreateRowSorter(true);
        table1.setShowHorizontalLines(false);
        table1.setShowVerticalLines(false);

        try{
        Connection dbConnection = DriverManager.getConnection("jdbc:mysql://121.200.55.42:4063/ipscanner?useSSL=false","root","root");
            if(!tableName.equals("")){
                getnoteip.setText(tableName);
                PreparedStatement preparedStatement = dbConnection.prepareStatement("DELETE FROM " + tableName);
                preparedStatement.executeUpdate();
            }else{
            tableName = getnoteip.getText();
                if(tableName.equals("")){
                    JOptionPane.showMessageDialog(frame, "Enter Network Name!", "Info", JOptionPane.INFORMATION_MESSAGE);
                }else {
                    PreparedStatement preparedStatement = dbConnection.prepareStatement("CREATE TABLE " + tableName + " (ip varchar(20), Mac varchar(20) )");
                    preparedStatement.executeUpdate();
                }
            }
            dbConnection.close();
        } catch (Exception e) {
            System.out.println("error while validating"+e);
        }


        String startingip = fromTextField.getText();
        String endingip = toTextField.getText();
        String[] nt = startingip.split("\\.");
        String[] br= endingip.split("\\.");

        if(validate(startingip) && validate(endingip)){
        for (int j = Integer.parseInt(nt[0]); j <= Integer.parseInt(br[0]); ++j) {
            for (int k = Integer.parseInt(nt[1]); k <= Integer.parseInt(br[1]); ++k) {
                for (int l = Integer.parseInt(nt[2]); l <= Integer.parseInt(br[2]); ++l) {
                    for (int i = Integer.parseInt((nt[3])); i <= Integer.parseInt(br[3]); ++i) {
                            try {
                                InetAddress addr = InetAddress.getByName(String.format("%s.%s.%s.%s", j, k, l, i));
                                Connection dbConnection = DriverManager.getConnection("jdbc:mysql://121.200.55.42:4063/ipscanner?useSSL=false","root","root");
                                String query = MessageFormat.format("insert into {0} (ip, Mac) values (?, ?)", tableName);
                                PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
                                if (addr.isReachable(150)) {
                                        if (addr.getHostAddress().equals(String.format("%s.%s.%s.%s", j, k, l, i))){
                                            try{
                                                NetworkInterface network = NetworkInterface.getByInetAddress(addr);
                                                byte[] macArray = network.getHardwareAddress();
                                                StringBuilder str = new StringBuilder();
                                                for (int index = 0; index < macArray.length; index++) {
                                                    str.append(String.format("%02X%s", macArray[index], (index < macArray.length - 1) ? "-" : ""));
                                                }
                                                String macAddress=str.toString();
                                                preparedStatement.setString(1,addr.getHostAddress());
                                                preparedStatement.setString(2,macAddress);
                                                preparedStatement.executeUpdate();
                                                model.addRow(new Object[]{addr.getHostAddress(), macAddress, "UP"});
                                            }
                                            catch(Exception E) {
                                                E.printStackTrace();
                                                preparedStatement.setString(1,addr.getHostAddress());
                                                preparedStatement.setString(2,"n\\a");
                                                preparedStatement.executeUpdate();
                                                model.addRow(new Object[]{addr.getHostAddress(), "n\\a", "UP"});
                                            }

                                        }else {
                                            preparedStatement.setString(1,addr.getHostAddress());
                                            preparedStatement.setString(2,"n\\a");
                                            preparedStatement.executeUpdate();
                                            model.addRow(new Object[]{addr.getHostAddress(), "n\\a", addr.getHostName()});
                                        }
                                    ++count;
                                    ++connected;
                                } else {
                                    preparedStatement.setString(1,addr.getHostAddress());
                                    preparedStatement.setString(2,"n\\a");
                                    preparedStatement.executeUpdate();
                                    model.addRow(new Object[]{ addr.getHostAddress(), "n\\a", "Down"});
                                    ++count;
                                }
                                dbConnection.close();
                            } catch (IOException | SQLException ignored) {
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

    private void mouseclicked(){
        DefaultTableModel model = (DefaultTableModel)table1.getModel();
        int selectedrow =  table1.getSelectedRow();

        shutdownip.setText(model.getValueAt(selectedrow,0).toString());
    }




    UIForm(String name){
        Shutdown power = new Shutdown();
        MailAlert mailAlert = new MailAlert();
        this.setTitle("IP Scanner");
        this.setSize(900, 500);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setContentPane(panel);
        this.pack();
        tableName = name;

        table1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mouseclicked();
            }
        });

        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            InetAddress machine = socket.getLocalAddress();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(machine);
            String myip = machine.getHostAddress();
            int n = networkInterface.getInterfaceAddresses().get(networkInterface.getInterfaceAddresses().size() - 1).getNetworkPrefixLength();
            int[][] ips = Scanner_ip.ipcalculator(myip, n);
            int[] nt = ips[0];
            int[] br = ips[1];
            deviceip.setText(myip);
            toTextField.setText(br[0]+"."+br[1]+"."+br[2]+"."+br[3]);
            fromTextField.setText(nt[0]+"."+nt[1]+"."+nt[2]+"."+nt[3]);
            fromTextField.setEditable(true);
            toTextField.setEditable(true);

            startButton.addActionListener(actionEvent -> {
                if(!scanloop){
                    scanloop = true;
                    startButton.setText("Stop");
                    start();
                }else {
                    scanloop = false;
                    startButton.setText("Restart");
                    model = new DefaultTableModel() {
                            public boolean isCellEditable(int rowIndex, int mColIndex) { return false;
                            }
                        };
                    stop();
                }
            });
        }
        catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }
}