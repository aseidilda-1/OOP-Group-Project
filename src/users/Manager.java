package users;

public class Manager extends Employee {
    private String managedDepartment;

    public Manager(int id, String name, String email, String password, String department, String managedDepartment) {
        super(id, name, email, password, department);
        this.managedDepartment = managedDepartment;
    }

    public String getManagedDepartment() {
        return managedDepartment;
    }

    public void setManagedDepartment(String managedDepartment) {
        this.managedDepartment = managedDepartment;
    }

    @Override
    public String getRole() {
        return "Manager";
    }
}
