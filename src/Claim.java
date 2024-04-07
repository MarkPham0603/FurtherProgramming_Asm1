/**
 * @author <Pham Minh Hoa - s3929256>
 */
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Claim {
    private String id;
    private String claimDate;
    private Customer insuredPerson;
    private String cardNumber;
    private String examDate;
    private List<String> documents; // List of document paths
    private int claimAmount;
    private String status; // New, Processing, Done
    private ReceiverBankInfo receiverBankInfo;

    // constructor


    public Claim(String id, String claimDate, Customer insuredPerson,
                 String cardNumber, String examDate, List<String> documents,
                 int claimAmount, String status, ReceiverBankInfo receiverBankInfo) {
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

    public String getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(String claimDate) {
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

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    public int getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(int claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ReceiverBankInfo getReceiverBankInfo() {
        return receiverBankInfo;
    }

    public void setReceiverBankInfo(ReceiverBankInfo receiverBankInfo) {
        this.receiverBankInfo = receiverBankInfo;
    }


    public boolean matchesSearchCriteria(String searchCriteria) {
        // Implement search logic based on claim attributes
        // Here's an example considering ID, customer name, and status
        return  getId().contains(searchCriteria) ||
                getInsuredPerson().getFullName().toLowerCase().contains(searchCriteria.toLowerCase()) ||
                getStatus().toLowerCase().contains(searchCriteria.toLowerCase());
    }

    @Override
    public String toString() {
        return id + ',' + claimDate + ',' + insuredPerson + ',' +cardNumber + ',' + examDate + ',' + documents + ',' + claimAmount + ',' + status + ',' + receiverBankInfo+'\n';
    }


}

