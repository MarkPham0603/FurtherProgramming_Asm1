import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, ClaimProcessException, ParseException, ClaimNotFoundException {
        ClaimProcessManager claimProcessManager = new DefaultClaimProcessManager();
        CustomerManager customerManager = new CustomerManager();
        System.out.println(" FURTHER PROGRAMMING - ASSIGNMENT 1: BUILD A CONSLE APP ");
        System.out.println("--------------------------------------------------------");
        System.out.println("Student Name: Pham Minh Hoa\n" +
                "SID: s3929256\n" +
                "Lecturer: Minh Vu Thanh");
        System.out.println("--------------------------------------------------------");
        System.out.println("WELCOME TO INSURANCE CLAIMS MANAGEMENT SYSTEM");
        Scanner scanner = new Scanner(System.in);
        boolean system = true;
        while (system == true){
            System.out.println("--------------------------------------------------------");
            System.out.println("You want to use this system as:\n" +
                    "1. Admin\n" +
                    "2. PolicyHolder\n" +
                    "3. to quit");
            System.out.println("Please enter your choice:");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 1){
                System.out.println("--------------------------------------------------------");
                System.out.println("1. View all customers\n" +
                        "2. View all Dependents\n" +
                        "3. View all Policy Holder\n" +
                        "4. Search for customer\n" +
                        "5. Update Claim Status\n" +
                        "6. Search Claim by ID/Status\n" +
                        "7. View all Claims\n" +
                        "8. Delete a Claim");
                System.out.println("Please enter your choice:");
                int choice1 = Integer.parseInt(scanner.nextLine());
                switch (choice1){
                    case 1: {
                        customerManager.displayAllCustomer();
                       break;
                    }
                    case 2: {
                        customerManager.displayAllDependent("customer.txt");
                        break;
                    }
                    case 3: {
                        customerManager.displayAllPolicyHolder("customer.txt");
                        break;
                    }
                    case 4: {
                        customerManager.searchCustomer("customer.txt");
                        break;
                    }
                    case 5: {
                        customerManager.updateClaim("claim.txt");
                        break;
                    }
                    case 6: {
                        System.out.println("You want to search Claim by id/status");
                        System.out.println("Enter id/status: ");
                        String input = scanner.nextLine();
                        customerManager.searchClaims(input);
                        break;
                    } case 7: {
                        customerManager.displayAllClaim();
                        break;
                    }
                    case 8: {
                        System.out.println("Enter id: ");
                        String id = scanner.nextLine();
                        customerManager.deleteClaim(id,"Newclaim.txt");
                        break;
                    } default:
                }
            } else if (choice == 2) {
                System.out.println("--------------------------------------------------------");
                System.out.println("1. Register\n" +
                        "2. Update Info\n" +
                        "3. View Account/Dependent Info\n" +
                        "4. View Claim\n" +
                        "5. View Insurance Card");
                System.out.println("Please enter your choice:");
                int choice1 = Integer.parseInt(scanner.nextLine());
                switch (choice1){
                    case 1: {
                        customerManager.registerPolicyHolder();
                        customerManager.generateCustomerReport("customer.txt");
                        customerManager.readInsuranceCardReport("insurancecard.txt");
                        break;
                    }
                    case 2: {
                        customerManager.updateCustomerInfo("customer.txt");
                        break;
                    }
                    case 3: {
                        customerManager.searchCustomer("customer.txt");
                        break;

                    }
                    case 4: {
                        System.out.println("You want to search Claim by id");
                        System.out.println("Enter id ");
                        String input = scanner.nextLine();
                        customerManager.searchClaims(input);
                        break;
                    }
                    case 5: {
                        customerManager.viewInsuranceCard("insurance.txt");
                        break;
                    } default:
                }
            } else {
                system = false;
            }
        }
        System.out.println("Thank you for using our system. See you again!");
    }
}
