package com.dm.bomber.services.curl;

import java.util.LinkedList;
import java.util.List;

public class ArgumentTokenizer {
    private static final int NO_TOKEN_STATE = 0;
    private static final int NORMAL_TOKEN_STATE = 1;
    private static final int SINGLE_QUOTE_STATE = 2;
    private static final int DOUBLE_QUOTE_STATE = 3;

    public static List<String> tokenize(String arguments) {
        LinkedList<String> argList = new LinkedList<>();
        StringBuilder currArg = new StringBuilder();
        boolean escaped = false;
        int state = NO_TOKEN_STATE;
        int len = arguments.length();

        for (int i = 0; i < len; i++) {
            char c = arguments.charAt(i);
            if (escaped) {
                escaped = false;
                currArg.append(c);
            } else {
                switch (state) {
                    case SINGLE_QUOTE_STATE:
                        if (c == '\'') {
                            state = NORMAL_TOKEN_STATE;
                        } else {
                            currArg.append(c);
                        }
                        break;
                    case DOUBLE_QUOTE_STATE:
                        if (c == '"') {
                            state = NORMAL_TOKEN_STATE;
                        } else if (c == '\\') {
                            i++;
                            char next = arguments.charAt(i);
                            if (next != '"' && next != '\\') {
                                currArg.append(c);
                            }
                            currArg.append(next);
                        } else {
                            currArg.append(c);
                        }
                        break;
                    case NO_TOKEN_STATE:
                    case NORMAL_TOKEN_STATE:
                        switch (c) {
                            case '\\':
                                escaped = true;
                                state = NORMAL_TOKEN_STATE;
                                break;
                            case '\'':
                                state = SINGLE_QUOTE_STATE;
                                break;
                            case '"':
                                state = DOUBLE_QUOTE_STATE;
                                break;
                            default:
                                if (!Character.isWhitespace(c)) {
                                    currArg.append(c);
                                    state = NORMAL_TOKEN_STATE;
                                } else if (state == NORMAL_TOKEN_STATE) {
                                    argList.add(currArg.toString());
                                    currArg = new StringBuilder();
                                    state = NO_TOKEN_STATE;
                                }
                        }
                        break;
                    default:
                        throw new IllegalStateException("ArgumentTokenizer state " + state + " is invalid!");
                }
            }
        }

        if (escaped) {
            currArg.append('\\');
            argList.add(currArg.toString());
        } else if (state != NO_TOKEN_STATE) {
            argList.add(currArg.toString());
        }

        return argList;
    }
}