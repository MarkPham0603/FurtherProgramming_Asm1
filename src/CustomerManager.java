import java.io.*;
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
            // Write header row (optional)
            writer.write("Customer ID, Full Name, Type\n");

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
        List<Customer> customers = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        try {
            // Skip header row (assuming the first line is a header)
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                // Extract customer information from each line (assuming comma-separated values)
                String[] data = line.split(",");
                String id = data[0].trim();
                String fullName = data[1].trim();
                String type = data[2].trim();
                Customer customer;
                if (type.equals("Policy Holder")) {
                    customer = new PolicyHolder(id, fullName, new ArrayList<>(), null); // Placeholder for insurance card
                } else if (type.equals("Dependent")) {
                    customer = new Dependent(null); // Policyholder will be assigned later
                } else {
                    // Handle unexpected customer type
                    throw new RuntimeException("Unknown customer type: " + type);
                }
                customers.add(customer);
            }
        } finally {
            reader.close();
        }

        return customers;
    }
    public PolicyHolder registerPolicyHolder() throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Generate unique ID for the PolicyHolder
        String id = generateUniqueID();
        System.out.println("Policy Holder ID: " + id);

        System.out.println("Enter Policy Holder Full Name: ");
        String fullName = scanner.nextLine().trim();

        // Get optional dependent information
        List<Dependent> dependents = new ArrayList<>();
        addDependents(dependents);

        // Skip adding insurance card during registration
        InsuranceCard insuranceCard = null;

        // Create the PolicyHolder object
        PolicyHolder newPolicyHolder = new PolicyHolder(id, fullName, dependents, insuranceCard);
        claimProcessManager.registerCustomer(newPolicyHolder);

        System.out.println("Policy holder successfully registered!");
        System.out.println("Policy Holder ID: " + newPolicyHolder.getID());
        System.out.println("Policy Holder Name: " + newPolicyHolder.getFullName());

        return newPolicyHolder;
    }

    public void addDependents(List<Dependent> dependents) throws IOException {
        Scanner scanner = new Scanner(System.in);
        char addDependent;

        do {
            System.out.println("Do you want to add a dependent (y/n)? ");
            addDependent = scanner.nextLine().charAt(0);
            addDependent = Character.toLowerCase(addDependent);

            if (addDependent == 'y') {
                System.out.println("Enter Dependent ID: "); // Can be user-entered or system-generated
                String dependentId = scanner.nextLine().trim();

                System.out.println("Enter Dependent Full Name: ");
                String dependentName = scanner.nextLine().trim();

                // Assuming no further dependent attributes, create a Dependent object
                dependents.add(new Dependent(null)); // Policyholder will be assigned later
            }
        } while (addDependent == 'y');

        for (Dependent dependent : dependents) {
            claimProcessManager.registerCustomer(dependent); // Add each dependent to the central customer list
        }
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
        Calendar cal= Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.MONTH, 10);
        Date expirationDate = cal.getTime();

        // Create an InsuranceCard object with the PolicyHolder reference
        InsuranceCard insuranceCard = new InsuranceCard(cardNumber, policyHolder, cardOwner, expirationDate);

        // Update the PolicyHolder object to set the insurance card (optional)
        policyHolder.setInsuranceCard(insuranceCard);
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
}
