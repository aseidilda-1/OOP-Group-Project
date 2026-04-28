package research;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public interface Researcher extends Serializable {
    int calculateHIndex();
    void printPapers(Comparator<ResearchPaper> comparator);
    void addPaper(ResearchPaper paper);
    List<ResearchPaper> getPapers();
    void addProject(ResearchProject project);
    Set<ResearchProject> getProjects();
    String getResearcherName();
}
