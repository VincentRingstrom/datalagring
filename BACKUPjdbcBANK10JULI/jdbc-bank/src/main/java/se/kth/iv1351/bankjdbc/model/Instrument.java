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

package se.kth.iv1351.bankjdbc.model;

/**
 * An account in the bank.
 */
public class Instrument implements InstrumentDTO { // Account = INSTRUMENT LISTING.

    private int instrument_id;
    private String name;
    private String kind_of_instrument;
    private String brand;
    private int price;


    /**
     * Creates an Instrument
     *
     * @param instrument_id      The instrument id.
     * @param name               The instrument name.
     * @param kind_of_instrument The kind of instrument
     * @param brand              The brand of instrument
     * @param price              The price of instrument
     * 
     */
    public Instrument(int instrument_id, String name, String kind_of_instrument, String brand, int price) {
        this.instrument_id = instrument_id;
        this.name = name;
        this.kind_of_instrument = kind_of_instrument;
        this.brand = brand;
        this.price = price;

    }
    /**
     * @return The instrumentID number.
     */
    public int getInstrumentNo() {
        return instrument_id;
    }

    /**
     * @return The name of instrument.
     */
    public String getInstrumentName() {
        return name;
    }

    /**
     * @return The brand of instrument
     */
    public String getInstrumentBrand() {
        return brand;
    }

    /**
     * @return The instrumentPrice
     */
    public int getInstrumentPrice() {
        return price;
    }

    public String getInstrumentType() {
        return kind_of_instrument;
    }

    /**
     * @return A string representation of all fields in this object.
     */
    @Override
    public String toString() {
        StringBuilder stringRepresentation = new StringBuilder();
        stringRepresentation.append("Account: [");
        stringRepresentation.append("account number: ");
        stringRepresentation.append(instrument_id);
        stringRepresentation.append(", holder: ");
        stringRepresentation.append(brand);
        stringRepresentation.append(kind_of_instrument);
        stringRepresentation.append(price);
        stringRepresentation.append(name);
        stringRepresentation.append(", balance: ");

        stringRepresentation.append(price);
        stringRepresentation.append("]");
        return stringRepresentation.toString();
    }
}
