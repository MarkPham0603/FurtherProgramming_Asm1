/**
 * @author <Pham Minh Hoa - s3929256>
 */
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public interface ClaimProcessManager {
    void addClaim(Claim claim) throws ClaimProcessException;
    boolean updateClaim(Claim claim) throws ClaimProcessException;
    boolean deleteClaim(Claim claim) throws ClaimProcessException;
    Claim getClaim(String id) throws ClaimNotFoundException;
    List<Claim> getAllClaims();

    List<Customer> getAllCustomers();

    List<Claim> filterClaimsByStatus(String status);

    void registerCustomer(Customer customer);
    public void registerClaim(Claim claim);

    void updateCustomer(Customer updatedCustomer);

    void removeCustomer(Customer customerToRemove);

    List<Claim> searchClaims(String searchCriteria);

    List<InsuranceCard> getAllInsuranceCards();
    void registerInsuranceCard(InsuranceCard insuranceCard);

    List<Claim> loadClaimsFromFile(String filename) throws IOException, ParseException;
    void saveClaimsToFile(String filename) throws IOException;
    Claim createClaimFromUserInput() throws ParseException, IOException;

}