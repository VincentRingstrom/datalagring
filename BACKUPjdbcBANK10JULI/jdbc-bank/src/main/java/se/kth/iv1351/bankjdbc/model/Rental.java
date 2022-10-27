package se.kth.iv1351.bankjdbc.model;

public class Rental implements RentalDTO { // Account = INSTRUMENT LISTING.

    private int instrument_id;
    private String name;
    private String kind_of_instrument;
    private String brand;
    private int price;

    /**
     * Creates an new Rental object for the specified Student with the specified
     * instrument
     * 
     * @param instrument_id      The instrument id.
     * @param name               The instrument name.
     * @param kind_of_instrument The kind of instrument
     * @param brand              The brand of instrument
     * @param price              The price of instrument
     * 
     */
    public Rental(int instrument_id, String name, String kind_of_instrument, String brand, int price) {
        this.instrument_id = instrument_id;
        this.name = name;
        this.kind_of_instrument = kind_of_instrument;
        this.brand = brand;
        this.price = price;

    }

    /**
     * @return The instrumentID number.
     */
    public int getRentalNo() {
        return instrument_id;
    }

    /**
     * @return The name of instrument.
     */
    public String getRentalName() {
        return name;
    }

    /**
     * @return The brand of instrument
     */
    public String getRentalBrand() {
        return brand;
    }

    /**
     * @return The instrumentPrice
     */
    public int getRentalPrice() {
        return price;
    }

    public String getRentalType() {
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
