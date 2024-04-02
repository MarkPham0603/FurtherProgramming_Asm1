/**
 * @author <Pham Minh Hoa - s3929256>
 */
import java.util.List;

public class Dependent extends Customer {
    private PolicyHolder policyHolder;

    // Getters, setters, constructor (including referencing policyholder)

    public Dependent(PolicyHolder policyHolder) {
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
}
