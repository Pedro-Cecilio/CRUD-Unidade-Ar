package com.dbserver.crud.utils;

import java.util.regex.Pattern;

public class Utils {
    protected Utils(){}

    public static boolean validarCpf(String cpf){
        String regex = "\\b\\d{11}\\b";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(cpf).matches();
    }
}
