/**
 * @author <Pham Minh Hoa - s3929256>
 */
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class InsuranceCard {
    private static final String FILE_NAME = "insurance_cards.txt";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private String cardNumber;
    private PolicyHolder cardHolder;
    private String policyOwner;
    private Date expirationDate;

    // Getters, setters, constructor (ensure only one card holder)

    public InsuranceCard(String cardNumber, PolicyHolder cardHolder,
                         String policyOwner, Date expirationDate) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.policyOwner = policyOwner;
        this.expirationDate = expirationDate;
    }

    public void setCardHolder(PolicyHolder cardHolder) {
        if (!(cardHolder instanceof PolicyHolder)) {
            throw new IllegalArgumentException("Only PolicyHolder can be a card holder");
        }
        this.cardHolder = cardHolder;
    }

    public Customer getCardHolder() {
        return cardHolder;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPolicyOwner() {
        return policyOwner;
    }

    public void setPolicyOwner(String policyOwner) {
        this.policyOwner = policyOwner;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void save() throws IOException {
        File file = new File(FILE_NAME);
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) { // Append mode (true)
            writer.println(cardNumber + "|" +
                    cardHolder.getId() + "|" +  // Assuming getId() in Customer
                    policyOwner + "|" +  // Assuming getId() in PolicyHolder
                    DATE_FORMAT.format(expirationDate));
        }
    }

    public static InsuranceCard read() throws FileNotFoundException, IOException, ParseException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return null; // Indicate no file or no insurance cards found
        }

        try (Scanner scanner = new Scanner(new FileReader(file))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split("\\|"); // Split by pipe delimiter "|"

                if (data.length != 4) {
                    System.out.println("Warning: Invalid data format in insurance card file!");
                    continue; // Skip to next line if format is wrong
                }

                String cardNumber = data[0];
                String cardHolderId = data[1];
                String policyOwnerId = data[2];
                Date expirationDate = DATE_FORMAT.parse(data[3]);

                // Replace with logic to find PolicyHolder and Customer objects based on IDs
                PolicyHolder cardHolder = null; // Implement logic to find card holder by ID
                String policyOwner = null; // Implement logic to find policy owner by ID

                return new InsuranceCard(cardNumber, cardHolder, policyOwner, expirationDate);
            }
        }

        return null; // If no valid insurance cards found
    }
}


