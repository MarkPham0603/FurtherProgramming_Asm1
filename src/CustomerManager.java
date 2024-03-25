import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerManager {
    private ClaimProcessManager claimProcessManager; // Reference to ClaimProcessManager for access to customers

    public CustomerManager(ClaimProcessManager claimProcessManager) {
        this.claimProcessManager = claimProcessManager;
    }

    // Function to print all customers
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

}
