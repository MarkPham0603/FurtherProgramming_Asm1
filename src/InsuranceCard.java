/**
 * @author <Pham Minh Hoa - s3929256>
 */
import java.util.Date;

public class InsuranceCard {
    private String cardNumber;
    private Customer cardHolder;
    private PolicyHolder policyOwner;
    private Date expirationDate;

    // Getters, setters, constructor (ensure only one card holder)

    public InsuranceCard(String cardNumber, Customer cardHolder,
                         PolicyHolder policyOwner, Date expirationDate) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.policyOwner = policyOwner;
        this.expirationDate = expirationDate;
    }

    public void setCardHolder(Customer cardHolder) {
        if (!(cardHolder instanceof PolicyHolder)) {
            throw new IllegalArgumentException("Only PolicyHolder can be a card holder");
        }
        this.cardHolder = cardHolder;
    }

    public Customer getCardHolder() {
        return cardHolder;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public PolicyHolder getPolicyOwner() {
        return policyOwner;
    }

    public void setPolicyOwner(PolicyHolder policyOwner) {
        this.policyOwner = policyOwner;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}

