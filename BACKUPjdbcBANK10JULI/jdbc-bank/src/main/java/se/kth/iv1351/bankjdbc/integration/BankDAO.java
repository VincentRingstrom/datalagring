/*
 * The MIT License (MIT)
 * Copyright (c) 2020 Leif Lindbäck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction,including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so,subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package se.kth.iv1351.bankjdbc.integration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.xdevapi.Result;

import se.kth.iv1351.bankjdbc.model.Instrument;
import se.kth.iv1351.bankjdbc.model.InstrumentDTO;

/**
 * This data access object (DAO) encapsulates all database calls in the bank
 * application. No code outside this class shall have any knowledge about the
 * database.
 */
public class BankDAO {

    private static final String INSTRUMENT_TABLE_NAME = "instrument_inventory_at_school";
    private String INSTRUMENT_KIND; // NEW PARTS.

    private Connection connection;

    private PreparedStatement findAccountByAcctNoStmt;
    private PreparedStatement findAccountByAcctNoStmtLockingForUpdate;
    private PreparedStatement findAllAccountsStmt;
    private PreparedStatement deleteAccountStmt;
    private PreparedStatement changeBalanceStmt;

    private PreparedStatement findAllInstrumentsStmt;
    private PreparedStatement findInstrumentByNameStmt;
    private PreparedStatement finalStmt;
    private PreparedStatement createRentalStmt;
    private PreparedStatement terminateRentalStmt;
    private PreparedStatement instrumentAlreadyRentedStmt;
    private PreparedStatement instrumentTooManyStmt;

    private PreparedStatement updateInstrumentAvailabilityStmt; // NY.
    private PreparedStatement instrumentAvailableAgainStmt;

    private PreparedStatement finalcreateRentalStmt10;

    /**
     * Constructs a new DAO object connected to the bank database.
     */
    public BankDAO() throws BankDBException {
        try {
            connectToBankDB();
            prepareStatements();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new BankDBException("Could not connect to datasource.", exception);
        }
    }

    /**
     * Creates a new account.
     *
     * @param account The account to create.
     * @throws BankDBException If failed to create the specified account.
     */

    public void skapaRental(int student_id, int instrument_id) throws BankDBException { // CHANGE RENTAL DATE. UPDATE
                                                                                        // DATE TO 2022 to indicate
                                                                                        // terminated rental.
        String failureMsg = "Could not update the account: " + student_id + " and b = " + instrument_id;
        String failureAlreadyRentedMsg = "Student:  " + student_id + " already rents: " + instrument_id;
        String failureTooManyMsg = "Student:  " + student_id + " already rents 2 instruments " + instrument_id;
        ResultSet resultSet = null;
        try {
            // instrumentAlreadyRentedStmt.setInt(2, student_id);
            instrumentAlreadyRentedStmt.setInt(1, instrument_id); // CHECK IF STUDENT ALREADY RENTS INSTRUMENTS.
            resultSet = instrumentAlreadyRentedStmt.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                if (id != 0) {
                    handleException(failureAlreadyRentedMsg, null);
                }
            }
            connection.commit();
            closeResultSet(failureAlreadyRentedMsg, resultSet);

        } catch (SQLException sqle) {
            handleException(failureAlreadyRentedMsg, sqle);
        }

