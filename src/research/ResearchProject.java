package research;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ResearchProject implements Serializable {
    private final int id;
    private final String topic;
    private final Set<Researcher> participants;
    private final List<ResearchPaper> papers;
    private final LocalDate startDate;
    private LocalDate endDate;
    private double budget;

    public ResearchProject(int id, String topic, LocalDate startDate, LocalDate endDate, double budget) {
        this.id = id;
        this.topic = topic;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.participants = new LinkedHashSet<Researcher>();
        this.papers = new ArrayList<ResearchPaper>();
    }

    public int getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public Set<Researcher> getParticipants() {
        return participants;
    }

    public List<ResearchPaper> getPapers() {
        return papers;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public void addParticipant(Researcher researcher) {
        participants.add(researcher);
        researcher.addProject(this);
    }

    public void removeParticipant(Researcher researcher) {
        participants.remove(researcher);
    }

    public void addPaper(ResearchPaper paper) {
        papers.add(paper);
    }

    public String getStatus() {
        LocalDate today = LocalDate.now();
        if (endDate != null && today.isAfter(endDate)) {
            return "Completed";
        }
        if (today.isBefore(startDate)) {
            return "Planned";
        }
        return "Active";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResearchProject)) {
            return false;
        }
        ResearchProject that = (ResearchProject) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ResearchProject{id=" + id + ", topic='" + topic + "', participants=" + participants.size()
                + ", papers=" + papers.size() + ", status='" + getStatus() + "', budget=" + budget + "}";
    }
}
