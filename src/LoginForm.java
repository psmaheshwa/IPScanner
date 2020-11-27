import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm implements ActionListener {

    private JFrame frame = new JFrame();
    private JLabel nameLabel=new JLabel("Username");
    private JLabel passwordLabel=new JLabel("Password");
    private JTextField usernameField = new JTextField();
    private JPasswordField passwordField=new JPasswordField();
    private JButton loginButton=new JButton("Login");

    LoginForm(){
        createWindow();
        setLocationAndSize();
        addComponentsToFrame();
        actionEvent();
    }

    private void createWindow()
    {

        frame.setTitle("Login");
        frame.setBounds(40,40,400,300);
        frame.getContentPane();
        frame.getContentPane().setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
    }

    private void setLocationAndSize()
    {
        nameLabel.setBounds(20,20,100,70);
        passwordLabel.setBounds(20,100,100,70);
        usernameField.setBounds(150,45,165,23);
        passwordField.setBounds(150,125,165,23);
        loginButton.setBounds(160,200,100,35);
    }
    private void addComponentsToFrame()
    {
        frame.add(nameLabel);
        frame.add(usernameField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(loginButton);
    }

    private void actionEvent(){
        loginButton.addActionListener(this);
    }

    private Boolean checkLogin(String uname, String pwd)
    {
        try {
            Connection dbConnection = DriverManager.getConnection("jdbc:mysql://121.200.55.42:4063/ipscanner?useSSL=false","root","root");
            PreparedStatement preparedStatement = dbConnection.prepareStatement("select * from user where user=? and password=?");
            ResultSet resultSet;
            preparedStatement.setString(1, uname);
            preparedStatement.setString(2, pwd);
            resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            System.out.println("error while validating"+e);
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        if(actionEvent.getSource() == loginButton){
            char[] temp_pwd=passwordField.getPassword();
            String password = String.copyValueOf(temp_pwd);
            if(!checkLogin(usernameField.getText(), password)) {
                frame.setVisible(false);
               new Table();
            }
            else {
                JOptionPane.showMessageDialog(null, "Login failed!","Failed!!",
                            JOptionPane.ERROR_MESSAGE);
            }
        }

    }
}
