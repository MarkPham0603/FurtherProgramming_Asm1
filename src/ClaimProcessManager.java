/**
 * @author <Pham Minh Hoa - s3929256>
 */
import java.util.ArrayList;
import java.util.List;

public interface ClaimProcessManager {
    void addClaim(Claim claim);
    void updateClaim(Claim claim);
    void deleteClaim(Claim claim);
    Claim getClaim(String id);
    List<Claim> getAllClaims();

    List<Customer> customers = new ArrayList<>();
}