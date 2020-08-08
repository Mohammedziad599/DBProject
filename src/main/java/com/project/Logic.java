package com.project;

import org.json.simple.*;
import org.json.simple.parser.*;
import org.json.simple.parser.ParseException;

import javax.crypto.*;
import javax.crypto.spec.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.sql.*;
import java.util.*;

/**
 * @author Mohammed Zaid
 */
public class Logic{
  private static final SecureRandom RAND = new SecureRandom();
  private static final int ITERATION = 65535;
  private static final int KEY_LENGTH = 512;
  private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
  private static final int RESET_STRING_SIZE = 10;
  private final String EMAIL;
  private final String EMAIL_PASSWORD;
  private final String HOST;
  private final String SSL_PORT;
  /**
   * Variables Declarations Area.
   */
  private final JFrame resetFrame;
  private final JFrame mainFrame;
  private final LoginPanel loginPanel;
  private final AddressPanel addressPanel;
  private final AddUsersPanel addUsersPanel;
  private final InsertDataPanel insertDataPanel;
  private final SearchPanel searchPanel;
  private final ChangePasswordPanel changePasswordPanel;
  private final ResetPasswordFromLoginPanel resetPasswordFromLoginPanel;
  private final String USER;
  private final String PASSWORD;

  private boolean isAdmin = false;
  private String username = "";
  private boolean isAPhoneNumberFlag = false;
  private String dbURL;
  // Password Handling Variables
  private String resetPasswordString = "";


  /**
   * Default Constructor.
   */
  public Logic(){
    String SSL_PORT1;
    String HOST1;
    String EMAIL_PASSWORD1;
    String EMAIL1;
    String PASSWORD1;
    String USER1;
    // init
    JSONParser jsonParser = new JSONParser();
    try{
      JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("settings.json"));
      USER1 = (String) jsonObject.get("DBUser");
      PASSWORD1 = (String) jsonObject.get("DBPassword");
      EMAIL1 = (String) jsonObject.get("Email");
      EMAIL_PASSWORD1 = (String) jsonObject.get("EmailPassword");
      HOST1 = (String) jsonObject.get("EmailHost");
      SSL_PORT1 = (String) jsonObject.get("EmailSslPort");
    }catch(ParseException | IOException e){
      USER1 = "root";
      PASSWORD1 = "root";
      EMAIL1 = "example@example.com";
      EMAIL_PASSWORD1 = "root";
      HOST1 = "host.example.com";
      SSL_PORT1 = "2222";
    }
    SSL_PORT = SSL_PORT1;
    HOST = HOST1;
    EMAIL_PASSWORD = EMAIL_PASSWORD1;
    EMAIL = EMAIL1;
    PASSWORD = PASSWORD1;
    USER = USER1;
    addressPanel = new AddressPanel();
    loginPanel = new LoginPanel();
    addUsersPanel = new AddUsersPanel();
    insertDataPanel = new InsertDataPanel();
    searchPanel = new SearchPanel();
    changePasswordPanel = new ChangePasswordPanel();
    resetPasswordFromLoginPanel = new ResetPasswordFromLoginPanel();
    resetFrame = new JFrame("Reset Password");
    ImageIcon img = new ImageIcon("./logo.png");
    mainFrame = new JFrame();
    mainFrame.setIconImage(img.getImage());

    // Add the address panel and set the frame in the middle screen.
    mainFrame.setResizable(true);
    mainFrame.setTitle("Address");
    mainFrame.add(addressPanel);
    mainFrame.setResizable(false);
    mainFrame.pack();
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    mainFrame.setLocation(dim.width / 2 - mainFrame.getSize().width / 2
        , dim.height / 2 - mainFrame.getSize().height / 2);
    mainFrame.setVisible(true);
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Add listeners to addressPanel
    addressPanel.connectToAddress.addActionListener(e -> checkConnectionAddress());
    addressPanel.getAddressField().addActionListener(e -> checkConnectionAddress());

