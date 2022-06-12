package com.database;

import com.caclulator.CalculatedEntry;

import java.sql.*;

public class Database implements DatabaseConstants{
    private static Database instance;

    public static Database getInstance(){
        if (instance == null) {
            try {
                Driver driver = new com.mysql.cj.jdbc.Driver();
                DriverManager.registerDriver(driver);
                Connection connection = DriverManager.getConnection(URL + DBNAME, USER, PASSWORD);
                instance = new Database(connection);
            } catch (SQLException e) {
                createDatabaseAndTable();
            }
        }
        return instance;
    }

    private static void createDatabaseAndTable() {
        try {
            Driver driver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(driver);
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            statement.execute("CREATE DATABASE IF NOT EXISTS " + DBNAME);
            statement.execute("USE " + DBNAME);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS calculated(
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    infix_expression varchar(256) NOT NULL,
                    postfix_expression varchar(256) NOT NULL,
                    result INT(12) NOT NULL
                    );""");
            instance = new Database(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private final Connection connection;
    private final Statement statement;

    public Database (Connection connection) throws SQLException {
        this.connection = connection;
        this.statement = connection.createStatement();
    }

    public ResultSet listAll() throws SQLException {
        return statement.executeQuery("SELECT * FROM calculated");
    }

    public ResultSet searchById(int id) throws SQLException {
        return statement.executeQuery("SELECT * FROM calculated WHERE id=" + id);
    }
    public ResultSet searchByValue(String queryPart) throws SQLException {
        return statement.executeQuery("SELECT * FROM calculated WHERE result" + queryPart);
    }

    public void deleteById(int id) throws SQLException {
        statement.execute("DELETE FROM calculated where id=" + id);
    }


    public void deleteByValue(String queryPart) throws SQLException {
        statement.execute("DELETE FROM calculated where result" + queryPart);
    }

    public void modify(int id, CalculatedEntry newEntry) throws SQLException {
        statement.execute("UPDATE calculated SET" +
                " infix_expression = " + "'" + newEntry.expression()  + "', " +
                " postfix_expression = " + "'" + newEntry.postfixExpression()  + "', " +
                " result = " + newEntry.getValue() +
                "WHERE id = " + id + " " +
                ";");
    }

    public void saveEntry(CalculatedEntry entry) throws SQLException {
        String sql = "INSERT INTO calculated(infix_expression, postfix_expression, result) " +
                "VALUES(" +
                "'" + entry.getExpression() + "'" + ", " +
                "'" + entry.getPostfixExpression() + "'" + ", " +
                entry.getValue() +
                ")";
        statement.execute(sql);
    }

    public void close() throws SQLException {
        connection.close();
    }



}

