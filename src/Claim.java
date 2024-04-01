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
    private Date claimDate;
    private Customer insuredPerson;
    private String cardNumber;
    private Date examDate;
    private List<String> documents; // List of document paths
    private double claimAmount;
    private String status; // New, Processing, Done
    private ReceiverBankInfo receiverBankInfo;

    // constructor


    public Claim(String id, Date claimDate, Customer insuredPerson,
                 String cardNumber, Date examDate, List<String> documents,
                 double claimAmount, String status, ReceiverBankInfo receiverBankInfo) {
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

    public ReceiverBankInfo getReceiverBankInfo() {
        return receiverBankInfo;
    }

    public void setReceiverBankInfo(ReceiverBankInfo receiverBankInfo) {
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

    public List<Claim> readClaimsFromFile(String filename) throws IOException, ParseException {
        List<Claim> claims = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        String line;
        reader.readLine(); // Skip header row (if present)
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");

            // Parse claim data fields
            String id = data[0];
            Date claimDate = new SimpleDateFormat("yyyy-MM-dd").parse(data[1]); // Adjust date format if needed
            String customerID = data[2]; // Assuming customerID used to link to Customer
            String cardNumber = data[3];
            Date examDate = new SimpleDateFormat("yyyy-MM-dd").parse(data[4]); // Adjust date format if needed

            // ... (parsing for documentPaths, claimAmount, status) ...

            List<String> documentPaths = new ArrayList<>();
            for (String path : data[5].split(";")) { // Change delimiter if paths use different separator (e.g., ",")
                documentPaths.add(path.trim());
            }

            double claimAmount = Double.parseDouble(data[6]);
            String status = data[7];

            // Parse receiver bank info (assuming data[8] contains comma-separated bank name, account name, account number)
            String[] bankInfo = data[8].split(",");
            String bankName = bankInfo[0];
            String accountName = bankInfo[1];
            String accountNumber = bankInfo[2];
            ReceiverBankInfo receiverBankInfo = new ReceiverBankInfo(bankName, accountName, accountNumber);

            // Retrieve Customer object based on customerID
            Customer insuredPerson = getInsuredPerson(); // Replace with your actual customer retrieval logic

            // Create and add Claim object
            Claim claim = new Claim(id, claimDate, insuredPerson, cardNumber, examDate, documentPaths, claimAmount, status, receiverBankInfo);
            claims.add(claim);
        }

        reader.close();
        return claims;
    }

    public void writeToFile(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

        // Write header row (optional)
        writer.write("id,claimDate,insuredPerson,cardNumber,examDate,documentPaths,claimAmount,status,receiverBankInfo\n");

        // Format claim data for CSV
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Adjust date format if needed

        String documentList = String.join(",", documents); // Join document paths with comma separator

        writer.write(String.format(
                "%s,%s,%s,%s,%s,%s,%.2f,%s,%s\n",
                id,
                dateFormat.format(claimDate),
                insuredPerson, // Assuming getCustomerID() returns customer ID
                cardNumber,
                dateFormat.format(examDate),
                documentList,
                claimAmount,
                status,
                receiverBankInfo.getBankName() + "," + receiverBankInfo.getAccountName() + "," + receiverBankInfo.getAccountNumber()
        ));

        writer.close();
    }

    public boolean matchesSearchCriteria(String searchCriteria) {
        // Implement search logic based on claim attributes
        // Here's an example considering ID, customer name, and status
        return  getId().contains(searchCriteria) ||
                getInsuredPerson().getFullName().toLowerCase().contains(searchCriteria.toLowerCase()) ||
                getStatus().toLowerCase().contains(searchCriteria.toLowerCase());
    }

}

