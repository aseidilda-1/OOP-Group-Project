package users;

import java.util.LinkedHashSet;
import java.util.Set;

import communication.TechSupportRequest;

public class TechSupportSpecialist extends Employee {
    private final Set<TechSupportRequest> handledRequests;

    public TechSupportSpecialist(int id, String name, String email, String password, String department) {
        super(id, name, email, password, department);
        this.handledRequests = new LinkedHashSet<TechSupportRequest>();
    }

    public Set<TechSupportRequest> getHandledRequests() {
        return handledRequests;
    }

    public void addHandledRequest(TechSupportRequest request) {
        handledRequests.add(request);
    }

    @Override
    public String getRole() {
        return "TechSupportSpecialist";
    }
}
