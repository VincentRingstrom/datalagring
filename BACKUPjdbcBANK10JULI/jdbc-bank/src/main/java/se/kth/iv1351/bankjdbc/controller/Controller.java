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

package se.kth.iv1351.bankjdbc.controller;

import java.util.ArrayList;
import java.util.List;

import se.kth.iv1351.bankjdbc.integration.BankDAO;
import se.kth.iv1351.bankjdbc.integration.BankDBException;
import se.kth.iv1351.bankjdbc.model.Instrument;
import se.kth.iv1351.bankjdbc.model.InstrumentDTO;
import se.kth.iv1351.bankjdbc.model.AccountException;
import se.kth.iv1351.bankjdbc.model.RejectedException;

/**
 * This is the application's only controller, all calls to the model pass here.
 * The controller is also responsible for calling the DAO. Typically, the
 * controller first calls the DAO to retrieve data (if needed), then operates on
 * the data, and finally tells the DAO to store the updated data (if any).
 */
public class Controller {
    private final BankDAO bankDb;

    /**
     * Creates a new instance, and retrieves a connection to the database.
     * 
     * @throws BankDBException If unable to connect to the database.
     */
    public Controller() throws BankDBException {
        bankDb = new BankDAO();
    }

    /**
     * Creates a new account for the specified account holder.
     * 
     * @param holderName The account holder's name.
     * @throws AccountException If unable to create account.
     */

    public void createRentalCtr(int student_id, int instrument_id) throws AccountException {

        String failureMsg = "Could not rent instrument: " + instrument_id + " for student: " + student_id
                + " Because instrument either already rented or student exceeded 2 rentals at a time.";

        String failureParamsMsg = "Student_id or instrument_id contains negative value and that is not allowed.";

        if (student_id < 0 | instrument_id < 0) { // NO NEGATIVE STUDENT_ID OR
            throw new AccountException(failureParamsMsg);
        }

        try {
            bankDb.skapaRental(student_id, instrument_id);
        } catch (Exception e) {
            throw new AccountException(failureMsg, e);
        }

        try {
            System.out.println("Student " + student_id + " now rents instrument: " + instrument_id); // STUDENT NOW
                                                                                                     // RENTS THE
                                                                                                     // INSTRUMENT.
        } catch (Exception e) {
            throw new AccountException(failureMsg, e);
        }

    }

    public void terminateRentalCtr(int student_id, int instrument_id) throws AccountException {

        String failureMsg = "Could not terminate rental of instrument: " + instrument_id + " for student: " + student_id
                + " Because there is no active rental between this instrument and this student.";

        String failureParamsMsg = "Student_id or instrument_id contains negative value and that is not allowed.";

        if (student_id < 0 | instrument_id < 0) {
            throw new AccountException(failureParamsMsg);
        }

        try {
            bankDb.terminateRental(student_id, instrument_id);
            System.out.println("Student " + student_id + " no longer rents instrument: " + instrument_id);
        } catch (Exception e) {
            throw new AccountException(failureMsg, e);
        }

    }

    /**
     * Lists all available instruments in the Soundgood School.
     * 
     * @return A list containing all instruments to be printed to console. The list is empty if there are no
     *         available instruments.
     * @throws AccountException If unable to retrieve instruments.
     */
    public List<? extends InstrumentDTO> getAllInstruments() throws AccountException {                                                                            // FIRST METHOD.
        try {
            return bankDb.findAllInstruments(); 
        } catch (Exception e) {
            throw new AccountException("Unable to list instruments.", e);
        }
    }

    /**
     * Lists all instruments by name
     * 
     * @param InstrumentName The name of instrument that should be returned.
     * @return A list with all instruments by name. The list is
     *         empty if there are no available instuments of the looked after name.
     * @throws AccountException If unable to retrieve the instruments
     */
    public List<? extends InstrumentDTO> getInstrumentForName(String InstrumentName) throws AccountException { // IS USED TO FIND KINDS OF INSTRUMENTS.
        if (InstrumentName == null) {
            return new ArrayList<>();
        }
        try {
            return bankDb.findInstrumentByName(InstrumentName);
        } catch (Exception e) {
            throw new AccountException("Could not search for instruments", e);
        }
    }

 
    private void commitOngoingTransaction(String failureMsg) throws AccountException {
        try {
            bankDb.commit();
        } catch (BankDBException bdbe) {
            throw new AccountException(failureMsg, bdbe);
        }
    }

    /**
     * Deletes the account with the specified account number.
     * 
     * @param acctNo The number of the account that shall be deleted.
     * @throws AccountException If failed to delete the specified account.
     */
    public void deleteAccount(String acctNo) throws AccountException {
        String failureMsg = "Could not delete account: " + acctNo;

        if (acctNo == null) {
            throw new AccountException(failureMsg);
        }

        try {
            bankDb.deleteAccount(acctNo);
        } catch (Exception e) {
            throw new AccountException(failureMsg, e);
        }
    }
}
