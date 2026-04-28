package exceptions;

public class CreditLimitExceededException extends UniversitySystemException {
    public CreditLimitExceededException(int requestedCredits) {
        super("Credit limit exceeded. Requested total credits: " + requestedCredits + ", allowed maximum: 21.");
    }
}
