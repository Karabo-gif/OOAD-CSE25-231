package bank.dao;

import bank.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private final AccountDAO accountDAO = new AccountDAO();

    // CREATE (customer plus all their accounts)
    public void insert(Customer c) {
        String sql = "INSERT INTO customers (customer_id, customer_type, address, user_password, "
                + "first_name, surname, id_number, age, employed, company_name, company_registry) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCustomerId());
            ps.setString(3, c.getAddress());
            ps.setString(4, c.getPassword());
            if (c instanceof IndividualCustomer ind) {
                ps.setString(2, "INDIVIDUAL");
                ps.setString(5, ind.getFirstName());
                ps.setString(6, ind.getSurname());
                ps.setString(7, ind.getIdNumber());
                ps.setInt(8, ind.getAge());
                ps.setBoolean(9, ind.isEmployed());
                ps.setNull(10, Types.VARCHAR);
                ps.setNull(11, Types.VARCHAR);
            } else {
                CompanyCustomer co = (CompanyCustomer) c;
                ps.setString(2, "COMPANY");
                ps.setNull(5, Types.VARCHAR);
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.VARCHAR);
                ps.setNull(8, Types.INTEGER);
                ps.setNull(9, Types.BOOLEAN);
                ps.setString(10, co.getFullName());
                ps.setString(11, co.getCompanyRegistry());
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not insert customer", e);
        }
        for (Account a : c.getAccounts()) {
            accountDAO.insert(a);
        }
    }

    // READ one
    public Customer findById(String id) {
        Customer c = null;
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM customers WHERE customer_id = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c = build(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not read customer", e);
        }
        if (c != null) {
            loadAccounts(c);
        }
        return c;
    }

    // READ all
    public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
        try (Connection con = DatabaseManager.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM customers ORDER BY customer_id")) {
            while (rs.next()) {
                list.add(build(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not read customers", e);
        }
        for (Customer c : list) {
            loadAccounts(c);
        }
        return list;
    }

    // UPDATE
    public void updateAddress(String id, String newAddress) {
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE customers SET address = ? WHERE customer_id = ?")) {
            ps.setString(1, newAddress);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not update customer", e);
        }
    }

    // DELETE
    public void delete(String id) {
        accountDAO.deleteByCustomer(id);
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM customers WHERE customer_id = ?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete customer", e);
        }
    }

    private void loadAccounts(Customer c) {
        for (Account a : accountDAO.findByCustomer(c)) {
            c.attachAccount(a);
        }
    }

    private Customer build(ResultSet rs) throws SQLException {
        String id = rs.getString("customer_id");
        String address = rs.getString("address");
        String password = rs.getString("user_password");
        if ("INDIVIDUAL".equals(rs.getString("customer_type"))) {
            return new IndividualCustomer(id, address, password,
                    rs.getString("first_name"), rs.getString("surname"),
                    rs.getString("id_number"), rs.getInt("age"), rs.getBoolean("employed"));
        }
        return new CompanyCustomer(id, address, password,
                rs.getString("company_name"), rs.getString("company_registry"));
    }
}