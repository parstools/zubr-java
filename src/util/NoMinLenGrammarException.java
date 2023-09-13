package util;

public class NoMinLenGrammarException extends GrammarException {
    public NoMinLenGrammarException(String errorMessage) {
        super(errorMessage, new Throwable());
    }
}
