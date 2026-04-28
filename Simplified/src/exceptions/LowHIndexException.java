package exceptions;

public class LowHIndexException extends UniversitySystemException {
    public LowHIndexException(int hIndex) {
        super("Supervisor must have h-index >= 3. Current h-index: " + hIndex + ".");
    }
}
