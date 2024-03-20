/**
 * @author <Pham Minh Hoa - s3929256>
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PolicyHolder extends Customer {
    private List<Dependent> dependents;
    private InsuranceCard insuranceCard;

    // Getters, setters, constructor (including handling dependents and insurance card)


    public PolicyHolder(String id, String fullName, List<Dependent> dependents, InsuranceCard insuranceCard) {
        super.setId(id);
        super.setFullName(fullName);
        this.dependents = dependents;
        this.insuranceCard = insuranceCard;
    }

    public List<Dependent> getDependents() {
        return dependents;
    }

    public void setDependents(List<Dependent> dependents) {
        this.dependents = dependents;
    }

    public InsuranceCard getInsuranceCard() {
        return insuranceCard;
    }

    public void setInsuranceCard(InsuranceCard insuranceCard) {
        this.insuranceCard = insuranceCard;
    }



    @Override
    public String getType() {
        return "Policy Holder";
    }

    @Override
    public String getFullName() {
        return super.getFullName();
    }

    @Override
    public String getID(){
        return super.getId();
    }

    public List<Dependent> sortDependentsByName() {
        List<Dependent> sortedDependents = new ArrayList<>(dependents);
        Collections.sort(sortedDependents, (d1, d2) -> d1.getFullName().compareTo(d2.getFullName()));
        return sortedDependents;
    }

    public List<Dependent> sortDependentsByID() {
        List<Dependent> sortedDependents = new ArrayList<>(dependents);
        Collections.sort(sortedDependents, (d1, d2) -> d1.getId().compareTo(d2.getId()));
        return sortedDependents;
    }
}