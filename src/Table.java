import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Table{
    private JPanel panel;
    private JButton button1;
    private JTable table;
    private JScrollPane scrollPane;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton scanButton;
    private DefaultTableModel model = new DefaultTableModel() {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
            return false;
        }
    };
    String tableName;

    Table() {
        JFrame frame = new JFrame();
        frame.setTitle("Network");
        frame.setSize(900, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setResizable(false);
        frame.pack();
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mouseclicked();
            }
        });



            try {
                Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/IPSCANNER?useSSL=false","mahesh","P@ssw0rd");
                PreparedStatement preparedStatement = dbConnection.prepareStatement("show tables");
                ResultSet resultSet = preparedStatement.executeQuery();
                table.setModel(model);
                model.addColumn("Location");
                while (resultSet.next()) {
                    model.addRow(new Object[]{resultSet.getString("Tables_in_IPSCANNER")});
                }
            } catch (Exception e) {
                System.out.println("error while validating"+e);
            }



        button1.addActionListener(actionEvent -> {
            frame.setVisible(false);
            new UIForm();
        });
    }

    private void mouseclicked(){
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        int selectedrow =  table.getSelectedRow();
        tableName = model.getValueAt(selectedrow,0).toString();
    }

}
