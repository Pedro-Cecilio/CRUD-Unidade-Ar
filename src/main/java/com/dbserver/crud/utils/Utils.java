package com.dbserver.crud.utils;

import java.util.regex.Pattern;

public class Utils {
    protected Utils() {
    }

    public static final String REGEX_CPF = "\\b\\d{11}\\b";
    public static final String REGEX_CEP = "^\\d{8}+$";
    public static final String REGEX_NUMERO_ENDERECO = "^\\d{8}+$";

    public static boolean validarRegex(String regex, String conteudo) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(conteudo).matches();
    }

}
