package lr;

enum ActionKind {
    SHIFT,
    REDUCTION,
    ACCEPT,
    ERRPR,
}

public class Action {
    ActionKind kind;
    int number;
}