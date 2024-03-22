/**
 * @author <Pham Minh Hoa - s3929256>
 */
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
}