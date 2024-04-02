import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, ClaimProcessException {
        ClaimProcessManager claimProcessManager = new DefaultClaimProcessManager();
        CustomerManager customerManager = new CustomerManager();

        List<Customer> customers = new ArrayList<>();
        PolicyHolder policyHolder = customerManager.registerPolicyHolder();
        customers.add(policyHolder);

        // Prompt user to add dependents
        // Add Insurance Cards (remaining code)
        // ...

        System.out.println(customers);
        // ... (rest of the code remains the same)
    }
        /**
        // Read Customers from Report (Optional)
        List<Customer> readCustomers = customerManager.readCustomerReport(reportFilename);
        System.out.println("\nCustomers Read from Report:");
        for (Customer customer : readCustomers) {
            System.out.println(customer);
        }

        // Create Claims (assuming a Claim constructor with minimal data)
        // ... (existing claim creation logic) ...

        // Print All Customers
        customerManager.printAllCustomers();**/

}
