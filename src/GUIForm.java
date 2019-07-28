import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class GUIForm {
    JFrame f;
    private JPanel panel;
    private JTable table;

    GUIForm(){
        f=new JFrame();
        DefaultTableModel model = new DefaultTableModel();
        String column[]={"ID","ADDRESS","STATUS"};
        model.setColumnIdentifiers(column);
        table=new JTable();
        table.setModel(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        for (int i =0; i< (Scanner_ip.Available_Devices).size();i++){
            model.addRow(new Object[]{i+1,Scanner_ip.Available_Devices.get(i),"UP"});
        }
        f.add(scroll);
        f.setSize(300,400);
        f.setVisible(true);
    }

    public static void main(String[] args) throws UnknownHostException {
        Scanner_ip.ipscanner();
        new GUIForm();

    }


}
