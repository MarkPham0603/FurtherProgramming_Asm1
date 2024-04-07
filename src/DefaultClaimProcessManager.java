import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DefaultClaimProcessManager implements ClaimProcessManager{
    private List<Customer> customers;
    private List<Claim> claims;
    private List<InsuranceCard> insuranceCards;



    public DefaultClaimProcessManager() {
        this.customers = new ArrayList<>();
        this.claims = new ArrayList<>();
        this.insuranceCards = new ArrayList<>();
    }


    @Override
    public List<Customer> getAllCustomers() {
        return customers; // Return an unmodifiable list
    }

    @Override
    public void registerCustomer(Customer customer) {
        customers.add(customer);
    }

    public void registerClaim(Claim claim) {
        claims.add(claim);
    }
    @Override
    public void updateCustomer(Customer updatedCustomer) {
        try {
            int index = customers.indexOf(updatedCustomer);
            if (index != -1) {
                customers.set(index, updatedCustomer);
                System.out.println("Customer information updated successfully!");
            } else {
                System.out.println("Customer not found in the list.");
            }
        } catch (Exception e) {
            System.err.println("Error updating customer: " + e.getMessage());
        }
    }

    @Override
    public void removeCustomer(Customer customerToRemove) {
        if (customers.remove(customerToRemove)) {
            System.out.println("Customer removed successfully!");
        } else {
            System.out.println("Customer not found in the list.");
        }
    }

    @Override
    public void addClaim(Claim claim) throws ClaimProcessException {
        if (claim == null) {
            throw new ClaimProcessException("Invalid claim object provided.");
        }
        claims.add(claim);
    }

    @Override
    public boolean updateClaim(Claim claim) throws ClaimProcessException {
        if (claim == null) {
            throw new ClaimProcessException("Invalid claim object provided.");
        }
        int index = findClaimIndex(claim.getId());
        if (index == -1) {
            return false; // Claim not found
        }
        claims.set(index, claim);
        return true;
    }

    @Override
    public boolean deleteClaim(Claim claim) throws ClaimProcessException {
        if (claim == null) {
            throw new ClaimProcessException("Invalid claim object provided.");
        }
        int index = findClaimIndex(claim.getId());
        if (index == -1) {
            return false; // Claim not found
        }
        claims.remove(index);
        return true;
    }

    @Override
    public Claim getClaim(String id) throws ClaimNotFoundException {
        int index = findClaimIndex(id);
        if (index == -1) {
            throw new ClaimNotFoundException("Claim with ID " + id + " not found.");
        }
        return claims.get(index);
    }


    @Override
    public List<Claim> getAllClaims() {
        return claims; // Return a copy to avoid modification of internal list
    }

    @Override
    public List<Claim> filterClaimsByStatus(String status) {
        List<Claim> filteredClaims = new ArrayList<>();
        for (Claim claim : claims) {
            if (claim.getStatus().equals(status)) {
                filteredClaims.add(claim);
            }
        }
        return filteredClaims;
    }

    // Helper method to find the index of a claim by its ID
    private int findClaimIndex(String id) {
        for (int i = 0; i < claims.size(); i++) {
            if (claims.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public List<Claim> searchClaims(String searchCriteria) {
        List<Claim> filteredClaims = new ArrayList<>();
        for (Claim claim : claims) {
            if (claim.matchesSearchCriteria(searchCriteria)) { // Delegate search logic to Claim class
                filteredClaims.add(claim);
            }
        }
        return filteredClaims;
    }

    @Override
    public List<InsuranceCard> getAllInsuranceCards() {
        return insuranceCards;
    }

    public void registerInsuranceCard(InsuranceCard insuranceCard) {
        insuranceCards.add(insuranceCard);
    }

    public void saveClaimsToFile(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));

        try {

            //writer.write("id,claimDate,insuredPerson (CustomerID),cardNumber,examDate,documents (comma-separated paths),claimAmount,status,receiverBankInfo (bankName,accountName,accountNumber)");
            for (Claim claim : claims) {
                // Format claim data into a comma-separated string
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%o,%s,%s",
                        claim.getId(), claim.getClaimDate(), claim.getInsuredPerson().getId(),
                        claim.getCardNumber(), claim.getExamDate(), String.join(",", claim.getDocuments()),
                        claim.getClaimAmount(), claim.getStatus(),
                        claim.getReceiverBankInfo().toString())); // Use ReceiverBankInfo's toString()

            }
        } finally {
            writer.close();
        }
    }
    public List<Claim> loadClaimsFromFile(String filename) throws IOException, ParseException {
        CustomerManager customerManager = new CustomerManager();
        customerManager.readCustomerReport("customer.txt");
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        try {
            reader.readLine(); // Skip header row (if exists)

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 9) { // Adjust based on the number of expected data fields
                    System.out.println("Invalid line format: " + line);
                    continue;
                }

                // Parse claim data
                String id = data[0];
                String claimDate = data[1];

                // Assuming Customer object retrieval by ID (modify as needed)
                Customer insuredPerson = customerManager.findCustomerById(data[2]);
                if (insuredPerson == null) {
                    System.out.println("Customer not found for claim: " + data[2]);
                    continue;
                }

                String cardNumber = data[3];
                String examDate = data[4]; // Assuming date format

                List<String> documents = new ArrayList<>(Arrays.asList(data[5].split(";"))); // Convert ; separated document paths to a list

                int claimAmount = Integer.parseInt(data[6]);
                String status = data[7];

                String receiverBankInfoData = data[8];
                String[] bankInfoParts = receiverBankInfoData.split(";");
                ReceiverBankInfo receiverBankInfo = new ReceiverBankInfo(bankInfoParts[0], bankInfoParts[1], bankInfoParts[2]);

                // Create a Claim object with the parsed data
                Claim claim = new Claim(id, claimDate, insuredPerson, cardNumber, examDate, documents, claimAmount, status, receiverBankInfo);

                // Add the claim object to the claims list
                registerClaim(claim);
            }
        } finally {
            reader.close();
        }
        System.out.println(getAllClaims());
        return getAllClaims();
    }

    public Claim createClaimFromUserInput(PolicyHolder policyHolder) throws ParseException, IOException {
        CustomerManager customerManager = new CustomerManager();
        if (policyHolder == null) {
            System.out.println("Invalid PolicyHolder. Please register a policy holder first.");
        }
        Scanner scanner = new Scanner(System.in);

        System.out.println("** Enter Claim Information **");

        System.out.print("Claim ID: ");
        String claimId = scanner.nextLine().trim();

        System.out.print("Claim Date (yyyy-MM-dd): ");
        String claimDateString = scanner.nextLine().trim();
        Date claimDate = new SimpleDateFormat("yyyy-MM-dd").parse(claimDateString);
        String formattedClaimDate = new SimpleDateFormat("yyyy-MM-dd").format(claimDate);

        Customer insuredPerson = customerManager.findCustomerById(policyHolder.getID());
        System.out.print("Customer ID: " + policyHolder.getID());
        System.out.println(insuredPerson);

        if (insuredPerson == null) {
            System.out.println("Customer not found. Please try again.");
            return null;  // Indicate unsuccessful claim creation
        }

        System.out.print("Insurance Card Number: ");
        String cardNumber = scanner.nextLine().trim();

        System.out.print("Exam Date (yyyy-MM-dd): ");
        String examDateString = scanner.nextLine().trim();
        Date examDate = new SimpleDateFormat("yyyy-MM-dd").parse(examDateString);
        String formattedExamDate = new SimpleDateFormat("yyyy-MM-dd").format(examDate);


        List<String> documents = new ArrayList<>();  // List to store document paths
        boolean addMoreDocuments = true;
        while (addMoreDocuments) {
            System.out.print("Enter document path (or 'q' to quit): ");
            String documentPath = scanner.nextLine().trim();
            if (documentPath.equalsIgnoreCase("q")) {
                addMoreDocuments = false;
            } else {
                documents.add(documentPath);
            }
        }

        System.out.print("Claim Amount: ");
        int claimAmount = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Claim Status (New, Processing, Done): ");
        String status = scanner.nextLine().trim();

        System.out.println("** Enter Receiver Bank Information **");

        System.out.print("Bank Name: ");
        String bankName = scanner.nextLine().trim();

        System.out.print("Account Name: ");
        String accountName = scanner.nextLine().trim();

        System.out.print("Account Number: ");
        String accountNumber = scanner.nextLine().trim();

        ReceiverBankInfo receiverBankInfo = new ReceiverBankInfo(bankName, accountName, accountNumber);

        Claim claim = new Claim(claimId, formattedClaimDate, insuredPerson, cardNumber, formattedExamDate, documents, claimAmount, status, receiverBankInfo);
        claims.add(claim);

        System.out.println("Claim created successfully!");
        return claim;
    }
}