    // Add listeners to loginPanel
    loginPanel.loginButton.addActionListener(e -> login());
    loginPanel.getUserNameField().addActionListener(e -> login());
    loginPanel.getPasswordField().addActionListener(e -> login());
    loginPanel.resetPasswordButton.addActionListener(e -> new Thread(){
      @Override
      public void run(){
        loginPanel.resetPasswordButton.setEnabled(false);
        loginPanel.resetPasswordButton.setText("Working");
        Connection con = null;
        String sql;
        try{
          Class.forName("com.mysql.cj.jdbc.Driver");
          String username = loginPanel.getUserName().toLowerCase().trim();
          boolean isAPhoneNumber = checkIfMobileNumber(username);
          if(isAPhoneNumber){
            sql = "select Email from Users where Phonenumber=?";
          }else{
            sql = "select Email from Users where Username=?";
          }
          con = DriverManager.getConnection(dbURL, Logic.this.USER, Logic.this.PASSWORD);
          PreparedStatement preparedStatement = con.prepareStatement(sql);
          preparedStatement.setString(1, username);
          ResultSet resultSet = preparedStatement.executeQuery();
          if(resultSet.next()){
            String recipient = resultSet.getString("Email");
            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", HOST);
            properties.put("mail.smtp.port", SSL_PORT);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");
            Session session = Session.getDefaultInstance(properties, new Authenticator(){
              @Override
              protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(EMAIL, EMAIL_PASSWORD);
              }
            });
            session.setDebug(false);
            try{
              MimeMessage message = new MimeMessage(session);
              message.setFrom(new InternetAddress(EMAIL));
              message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
              message.setSubject("Account Reset Code");
              resetPasswordString = generateRandomString(RESET_STRING_SIZE);
              message.setContent(
                  "Use this code to reset your password: <b>" + resetPasswordString + "</b>",
                  "text/html");
              Transport.send(message);
              String receivedCode = JOptionPane.showInputDialog(Logic.this.mainFrame, "Enter the Reset Code Here"
                  , "Reset Code", JOptionPane.QUESTION_MESSAGE);
              try{
                if(receivedCode.equals(resetPasswordString)){
                  resetPasswordFromLoginPanel.setUsername(username);
                  resetFrame.add(resetPasswordFromLoginPanel);
                  resetFrame.pack();
                  resetFrame.setLocationRelativeTo(Logic.this.mainFrame);
                  resetFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                  resetFrame.setVisible(true);
                }else{
                  resetPasswordString = "";
                  loginPanel.resetPasswordButton.setText("Reset Password");
                  loginPanel.resetPasswordButton.setEnabled(true);
                  return;
                }
              }catch(NullPointerException exception){
                loginPanel.resetPasswordButton.setText("Reset Password");
                loginPanel.resetPasswordButton.setEnabled(true);
                return;
              }
            }catch(MessagingException addressException){
              JOptionPane.showMessageDialog(Logic.this.mainFrame, addressException.getMessage()
                  , "Message not sent", JOptionPane.ERROR_MESSAGE);
            }
          }else{
            JOptionPane.showMessageDialog(Logic.this.mainFrame, "Enter the correct Username or PhoneNumber" +
                "to be able to reset the Password", "Wrong Username", JOptionPane.ERROR_MESSAGE);
          }
          con.close();
        }catch(ClassNotFoundException | SQLException exception){
          JOptionPane.showMessageDialog(Logic.this.mainFrame, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }finally{
          if(con != null){
            try{
              con.close();
            }catch(SQLException throwable){
              JOptionPane.showMessageDialog(Logic.this.mainFrame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
          }
        }
        loginPanel.resetPasswordButton.setText("Reset Password");
        loginPanel.resetPasswordButton.setEnabled(true);
      }
    }.start());

    resetPasswordFromLoginPanel.changePasswordButton.addActionListener(e -> new Thread(this::resetPassword).start());
    resetPasswordFromLoginPanel.getConfirmPasswordField().addActionListener(e -> new Thread(this::resetPassword).start());
    resetPasswordFromLoginPanel.getNewPasswordField().addActionListener(e -> new Thread(this::resetPassword).start());

    insertDataPanel.saveButton.addActionListener(e -> {
      String uniqueID = UUID.randomUUID().toString();
      String name = insertDataPanel.getPersonName().trim().toLowerCase();
      String companyName = insertDataPanel.getCompanyName().trim().toLowerCase();
      String emailAddress = insertDataPanel.getEmailAddress().trim().toLowerCase();
      String address = insertDataPanel.getAddress().trim().toLowerCase();
      String mobileNumber = insertDataPanel.getMobileNumber().trim();
      String phoneNumber = insertDataPanel.getPhoneNumber().trim();
      if(name.isBlank()
          || companyName.isBlank()
          || emailAddress.isBlank()
          || address.isBlank()
          || mobileNumber.isBlank()
          || phoneNumber.isBlank()){
        JOptionPane.showMessageDialog(this.mainFrame,"All Field Must be Entered.","Error"
            ,JOptionPane.ERROR_MESSAGE);
        return;
      }
      if(!checkIfMobileNumber(mobileNumber)){
        JOptionPane.showMessageDialog(this.mainFrame,"The Mobile Number is invalid.","Error"
            ,JOptionPane.ERROR_MESSAGE);
        return;
      }
      if(!checkIfPhoneNumber(phoneNumber)){
        JOptionPane.showMessageDialog(this.mainFrame,"The Phone Number is invalid.","Error"
            ,JOptionPane.ERROR_MESSAGE);
        return;
      }
      if(!checkIfEmail(emailAddress)){
        JOptionPane.showMessageDialog(this.mainFrame,"The Email Address is invalid.","Error"
            ,JOptionPane.ERROR_MESSAGE);
        return;
      }
      Connection con = null;
      PreparedStatement preparedStatement;
      try{
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(dbURL, this.USER, this.PASSWORD);
        con.setAutoCommit(false);
        String firstCheck = "Select ID from Customers where ID=?";
        while(true){
          preparedStatement = con.prepareStatement(firstCheck);
          preparedStatement.setString(1, uniqueID);
          ResultSet check = preparedStatement.executeQuery();
          if(!check.next()){
            break;
          }
          uniqueID = UUID.randomUUID().toString();
        }
        String secondCheck = "select * from Customers where PersonName=? and CompanyName=? and Email=? and Address=? and MobileNumber=? and PhoneNumber=?";
        preparedStatement = con.prepareStatement(secondCheck);
        preparedStatement.setString(1,name);
        preparedStatement.setString(2,companyName);
        preparedStatement.setString(3,emailAddress);
        preparedStatement.setString(4,address);
        preparedStatement.setString(5,mobileNumber);
        preparedStatement.setString(6,phoneNumber);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(!resultSet.next()){
          String insertSQL = "insert into Customers Values(?,?,?,?,?,?,?)";
          preparedStatement = con.prepareStatement(insertSQL);
          preparedStatement.setString(1,uniqueID);
          preparedStatement.setString(2,name);
          preparedStatement.setString(3,companyName);
          preparedStatement.setString(4,emailAddress);
          preparedStatement.setString(5,address);
          preparedStatement.setString(6,mobileNumber);
          preparedStatement.setString(7,phoneNumber);
          int result = preparedStatement.executeUpdate();
          if (result == 1){
            JOptionPane.showMessageDialog(this.mainFrame,"Data has been saved","Success"
                ,JOptionPane.INFORMATION_MESSAGE);
            this.insertDataPanel.clearFields();
            con.commit();
          }else{
            con.rollback();
          }
        }else{
          JOptionPane.showMessageDialog(this.mainFrame,"Customer Exist","Exist"
              ,JOptionPane.INFORMATION_MESSAGE);
          this.insertDataPanel.clearFields();
          con.rollback();
        }
      }catch(ClassNotFoundException | SQLException exception){
        try{
          if (con != null)
          con.rollback();
        }catch(SQLException throwables){
          throwables.printStackTrace();
        }
        JOptionPane.showMessageDialog(this.mainFrame, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }finally{
          try{
            if (con != null)
            con.close();
          }catch(SQLException throwable){
            JOptionPane.showMessageDialog(this.mainFrame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
      }
    });
    insertDataPanel.logOutButton.addActionListener(e -> goToLogin());
    insertDataPanel.searchButton.addActionListener(e -> goToSearchPanel());
    insertDataPanel.addUsersButton.addActionListener(e -> goToAddUsersPanel());
    insertDataPanel.changePasswordButton.addActionListener(e -> goToChangePasswordPanel());

    changePasswordPanel.changePasswordButton.addActionListener(e -> {
      char[] currentPassword = changePasswordPanel.getCurrentPassword();
      char[] newPassword = changePasswordPanel.getNewPassword();
      boolean passwordMatches = changePasswordPanel.newPasswordMatches();
      Connection con = null;
      if(passwordMatches){
        try{
          String sql;
          if(isAPhoneNumberFlag){
            sql = "select Password,Salt from Users where Phonenumber=?";
          }else{
            sql = "select Password,Salt from Users where Username=?";
          }
          Class.forName("com.mysql.cj.jdbc.Driver");
          con = DriverManager.getConnection(dbURL, this.USER, this.PASSWORD);
          con.setAutoCommit(false);
          PreparedStatement preparedStatement = con.prepareStatement(sql);
          preparedStatement.setString(1,username);
          ResultSet resultSet = preparedStatement.executeQuery();
          if(resultSet.next()){
            String password = resultSet.getString("Password");
            String salt = resultSet.getString("Salt");
            boolean checkCurrentPassword = verifyPassword(currentPassword,password,salt);
            if (checkCurrentPassword){
              if(isAPhoneNumberFlag){
                sql = "update Users set Password=?, Salt=? where Phonenumber=?";
              }else{
                sql = "update Users set Password=?, Salt=? where Username=?";
              }
              preparedStatement = con.prepareStatement(sql);
              Optional<String> newSalt = generateSalt(32);
              salt = newSalt.orElse("");
              Optional<String> key = hashPassword(newPassword,salt);
              String newPasswordKey = key.orElse("");
              preparedStatement.setString(1,newPasswordKey);
              preparedStatement.setString(2,salt);
              preparedStatement.setString(3,username);
              int result = preparedStatement.executeUpdate();
              if (result == 1){
                JOptionPane.showMessageDialog(this.mainFrame,"Password has been Changed","Success"
                    ,JOptionPane.INFORMATION_MESSAGE);
                changePasswordPanel.clearFields();
                con.commit();
              }else{
                con.rollback();
              }
            }else{
              JOptionPane.showMessageDialog(this.mainFrame,"Current Password does not match your password."
                  ,"Error",JOptionPane.ERROR_MESSAGE);
              con.rollback();
            }
          }

        }catch(ClassNotFoundException | SQLException exception){
          JOptionPane.showMessageDialog(this.mainFrame, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }finally{
          if(con != null){
            try{
              con.close();
            }catch(SQLException throwable){
              JOptionPane.showMessageDialog(this.mainFrame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
          }
        }
      }else{
        JOptionPane.showMessageDialog(this.mainFrame,"New passwords does not matches.","Error"
            ,JOptionPane.ERROR_MESSAGE);
      }
    });
    changePasswordPanel.searchButton.addActionListener(e -> goToSearchPanel());
    changePasswordPanel.logOutButton.addActionListener(e -> goToLogin());
    changePasswordPanel.insertButton.addActionListener(e -> goToInsertDataPanel());
    changePasswordPanel.addUsersButton.addActionListener(e -> goToAddUsersPanel());

    searchPanel.searchByCompanyNameButton.addActionListener(e -> {
      searchPanel.clearChangedIndex();
      searchPanel.clearSearchTable();
      String companyName = searchPanel.getCompanyName();
      Connection con = null;
      try{
        String sql = "Select * from Customers where CompanyName Like ? Escape '!'";
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(dbURL,USER,PASSWORD);
        con.setAutoCommit(false);
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        companyName = companyName.replace("!", "!!")
            .replace("%", "!%")
            .replace("_", "!_")
            .replace("[", "![");
        preparedStatement.setString(1,"%"+companyName+"%");
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
          searchPanel.addToTheTable(resultSet.getString("ID"),resultSet.getString("PersonName")
          ,resultSet.getString("CompanyName"),resultSet.getString("Address")
              ,resultSet.getString("Email"),resultSet.getString("PhoneNumber")
              ,resultSet.getString("MobileNumber"));
        }
        searchPanel.getSearchTable().getModel().addTableModelListener(new TableModelListener(){
          @Override
          public void tableChanged(TableModelEvent e){
            searchPanel.addChangedIndex(e.getFirstRow());
          }
        });
        searchPanel.getSearchTable().getDefaultEditor(String.class).addCellEditorListener(new CellEditorListener(){
          @Override
          public void editingStopped(ChangeEvent e){
            searchPanel.changeTableStatus("Data Has Changed.");
          }

          @Override
          public void editingCanceled(ChangeEvent e){
          }
        });
      }catch(ClassNotFoundException | SQLException exception){
        exception.printStackTrace();
      }finally{
        if (con !=null){
          try{
            con.close();
          }catch(SQLException throwables){
            throwables.printStackTrace();
          }
        }
      }
    });
    searchPanel.searchByPersonNameButton.addActionListener(e -> {
      searchPanel.clearChangedIndex();
      searchPanel.clearSearchTable();
      String personName = searchPanel.getPersonName();
      Connection con = null;
      try{
        String sql = "Select * from Customers where PersonName Like ? Escape '!'";
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(dbURL,USER,PASSWORD);
        con.setAutoCommit(false);
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        personName = personName.replace("!", "!!")
            .replace("%", "!%")
            .replace("_", "!_")
            .replace("[", "![");
        preparedStatement.setString(1,"%"+personName+"%");
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
          searchPanel.addToTheTable(resultSet.getString("ID"),resultSet.getString("PersonName")
              ,resultSet.getString("CompanyName"),resultSet.getString("Address")
              ,resultSet.getString("Email"),resultSet.getString("PhoneNumber")
              ,resultSet.getString("MobileNumber"));
        }
        searchPanel.getSearchTable().getModel().addTableModelListener(new TableModelListener(){
          @Override
          public void tableChanged(TableModelEvent e){
            searchPanel.addChangedIndex(e.getFirstRow());
          }
        });
        searchPanel.getSearchTable().getDefaultEditor(String.class).addCellEditorListener(new CellEditorListener(){
          @Override
          public void editingStopped(ChangeEvent e){
            searchPanel.changeTableStatus("Data Has Changed.");
          }

          @Override
          public void editingCanceled(ChangeEvent e){
          }
        });
      }catch(ClassNotFoundException | SQLException exception){
        exception.printStackTrace();
      }finally{
        if (con !=null){
          try{
            con.close();
          }catch(SQLException throwables){
            throwables.printStackTrace();
          }
        }
      }
    });
    searchPanel.searchByAllButton.addActionListener(e -> {
      searchPanel.clearChangedIndex();
      searchPanel.clearSearchTable();
      String companyName = searchPanel.getCompanyName();
      String personName = searchPanel.getPersonName();
      Connection con = null;
      try{
        String sql = "Select * from Customers where CompanyName Like ? or PersonName Like ? Escape '!'";
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(dbURL,USER,PASSWORD);
        con.setAutoCommit(false);
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        companyName = companyName.replace("!", "!!")
            .replace("%", "!%")
            .replace("_", "!_")
            .replace("[", "![");
        personName = personName.replace("!", "!!")
            .replace("%", "!%")
            .replace("_", "!_")
            .replace("[", "![");
        preparedStatement.setString(1,"%"+companyName+"%");
        preparedStatement.setString(2,"%"+personName+"%");
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
          searchPanel.addToTheTable(resultSet.getString("ID"),resultSet.getString("PersonName")
              ,resultSet.getString("CompanyName"),resultSet.getString("Address")
              ,resultSet.getString("Email"),resultSet.getString("PhoneNumber")
              ,resultSet.getString("MobileNumber"));
        }
        searchPanel.getSearchTable().getModel().addTableModelListener(new TableModelListener(){
          @Override
          public void tableChanged(TableModelEvent e){
            searchPanel.addChangedIndex(e.getFirstRow());
          }
        });
        searchPanel.getSearchTable().getDefaultEditor(String.class).addCellEditorListener(new CellEditorListener(){
          @Override
          public void editingStopped(ChangeEvent e){
            searchPanel.changeTableStatus("Data Has Changed.");
          }

          @Override
          public void editingCanceled(ChangeEvent e){
          }
        });
      }catch(ClassNotFoundException | SQLException exception){
        exception.printStackTrace();
      }finally{
        if (con !=null){
          try{
            con.close();
          }catch(SQLException throwables){
            throwables.printStackTrace();
          }
        }
      }
    });
    searchPanel.saveModificationButton.addActionListener(e -> {
      if (searchPanel.checkIfTableIsChanged()){
        DefaultTableModel model = (DefaultTableModel) searchPanel.getSearchTable().getModel();
        String id;
        String name;
        String companyName;
        String email;
        String address;
        String mobileNumber;
        String phoneNumber;
        for(Integer i:searchPanel.getChangedIndexes()){
          id = (String) model.getValueAt(i,0);
          name = (String) model.getValueAt(i,1);
          companyName = (String) model.getValueAt(i,2);
          address = (String) model.getValueAt(i,3);
          email = (String) model.getValueAt(i,4);
          phoneNumber = (String) model.getValueAt(i,5);
          mobileNumber = (String) model.getValueAt(i,6);
          if(name.isBlank()
              && companyName.isBlank()
              && email.isBlank()
              && address.isBlank()
              && mobileNumber.isBlank()
              && phoneNumber.isBlank()){
            model.removeRow(i);
            this.searchPanel.getSearchTable().revalidate();
          }
          if (!addCustomerToDataBase(id,name,companyName,address,email,phoneNumber,mobileNumber)){
            JOptionPane.showMessageDialog(this.mainFrame,"Check the Values in Row " + i,"Error"
                ,JOptionPane.ERROR_MESSAGE);
            return;
          }
        }
        JOptionPane.showMessageDialog(this.mainFrame,"Data Saved","Success"
            ,JOptionPane.INFORMATION_MESSAGE);
        searchPanel.changeTableStatus("The Data did not Changed.");
        searchPanel.clearChangedIndex();
      }
    });
    searchPanel.addUsersButton.addActionListener(e -> goToAddUsersPanel());
    searchPanel.insertButton.addActionListener(e -> goToInsertDataPanel());
    searchPanel.logOutButton.addActionListener(e -> goToLogin());
    searchPanel.changePasswordButton.addActionListener(e -> goToChangePasswordPanel());

    addUsersPanel.createUserButton.addActionListener(e -> {
      String userName = addUsersPanel.getUserName().toLowerCase().trim();
      char[] password = addUsersPanel.getPassword();
      String emailAddress = addUsersPanel.getEmailAddress().toLowerCase().trim();
      String mobileNumber = addUsersPanel.getPhoneNumber().trim();
      boolean admin = addUsersPanel.adminAccount();
      if (checkIfMobileNumber(mobileNumber) || checkIfPhoneNumber(mobileNumber)){
        if (checkIfEmail(emailAddress)){
          Connection con = null;
          try{
            String sql = "insert into Users Values(?,?,?,?,?,?)";
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(dbURL, this.USER, this.PASSWORD);
            con.setAutoCommit(false);
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1,userName);
            preparedStatement.setString(2,mobileNumber);
            Optional<String> tmp = generateSalt(32);
            String salt = tmp.orElse("");
            tmp = hashPassword(password,salt);
            String key = tmp.orElse("");
            preparedStatement.setString(3,key);
            preparedStatement.setString(4,emailAddress);
            preparedStatement.setBoolean(5,admin);
            preparedStatement.setString(6,salt);
            int result = preparedStatement.executeUpdate();
            if (result == 1){
              JOptionPane.showMessageDialog(this.mainFrame,"User Added"
                  ,"Success",JOptionPane.INFORMATION_MESSAGE);
              con.commit();
            }else{
              con.rollback();
            }
          }catch(ClassNotFoundException | SQLException exception){
            JOptionPane.showMessageDialog(this.mainFrame,exception.getMessage()
            ,"Error",JOptionPane.ERROR_MESSAGE);
          }finally{
            if (con != null){
              try{
                con.close();
              }catch(SQLException throwables){
                JOptionPane.showMessageDialog(this.mainFrame,throwables.getMessage()
                ,"Error",JOptionPane.ERROR_MESSAGE);
              }
            }
          }
        }else{
          JOptionPane.showMessageDialog(this.mainFrame,"Please enter a valid Email Address");
        }
      }else{
        JOptionPane.showMessageDialog(this.mainFrame,"Please enter either a valid Phone Number" +
            "or a Mobile Number","Error",JOptionPane.ERROR_MESSAGE);
      }
    });
    addUsersPanel.logOutButton.addActionListener(e -> goToLogin());
    addUsersPanel.insertButton.addActionListener(e -> goToInsertDataPanel());
    addUsersPanel.searchButton.addActionListener(e -> goToSearchPanel());
    addUsersPanel.changePasswordButton.addActionListener(e -> goToChangePasswordPanel());
  }

  private void deleteCustomerFromDataBase(String id){
    Connection con = null;
    PreparedStatement preparedStatement;
    try{
      Class.forName("com.mysql.cj.jdbc.Driver");
      con = DriverManager.getConnection(dbURL, this.USER, this.PASSWORD);
      con.setAutoCommit(false);
      String deleteSQL = "delete from Customers where ID = ?";
      preparedStatement = con.prepareStatement(deleteSQL);
      preparedStatement.setString(1,id);
      int result = preparedStatement.executeUpdate();
      if (result == 1){
        con.commit();
      }else{
        con.rollback();
      }
    }catch(ClassNotFoundException | SQLException exception){
      try{
        if (con != null)
          con.rollback();
      }catch(SQLException throwables){
        throwables.printStackTrace();
      }
      JOptionPane.showMessageDialog(this.mainFrame, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }finally{
      try{
        if (con != null)
          con.close();
      }catch(SQLException throwable){
        JOptionPane.showMessageDialog(this.mainFrame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private boolean addCustomerToDataBase(String id,String name,String companyName
      ,String address,String email
      ,String phoneNumber,String mobileNumber){
    if(name.isBlank()
        && companyName.isBlank()
        && email.isBlank()
        && address.isBlank()
        && mobileNumber.isBlank()
        && phoneNumber.isBlank()){
      deleteCustomerFromDataBase(id);
      return true;
    }else if(name.isBlank()
        || companyName.isBlank()
        || email.isBlank()
        || address.isBlank()
        || mobileNumber.isBlank()
        || phoneNumber.isBlank()){
      return false;
    }
    if(!checkIfMobileNumber(mobileNumber)){
      return false;
    }
    if(!checkIfPhoneNumber(phoneNumber)){
      return false;
    }
    if(!checkIfEmail(email)){
      return false;
    }
    Connection con = null;
    PreparedStatement preparedStatement;
    try{
      Class.forName("com.mysql.cj.jdbc.Driver");
      con = DriverManager.getConnection(dbURL, this.USER, this.PASSWORD);
      con.setAutoCommit(false);
      String check = "select * from Customers where PersonName=? and CompanyName=? and Email=? and Address=? and MobileNumber=? and PhoneNumber=?";
      preparedStatement = con.prepareStatement(check);
      preparedStatement.setString(1,name);
      preparedStatement.setString(2,companyName);
      preparedStatement.setString(3,email);
      preparedStatement.setString(4,address);
      preparedStatement.setString(5,mobileNumber);
      preparedStatement.setString(6,phoneNumber);
      ResultSet resultSet = preparedStatement.executeQuery();
      if(!resultSet.next()){
        String updateSQL = "update Customers " +
            "set PersonName = ?, CompanyName = ?, Email = ?,Address = ?,MobileNumber = ?, PhoneNumber = ?" +
            "where id = ?";
        preparedStatement = con.prepareStatement(updateSQL);
        preparedStatement.setString(1,name);
        preparedStatement.setString(2,companyName);
        preparedStatement.setString(3,email);
        preparedStatement.setString(4,address);
        preparedStatement.setString(5,mobileNumber);
        preparedStatement.setString(6,phoneNumber);
        preparedStatement.setString(7,id);
        int result = preparedStatement.executeUpdate();
        if (result == 1){
          con.commit();
        }else{
          con.rollback();
          return false;
        }
      }else{
        con.rollback();
        return false;
      }
    }catch(ClassNotFoundException | SQLException exception){
      try{
        if (con != null)
          con.rollback();
      }catch(SQLException throwables){
        throwables.printStackTrace();
      }
      return false;
    }finally{
      try{
        if (con != null)
          con.close();
      }catch(SQLException throwable){
        JOptionPane.showMessageDialog(this.mainFrame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    return true;
  }

  private void goToAddUsersPanel(){
    clearAllFields();
    this.mainFrame.getContentPane().removeAll();
    this.mainFrame.repaint();
    this.mainFrame.add(addUsersPanel);
    this.mainFrame.setTitle("Add Users");
    this.mainFrame.pack();
  }

  private void goToChangePasswordPanel(){
    clearAllFields();
    this.mainFrame.getContentPane().removeAll();
    this.mainFrame.repaint();
    this.mainFrame.add(changePasswordPanel);
    this.mainFrame.setTitle("Change Password");
    changePasswordPanel.addUsersButton.setEnabled(isAdmin);
    this.mainFrame.pack();
  }

  private void goToInsertDataPanel(){
    clearAllFields();
    this.mainFrame.getContentPane().removeAll();
    this.mainFrame.repaint();
    this.mainFrame.add(insertDataPanel);
    this.mainFrame.setTitle("Insert");
    insertDataPanel.addUsersButton.setEnabled(isAdmin);
    this.mainFrame.pack();
  }

  private void goToSearchPanel(){
    clearAllFields();
    this.mainFrame.getContentPane().removeAll();
    this.mainFrame.repaint();
    this.mainFrame.add(searchPanel);
    this.mainFrame.setTitle("Search");
    searchPanel.addUsersButton.setEnabled(this.isAdmin);
    this.mainFrame.pack();
  }

  private void goToLogin(){
    clearAllFields();
    this.mainFrame.getContentPane().removeAll();
    this.mainFrame.repaint();
    this.mainFrame.setTitle("Login");
    this.mainFrame.add(loginPanel);
    this.mainFrame.pack();
  }

  private void clearAllFields(){
    loginPanel.clearFields();
    insertDataPanel.clearFields();
    searchPanel.clearFields();
    addUsersPanel.clearFields();
    changePasswordPanel.clearFields();
  }

  private void checkConnectionAddress(){
    String address = addressPanel.getAddress();
    boolean firstMatch = address.matches(
        "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    boolean secondMatch = address.matches("[a-zA-z-_]+");
    if(firstMatch || secondMatch){

      Connection con = null;
      try{
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://" + address + ":3306/Database", this.USER, this.PASSWORD);
        addressPanel.clearFields();
        goToLogin();
        dbURL = "jdbc:mysql://" + address + ":3306/Database";
        con.close();
      }catch(ClassNotFoundException | SQLException exception){
        JOptionPane.showMessageDialog(this.mainFrame, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }finally{
        if(con != null){
          try{
            con.close();
          }catch(SQLException throwable){
            JOptionPane.showMessageDialog(this.mainFrame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    }else{
      JOptionPane.showMessageDialog(mainFrame,
          "The Address Should Look Like those Examples\n"
              + "xxx.xxx.xxx.xxx\n "
              + "hostname\n"
              + "x represent a number from 0-9",
          "Wrong Address", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void login(){
    Connection con = null;
    String sql;
    try{
      Class.forName("com.mysql.cj.jdbc.Driver");
      String username = loginPanel.getUserName().toLowerCase().trim();
      char[] password = loginPanel.getPassword();
      boolean isAPhoneNumber = checkIfMobileNumber(username);
      if(isAPhoneNumber){
        sql = "select Password,Salt,Admin from Users where Phonenumber=?";
      }else{
        sql = "select Password,Salt,Admin from Users where Username=?";
      }
      con = DriverManager.getConnection(dbURL, this.USER, this.PASSWORD);
      PreparedStatement preparedStatement = con.prepareStatement(sql);
      preparedStatement.setString(1, username);
      ResultSet resultSet = preparedStatement.executeQuery();
      if(resultSet.next()){
        String key = resultSet.getString("Password");
        String salt = resultSet.getString("Salt");
        boolean isAdmin = resultSet.getBoolean("Admin");
        boolean correctPassword = verifyPassword(password, key, salt);
        if(correctPassword){
          this.username = loginPanel.getUserName();
          this.isAPhoneNumberFlag = isAPhoneNumber;
          Arrays.fill(password, Character.MIN_VALUE);
          this.isAdmin = isAdmin;
          goToInsertDataPanel();
        }else{
          JOptionPane.showMessageDialog(this.mainFrame, "Wrong Password", "Wrong Password"
              , JOptionPane.ERROR_MESSAGE);
        }
      }else{
        JOptionPane.showMessageDialog(this.mainFrame, "Username or the phonenumber does not exist"
            , "Wrong Username", JOptionPane.ERROR_MESSAGE);
      }
      con.close();
    }catch(ClassNotFoundException | SQLException exception){
      JOptionPane.showMessageDialog(this.mainFrame, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }finally{
      if(con != null){
        try{
          con.close();
        }catch(SQLException throwable){
          JOptionPane.showMessageDialog(this.mainFrame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  /**
   * this method is used to check if the given String is a Valid Email Address.
   * @param string the String that contains the Email Address to check.
   * @return true if it matches the Regex, false otherwise.
   */
  public boolean checkIfEmail(String string){
    return string.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
        "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])" +
        "*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]" +
        "|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c" +
        "\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+))");
  }

  /**
   * this method is used to check if the given String is a Mobile Number wich looks like this example
   * 0561234567
   * Note that those phoneNumber is only for Palestine Cites.
   * @param string the String that is contain the Mobile Number to check.
   * @return true if it match the Regex, false other Wise.
   */
  public boolean checkIfMobileNumber(String string){
    return string.matches("^05[0-9]{8}");
  }

  /**
   * this method will check if the given string is a phone number which looks like this example
   * 092312345
   * Note that those phoneNumber is only for Palestine Cites.
   * @param string the String that contain the Phone Number to check.
   * @return true if it match the Regex, false other Wise.
   */
  public boolean checkIfPhoneNumber(String string){
    return string.matches("^0[294]2[2345679][0-9]{5}");
  }

  /**
   * this method will create kind of alphanumeric String with the given length.
   * @param length the length of the String.
   * @return a Random alphanumeric String.
   */
  public static String generateRandomString(final int length){
    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'
    Random random = new Random();

    return random.ints(leftLimit, rightLimit + 1)
        .limit(length)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }

  /**
   * this method is used to reset the Password from the Login Panel this method will not be used in
   * the mainFrame insted will be used in the frame that will come after the used enter the code that
   * has been sent to his email address
   */
  public void resetPassword(){
    Connection con = null;
    String sql;
    String username = resetPasswordFromLoginPanel.getUsername();
    boolean isAPhoneNumber = checkIfMobileNumber(username);
    if(isAPhoneNumber){
      sql = "update Users set Password=?, Salt=? where Phonenumber=? ";
    }else{
      sql = "update Users set Password=?, Salt=? where Username=?";
    }
    if(resetPasswordFromLoginPanel.passwordMatches()){
      char[] password = resetPasswordFromLoginPanel.getPassword();
      try{
        Optional<String> optional = generateSalt(32);
        String salt = optional.orElse("");
        optional = hashPassword(password, salt);
        String key = optional.orElse("");
        Arrays.fill(password, Character.MIN_VALUE);
        con = DriverManager.getConnection(dbURL, Logic.this.USER, Logic.this.PASSWORD);
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, key);
        preparedStatement.setString(2, salt);
        preparedStatement.setString(3, username);
        int rowAffected = preparedStatement.executeUpdate();
        if(rowAffected != 0){
          JOptionPane.showMessageDialog(Logic.this.mainFrame, "Password has been changed"
              , "Success", JOptionPane.INFORMATION_MESSAGE);
        }
      }catch(SQLException throwable){
        JOptionPane.showMessageDialog(Logic.this.mainFrame, throwable.getMessage()
            , "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }finally{
        if(con != null){
          try{
            con.close();
          }catch(SQLException throwables){
            JOptionPane.showMessageDialog(Logic.this.mainFrame, throwables.getMessage(), "Error"
                , JOptionPane.ERROR_MESSAGE);
          }
        }
        resetPasswordString = "";
      }
      resetPasswordFromLoginPanel.clearFields();
      resetFrame.setVisible(false);
      resetFrame.remove(resetPasswordFromLoginPanel);
    }
  }

  /**
   * this method is used to check if the given password is equal to the given key
   * by checking if the password after hashing is equal to the same key and the same Salt
   * @param password the password that need checking.
   * @param key the key to check if the password is right.
   * @param salt the salt that has been used to hash the key.
   * @return true if the password matches the key after hashing, or false if not.
   */
  public static boolean verifyPassword(char[] password, String key, String salt){
    Optional<String> optEncrypted = hashPassword(password, salt);
    return optEncrypted.map(s -> s.equals(key)).orElse(false);
  }

  /**
   * this method is used to Generate Salt for the hashing algorithm.
   * @param length the size of the Salt should be bigger than 1
   * @return Optional Obj with the Salt String in it.
   */
  public static Optional<String> generateSalt(final int length){
    if(length < 1){
      System.out.println("error in generateSalt: length must be > 0");
      return Optional.empty();
    }
    byte[] salt = new byte[length];
    RAND.nextBytes(salt);

    return Optional.of(Base64.getEncoder().encodeToString(salt));
  }

  /**
   * this method will hash the given password Array of Characters.
   * @param password the password that needs hashing.
   * @param salt the salt to use with the password.
   * @return Optional Obj with the hashed password in it.
   */
  public static Optional<String> hashPassword(char[] password, String salt){
    byte[] bytes = salt.getBytes();

    PBEKeySpec spec = new PBEKeySpec(password, bytes, ITERATION, KEY_LENGTH);

    Arrays.fill(password, Character.MIN_VALUE);

    try{
      SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
      byte[] securePassword = fac.generateSecret(spec).getEncoded();
      return Optional.of(Base64.getEncoder().encodeToString(securePassword));
    }catch(NoSuchAlgorithmException | InvalidKeySpecException e){
      System.err.println("Exception encountered in hashPassword()");
      return Optional.empty();
    }finally{
      spec.clearPassword();
    }
  }

  /**
   * @param args
   *     the command line arguments
   */
  public static void main(String[] args){
    new Logic();
  }

}
