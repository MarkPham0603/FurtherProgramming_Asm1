import java.io.*;
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
    public void generateCustomerReport(String filename) throws IOException {
        List<Customer> customers = sortCustomersByType(); // Sort by type before generating report
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

        try {

            // Write customer data
            for (Customer customer : customers) {
                writer.write(String.format("%s, %s, %s\n", customer.getID(), customer.getFullName(), customer.getType()));
            }
        } finally {
            writer.close();
        }
    }

    // Function to read customer information from text file
    public List<Customer> readCustomerReport(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                // Extract customer information based on your text file format
                String[] data = line.split(",");// Split on tabs by default (adjust delimiter if needed)
                if (data.length >= 3) {
                    // Proceed with extracting ID, full name, and type
                } else {
                    System.out.println("Invalid line format: " + line);
                    // Handle the invalid line (e.g., skip it or log an error)
                }
                String id = data[0].trim();
                String fullName = data[1].trim();
                String type = data[2].trim();
                Customer customer;

                if (type.equals("Policy Holder")) {
                    customer = new PolicyHolder(id, fullName, new ArrayList<>(), null); // Placeholder for insurance card
                } else if (type.equals("Dependent")) {
                    customer = new Dependent(id,fullName,new ArrayList<>(),null); // No need to assign policy holder here
                } else {
                    // Handle unexpected customer type
                    throw new RuntimeException("Unknown customer type: " + type);
                }
                claimProcessManager.registerCustomer(customer);
            }
        } finally {
            reader.close();  // Ensure closing the reader even if exceptions occur
        }
        System.out.println(claimProcessManager.getAllCustomers());
        return claimProcessManager.getAllCustomers();
    }

    public PolicyHolder registerPolicyHolder() throws IOException {
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
        // Pass PolicyHolder ID to establish connection

        // Skip adding insurance card during registration
        InsuranceCard insuranceCard = null;

        // Create the PolicyHolder object
        PolicyHolder newPolicyHolder = new PolicyHolder(id, fullName, dependents, insuranceCard);
        claimProcessManager.registerCustomer(newPolicyHolder);
        addDependents(dependents, id);
        addInsuranceCard(newPolicyHolder);
        System.out.println("Policy holder successfully registered!");
        System.out.println("Policy Holder ID: " + newPolicyHolder.getID());
        System.out.println("Policy Holder Name: " + newPolicyHolder.getFullName());

        displayAndCountDependents(newPolicyHolder);
        System.out.println(claimProcessManager.getAllCustomers());
        System.out.println(claimProcessManager.getAllInsuranceCards());
        return newPolicyHolder;
    }

    public void addDependents(List<Dependent> dependents, String policyHolderID) throws IOException {
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
                Dependent dependent = new Dependent(id,fullName,null,null); // Assuming no PolicyHolder argument in constructor

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

    // New function to add an insurance card to an existing PolicyHolder
    public void addInsuranceCard(PolicyHolder policyHolder) throws IOException {
        if (policyHolder == null) {
            System.out.println("Invalid PolicyHolder. Please register a policy holder first.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Insurance Card Number: ");
        String cardNumber = scanner.nextLine().trim();

        // Assuming you have a method to get the current date
        Date currentDate = new Date(); // Replace with your logic to get current date

        String cardOwner = "RMIT";


        // Calculate expiration date by adding 10 months
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.MONTH, 10);
        Date expirationDate = cal.getTime();

        // Create an InsuranceCard object with the PolicyHolder reference
        InsuranceCard insuranceCard = new InsuranceCard(cardNumber, policyHolder, cardOwner, expirationDate);

        // Update the PolicyHolder object to set the insurance card (optional)
        policyHolder.setInsuranceCard(insuranceCard);
        claimProcessManager.registerInsuranceCard(insuranceCard);

    }
    private String generateUniqueID() {
        return UUID.randomUUID().toString();
    }

    public void updatePolicyHolder(PolicyHolder policyHolder) throws IOException {
        if (policyHolder == null) {
            System.out.println("Invalid PolicyHolder. Please register a policy holder first.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        // Verify PolicyHolder by full name
        System.out.println("Enter your full name to verify: ");
        String enteredFullName = scanner.nextLine().trim();

        if (!enteredFullName.equals(policyHolder.getFullName())) {
            System.out.println("Full name does not match. Update cancelled.");
            return;
        }

        System.out.println("What would you like to update?");
        System.out.println("1. Full Name");
        System.out.println("2. Dependents");
        System.out.println("3. Insurance Card");
        System.out.println("Enter your choice (1-3): ");

        int choice = Integer.parseInt(scanner.nextLine().trim());

        switch (choice) {
            case 1:
                // Update Full Name
                System.out.println("Enter new Full Name: ");
                String newFullName = scanner.nextLine().trim();
                policyHolder.setFullName(newFullName);
                System.out.println("Full Name updated successfully!");
                claimProcessManager.updateCustomer(policyHolder); // Call updateCustomer
                break;
            case 2:
                // Update Dependents
                updateDependents(policyHolder);
                break;
            case 3:
                // Update Insurance Card
                addInsuranceCard(policyHolder);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // Function to update the list of dependents for a PolicyHolder
    public void updateDependents(PolicyHolder policyHolder) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("** Dependent Management **");
        System.out.println("1. Add Dependent");
        System.out.println("2. Remove Dependent");
        System.out.println("3. Exit");
        System.out.println("Enter your choice (1-3): ");

        int choice = Integer.parseInt(scanner.nextLine().trim());

        switch (choice) {
            case 1:
                // Add Dependent (existing logic)
                break;
            case 2:
                // Remove Dependent
                if (policyHolder.getDependents().isEmpty()) {
                    System.out.println("No dependents to remove.");
                    break;
                }

                System.out.println("List of Dependents:");
                int dependentIndex = 1;
                for (Dependent dependent : policyHolder.getDependents()) {
                    System.out.println(dependentIndex + ". " + dependent.getFullName());
                    dependentIndex++;
                }

                System.out.println("Enter the number of the dependent to remove (or 0 to cancel): ");
                int removeIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;

                if (removeIndex >= 0 && removeIndex < policyHolder.getDependents().size()) {
                    Dependent dependentToRemove = policyHolder.getDependents().remove(removeIndex);
                    claimProcessManager.removeCustomer(dependentToRemove); // Remove from ClaimProcessManager's list
                    System.out.println("Dependent removed successfully!");
                } else {
                    System.out.println("Invalid selection.");
                }
                break;
            case 3:
                // Exit (existing logic)
                break;
            default:
                System.out.println("Invalid choice.");
        }
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
                System.out.println("- " + dependent.getFullName());  // Assuming getFullName() in Dependent
            }
        }
    }

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
                Date expirationDate = data[3].trim(); // Adjust format if needed

                // You'll likely need to modify this to retrieve the PolicyHolder object
                PolicyHolder cardHolder = findPolicyHolderById(policyHolderID);

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
