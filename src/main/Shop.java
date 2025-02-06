package main;

import java.util.HashSet;
import java.util.Locale;
import model.Product;
import model.Sale;
import java.util.Scanner;
import java.util.Set;

public class Shop {

    private double cash = 100.00;
    private Product[] inventory;
    private int numberProducts;
    private Sale[] sales;

    final static double TAX_RATE = 1.04;

    public Shop() {
        inventory = new Product[10];
        sales = new Sale[10];
    }

    public static void main(String[] args) {
        Shop shop = new Shop();

        shop.loadInventory();

        Scanner scanner = new Scanner(System.in);
        int opcion = 0;
        boolean exit = false;

        do {
            System.out.println("\n");
            System.out.println("===========================");
            System.out.println("Menu principal miTienda.com");
            System.out.println("===========================");
            System.out.println("1) Contar caja");
            System.out.println("2) Anadir producto");
            System.out.println("3) Anadir stock");
            System.out.println("4) Marcar producto proxima caducidad");
            System.out.println("5) Ver inventario");
            System.out.println("6) Venta");
            System.out.println("7) Ver ventas");
            System.out.println("10) Salir programa");
            System.out.print("Seleccione una opcion: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    shop.showCash();
                    break;

                case 2:
                    shop.addProduct();
                    break;

                case 3:
                    shop.addStock();
                    break;

                case 4:
                    shop.setExpired();
                    break;

                case 5:
                    shop.showInventory();
                    break;

                case 6:
                    shop.sale();
                    break;

                case 7:
                    shop.showSales();
                    break;

                case 10:
                    exit = true;
                    break;
            }
        } while (!exit);
    }

    /**
     * load initial inventory to shop
     */
    public void loadInventory() {
        addProduct(new Product("Manzana", 10.00, true, 10));
        addProduct(new Product("Pera", 20.00, true, 20));
        addProduct(new Product("Hamburguesa", 30.00, true, 30));
        addProduct(new Product("Fresa", 5.00, true, 20));
    }

    /**
     * show current total cash
     */
    private void showCash() {
        System.out.println("Dinero actual: " + this.cash + "$");
    }

    /**
     * add a new product to inventory getting data from console
     */
    public void addProduct() {
        if (isInventoryFull()) {
            System.out.println("No se pueden anadir mas productos");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(Locale.US);

        String name = "";
        boolean esRepe;
        Product product = this.findProduct(name);
        do {
            System.out.print("Nombre: ");
            name = scanner.nextLine();

            esRepe = false;
            product = this.findProduct(name);
            // si product es null todo ok
            if (product == null) {
                esRepe = false;
            }
            // si no es null, es repe
            if (product != null) {
                System.out.println("El producto esta repetido");
                esRepe = true;
            }

        } while (esRepe);
        System.out.print("Precio mayorista: ");
        double wholesalerPrice = scanner.nextDouble();
        System.out.print("Stock: ");
        int stock = scanner.nextInt();
        addProduct(new Product(name, wholesalerPrice, true, stock));
    }

    /**
     * add stock for a specific product
     */
    public void addStock() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Seleccione un nombre de producto: ");
        String name = scanner.next();
        Product product = findProduct(name);

        if (product != null) {
            // ask for stock
            System.out.print("Seleccione la cantidad a aÃ±adir: ");
            int stock = scanner.nextInt();
            // update stock product
            int newStock = stock + product.getStock();
            product.setStock(newStock);
            System.out.println("El stock del producto " + name + " ha sido actualizado a " + product.getStock());

        } else {
            System.out.println("No se ha encontrado el producto con nombre " + name);
        }
    }

    /**
     * set a product as expired
     */
    private void setExpired() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Seleccione un nombre de producto: ");
        String name = scanner.next();

        Product product = findProduct(name);

