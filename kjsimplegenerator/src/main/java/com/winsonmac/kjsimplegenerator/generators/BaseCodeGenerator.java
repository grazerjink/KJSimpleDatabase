package com.winsonmac.kjsimplegenerator.generators;

public class BaseCodeGenerator {

    protected String ROOT_PACKAGE_NAME = "com.winsonmac.kjsimpledatabase";
    protected ProcessProvider provider;

    public BaseCodeGenerator(ProcessProvider processProvider) {
        this.provider = processProvider;
    }

    public String capitalize(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

}
