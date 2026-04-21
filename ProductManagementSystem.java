package ProductManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ProductManagementSystem extends JFrame {

    JTable catTable, subCatTable, productTable;
    DefaultTableModel catModel, subCatModel, productModel;

    JTextField catField, subCatField;
    JTextField nameField, priceField, quantityField, descField, searchField;

    Connection con;

    String selectedCatId = "";
    String selectedSubId = "";
    String selectedProductId = "";

    public ProductManagementSystem() {

        setTitle("Super Market Advanced");
        setSize(1200, 650);
        setLayout(new BorderLayout(10,10));

        // ================= FORM =================
        JPanel form = new JPanel(new GridLayout(7,2,10,10));
        form.setBorder(BorderFactory.createTitledBorder("Details"));

        catField = new JTextField();
        subCatField = new JTextField();
        nameField = new JTextField();
        priceField = new JTextField();
        quantityField = new JTextField();
        descField = new JTextField();
        searchField = new JTextField();

        form.add(new JLabel("Category"));
        form.add(catField);

        form.add(new JLabel("Sub Category"));
        form.add(subCatField);

        form.add(new JLabel("Product Name"));
        form.add(nameField);

        form.add(new JLabel("Price"));
        form.add(priceField);

        form.add(new JLabel("Quantity"));
        form.add(quantityField);

        form.add(new JLabel("Description"));
        form.add(descField);

        form.add(new JLabel("Search"));
        form.add(searchField);

        add(form, BorderLayout.NORTH);

        // ================= TABLES =================
        JPanel panel = new JPanel(new GridLayout(1,3,10,10));

        catModel = new DefaultTableModel();
        catTable = new JTable(catModel);

        subCatModel = new DefaultTableModel();
        subCatTable = new JTable(subCatModel);

        productModel = new DefaultTableModel();
        productTable = new JTable(productModel);

        panel.add(new JScrollPane(catTable));
        panel.add(new JScrollPane(subCatTable));
        panel.add(new JScrollPane(productTable));

        add(panel, BorderLayout.CENTER);

        // ================= BUTTONS =================
        JPanel btn = new JPanel();

        JButton addCat = new JButton("Add Category");
        JButton addSub = new JButton("Add SubCategory");
        JButton add = new JButton("Add Product");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton searchBtn = new JButton("Search");

        btn.add(addCat);
        btn.add(addSub);
        btn.add(add);
        btn.add(update);
        btn.add(delete);
        btn.add(searchBtn);

        add(btn, BorderLayout.SOUTH);

        connectDB();
        loadCategories();

        // ================= CATEGORY CLICK =================
        catTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = catTable.getSelectedRow();
                if(row == -1) return;

                selectedCatId = catModel.getValueAt(row, 0).toString();

                productModel.setRowCount(0); // clear products
                loadSubCategories(selectedCatId);
            }
        });

        // ================= SUB CATEGORY CLICK =================
        subCatTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = subCatTable.getSelectedRow();
                if(row == -1) return;

                selectedSubId = subCatModel.getValueAt(row, 0).toString();
                loadProducts(selectedSubId);
            }
        });

        // ================= PRODUCT CLICK =================
        productTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = productTable.getSelectedRow();
                if(row == -1) return;

                selectedProductId = productModel.getValueAt(row, 0).toString();

                nameField.setText(productModel.getValueAt(row, 1).toString());
                priceField.setText(productModel.getValueAt(row, 2).toString());
                quantityField.setText(productModel.getValueAt(row, 4).toString());
                descField.setText(productModel.getValueAt(row, 5).toString());
            }
        });

        // ================= BUTTON ACTIONS =================

        // ADD CATEGORY (matches your DB: name)
        addCat.addActionListener(e -> {
            try {
                PreparedStatement pst = con.prepareStatement(
                        "INSERT INTO categories (name) VALUES (?)"
                );
                pst.setString(1, catField.getText());
                pst.executeUpdate();

                loadCategories();
                catField.setText("");

            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, ex);
            }
        });

        // ADD SUB CATEGORY
        addSub.addActionListener(e -> {
            try {

                if(selectedCatId.equals("")){
                    JOptionPane.showMessageDialog(this,"Select Category first!");
                    return;
                }

                PreparedStatement pst = con.prepareStatement(
                        "INSERT INTO sub_categories (name, category_id) VALUES (?,?)"
                );

                pst.setString(1, subCatField.getText());
                pst.setInt(2, Integer.parseInt(selectedCatId));

                pst.executeUpdate();

                loadSubCategories(selectedCatId);
                subCatField.setText("");

            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, ex);
            }
        });
        add.addActionListener(e -> addProduct());
        update.addActionListener(e -> updateProduct());
        delete.addActionListener(e -> deleteProduct());
        searchBtn.addActionListener(e -> searchProduct(searchField.getText()));

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    // ================= DB =================
    void connectDB(){
        try{
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/keerthana_database",
                    "root",
                    "Kani@2006"
            );
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, e);
        }
    }

    // ================= LOAD =================
    void loadCategories(){
        try{
            catModel.setRowCount(0);

            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM categories");

            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();

            String[] col = new String[cols];
            for(int i=0;i<cols;i++)
                col[i]=md.getColumnName(i+1);

            catModel.setColumnIdentifiers(col);

            while(rs.next()){
                Object[] row = new Object[cols];
                for(int i=0;i<cols;i++)
                    row[i]=rs.getObject(i+1);
                catModel.addRow(row);
            }

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,e);
        }
    }

    void loadSubCategories(String catId){
        try{
            subCatModel.setRowCount(0);

            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM sub_categories WHERE category_id=?"
            );
            pst.setInt(1, Integer.parseInt(catId));

            ResultSet rs = pst.executeQuery();

            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();

            String[] col = new String[cols];
            for(int i=0;i<cols;i++)
                col[i]=md.getColumnName(i+1);

            subCatModel.setColumnIdentifiers(col);

            while(rs.next()){
                Object[] row = new Object[cols];
                for(int i=0;i<cols;i++)
                    row[i]=rs.getObject(i+1);
                subCatModel.addRow(row);
            }

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,e);
        }
    }

    void loadProducts(String subId){
        try{
            productModel.setRowCount(0);

            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM products WHERE sub_category_id=?"
            );
            pst.setInt(1, Integer.parseInt(subId));

            ResultSet rs = pst.executeQuery();

            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();

            String[] col = new String[cols];
            for(int i=0;i<cols;i++)
                col[i]=md.getColumnName(i+1);

            productModel.setColumnIdentifiers(col);

            while(rs.next()){
                Object[] row = new Object[cols];
                for(int i=0;i<cols;i++)
                    row[i]=rs.getObject(i+1);
                productModel.addRow(row);
            }

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,e);
        }
    }

    // ================= PRODUCT =================
    void addProduct(){
        try{

            if(selectedSubId.equals("")){
                JOptionPane.showMessageDialog(this,"Select Sub Category first!");
                return;
            }

            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO products (product_name, price, sub_category_id, quantity, description) VALUES (?,?,?,?,?)"
            );

            pst.setString(1, nameField.getText());
            pst.setDouble(2, Double.parseDouble(priceField.getText()));
            pst.setInt(3, Integer.parseInt(selectedSubId));
            pst.setInt(4, quantityField.getText().isEmpty()?0:Integer.parseInt(quantityField.getText()));
            pst.setString(5, descField.getText());

            pst.executeUpdate();

            loadProducts(selectedSubId);
            clearFields();

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,e);
        }
    }

    void updateProduct(){
        try{

            if(selectedProductId.equals("")){
                JOptionPane.showMessageDialog(this,"Select Product first!");
                return;
            }

            PreparedStatement pst = con.prepareStatement(
                    "UPDATE products SET product_name=?, price=?, quantity=?, description=? WHERE id=?"
            );

            pst.setString(1, nameField.getText());
            pst.setDouble(2, Double.parseDouble(priceField.getText()));
            pst.setInt(3, quantityField.getText().isEmpty()?0:Integer.parseInt(quantityField.getText()));
            pst.setString(4, descField.getText());
            pst.setInt(5, Integer.parseInt(selectedProductId));

            pst.executeUpdate();

            loadProducts(selectedSubId);
            clearFields();

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,e);
        }
    }

    void deleteProduct(){
        try{

            if(selectedProductId.equals("")){
                JOptionPane.showMessageDialog(this,"Select Product first!");
                return;
            }

            PreparedStatement pst = con.prepareStatement(
                    "DELETE FROM products WHERE id=?"
            );

            pst.setInt(1, Integer.parseInt(selectedProductId));
            pst.executeUpdate();

            loadProducts(selectedSubId);
            clearFields();

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,e);
        }
    }

    void searchProduct(String keyword){
        try{

            productModel.setRowCount(0);

            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM products WHERE product_name LIKE ?"
            );

            pst.setString(1, "%" + keyword + "%");

            ResultSet rs = pst.executeQuery();

            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();

            String[] col = new String[cols];
            for(int i=0;i<cols;i++)
                col[i]=md.getColumnName(i+1);

            productModel.setColumnIdentifiers(col);

            while(rs.next()){
                Object[] row = new Object[cols];
                for(int i=0;i<cols;i++)
                    row[i]=rs.getObject(i+1);
                productModel.addRow(row);
            }

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,e);
        }
    }

    void clearFields(){
        nameField.setText("");
        priceField.setText("");
        quantityField.setText("");
        descField.setText("");
    }

    public static void main(String[] args){
        new ProductManagementSystem();
    }
}