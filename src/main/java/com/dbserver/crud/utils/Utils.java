package com.dbserver.crud.utils;

import java.util.regex.Pattern;

public class Utils {
    protected Utils() {
    }

    public static final String REGEX_CPF = "^\\d{11}$";
    public static final String REGEX_CEP = "^\\d{8}$";
    public static final String REGEX_NUMERO_ENDERECO = "^\\d+$";

    public static boolean validarRegex(String regex, String conteudo) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(conteudo).matches();
    }

}
