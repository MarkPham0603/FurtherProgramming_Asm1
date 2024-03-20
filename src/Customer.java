/**
 * @author <Pham Minh Hoa - s3929256>
 */
import java.util.ArrayList;
import java.util.List;

public abstract class Customer {
    private String id;
    private String fullName;
    private List<Claim> claims;

    // Getters, setters, and constructor

    public Customer(String id, String fullName, List<Claim> claims) {
        this.id = id;
        this.fullName = fullName;
        this.claims = claims;
    }

    public Customer() {
        this.id = ""; // Set default empty string for id
        this.fullName = ""; // Set default empty string for full name
        this.claims = new ArrayList<Claim>(); // Initialize an empty list for claims
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }

    public abstract String getType(); // differentiate PolicyHolder and Dependent

    public String getFullName() {
        return fullName;
    }

    public String getID() {
        return id;
    }
}
