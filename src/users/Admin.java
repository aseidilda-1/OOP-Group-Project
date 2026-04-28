package users;

public class Admin extends Employee {
    private int accessLevel;

    public Admin(int id, String name, String email, String password, String department, int accessLevel) {
        super(id, name, email, password, department);
        this.accessLevel = accessLevel;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public String getRole() {
        return "Admin";
    }
}
