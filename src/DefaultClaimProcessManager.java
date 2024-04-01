import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultClaimProcessManager implements ClaimProcessManager{
    private List<Customer> customers;
    private List<Claim> claims;



    public DefaultClaimProcessManager() {
        this.customers = new ArrayList<>();
        this.claims = new ArrayList<>();
    }

    @Override
    public List<Customer> getAllCustomers() {
        return Collections.unmodifiableList(customers); // Return an unmodifiable list
    }

    @Override
    public void registerCustomer(Customer customer) {
        customers.add(customer);
    }
    @Override
    public void updateCustomer(Customer updatedCustomer) {
        try {
            int index = customers.indexOf(updatedCustomer);
            if (index != -1) {
                customers.set(index, updatedCustomer);
                System.out.println("Customer information updated successfully!");
            } else {
                System.out.println("Customer not found in the list.");
            }
        } catch (Exception e) {
            System.err.println("Error updating customer: " + e.getMessage());
        }
    }

    @Override
    public void removeCustomer(Customer customerToRemove) {
        if (customers.remove(customerToRemove)) {
            System.out.println("Customer removed successfully!");
        } else {
            System.out.println("Customer not found in the list.");
        }
    }

    @Override
    public void addClaim(Claim claim) throws ClaimProcessException {
        if (claim == null) {
            throw new ClaimProcessException("Invalid claim object provided.");
        }
        claims.add(claim);
    }

    @Override
    public boolean updateClaim(Claim claim) throws ClaimProcessException {
        if (claim == null) {
            throw new ClaimProcessException("Invalid claim object provided.");
        }
        int index = findClaimIndex(claim.getId());
        if (index == -1) {
            return false; // Claim not found
        }
        claims.set(index, claim);
        return true;
    }

    @Override
    public boolean deleteClaim(Claim claim) throws ClaimProcessException {
        if (claim == null) {
            throw new ClaimProcessException("Invalid claim object provided.");
        }
        int index = findClaimIndex(claim.getId());
        if (index == -1) {
            return false; // Claim not found
        }
        claims.remove(index);
        return true;
    }

    @Override
    public Claim getClaim(String id) throws ClaimNotFoundException {
        int index = findClaimIndex(id);
        if (index == -1) {
            throw new ClaimNotFoundException("Claim with ID " + id + " not found.");
        }
        return claims.get(index);
    }


    @Override
    public List<Claim> getAllClaims() {
        return new ArrayList<>(claims); // Return a copy to avoid modification of internal list
    }

    @Override
    public List<Claim> filterClaimsByStatus(String status) {
        List<Claim> filteredClaims = new ArrayList<>();
        for (Claim claim : claims) {
            if (claim.getStatus().equals(status)) {
                filteredClaims.add(claim);
            }
        }
        return filteredClaims;
    }

    // Helper method to find the index of a claim by its ID
    private int findClaimIndex(String id) {
        for (int i = 0; i < claims.size(); i++) {
            if (claims.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public List<Claim> searchClaims(String searchCriteria) {
        List<Claim> filteredClaims = new ArrayList<>();
        for (Claim claim : claims) {
            if (claim.matchesSearchCriteria(searchCriteria)) { // Delegate search logic to Claim class
                filteredClaims.add(claim);
            }
        }
        return filteredClaims;
    }
}
