package parstools.zubr.util;

public class GrammarException extends RuntimeException {
    public GrammarException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
