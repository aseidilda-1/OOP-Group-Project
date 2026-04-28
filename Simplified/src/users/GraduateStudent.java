package users;

import exceptions.LowHIndexException;
import research.Researcher;

public class GraduateStudent extends Student {
    private Researcher supervisor;
    private String thesisTopic;

    public GraduateStudent(int id, String name, String email, String password, String thesisTopic) {
        super(id, name, email, password, "Graduate Research");
        this.thesisTopic = thesisTopic;
    }

    public Researcher getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Researcher supervisor) throws LowHIndexException {
        if (supervisor.calculateHIndex() < 3) {
            throw new LowHIndexException(supervisor.calculateHIndex());
        }
        this.supervisor = supervisor;
    }

    public String getThesisTopic() {
        return thesisTopic;
    }

    public void setThesisTopic(String thesisTopic) {
        this.thesisTopic = thesisTopic;
    }

    @Override
    public String getRole() {
        return "GraduateStudent";
    }
}