        if (product != null) {
            double precioCaducado = 0.0;
            int stock = 0;
            boolean available = false;
            product.setStock(stock);
            product.setWholesalerPrice(precioCaducado);
            product.setAvailable(available);
            System.out.println("El stock del producto " + name + " ha sido actualizado a " + product.getStock());
            System.out.println("El precio del producto " + name + " ha sido actualizado a " + product.getWholesalerPrice());
            System.out.println("El estado del producto " + name + " ha sido actualizado a " + product.isAvailable());

        }
    }

    /**
     * show all inventory
     */
    public void showInventory() {
        System.out.println("Contenido actual de la tienda:");
        for (Product product : inventory) {
            if (product != null) {
                int id = product.getId();
                String name = product.getName();
                double price = product.getWholesalerPrice();
                boolean avalible = product.isAvailable();
                int stock = product.getStock();
                System.out.println("id " + id + " name " + name + " price " + price + "$" + " Is avalible? " + avalible + " Stock: " + stock);
            }
        }
    }

    /**
     * make a sale of products to a client
     */
    public void sale() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Realizar venta, escribir nombre cliente");
        String client = sc.nextLine();

        double totalAmount = 0.0;
        String name = "";
        Product[] purchasedProducts = new Product[10]; 
        int productCount = 0;

        while (!name.equals("0") ) {
            System.out.println("Introduce el nombre del producto, escribir 0 para terminar:");
            name = sc.nextLine();

            if (name.equals("0")) {
                break;
            }
            Product product = findProduct(name);

            if (product != null && product.isAvailable() && product.getStock() > 0) {
                totalAmount += product.getWholesalerPrice();
                product.setStock(product.getStock() - 1);
                if (product.getStock() == 0) {
                    product.setAvailable(false);
                }
                purchasedProducts[productCount] = product;
                productCount++;
                System.out.println("Producto añadido con éxito.");
            } else {
                System.out.println("Producto no encontrado o sin stock.");
            }
        }

        if (productCount == 0) {
            System.out.println("No se realizó la venta, ningún producto válido seleccionado.");
            return;
        }

        totalAmount *= TAX_RATE;
        cash += totalAmount;

        Product[] finalProducts = new Product[productCount];
        System.arraycopy(purchasedProducts, 0, finalProducts, 0, productCount);
        Sale sale = new Sale(client, finalProducts, totalAmount);
        for (int i = 0; i < sales.length; i++) {
            if (sales[i] == null) {
                sales[i] = sale;
                break;
            }
        }

        System.out.println("Venta realizada con éxito, total: " + totalAmount);
    }

    private void showSales() {
        System.out.println("Lista de ventas:");
        boolean hasSales = false;

        for (Sale sale : sales) {
            if (sale != null) {
                System.out.println("------------------------");
                System.out.println("Cliente: " + sale.getClient());
                System.out.println("Total: " + sale.getAmount()+ "$");
                System.out.println("Productos vendidos:");
                for (Product product : sale.getProducts()) {
                    System.out.println("- " + product.getName() + " (" + product.getWholesalerPrice() + "$)");
                }
                System.out.println("------------------------");
                hasSales = true;
            }
        }

        if (!hasSales) {
            System.out.println("No hay ventas registradas.");
        }
    }

    /**
     * add a product to inventory
     *
     * @param product
     */
    public void addProduct(Product product) {
        if (isInventoryFull()) {
            System.out.println("No se pueden aÃ±adir mÃ¡s productos, se ha alcanzado el mÃ¡ximo de " + inventory.length);
            return;
        }
        inventory[numberProducts] = product;
        numberProducts++;
    }

    /**
     * check if inventory is full or not
     *
     * @return true if inventory is full
     */
    public boolean isInventoryFull() {
        if (numberProducts == 10) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * find product by name
     *
     * @param name
     * @return product found by name
     */
    public Product findProduct(String name) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null && inventory[i].getName().equalsIgnoreCase(name)) {
                return inventory[i];
            }
        }
        return null;
    }

}
