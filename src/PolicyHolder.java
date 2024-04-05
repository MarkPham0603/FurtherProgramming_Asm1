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


    public PolicyHolder(String id, String fullName, List<Claim> claims, List<Dependent> dependents, InsuranceCard insuranceCard) {
        super(id, fullName, claims);
        this.dependents = new ArrayList<>();
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

    public void addDependent (Dependent dependent){
        dependents.add(dependent);
    }

    public void setInsuranceCardbyid (String insuranceCardbyid) {
        ClaimProcessManager claimProcessManager = new DefaultClaimProcessManager();
        List<InsuranceCard> insuranceCards = claimProcessManager.getAllInsuranceCards();
        for (InsuranceCard insuranceCard1: insuranceCards) {
            if (insuranceCardbyid.equals(insuranceCard1.getCardNumber())){
                this.insuranceCard = insuranceCard1;
            }else {
                System.out.println("Can not found the Insurance Card ID!");
            }
        }
    }
}