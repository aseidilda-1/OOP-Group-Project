package research;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import enums.CitationFormat;

public class ResearchPaper implements Serializable {
    private final int id;
    private final String title;
    private final List<Researcher> authors;
    private int citations;
    private final String journal;
    private final String pages;
    private final LocalDate date;
    private final String doi;

    public ResearchPaper(int id, String title, int citations, String journal, String pages, LocalDate date, String doi) {
        this.id = id;
        this.title = title;
        this.citations = citations;
        this.journal = journal;
        this.pages = pages;
        this.date = date;
        this.doi = doi;
        this.authors = new ArrayList<Researcher>();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Researcher> getAuthors() {
        return authors;
    }

    public int getCitations() {
        return citations;
    }

    public String getJournal() {
        return journal;
    }

    public String getPages() {
        return pages;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDoi() {
        return doi;
    }

    public void addAuthor(Researcher researcher) {
        authors.add(researcher);
    }

    public void incrementCitations() {
        // TODO: full citation tracking in final version
        citations++;
    }

    public String getCitation(CitationFormat format) {
        // TODO: full citation formatting in final version
        // simplified for demonstration
        StringJoiner joiner = new StringJoiner(", ");
        for (Researcher author : authors) {
            joiner.add(author.getResearcherName());
        }

        if (format == CitationFormat.BIBTEX) {
            return "@article{" + doi.replace("/", "_") + ", title={" + title + "}, author={" + joiner.toString()
                    + "}, journal={" + journal + "}, year={" + date.getYear() + "}, pages={" + pages + "}, doi={"
                    + doi + "}}";
        }

        return joiner.toString() + ". \"" + title + "\". " + journal + ", " + date.getYear() + ", pp. " + pages
                + ". DOI: " + doi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResearchPaper)) {
            return false;
        }
        ResearchPaper that = (ResearchPaper) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ResearchPaper{id=" + id + ", title='" + title + "', journal='" + journal + "', citations="
                + citations + ", doi='" + doi + "'}";
    }
}
