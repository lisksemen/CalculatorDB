package com.main;

import com.caclulator.CalculatedEntry;
import com.caclulator.Calculator;
import com.database.Database;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        Scanner scanner = new Scanner(System.in);
        try {
            Database database = Database.getInstance();
            boolean exit = false;
            while (!exit) {
                System.out.println("Select action:");
                System.out.println("1) Calculate and add expression to the database");
                System.out.println("2) List all the expressions and values in the database");
                System.out.println("3) Search for expressions in the database");
                System.out.println("4) Delete specified expression");
                System.out.println("5) Modify specified expression");
                System.out.println("0) Exit");
                switch (scanner.nextLine()) {
                    case "1" -> calculateMenu(calculator, database, scanner);
                    case "2" -> listDatabase(database, scanner);
                    case "3" -> searchInDatabase(database, scanner);
                    case "4" -> deleteFromDatabase(database, scanner);
                    case "5" -> modify(calculator, database, scanner);
                    case "0" -> exit = true;
                    default -> System.out.println("Invalid input. Please try again");
                }
            }
            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void modify(Calculator calculator, Database database, Scanner scanner) throws SQLException {
        clearScreen();
        System.out.println("Do you want to list everything from database? (y/n)");
        if (scanner.nextLine().equals("y"))
            listResultSet(database.listAll());
        else
            System.out.println("Ok");

        while (true) {
            System.out.println("Please enter id of expression to modify:");
            try {
                int id = Integer.parseInt(scanner.nextLine());
                ResultSet set = database.searchById(id);
                CalculatedEntry entry = null;
                if (set.next()) {
                    entry = new CalculatedEntry(set.getInt("result"),
                            set.getString("infix_expression"),
                            set.getString("postfix_expression"));
                }
                if (entry == null) {
                    System.out.println("Id not found. Please try again");
                    pause(scanner);
                    clearScreen();
                    continue;
                }
                while (true) {
                    System.out.println("Please enter a new expression:");
                    String expression = scanner.nextLine();
                    CalculatedEntry newEntry = calculator.calculate(expression);
                    if (newEntry == null) {
                        System.out.println("Invalid expression input. Please try again");
                        pause(scanner);
                        clearScreen();
                        continue;
                    }
                    System.out.println("Value = " + newEntry.getValue());
                    System.out.println("Save to DB? (y/n)");
                    if (scanner.nextLine().equals("y")) {
                        database.modify(id, newEntry);
                        System.out.println("Saved successfully");
                        pause(scanner);
                        clearScreen();
                        break;
                    }

                }

                break;

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please try again");
                pause(scanner);
                clearScreen();
            }

        }
    }

    private static void deleteFromDatabase(Database database, Scanner scanner) {
        boolean exit = false;
        while (!exit) {
            clearScreen();
            System.out.println("Please choose how to delete:");
            System.out.println("1) Delete by index");
            System.out.println("2) Delete by value");
            switch (scanner.nextLine()) {
                case "1" -> {
                    deleteByIndex(database, scanner);
                    exit = true;
                }
                case "2" -> {
                    deleteByValue(database, scanner);
                    exit = true;
                }
                default -> System.out.println("Invalid input. Please try again");
            }
        }
    }

    private static void deleteByValue(Database database, Scanner scanner) {
        String queryPart = getQueryPart(scanner);
        try {
            database.deleteByValue(queryPart);
            System.out.println("Deleted successfully");
        } catch (SQLException e) {
            System.err.println("Unable to delete");
            e.printStackTrace();
        }
        pause(scanner);
        clearScreen();

    }

    private static void deleteByIndex(Database database, Scanner scanner) {
        while (true) {
            int index;
            System.out.println("Please enter index:");
            try {
                index = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please try again");
                clearScreen();
                continue;
            }
            try {
                database.deleteById(index);
                System.out.println("Deleted successfully");

            } catch (SQLException e) {
                System.err.println("Unable to delete");
            }
            pause(scanner);
            clearScreen();
            break;

        }
    }

    private static void searchInDatabase(Database database, Scanner scanner) throws SQLException {
        String queryPart = getQueryPart(scanner);
        listResultSet(database.searchByValue(queryPart));
        pause(scanner);
        clearScreen();
    }

    private static String getQueryPart(Scanner scanner) {
        boolean exit = false;
        StringBuilder queryPart = new StringBuilder();
        while (!exit) {
            System.out.println("Please specify how to search:");
            System.out.println("1) With value bigger than specified");
            System.out.println("2) With value equal specified");
            System.out.println("3) With value less than specified");
            switch (scanner.nextLine()) {
                case "1" -> {
                    queryPart.append(">");
                    exit = true;
                }
                case "2" -> {
                    queryPart.append("=");
                    exit = true;
                }
                case "3" -> {
                    queryPart.append("<");
                    exit = true;
                }
                default -> System.out.println("Invalid input. Please try again");
            }
        }

        while (true) {
            System.out.println("Please specify value to compare with:");
            try {
                queryPart.append(Integer.parseInt(scanner.nextLine()));
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again");
                clearScreen();
            }
        }
        return queryPart + "";
    }

    private static void pause(Scanner scanner) {
        System.out.println("Enter any line to return to the main menu");
        scanner.nextLine();
    }

    private static void listDatabase(Database database, Scanner scanner) throws SQLException {
        System.out.println("\nDB content:");
        listResultSet(database.listAll());
        pause(scanner);
        clearScreen();
    }

    private static void listResultSet(ResultSet set) throws SQLException {
        while (set.next()) {
            System.out.println("id = " + set.getInt(1) +
                    " expression = " + set.getString(2) +
                    " result = " + set.getInt(4));
        }
    }

    private static void calculateMenu(Calculator calculator, Database database, Scanner scanner) throws SQLException {
        while (true) {
            clearScreen();
            System.out.println("Please enter expression:");
            CalculatedEntry entry = calculator.calculate(scanner.nextLine());
            if (entry == null) {
                System.out.println("Expression is not correct. Please try again");
                continue;
            }
            System.out.println("Value = " + entry.getValue());
            System.out.println("Save to db? (y/n)");
            if (scanner.nextLine().equals("y")) {
                database.saveEntry(entry);
                System.out.println("Saved successfully");
                pause(scanner);
                clearScreen();
                break;
            }
            System.out.println("Expression has not been saved");
            pause(scanner);
            clearScreen();
            break;

        }
    }

    private static void clearScreen() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (IOException e) {
            for (int i = 0; i < 20; i++) {
                System.out.println();
            }
        }
    }
}
