import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ClaimProcessException {
        ClaimProcessManager claimProcessManager = new DefaultClaimProcessManager();
        CustomerManager customerManager = new CustomerManager();

        // Test Registering a Customer (Policy Holder)
        String policyHolderID = "PH123";
        String policyHolderName = "John Doe";
        claimProcessManager.registerCustomer(new PolicyHolder(policyHolderID, policyHolderName, new ArrayList<>(), null));

        // Test Reading Customer Report from Text File (replace with your file path)
        List<Customer> customers = customerManager.readCustomerReport("customer_data.txt");
        System.out.println("Customers from Text File:");
        for (Customer customer : customers) {
            System.out.println(customer); // Assuming Customer has a proper toString() method
        }

        // Test Creating a Claim (assuming data is available)
        String claimID = "CLM001";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date claimDate;
        try {
            claimDate = sdf.parse("2024-04-01");
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing exception (e.g., invalid date format)
            return;
        }
        String status = "Open";
        PolicyHolder insuredPerson = (PolicyHolder) customers.get(0); // Assuming the first customer is the insured person
        double amount = 1000.00;
        claimProcessManager.addClaim(new Claim(claimID, claimDate, insuredPerson, null, null, new ArrayList<>(), amount, status, null));

        // Test Searching Claims (replace search criteria with your desired value)
        String searchCriteria = "John";
        List<Claim> filteredClaims = claimProcessManager.searchClaims(searchCriteria);
        System.out.println("\nClaims Matching '" + searchCriteria + "':");
        for (Claim claim : filteredClaims) {
            System.out.println(claim); // Assuming Claim has a proper toString() method
        }
    }
}
