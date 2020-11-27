import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class NetworkSection {
    private JPanel panel;
    private JScrollPane scrollpane;
    private JTable table;
    private JButton shutdownButton;
    private JButton wakeUpButton;
    private JButton updateButton;
    private DefaultTableModel model = new DefaultTableModel() {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
            return false;
        }
    };
    ArrayList<String> ipList = new ArrayList<>();
    ArrayList<String> MacList = new ArrayList<>();

    NetworkSection(String name) {
        JFrame frame = new JFrame();
        frame.setTitle(name);
        frame.setSize(900, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setResizable(false);
        frame.pack();

        try {
            Connection dbConnection = DriverManager.getConnection("jdbc:mysql://121.200.55.42:4063/ipscanner?useSSL=false","root","root");
            PreparedStatement preparedStatement = dbConnection.prepareStatement("select * from "+name);
            ResultSet resultSet = preparedStatement.executeQuery();
            table.setModel(model);
            model.addColumn("IP Address");
            model.addColumn("Mac Address");
            model.addColumn("Status");
            while (resultSet.next()) {
                String ip = resultSet.getString("ip");
                String Mac = resultSet.getString("Mac");
                new SwingWorker<>() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        InetAddress addr = InetAddress.getByName(ip);
                        String status = addr.isReachable(150) ? "On" : "Off";
                        model.addRow(new Object[]{ip,Mac,status});
                        ipList.add(ip);
                        MacList.add(Mac);
                        return null;
                    }
                }.execute();
            }
        } catch (Exception e) {
            System.out.println("error while validating"+e);
        }

        shutdownButton.addActionListener(actionEvent -> {
            model = new DefaultTableModel() {
                public boolean isCellEditable(int rowIndex, int mColIndex) {
                    return false;
                }
            };
            table.setModel(model);
            model.addColumn("IP Address");
            model.addColumn("Mac Address");
            model.addColumn("Status");
            for(int i = 0; i < ipList.size(); i++) {
                int finalI = i;
                new SwingWorker(){

                    @Override
                    protected Object doInBackground() throws IOException {
                        new Shutdown().down(ipList.get(finalI));
                        InetAddress addr = InetAddress.getByName(ipList.get(finalI));
                        String status = addr.isReachable(150) ? "On" : "Off";
                        model.addRow(new Object[]{ipList.get(finalI), MacList.get(finalI),status});
                        return null;
                    }
            }.execute();
        }
    });

        updateButton.addActionListener(actionEvent -> {
            frame.setVisible(false);
            new UIForm(name);
        });

        wakeUpButton.addActionListener(actionEvent -> {
            WakeUpOnLan wakeUpOnLan = new WakeUpOnLan();
            for( int i = 0; i < MacList.size(); i++) {
                if(!MacList.get(i).equals("n\\a")){
                    int finalI = i;
                    new SwingWorker<>() {
                        @Override
                        protected Object doInBackground() throws Exception {
                            WakeUpOnLan.shutdown(ipList.get(finalI),MacList.get(finalI));
                            return null;
                        }
                    }.execute();
                }
            }
        });
    }
}
