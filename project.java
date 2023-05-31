import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.util.*;

/**
 * CS 4322 Project
 * 
 * @author Sydney Hoglund
 * @author Jacob Rohde
 * @date 4/14/23
 */

public class project {

	String jdbcUrl = "jdbc:postgresql://localhost:63333/rohde135";
	Connection conn;

	public Connection getDBConnection() throws SQLException {

		if (conn == null) {
			// Display a message to get the password from the user
			JLabel label = new JLabel("Postgres Username: ");
			JTextField jtf = new JTextField();
			JLabel label2 = new JLabel("Postgres Password:");
			JPasswordField jpf = new JPasswordField();
			JOptionPane.showConfirmDialog(null, new Object[] { label, jtf, label2, jpf }, "Password:",
					JOptionPane.OK_CANCEL_OPTION);

			String password = String.valueOf(jpf.getPassword());
			conn = DriverManager.getConnection(jdbcUrl, jtf.getText(), password);
		}
		conn.setAutoCommit(true);
		return conn;
	}

	public static void main(String[] args) throws Exception {
		project q = new project();

		String pianoID = new String();
		String clientID = new String();
		String salesperson = new String();
		String salePrice = new String();

		String make = new String();
		String model = new String();
		String query = new String();

		try (Scanner sc = new Scanner(System.in)) {
			System.out.println("1: Sell piano to existing customer.\n2: Find all pianos of a given make and model.");
			System.out.println("\nWould you like to run query 1 or 2? Any other character to exit.");
			query = sc.nextLine();
			while (query.equals("1") || query.equals("2")) {

				switch (query) {
				case "1":
					System.out.println("\nEnter piano ID:");
					pianoID = sc.nextLine();
					System.out.println("\nEnter client ID:");
					clientID = sc.nextLine();
					System.out.println("\nEnter salesperson:");
					salesperson = sc.nextLine();
					System.out.println("\nEnter sale price:");
					salePrice = sc.nextLine();

					boolean success = q.createReceipt(pianoID, clientID, salesperson, salePrice);

					System.out.println("Tables successfully updated: " + success + "\n");

					System.out.println("\nWould you like to run query 1 or 2? Any other character to exit.");
					query = sc.nextLine();
					break;

				case "2":
					System.out.println("\nEnter piano make:");
					make = sc.nextLine();
					System.out.println("\nEnter piano model:");
					model = sc.nextLine();

					q.searchPiano(make, model);

					System.out.println("\nWould you like to run query 1 or 2? Any other character to exit.");
					query = sc.nextLine();
					break;

				default:
					break;
				}
			}
		}
	}

	public boolean createReceipt(String pianoID, String clientID, String salesperson, String salePrice)
			throws Exception {

		assert (pianoID != null);
		assert (clientID != null);
		assert (salesperson != null);
		assert (salePrice != null);

		// Connect to the database.
		getDBConnection();
		int updatedReceiptRows = 0;
		int updatedPurchaseRows = 0;

		String sqlCheckPiano = "SELECT * FROM piano WHERE serial_number = ?";

		ResultSet pianoResult = null;
		try (PreparedStatement checkPianoStmt = conn.prepareStatement(sqlCheckPiano);) {

			checkPianoStmt.setString(1, pianoID);

			// Run the query
			pianoResult = checkPianoStmt.executeQuery();

		} catch (SQLException e) {
			// Rollback the transaction
			// conn.rollback();
			e.printStackTrace();
		} finally {
			// conn.setAutoCommit(true);
		}

		String sqlCheckClient = "SELECT * FROM client WHERE client_id = ?";

		ResultSet clientResult = null;
		try (PreparedStatement checkClientStmt = conn.prepareStatement(sqlCheckClient);) {

			checkClientStmt.setString(1, clientID);

			clientResult = checkClientStmt.executeQuery();

		} catch (SQLException e) {
			// Rollback the transaction
			// conn.rollback();
			e.printStackTrace();
		} finally {
			// conn.setAutoCommit(true);
		}

		if (!(clientResult != null && pianoResult != null)) {
			throw new Exception("Invalid Piano or Client ID");
		}

		String sqlGetMaxReceipt = "SELECT MAX(receipt_id) FROM receipt";

		int receiptID = 0;
		try (PreparedStatement GetMaxReceiptStmt = conn.prepareStatement(sqlGetMaxReceipt);) {

			ResultSet maxReceipt = GetMaxReceiptStmt.executeQuery();

			maxReceipt.next();

			receiptID = maxReceipt.getInt("max") + 1;

		} catch (SQLException e) {
			// Rollback the transaction
			// conn.rollback();
			e.printStackTrace();
		} finally {
			// conn.setAutoCommit(true);
		}

		double salePriceDouble = Double.parseDouble(salePrice);

		String sqlInsertReceipt = "INSERT INTO receipt VALUES(?,CURRENT_DATE,?,?,?)";

		try (PreparedStatement receiptInsertStmt = conn.prepareStatement(sqlInsertReceipt);) {

			receiptInsertStmt.setInt(1, receiptID);
			receiptInsertStmt.setDouble(2, salePriceDouble);
			receiptInsertStmt.setString(3, salesperson);
			receiptInsertStmt.setString(4, clientID);

			updatedReceiptRows = receiptInsertStmt.executeUpdate();

		} catch (SQLException e) {
			// Rollback the transaction
			// conn.rollback();
			e.printStackTrace();
		} finally {
			// conn.setAutoCommit(true);
		}

		String sqlInsertPurchase = "INSERT INTO purchases VALUES(?,?)";

		try (PreparedStatement purchaseInsertStmt = conn.prepareStatement(sqlInsertPurchase);) {

			purchaseInsertStmt.setString(1, pianoID);
			purchaseInsertStmt.setInt(2, receiptID);

			updatedPurchaseRows = purchaseInsertStmt.executeUpdate();

		} catch (SQLException e) {
			// Rollback the transaction
			// conn.rollback();
			e.printStackTrace();
		} finally {
			// conn.setAutoCommit(true);
		}

		return (updatedReceiptRows != 0 && updatedPurchaseRows != 0);
	}

	public void searchPiano(String make, String model) throws SQLException {

		assert (make != null);
		assert (model != null);
		// Connect to the database.
		getDBConnection();

		// Elaborate a string with the content of the query to search piano by make and
		// model
		String sqlSearchPiano = "SELECT serial_number, year, msrp " + "FROM piano " + "WHERE make = ? AND model = ?";

		try (PreparedStatement pianoSearch = conn.prepareStatement(sqlSearchPiano);) {

			pianoSearch.setString(1, make);
			pianoSearch.setString(2, model);

			// Run the query
			ResultSet rs = pianoSearch.executeQuery();

			// Print out query result line by line
			System.out.println("Serial_num  year  msrp");

			while (rs.next()) {
				String serialNum = rs.getString("serial_number");
				String year = rs.getString("year");
				String msrp = rs.getString("msrp");

				System.out.println(serialNum + " " + year + " " + msrp);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}