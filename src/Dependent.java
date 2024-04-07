/**
 * @author <Pham Minh Hoa - s3929256>
 */
import java.util.List;

public class Dependent extends Customer {
    private PolicyHolder policyHolder;

    // Getters, setters, constructor (including referencing policyholder)


    public Dependent(String id, String fullName, List<Claim> claims, PolicyHolder policyHolder) {
        super(id, fullName, claims);
        this.policyHolder = policyHolder;
    }

    public PolicyHolder getPolicyHolder() {
        return policyHolder;
    }

    public void setPolicyHolder(PolicyHolder policyHolder) {
        this.policyHolder = policyHolder;
    }

    @Override
    public String getType() {
        return "Dependent";
    }

    @Override
    public String getFullName() {
        return super.getFullName();
    }

    @Override
    public String getID() {
        return super.getID();
    }

    public void setPolicyHolderbyId (String policyHolderbyId) {
        ClaimProcessManager claimProcessManager = new DefaultClaimProcessManager();
        List<Customer> customers = claimProcessManager.getAllCustomers();
        for (Customer customer: customers) {
            if (policyHolderbyId.equals(customer.getID())){
                this.policyHolder = (PolicyHolder) customer;
            }else {
                System.out.println("Can not found the Policy Holder ID!");
            }
        }
    }

    @Override
    public String toString() {
        return getFullName() + ',' + policyHolder;
    }
}
