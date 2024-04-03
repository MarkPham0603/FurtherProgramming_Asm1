import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class InsuranceCardManager {
    private ClaimProcessManager claimProcessManager;
    private CustomerManager customerManager;

    public InsuranceCardManager() {
        this.claimProcessManager = new DefaultClaimProcessManager();
        this.customerManager = customerManager;
    }

    // Function to generate an insurance card report (text file)
    public void generateInsuranceCardReport(String filename) throws IOException {
        List<InsuranceCard> insuranceCards = claimProcessManager.getAllInsuranceCards(); // Assuming a way to get cards
        System.out.println(claimProcessManager.getAllInsuranceCards());
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

        try {
            // Write header row (optional)
            writer.write("Card Number, Policy Holder ID, Policy Owner, Expiration Date\n");

            // Write card data
            for (InsuranceCard card : insuranceCards) {
                writer.write(String.format("%s, %s, %s, %s\n",
                        card.getCardNumber(), card.getCardHolder().getID(), // Assuming cardHolder has an ID
                        card.getPolicyOwner(), card.getExpirationDate()));
            }
        } finally {
            writer.close();
        }
    }

    // Function to read an insurance card report (text file)
    public List<InsuranceCard> readInsuranceCardReport(String filename) throws IOException {
        List<InsuranceCard> insuranceCards = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }

                // Extract card information based on file format
                String[] data = line.split(","); // Assuming comma-separated values

                // Check for valid data length (at least 4 elements)
                if (data.length < 4) {
                    System.out.println("Invalid line format: " + line);
                    continue; // Skip invalid lines
                }

                String cardNumber = data[0].trim();
                String policyHolderID = data[1].trim();
                String policyOwner = data[2].trim();
                Date expirationDate = new SimpleDateFormat("yyyy-MM-dd").parse(data[3].trim()); // Adjust format if needed

                // You'll likely need to modify this to retrieve the PolicyHolder object
                PolicyHolder cardHolder = customerManager.findPolicyHolderById(policyHolderID);

                InsuranceCard insuranceCard = new InsuranceCard(cardNumber, cardHolder, policyOwner, expirationDate);
                insuranceCards.add(insuranceCard);
            }
        } catch (ParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
            // Handle parsing errors appropriately
        } finally {
            reader.close();
        }

        return insuranceCards;
    }
}
