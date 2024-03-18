import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
        // ... (Write data to the file using customer information)
        writer.close();
    }

}
