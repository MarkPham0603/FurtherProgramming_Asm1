/**
 * @author <Pham Minh Hoa - s3929256>
 */
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class InsuranceCard {



    private String cardNumber;
    private PolicyHolder cardHolder;
    private String policyOwner;
    private Date expirationDate;

    // Getters, setters, constructor (ensure only one card holder)

    public InsuranceCard(String cardNumber, PolicyHolder cardHolder,
                         String policyOwner, Date expirationDate) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.policyOwner = policyOwner;
        this.expirationDate = expirationDate;
    }

    public void setCardHolder(PolicyHolder cardHolder) {
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

    public String getPolicyOwner() {
        return policyOwner;
    }

    public void setPolicyOwner(String policyOwner) {
        this.policyOwner = policyOwner;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }


}


