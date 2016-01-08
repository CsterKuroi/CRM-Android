package com.melnykov.fab.sample.tools;

public class CRMValidate {

    public static boolean isEmail(String email) {
        return ((email != null) && (email.length() > 0) && email.matches("^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$"));
    }

}