        try {
            instrumentTooManyStmt.setInt(1, student_id); // TOO MANY STMT.
            resultSet = instrumentTooManyStmt.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                if (id > 1) {
                    System.out.println("LINE 137" + id);
                    handleException(failureTooManyMsg, null);
                }
            }
            connection.commit();
            closeResultSet(failureTooManyMsg, resultSet);

        } catch (SQLException sqle) {
            handleException(failureTooManyMsg, sqle);
        }

        try {
            createRentalStmt.setInt(2, student_id);
            createRentalStmt.setInt(1, instrument_id);
            updateInstrumentAvailabilityStmt.setInt(1, instrument_id);

            int updatedRows2 = updateInstrumentAvailabilityStmt.executeUpdate();

            int updatedRows = createRentalStmt.executeUpdate();
            if (updatedRows != 1 || updatedRows2 != 1) {
                handleException(failureMsg, null);
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }

    }

    public void terminateRental(int student_id, int instrument_id) throws BankDBException { // CHANGE RENTAL DATE.
                                                                                            // UPDATE DATE TO 2022 to
                                                                                            // indicate terminated
                                                                                            // rental.
        String failureMsg = "Could not update the account: " + student_id + " and b = " + instrument_id;
        try {
            terminateRentalStmt.setInt(2, student_id);
            terminateRentalStmt.setInt(1, instrument_id);
            instrumentAvailableAgainStmt.setInt(1, instrument_id);

            int updatedRows2 = instrumentAvailableAgainStmt.executeUpdate();
            int updatedRows = terminateRentalStmt.executeUpdate();
            if (updatedRows != 1 || updatedRows2 != 1) {
                handleException(failureMsg, null);
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }

    /**
     * Searches for the account with the specified account number.
     *
     * @param InstrumentName The instrument name to look for.
     * 
     * @return The instrument/s with the specified name, or null
     * 
     * @throws BankDBException If failed to search for an instrument.
     */

    public List<Instrument> findInstrumentByName(String InstrumentName) throws BankDBException {
        String failureMsg = "Could not list accounts.";
        List<Instrument> instrumentList = new ArrayList<>();
        try {
            finalStmt = connection.prepareStatement("SELECT *" + " FROM instrument_inventory_at_school "
                    + " WHERE instrument_inventory_at_school.name = '" + InstrumentName + "' AND available = 'true' ");

        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        try (
                ResultSet result = finalStmt.executeQuery()) { // CHANGE findAllAccountsStmt finalStmt.
            while (result.next()) {
                instrumentList.add(new Instrument(
                        result.getInt("instrument_id"),
                        result.getString("name"),
                        result.getString("kind_of_instrument"),
                        result.getString("brand"),
                        result.getInt("price")));
            }
            connection.commit();
            closeResultSet(failureMsg, result);
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        } catch (Exception e) {
        }
        return instrumentList;
    }

    /**
     * Retrieve all vacant instruments.
     *
     * @return A list with all existing instruments. The list is empty if there are
     *         no
     *         available instruments.
     * @throws BankDBException If failed to search for instruments.
     */
    public List<Instrument> findAllInstruments() throws BankDBException {
        String failureMsg = "Could not list accounts.";
        List<Instrument> instrumentList = new ArrayList<>();
        try (ResultSet result = findAllInstrumentsStmt.executeQuery()) {
            while (result.next()) {
                instrumentList.add(new Instrument(result.getInt("instrument_id"),
                        result.getString("name"),
                        result.getString("kind_of_instrument"),
                        result.getString("brand"),
                        result.getInt("price")));
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
        return instrumentList;
    }

    public void deleteAccount(String acctNo) throws BankDBException {
        String failureMsg = "Could not delete account: " + acctNo;
        try {
            deleteAccountStmt.setString(1, acctNo);
            int updatedRows = deleteAccountStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }

    /**
     * Commits the current transaction.
     * 
     * @throws BankDBException If unable to commit the current transaction.
     */
    public void commit() throws BankDBException {
        try {
            connection.commit();
        } catch (SQLException e) {
            handleException("Failed to commit", e);
        }
    }

    private void connectToBankDB() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/musicschool4Tar",
                "postgres", "990727"); // bankdb
        // connection =
        // DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb",
        // "mysql", "mysql");
        connection.setAutoCommit(false);
    }

    private void prepareStatements() throws SQLException {

        findAllInstrumentsStmt = connection.prepareStatement("SELECT * " + " FROM " // SELECT ALL INSTRUMENTS THAT ARE
                                                                                    // AVAILABLE
                + INSTRUMENT_TABLE_NAME + " WHERE available = 'true' ");

        updateInstrumentAvailabilityStmt = connection
                .prepareStatement("UPDATE instrument_inventory_at_school SET available = 'false'" +
                        " WHERE instrument_id = ? ");

        instrumentAvailableAgainStmt = connection
                .prepareStatement("UPDATE instrument_inventory_at_school SET available = 'true'" +
                        " WHERE instrument_id = ? ");

        findInstrumentByNameStmt = connection.prepareStatement("SELECT *" + " FROM instrument_inventory_at_school "
                + " WHERE instrument_inventory_at_school.name = '?' AND available = 'true ' ");

        createRentalStmt = connection.prepareStatement(
                "INSERT INTO instruments_rented_out (lease_start_date, lease_end_date, instrument_id, student_id) " +
                        "  VALUES (NOW() , NOW() + interval '1 year', ?, ? )");

        terminateRentalStmt = connection
                .prepareStatement("UPDATE instruments_rented_out SET lease_end_date = '2022-01-01' " +
                        " WHERE instrument_id = ? AND student_id = ? AND lease_end_date > '2022-02-02' ");

        instrumentAlreadyRentedStmt = connection
                .prepareStatement("SELECT COUNT(*) as rows from instruments_rented_out" +
                        " WHERE instrument_id = ? AND lease_end_date > '2022-02-02' ");

        instrumentTooManyStmt = connection.prepareStatement("SELECT COUNT(*) as rows from instruments_rented_out" +
                " WHERE student_id = ? AND lease_end_date > '2022-02-02' ");
    }

    private void handleException(String failureMsg, Exception cause) throws BankDBException {
        String completeFailureMsg = failureMsg;
        try {
            connection.rollback();
        } catch (SQLException rollbackExc) {
            completeFailureMsg = completeFailureMsg +
                    ". Also failed to rollback transaction because of: " + rollbackExc.getMessage();
        }

        if (cause != null) {
            throw new BankDBException(failureMsg, cause);
        } else {
            throw new BankDBException(failureMsg);
        }
    }

    private void closeResultSet(String failureMsg, ResultSet result) throws BankDBException {
        try {
            result.close();
        } catch (Exception e) {
            throw new BankDBException(failureMsg + " Could not close result set.", e);
        }
    }
}
