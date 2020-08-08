package com.project;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * @author Mohammed Zaid
 */
public class LoginPanel extends javax.swing.JPanel {

    /**
     * Creates new form LoginPanel
     */
    public LoginPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        loginButton = new javax.swing.JButton();
        javax.swing.JLabel userNameLabel = new javax.swing.JLabel();
        javax.swing.JLabel passwordLabel = new javax.swing.JLabel();
        userNameField = new javax.swing.JTextField();
        resetPasswordButton = new javax.swing.JButton();
        passwordField = new javax.swing.JPasswordField();

        loginButton.setText("Login");

        userNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        userNameLabel.setText("User Name or Phone Number:");

        passwordLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        passwordLabel.setText("Password:");

        userNameField.setToolTipText("you can login either by your phone number or your username.");

        resetPasswordButton.setText("Reset Password");

        passwordField.setToolTipText("enter your password here.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(userNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(passwordLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(userNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                                .addComponent(passwordField)))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(resetPasswordButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(loginButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(userNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(userNameLabel))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(passwordLabel)
                        .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(loginButton)
                        .addComponent(resetPasswordButton))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>

    /**
     * this method for getting the user name from the field.
     * @return the user name that the user entered in the userNameField.
     */
    public String getUserName(){
        return this.userNameField.getText();
    }
    
    /**
     * this method for getting the password that the user entered.
     * @return the password the user entered in the passwordField.
     */
    public char[] getPassword(){
        return this.passwordField.getPassword();
    }
    
    public JPasswordField getPasswordField(){
        return this.passwordField;
    }
    
    public JTextField getUserNameField(){
        return this.userNameField;
    }

    public void clearFields(){
        this.passwordField.setText("");
        this.userNameField.setText("");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton loginButton;
    private javax.swing.JPasswordField passwordField;
    protected javax.swing.JButton resetPasswordButton;
    private javax.swing.JTextField userNameField;
    // End of variables declaration//GEN-END:variables
}
