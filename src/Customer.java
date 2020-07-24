public class Customer {

    private int id;
    private String firstName;
    private String lastName;
    private String ccId;
    private String address;
    private String email;
    private String password;

    public Customer(int id, String firstName, String lastName, String ccId,
                    String address, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ccId = ccId;
        this.address = address;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }
}
