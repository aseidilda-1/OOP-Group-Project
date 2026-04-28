package users;

public class Employee extends User {
    private String department;

    public Employee(int id, String name, String email, String password, String department) {
        super(id, name, email, password);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String getRole() {
        return "Employee";
    }

    @Override
    public String toString() {
        return super.toString() + ", department='" + department + "'";
    }
}
