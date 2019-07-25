import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class GUIForm {
    private JPanel panel;
    private JButton button1;
    private JTable table1;

    public GUIForm() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(null,"Hello World");
            }
        });
    }

    public static void main(String[] args) throws UnknownHostException {
        JFrame frame = new JFrame("IP Scanner");
        frame.setContentPane(new GUIForm().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        Scanner_ip scanner = new Scanner_ip();
        JTable table1 = new JTable();
        ArrayList<String> avail = Scanner_ip.ipscanner();



    }


}
