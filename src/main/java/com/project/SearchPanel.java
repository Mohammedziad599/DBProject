package com.project;

import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

/**
 * @author Mohammed Zaid
 */
public class SearchPanel extends javax.swing.JPanel {

    /**
     * Creates new form SearchPanel
     */
    public SearchPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        changedIndex = new HashSet<>();
        JScrollPane jScrollPane1 = new JScrollPane();
        searchTable = new javax.swing.JTable();
        companyNameField = new javax.swing.JTextField();
        searchByAllButton = new javax.swing.JButton();
        JLabel companyNameLabel = new JLabel();
        personNameField = new javax.swing.JTextField();
        searchByPersonNameButton = new javax.swing.JButton();
        JLabel personNameLabel = new JLabel();
        searchByCompanyNameButton = new javax.swing.JButton();
        saveModificationButton = new javax.swing.JButton();
        tableStatusLabel = new javax.swing.JLabel();
        insertButton = new javax.swing.JButton();
        addUsersButton = new javax.swing.JButton();
        logOutButton = new javax.swing.JButton();
        changePasswordButton = new javax.swing.JButton();

        //noinspection rawtypes
        searchTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Person Name", "Company Name", "Address", "Email", "Phone Number", "Mobile Number"
            }
        ) {
            final Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            final boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        searchTable.setToolTipText("the data will appear here");
        searchTable.setColumnSelectionAllowed(true);
        searchTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(searchTable);
        searchTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (searchTable.getColumnModel().getColumnCount() > 0) {
            searchTable.getColumnModel().getColumn(0).setMinWidth(0);
            searchTable.getColumnModel().getColumn(0).setPreferredWidth(0);
            searchTable.getColumnModel().getColumn(0).setMaxWidth(0);
        }

        searchByAllButton.setText("Search By All");
        searchByAllButton.setToolTipText("search by both the person name and the company name");

        companyNameLabel.setText("Company Name:");

        searchByPersonNameButton.setText("Search By Person Name");
        searchByPersonNameButton.setToolTipText("search by the person name");

        personNameLabel.setText("Person Name:");

        searchByCompanyNameButton.setText("Search By Company Name");
        searchByCompanyNameButton.setToolTipText("search by the company name");

        saveModificationButton.setText("Save");
        saveModificationButton.setToolTipText("save the modification done on the table");

        tableStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tableStatusLabel.setText("The Data did not Changed.");

        insertButton.setText("Insert");

        addUsersButton.setText("Add Users");

        logOutButton.setText("Logout");

        changePasswordButton.setText("Change Password");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saveModificationButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(companyNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(personNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(companyNameField)
                            .addComponent(personNameField)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchByCompanyNameButton, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchByPersonNameButton, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchByAllButton, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                    .addComponent(tableStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(logOutButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addUsersButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(insertButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(changePasswordButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(insertButton)
                    .addComponent(addUsersButton)
                    .addComponent(logOutButton)
                    .addComponent(changePasswordButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(companyNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(companyNameLabel))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(personNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(personNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchByCompanyNameButton)
                    .addComponent(searchByPersonNameButton)
                    .addComponent(searchByAllButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableStatusLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveModificationButton)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public void addChangedIndex(int index){
        changedIndex.add(index);
    }

    public Set<Integer> getChangedIndexes(){
        return this.changedIndex;
    }

    public void clearChangedIndex(){
        changedIndex = new HashSet<>();
    }

    public String getPersonName(){
        return this.personNameField.getText();
    }
    public String getCompanyName(){
        return this.companyNameField.getText();
    }

    public void addToTheTable(String id,String name,String companyName
                                ,String address,String email
                                ,String phoneNumber,String mobileNumber){
        DefaultTableModel model = (DefaultTableModel) searchTable.getModel();
        model.addRow(new Object[]{id,name,companyName,address,email,phoneNumber,mobileNumber});
    }
    public void clearFields(){
        clearSearchTable();
        this.companyNameField.setText("");
        this.personNameField.setText("");
        changeTableStatus("The Data did not Changed.");
    }

    public JTable getSearchTable(){
        return searchTable;
    }

    public void clearSearchTable(){
        DefaultTableModel model = (DefaultTableModel) this.searchTable.getModel();
        model.setRowCount(0);
        this.searchTable.revalidate();
    }

    public boolean checkIfTableIsChanged(){
        return !this.tableStatusLabel.getText().equals("The Data did not Changed.");
    }

    public void changeTableStatus(String text){
        this.tableStatusLabel.setText(text);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Set<Integer> changedIndex;
    protected javax.swing.JButton addUsersButton;
    protected javax.swing.JButton changePasswordButton;
    private javax.swing.JTextField companyNameField;
    protected javax.swing.JButton insertButton;
    protected javax.swing.JButton logOutButton;
    private javax.swing.JTextField personNameField;
    protected javax.swing.JButton saveModificationButton;
    protected javax.swing.JButton searchByAllButton;
    protected javax.swing.JButton searchByCompanyNameButton;
    protected javax.swing.JButton searchByPersonNameButton;
    protected javax.swing.JTable searchTable;
    private javax.swing.JLabel tableStatusLabel;
    // End of variables declaration//GEN-END:variables
}
