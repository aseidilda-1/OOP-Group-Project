package exceptions;

public class NotResearcherException extends UniversitySystemException {
    public NotResearcherException(String userName) {
        super(userName + " is not a researcher and cannot perform this action.");
    }
}
