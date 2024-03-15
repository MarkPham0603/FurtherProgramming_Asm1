/**
 * @author <Pham Minh Hoa - s3929256>
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Claim {
    private String id;
    private Date claimDate;
    private Customer insuredPerson;
    private String cardNumber;
    private Date examDate;
    private List<String> documents; // List of document paths
    private double claimAmount;
    private String status; // New, Processing, Done
    private String receiverBankInfo;

    // constructor


    public Claim(String id, Date claimDate, Customer insuredPerson,
                 String cardNumber, Date examDate, List<String> documents,
                 double claimAmount, String status, String receiverBankInfo) {
        this.id = id;
        this.claimDate = claimDate;
        this.insuredPerson = insuredPerson;
        this.cardNumber = cardNumber;
        this.examDate = examDate;
        this.documents = documents;
        this.claimAmount = claimAmount;
        this.status = status;
        this.receiverBankInfo = receiverBankInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(Date claimDate) {
        this.claimDate = claimDate;
    }

    public Customer getInsuredPerson() {
        return insuredPerson;
    }

    public void setInsuredPerson(Customer insuredPerson) {
        this.insuredPerson = insuredPerson;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    public double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceiverBankInfo() {
        return receiverBankInfo;
    }

    public void setReceiverBankInfo(String receiverBankInfo) {
        this.receiverBankInfo = receiverBankInfo;
    }

    public List<Claim> sortByStatusAndDate(List<Claim> claims) {
        List<Claim> sortedClaims = new ArrayList<>(claims); // Copy the input list of claims
        Collections.sort(sortedClaims, (c1, c2) -> {
            int statusComparison = c1.getStatus().compareTo(c2.getStatus());
            if (statusComparison != 0) {
                return statusComparison;
            } else {
                return c1.getClaimDate().compareTo(c2.getClaimDate());
            }
        });
        return sortedClaims;
    }

}

