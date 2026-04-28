package research;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import communication.Message;
import users.User;

public class ResearchDecorator extends User implements Researcher {
    private final User wrappedUser;
    private final List<ResearchPaper> papers;
    private final Set<ResearchProject> projects;

    public ResearchDecorator(User wrappedUser) {
        super(wrappedUser.getId(), wrappedUser.getName(), wrappedUser.getEmail(), "__decorated__");
        this.wrappedUser = wrappedUser;
        this.papers = new ArrayList<ResearchPaper>();
        this.projects = new LinkedHashSet<ResearchProject>();
    }

    public User getWrappedUser() {
        return wrappedUser;
    }

    @Override
    public String getName() {
        return wrappedUser.getName();
    }

    @Override
    public String getEmail() {
        return wrappedUser.getEmail();
    }

    @Override
    public void setName(String name) {
        wrappedUser.setName(name);
    }

    @Override
    public void setEmail(String email) {
        wrappedUser.setEmail(email);
    }

    @Override
    public void setPassword(String password) {
        wrappedUser.setPassword(password);
    }

    @Override
    public boolean checkPassword(String password) {
        return wrappedUser.checkPassword(password);
    }

    @Override
    public void receiveMessage(Message message) {
        wrappedUser.receiveMessage(message);
    }

    @Override
    public List<Message> getInbox() {
        return wrappedUser.getInbox();
    }

    @Override
    public void update(String event) {
        wrappedUser.update(event);
    }

    @Override
    public List<String> getNotifications() {
        return wrappedUser.getNotifications();
    }

    @Override
    public void clearNotifications() {
        wrappedUser.clearNotifications();
    }

    @Override
    public void subscribeToTopic(String topic) {
        wrappedUser.subscribeToTopic(topic);
    }

    @Override
    public void unsubscribeFromTopic(String topic) {
        wrappedUser.unsubscribeFromTopic(topic);
    }

    @Override
    public Set<String> getSubscriptions() {
        return wrappedUser.getSubscriptions();
    }

    @Override
    public String getRole() {
        return wrappedUser.getRole() + " + Researcher";
    }

    @Override
    public int calculateHIndex() {
        // TODO: full h-index calculation in final version
        // simplified for demonstration
        List<Integer> citationCounts = new ArrayList<Integer>();
        for (ResearchPaper paper : papers) {
            citationCounts.add(Integer.valueOf(paper.getCitations()));
        }
        Collections.sort(citationCounts, Collections.reverseOrder());

        int hIndex = 0;
        for (int i = 0; i < citationCounts.size(); i++) {
            if (citationCounts.get(i).intValue() >= i + 1) {
                hIndex = i + 1;
            }
        }
        return hIndex;
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> comparator) {
        // TODO: full paper sorting/printing in final version
        List<ResearchPaper> sortedPapers = new ArrayList<ResearchPaper>(papers);
        Collections.sort(sortedPapers, comparator);
        for (ResearchPaper paper : sortedPapers) {
            System.out.println(paper);
        }
    }

    @Override
    public void addPaper(ResearchPaper paper) {
        if (!papers.contains(paper)) {
            papers.add(paper);
        }
    }

    @Override
    public List<ResearchPaper> getPapers() {
        return papers;
    }

    @Override
    public void addProject(ResearchProject project) {
        projects.add(project);
    }

    @Override
    public Set<ResearchProject> getProjects() {
        return projects;
    }

    @Override
    public String getResearcherName() {
        return wrappedUser.getName();
    }

    @Override
    public String toString() {
        return "ResearchDecorator{wrappedUser=" + wrappedUser + ", hIndex=" + calculateHIndex() + ", papers="
                + papers.size() + ", projects=" + projects.size() + "}";
    }
}
