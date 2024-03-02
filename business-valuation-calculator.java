import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BusinessValuationCalculator {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean continueCalculations = true;

        while (continueCalculations) {
            System.out.println("Choose a calculation:");
            System.out.println("1. Discounted Cash Flow");
            System.out.println("2. Enterprise Value");
            System.out.print("Enter your choice (1 or 2): ");

            int choice = getValidIntegerInput(1, 2);

            if (choice == 1) {
                performDCFCalculation();
            } else if (choice == 2) {
                performEVCalculation();
            }

            System.out.print("Do you want to perform another calculation? (yes/no): ");
            continueCalculations = scanner.next().equalsIgnoreCase("yes");
        }

        scanner.close();

        System.out.println("Exiting program.");
    }

    private static void performDCFCalculation() {
        Scanner scanner = new Scanner(System.in);

        double discountPercentage;
        while (true) {
            try {
                System.out.println("Enter discount rate (as a percentage): ");
                String input = scanner.next();
                discountPercentage = Double.parseDouble(input);
                if (discountPercentage <= 0) {
                    System.out.println("Discount rate must be a positive number. Please try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        int periodCount;
        while (true) {
            try {
                System.out.println("Enter number of periods: ");
                String input = scanner.next();
                periodCount = Integer.parseInt(input);
                if (periodCount <= 0) {
                    System.out.println("Number of periods must be a positive integer. Please try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }

        double initialInvestment;
        while (true) {
            try {
                System.out.println("Enter cash flow for period 0 (initial investment): ");
                String input = scanner.next();
                initialInvestment = Math.abs(Double.parseDouble(input));
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number for cash flow.");
            }
        }

        List<Double> cashFlows = new ArrayList<>(periodCount + 1);
        cashFlows.add(-initialInvestment);

        for (int period = 1; period <= periodCount; period++) {
            double cashFlow;
            while (true) {
                try {
                    System.out.printf("Enter cash flow for period %d: ", period);
                    String input = scanner.next();
                    cashFlow = Double.parseDouble(input);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number for cash flow.");
                }
            }
            cashFlows.add(cashFlow);
        }

        List<Double> presentValues = new ArrayList<>(periodCount + 1);
        double npv = 0;
        for (int period = 0; period <= periodCount; period++) {
            double presentValue = cashFlows.get(period) / Math.pow(1 + discountPercentage / 100, period);
            presentValues.add(presentValue);
            npv += presentValue;
        }

        System.out.println("------- Results -------");
        System.out.printf("Discount rate: %.2f%%\n", discountPercentage);
        System.out.printf("Number of periods: %d\n", periodCount);
        System.out.println("Cash flows:");
        for (int period = 0; period <= periodCount; period++) {
            System.out.printf("Period %d: %.2f\n", period, cashFlows.get(period));
        }
        System.out.println("Present values:");
        for (int period = 0; period <= periodCount; period++) {
            System.out.printf("Period %d: %.2f\n", period, presentValues.get(period));
        }
        System.out.printf("Net present value (NPV): %.4f\n", npv);
    }

    private static void performEVCalculation() {
        Scanner scanner = new Scanner(System.in);

        double stockPrice = getValidInput(scanner, "Enter Stock Price: ");
        double numberOfShares = getValidInput(scanner, "Enter Number of Shares: ");
        double marketCap = calculateMarketCapitalization(stockPrice, numberOfShares);

        System.out.println("Market Capitalization: " + marketCap);

        double longTermDebt = getValidInput(scanner, "Enter long-term debt: ");
        double shortTermDebt = getValidInput(scanner, "Enter short-term debt: ");
        double leaseLiabilities = getValidInput(scanner, "Enter lease liabilities: ");
        double financeLeaseObligations = getValidInput(scanner, "Enter finance lease obligations: ");
        double notesPayable = getValidInput(scanner, "Enter notes payable: ");
        double otherDebt = getValidInput(scanner, "Enter other debt: ");
        double totalDebt = calculateTotalDebt(longTermDebt, shortTermDebt, leaseLiabilities,
                financeLeaseObligations, notesPayable, otherDebt);

        System.out.println("Total Debt: " + totalDebt);

        List<Double> minorityInterests = collectMinorityInterests(scanner);

        double cashBalance = getValidInput(scanner, "Enter Cash Balance: ");
        scanner.close();

        double enterpriseValue = calculateEnterpriseValue(marketCap, totalDebt, cashBalance, calculateTotalMinorityInterests(minorityInterests));
        System.out.println("Enterprise Value: " + enterpriseValue);
    }

    private static double getValidInput(Scanner scanner, String message) {
        double input;
        do {
            System.out.print(message);
            while (!scanner.hasNextDouble()) {
                System.out.println("Invalid input. Please enter a valid number.");
                System.out.print(message);
                scanner.next();
            }
            input = scanner.nextDouble();
            if (input < 0) {
                System.out.println("Input cannot be negative. Please enter a valid value.");
            }
        } while (input < 0);
        return input;
    }

    private static List<Double> collectMinorityInterests(Scanner scanner) {
        List<Double> minorityInterests = new ArrayList<>();
        System.out.print("Are there any minority interests? (yes/no): ");
        while (true) {
            String response = scanner.next();
            if (response.equalsIgnoreCase("yes")) {
                double ownershipPercentage = getValidInput(scanner, "Enter Ownership Percentage (in decimal): ");
                double subsidiaryNetAssets = getValidInput(scanner, "Enter Subsidiary's Net Assets: ");
                double totalMinorityInterest = ownershipPercentage * subsidiaryNetAssets;
                minorityInterests.add(totalMinorityInterest);
                System.out.println("Total Minority Interest: " + totalMinorityInterest);
                System.out.print("Are there any additional minority interests? (yes/no): ");
            } else if (response.equalsIgnoreCase("no")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter 'yes' or 'no'.");
            }
        }
        return minorityInterests;
    }

    public static double calculateMarketCapitalization(double stockPrice, double numberOfShares) {
        return stockPrice * numberOfShares;
    }

    public static double calculateTotalDebt(double longTermDebt, double shortTermDebt, double leaseLiabilities,
                                            double financeLeaseObligations, double notesPayable, double otherDebt) {
        return longTermDebt + shortTermDebt + leaseLiabilities + financeLeaseObligations + notesPayable + otherDebt;
    }

    public static double calculateEnterpriseValue(double marketCap, double totalDebt, double cashBalance, double totalMinorityInterests) {
        return marketCap + totalDebt - cashBalance + totalMinorityInterests;
    }

    public static double calculateTotalMinorityInterests(List<Double> minorityInterests) {
        double totalMinorityInterests = 0.0;
        for (double interest : minorityInterests) {
            totalMinorityInterests += interest;
        }
        return totalMinorityInterests;
    }

    private static int getValidIntegerInput(int lowerBound, int upperBound) {
        int input;
        do {
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.next();
            }
            input = scanner.nextInt();
            if (input < lowerBound || input > upperBound) {
                System.out.println("Input must be between " + lowerBound + " and " + upperBound);
            }
        } while (input < lowerBound || input > upperBound);
        return input;
    }
}
