import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CustomerManager {
    private ClaimProcessManager claimProcessManager;

    public CustomerManager() {
        // Use a concrete implementation of ClaimProcessManager
        this.claimProcessManager = new DefaultClaimProcessManager();
    }
    public void printAllCustomers() {
        List<Customer> customers = claimProcessManager.getAllCustomers(); // Get all customers from ClaimProcessManager
        for (Customer customer : customers) {
            System.out.println(customer); // Print customer details (override toString() in Customer class)
        }
    }

    // Function to sort customers by type
    public List<Customer> sortCustomersByType() {
        List<Customer> customers = claimProcessManager.getAllCustomers();
        return customers.stream()
                .sorted((c1, c2) -> c1.getType().compareTo(c2.getType()))
                .collect(Collectors.toList());
    }

    // Function to generate customer report (text file)
    public void generateCustomerReport(String filename) throws IOException, ParseException {
        readInsuranceCardReport("insurancecard.txt");
        loadClaimsFromFile("claim.txt");
        List<Customer> customers = sortCustomersByType(); // Sort by type before generating report
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));

        try {
            // Write customer data with additional information
            for (Customer customer : customers) {
                String additionalInfo = "";

                if (customer instanceof PolicyHolder) {
                    PolicyHolder policyHolder = (PolicyHolder) customer;
                    List<String> claimIdList = new ArrayList<>();  // List to store claim IDs
                    for (Claim claim : policyHolder.getClaims()) {
                        claimIdList.add(claim.getId());
                    }
                    String claimIds = String.join(";", claimIdList);  // Join claim IDs into a comma-separated string

                    List<String> dependentIdList = new ArrayList<>();  // List to store dependent IDs
                    for (Dependent dependent : policyHolder.getDependents()) {
                        dependentIdList.add(dependent.getId());
                    }
                    String dependentIds = String.join(";", dependentIdList);  // Join dependent IDs into a comma-separated string

                    String insuranceCardId = policyHolder.getInsuranceCard().getCardNumber();

                    additionalInfo = String.format(", %s, %s, %s", claimIds, dependentIds, insuranceCardId);
                } else if (customer instanceof Dependent) {
                    Dependent dependent = (Dependent) customer;
                    List<String> claimIdList = new ArrayList<>();  // List to store claim IDs
                    if (dependent.getClaims() != null){
                        for (Claim claim : dependent.getClaims()) {
                            claimIdList.add(claim.getId());
                        }
                    }
                    String claimIds = String.join(";", claimIdList);  // Join claim IDs into a comma-separated string
                    String policyHolderId = dependent.getPolicyHolder().getID();

                    additionalInfo = String.format(", %s, %s", claimIds, policyHolderId);
                }

                writer.write(String.format("%s, %s, %s%s\n", customer.getID(), customer.getFullName(), customer.getType(), additionalInfo));
            }
        } finally {
            writer.close();
        }
    }

    // Function to read customer information from text file
    public List<Customer> readCustomerReport(String filename) throws IOException {
        List<Customer> customers = claimProcessManager.getAllCustomers();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                // Extract customer information based on text file format
                String[] data = line.split(","); // Split on tabs by default (adjust delimiter if needed)
                if (data.length >= 3) {
                    String id = data[0].trim();
                    String fullName = data[1].trim();
                    String type = data[2].trim();

                    Customer customer;
                    if (type.equals("Policy Holder")) {
                        customer = new PolicyHolder(id, fullName, new ArrayList<>(), null,null); // Placeholder for insurance card
                    } else if (type.equals("Dependent")) {
                        customer = new Dependent(id, fullName, new ArrayList<>(), null);
                    } else {
                        // Handle unexpected customer type (throw exception or log error)
                        System.out.println("Unknown customer type: " + type);
                        continue;
                    }

                    // Extract additional information based on customer type
                    if (data.length > 3) {
                        String[] additionalInfo = Arrays.copyOfRange(data, 3, data.length);  // Extract additional data from index 3 onwards

                        if (customer instanceof PolicyHolder) {
                            PolicyHolder policyHolder = (PolicyHolder) customer;
                            String[] claimIds = additionalInfo[0].split(";");  // Split claim IDs
                            for (String claimId : claimIds) {
                                if (getClaim(claimId) != null) {
                                    policyHolder.addClaim(getClaim(claimId));
                                }
                            }

                            String[] dependentIds = additionalInfo[1].split(";");  // Split dependent IDs
                            for (String dependentId : dependentIds) {
                                // Assuming you have a method to add dependent by ID (update as needed)
                                policyHolder.addDependent(findDependentById(dependentId));
                            }

                            String insuranceCardId = additionalInfo[2];
                            policyHolder.setInsuranceCardbyid(insuranceCardId);
                        } else if (customer instanceof Dependent) {
                            Dependent dependent = (Dependent) customer;
                            String[] claimIds = additionalInfo[0].split(";");  // Split claim IDs
                            for (String claimId : claimIds) {
                                if (getClaim(claimId) != null) {
                                    dependent.addClaim(getClaim(claimId));
                                }
                            }

                            String policyHolderId = additionalInfo[1];
                            dependent.setPolicyHolderbyId(policyHolderId);
                        }
                    }

                    customers.add(customer);
                } else {
                    System.out.println("Invalid line format: " + line);
                    // Handle the invalid line (e.g., skip it or log an error)
                }
            }
        } catch (ClaimNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            reader.close();  // Ensure closing the reader even if exceptions occur
        }
        return customers;
    }

    public PolicyHolder registerPolicyHolder() throws IOException, ParseException, ClaimProcessException {
        Scanner scanner = new Scanner(System.in);

        // Generate unique ID for the PolicyHolder
        String id = generateUniqueID();
        System.out.println("Policy Holder ID: " + id);

        System.out.println("Enter Policy Holder Full Name (required): ");
        String fullName = scanner.nextLine().trim();
        if (fullName.isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }

        // Get optional dependent information
        List<Dependent> dependents = new ArrayList<>();
        List<Claim> claims = new ArrayList<>();
        // Pass PolicyHolder ID to establish connection

        // Skip adding insurance card during registration
        InsuranceCard insuranceCard = null;

        // Create the PolicyHolder object
        PolicyHolder newPolicyHolder = new PolicyHolder(id, fullName, claims, dependents, insuranceCard);
        claimProcessManager.registerCustomer(newPolicyHolder);
        addDependents(dependents, id);
        addInsuranceCard(newPolicyHolder);
        createClaimFromUserInput(newPolicyHolder);
        generateInsuranceCardReport("insurancecard1.txt");
        System.out.println("Policy holder successfully registered!");
        System.out.println("Policy Holder ID: " + newPolicyHolder.getID());
        System.out.println("Policy Holder Name: " + newPolicyHolder.getFullName());

        displayAndCountDependents(newPolicyHolder);
        System.out.println(claimProcessManager.getAllCustomers());
        return newPolicyHolder;
    }

    public void addDependents(List<Dependent> dependents, String policyHolderID) throws IOException, ClaimProcessException, ParseException {
        Scanner scanner = new Scanner(System.in);
        char addDependent;

        do {
            System.out.println("Do you want to add a dependent (y/n)? ");
            addDependent = scanner.nextLine().charAt(0); // Get the first character only
            addDependent = Character.toLowerCase(addDependent); // Convert to lowercase for easier comparison

            if (addDependent == 'y') {
                String id = generateUniqueID();
                System.out.println("Dpendent ID: " + id);
                System.out.println("Enter Dependent Full Name (required): ");
                String fullName = scanner.nextLine().trim();
                if (fullName.isEmpty()) {
                    throw new IllegalArgumentException("Full name cannot be empty.");
                }

                // Create a Dependent object
                Dependent dependent = new Dependent(id,fullName,null,null);

                // Find the PolicyHolder object based on policyHolderID (implementation needed)
                PolicyHolder policyHolder = findPolicyHolderById(policyHolderID);

                if (policyHolder != null) {
                    // Establish connection by setting the policy holder in the Dependent
                    dependent.setPolicyHolder(policyHolder);

                    // Add the Dependent to the PolicyHolder's internal list (assuming it exists)
                    policyHolder.getDependents().add(dependent);
                } else {
                    System.out.println("Policy Holder not found with ID: " + policyHolderID);
                }
                claimProcessManager.registerCustomer(dependent);
                System.out.println("--Add for your dependent--");
                createClaimFromUserInput(dependent);
            }
        } while (addDependent == 'y');
    }

    public PolicyHolder findPolicyHolderById(String policyHolderID) {
        List<Customer> allCustomers = claimProcessManager.getAllCustomers();

        for (Customer customer : allCustomers) {
            if (customer instanceof PolicyHolder && // Check if it's a PolicyHolder
                    customer.getID().equals(policyHolderID)) { // Compare IDs
                return (PolicyHolder) customer; // Cast to PolicyHolder if found
            }
        }

        return null; // Return null if not found
    }

    public Dependent findDependentById(String dependentID) {
        List<Customer> allCustomers = claimProcessManager.getAllCustomers();

        for (Customer customer : allCustomers) {
            if (customer instanceof Dependent &&
                    customer.getID().equals(dependentID)) { // Compare IDs
                return (Dependent) customer;
            }
        }

        return null; // Return null if not found
    }

    // New function to add an insurance card to an existing PolicyHolder
    public void addInsuranceCard(PolicyHolder policyHolder) throws IOException {
        if (policyHolder == null) {
            System.out.println("Invalid PolicyHolder. Please register a policy holder first.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Adding a Insurance Card ");
        System.out.println("Enter Insurance Card Number: ");
        String cardNumber = scanner.nextLine();

        Date currentDate = new Date(); // Replace with your logic to get current date

        String cardOwner = "RMIT";


        // Calculate expiration date by adding 10 months
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.MONTH, 10);
        Date expirationDate = cal.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedExpirationDate = dateFormat.format(cal.getTime());

        // Create an InsuranceCard object with the PolicyHolder reference
        InsuranceCard insuranceCard = new InsuranceCard(cardNumber, policyHolder, cardOwner, formattedExpirationDate);

        // Update the PolicyHolder object to set the insurance card
        policyHolder.setInsuranceCard(insuranceCard);
        claimProcessManager.registerInsuranceCard(insuranceCard);
        System.out.println(claimProcessManager.getAllInsuranceCards());
    }
    private String generateUniqueID() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        // Generate 7 random characters from the alphabet
        for (int i = 0; i < 7; i++) {
            int index = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public void displayAndCountDependents(PolicyHolder policyHolder) {
        if (policyHolder == null) {
            System.out.println("Invalid PolicyHolder. Please register a policy holder first.");
            return;
        }

        List<Dependent> dependents = policyHolder.getDependents();
        int dependentCount = dependents.size();

        if (dependentCount == 0) {
            System.out.println(policyHolder.getFullName() + " has no dependents registered.");
        } else {
            System.out.println(policyHolder.getFullName() + " has " + dependentCount + " dependents");
            for (Dependent dependent : dependents) {
                System.out.println("- " + dependent.getFullName());
            }
        }
    }

    public void generateInsuranceCardReport(String filename) throws IOException {
        List<InsuranceCard> insuranceCards = claimProcessManager.getAllInsuranceCards();
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));

        try {


            // Write card data with null check
            for (InsuranceCard card : insuranceCards) {
                String policyHolderId = card.getCardHolder() != null ? card.getCardHolder().getID() : "";  // Handle null case
                writer.write(card.getCardNumber() + ", " + policyHolderId + ", " + card.getPolicyOwner() + ", " + card.getExpirationDate() + "\n");
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
                String[] data = line.split(", ");

                // Check for valid data length (at least 4 elements)
                if (data.length < 4) {
                    System.out.println("Invalid line format: " + line);
                    continue; // Skip invalid lines
                }

                String cardNumber = data[0].trim();
                String policyHolderID = data[1].trim();
                String policyOwner = data[2].trim();
                String expirationDate = data[3].trim();

                PolicyHolder cardHolder = findPolicyHolderById(policyHolderID);

                InsuranceCard insuranceCard = new InsuranceCard(cardNumber, cardHolder, policyOwner, expirationDate);
                insuranceCards.add(insuranceCard);
                claimProcessManager.registerInsuranceCard(insuranceCard);
            }
        } finally {
            reader.close();
        }

        return insuranceCards;
    }

    public Customer findCustomerById(String customerId) {
        List<Customer> allCustomers = claimProcessManager.getAllCustomers();

        for (Customer customer : allCustomers) {
            if (customer.getID().equals(customerId)) { // Compare IDs
                return customer; // Cast to PolicyHolder if found
            }
        }

        return null; // Return null if not found
    }

    public Claim createClaimFromUserInput(Customer customer) throws ParseException, IOException, ClaimProcessException {
        List<Claim> claims = new ArrayList<>();
        if (customer == null) {
            System.out.println("Invalid customer. Please register a policy holder first.");
        }
        Scanner scanner = new Scanner(System.in);

        System.out.println("** Enter Claim Information **");

        System.out.print("Claim ID: ");
        String claimId = scanner.nextLine().trim();

        System.out.print("Claim Date (yyyy-MM-dd): ");
        String claimDateString = scanner.nextLine().trim();
        Date claimDate = new SimpleDateFormat("yyyy-MM-dd").parse(claimDateString);
        String formattedClaimDate = new SimpleDateFormat("yyyy-MM-dd").format(claimDate);

        Customer insuredPerson = findCustomerById(customer.getID());
        System.out.print("Customer ID: " + customer.getID());
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
        claimProcessManager.addClaim(claim);
        claims.add(claim);
        customer.setClaims(claims);

        System.out.println("Claim created successfully!");
        return claim;
    }

    public void saveClaimsToFile(String filename) throws IOException {
        List<Claim> claims = claimProcessManager.getAllClaims();
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));

        try {

            //writer.write("id,claimDate,insuredPerson (CustomerID),cardNumber,examDate,documents (comma-separated paths),claimAmount,status,receiverBankInfo (bankName,accountName,accountNumber)");
            for (Claim claim : claims) {
                // Format claim data into a comma-separated string
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%o,%s,%s",
                        claim.getId(), claim.getClaimDate(), claim.getInsuredPerson().getId(), // Assuming Customer has an ID
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

                List<String> documents = new ArrayList<>(Arrays.asList(data[5].split(";"))); // Convert comma-separated document paths to a list

                int claimAmount = Integer.parseInt(data[6]);
                String status = data[7];

                String receiverBankInfoData = data[8];
                String[] bankInfoParts = receiverBankInfoData.split(";"); // separating bank info
                ReceiverBankInfo receiverBankInfo = new ReceiverBankInfo(bankInfoParts[0], bankInfoParts[1], bankInfoParts[2]);

                // Create a Claim object with the parsed data
                Claim claim = new Claim(id, claimDate, insuredPerson, cardNumber, examDate, documents, claimAmount, status, receiverBankInfo);

                // Add the claim object to the claims list
                claimProcessManager.registerClaim(claim);
            }
        } finally {
            reader.close();
        }
        return null;
    }

    public Claim getClaim(String id) throws ClaimNotFoundException {
        List<Claim> allClaims =claimProcessManager.getAllClaims();
        for (Claim claim :allClaims){
            if (id.equals(claim.getId())){
                return claim;
            }
        }
        return null;
    }

    public List<Claim> searchClaims(String searchCriteria) throws IOException, ParseException {
        readCustomerReport("customer.txt");
        loadClaimsFromFile("claim.txt");
        List<Claim> filteredClaims = new ArrayList<>();
        for (Claim claim : claimProcessManager.getAllClaims()) {
            if (claim.matchesSearchCriteria(searchCriteria)) { // Delegate search logic to Claim class
                filteredClaims.add(claim);
            }
        }
        System.out.println(filteredClaims);
        return filteredClaims;
    }

    public List<Claim> filterClaimsByStatus(String status) throws IOException, ParseException {

        List<Claim> filteredClaims = new ArrayList<>();
        for (Claim claim : claimProcessManager.getAllClaims()) {
            if (claim.getStatus().equals(status)) {
                filteredClaims.add(claim);
            }
        }
        return filteredClaims;
    }

    public void deleteClaim(String claimId, String filename) throws IOException, IllegalArgumentException {
        int claimIndex = -1;
        for (int i = 0; i < claimProcessManager.getAllClaims().size(); i++) {
            if (claimProcessManager.getAllClaims().get(i).getId().equals(claimId)) {
                claimIndex = i;
                break;
            }
        }

        if (claimIndex == -1) {
            throw new IllegalArgumentException("Claim with ID " + claimId + " not found.");
        }

        claimProcessManager.getAllClaims().remove(claimIndex);

        // Rewrite claim data to the text file (assuming simple overwriting)
        FileWriter writer = new FileWriter(filename, false);  // Overwrite existing content
        try {
            for (Claim claim : claimProcessManager.getAllClaims()) {
                writer.write(claim.toString() + "\n");  // Write claim object details
            }
        } finally {
            writer.close();
        }
    }

    public void updateDependentInfo(String filename) throws IOException {
        List<String> updatedLines = new ArrayList<>();  // List to hold modified lines
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the full name of the customer to update: ");
        String customerName = scanner.nextLine().trim();

        boolean customerFound = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[1].trim().equals(customerName)) {
                    customerFound = true;

                    System.out.println("What information do you want to update?");
                    System.out.println("1. Full Name");
                    System.out.print("Enter your choice: ");

                    int choice = Integer.parseInt(scanner.nextLine().trim());

                    if (choice == 1) {
                        System.out.print("Enter new full name: ");
                        String newFullName = scanner.nextLine().trim();

                        // Update line for writing
                        String updatedLine = String.format("%s, %s,%s,%s,%s", data[0], newFullName, data[2], data[3], data[4]);
                        updatedLines.add(updatedLine);
                    } else {
                        System.out.println("Invalid choice. Only full name update is allowed for now.");
                        updatedLines.add(line);  // Add unmodified line back
                    }
                } else {
                    updatedLines.add(line);  // Add unmodified line for other customers
                }
            }
        }

        if (!customerFound) {
            System.out.println("Customer with name " + customerName + " not found.");
            return;
        }

        FileWriter writer = new FileWriter("temp_"+filename);
        try {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine + "\n");
            }
        } finally {
            writer.close();
        }

        // Assuming OS allows overwriting existing file
        new java.io.File(filename).delete();
        new java.io.File("temp_" + filename).renameTo(new java.io.File(filename));

        System.out.println("Customer information updated successfully.");
    }
    public void updatePolicyHolderInfo(String filename) throws IOException {
        List<String> updatedLines = new ArrayList<>();  // List to hold modified lines
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the full name of the customer to update: ");
        String customerName = scanner.nextLine().trim();

        boolean customerFound = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[1].trim().equals(customerName)) {
                    customerFound = true;

                    System.out.println("What information do you want to update?");
                    System.out.println("1. Full Name");
                    System.out.print("Enter your choice: ");

                    int choice = Integer.parseInt(scanner.nextLine().trim());

                    if (choice == 1) {
                        System.out.print("Enter new full name: ");
                        String newFullName = scanner.nextLine().trim();

                        // Update line for writing
                        String updatedLine = String.format("%s, %s,%s,%s,%s,%s", data[0], newFullName, data[2], data[3], data[4],data[5]);
                        updatedLines.add(updatedLine);
                    } else {
                        System.out.println("Invalid choice. Only full name update is allowed for now.");
                        updatedLines.add(line);  // Add unmodified line back
                    }
                } else {
                    updatedLines.add(line);  // Add unmodified line for other customers
                }
            }
        }

        if (!customerFound) {
            System.out.println("Customer with name " + customerName + " not found.");
            return;
        }

        FileWriter writer = new FileWriter("temp_"+filename);
        try {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine + "\n");
            }
        } finally {
            writer.close();
        }

        // Assuming OS allows overwriting existing file
        new java.io.File(filename).delete();
        new java.io.File("temp_" + filename).renameTo(new java.io.File(filename));

        System.out.println("Customer information updated successfully.");
    }

    public void updateCustomerInfo(String filename) throws IOException {
        System.out.println("You want to update Denpendent(d) or Policy Holder(p)");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();

        if (choice.equals("d")){
            updateDependentInfo(filename);
        } else if (choice.equals("p")) {
            updatePolicyHolderInfo(filename);
        } else {
            System.out.println("Wrong input please try again");
        }
    }

    public void updateClaim(String filename) throws IOException {
        List<String> updatedLines = new ArrayList<>();  // List to hold modified lines
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the ID of the Claim to update: ");
        String claimId = scanner.nextLine().trim();

        boolean claimFound = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 1 && data[0].trim().equals(claimId)) {
                    claimFound = true;

                    System.out.println("You to update Claim status?");
                    System.out.print("Enter your choice (y/n): ");

                    String choice = scanner.nextLine().trim();

                    if (choice.equals("y")) {
                        System.out.print("Enter new Status (New/Processing/Done): ");
                        String newStatus = scanner.nextLine().trim();

                        // Update line for writing
                        String updatedLine = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", data[0], data[1], data[2], data[3], data[4],data[5],data[6],newStatus,data[8]);
                        updatedLines.add(updatedLine);
                    } else {
                        System.out.println("Invalid choice. Only full name update is allowed for now.");
                        updatedLines.add(line);  // Add unmodified line back
                    }
                } else {
                    updatedLines.add(line);  // Add unmodified line for other customers
                }
            }
        }

        if (!claimFound) {
            System.out.println("Claim with name " + claimId + " not found.");
            return;
        }

        FileWriter writer = new FileWriter("temp_"+filename);
        try {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine + "\n");
            }
        } finally {
            writer.close();
        }


        new java.io.File(filename).delete();
        new java.io.File("temp_" + filename).renameTo(new java.io.File(filename));

        System.out.println("Customer information updated successfully.");
    }

    public void searchCustomer(String filename) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the full name of the customer to search: ");
        String customerName = scanner.nextLine().trim();

        boolean customerFound = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[1].trim().equals(customerName)) {
                    customerFound = true;

                    System.out.println("Customer Information:");
                    System.out.println("  ID: " + data[0]);
                    System.out.println("  Full Name: " + data[1]);
                    System.out.println("  Type: " + data[2]);

                    break;  // Exit loop after finding the customer
                }
            }
        }

        if (!customerFound) {
            System.out.println("Customer with name " + customerName + " not found.");
        }
    }

    public void displayAllCustomer() throws IOException {
        readCustomerReport("customer.txt");
        System.out.println(claimProcessManager.getAllCustomers());
    }

    public void displayAllClaim() throws IOException, ParseException {
        loadClaimsFromFile("claim.txt");
        System.out.println(claimProcessManager.getAllClaims());
    }

    public void displayAllDependent(String filename) throws IOException {
        readCustomerReport(filename);
        List<Customer> customers = claimProcessManager.getAllCustomers();
        for (Customer customer: customers){
            if (customer.getType().equals("Dependent")){
                System.out.println("Dependent ID: " +customer.getID() + " Name: " + customer.getFullName() + " Type: " + customer.getType());
            }
        }
    }

    public void displayAllPolicyHolder(String filename) throws IOException {
        readCustomerReport(filename);
        List<Customer> customers = claimProcessManager.getAllCustomers();
        for (Customer customer: customers){
            if (customer.getType().equals("Policy Holder")){
                System.out.println("Dependent ID: " +customer.getID() + " Name: " + customer.getFullName() + " Type: " + customer.getType());
            }
        }
    }

    public void viewInsuranceCard(String filename) throws IOException {
        readInsuranceCardReport(filename);
        List<InsuranceCard> insuranceCards = claimProcessManager.getAllInsuranceCards();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your Card number: ");
        String cardNumber = scanner.nextLine();
        for (InsuranceCard insuranceCard: insuranceCards){
            if (insuranceCard.getCardNumber().equals(cardNumber)){
                System.out.println("Card Number: "+insuranceCard.getCardNumber() + " Policy Owner: " + insuranceCard.getPolicyOwner() + " Expiration Date: " + insuranceCard.getExpirationDate());
            }
        }
    }
}