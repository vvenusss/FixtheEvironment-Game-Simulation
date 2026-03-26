import java.util.Scanner;
import java.util.ArrayList;
import java.text.DecimalFormat;

public class retailapp {
    public static void main(String[] args) {
        int choice = 0;
        DecimalFormat df = new DecimalFormat("0.00");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the store name: ");
        String storeName = scanner.nextLine();

        System.out.print("Enter starting budget (>=0): ");
        float budget = scanner.nextFloat();
        scanner.nextLine(); 

        System.out.println("Welcome to " + storeName + "!" + "Current budget: $" + df.format(budget));
        ArrayList<Item> inventory = new ArrayList<>();


        while(choice != 3){
            System.out.println();
            System.out.println("===== Retail Management System =====");
            System.out.println("1. Add Item");
            System.out.println("2. View Inventory");
            System.out.println("3. Exit");
            System.out.print("Choose an option (1-3): ");

            choice = scanner.nextInt();
            scanner.nextLine();

            if(choice == 1){
                System.out.println("--- Add Item ---");

                System.out.print("Enter SKU: ");
                String sku = scanner.nextLine();

                System.out.print("Enter item name: ");
                String name = scanner.nextLine();

                System.out.print("Enter price (> 0): ");
                double price = scanner.nextDouble();
                scanner.nextLine();

                System.out.print("Enter quantity (>= 0): ");
                int quantity = scanner.nextInt();
                scanner.nextLine();

                if(price > 0 && quantity >= 0){
                    Item item = new Item(sku, name, price, quantity);
                    inventory.add(item);
                    System.out.println("Item added successfully.");
                }
            }

            else if(choice == 2){
                if(inventory.isEmpty()){
                    System.out.println("No items in inventory.");
                    continue;
                }

                double totalValue = 0;
                System.out.println("--- Inventory ---");

                System.out.printf("%-6s %-20s %-10s %-10s %-10s\n", "SKU", "Name", "Price($)", "Qty", "Line Total");
                System.out.println("--------------------------------------------------------------");

                for(Item item : inventory){
                    System.out.printf("%-6s %-20s $%-10s %-10d $%-10s\n", item.sku, item.name, df.format(item.price), item.quantity, df.format(item.getTotalValue()));
                    totalValue += item.getTotalValue();
                }
                System.out.println("--------------------------------------------------------------");
                System.out.println("Total Inventory Value: $" + df.format(totalValue));
            }
        }

        if(choice == 3){
            System.out.println("Goodbye! Exiting program.");
            scanner.close();
        }
        else{
            System.out.println("Invalid choice.");
        }

    scanner.close();
    }
}

class Item{
    String sku;
    String name;
    double price;
    int quantity;

    //constructor
    public Item(String sku, String name, double price, int quantity){
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    //method
    public double getTotalValue(){
        return price * quantity;
    }
}