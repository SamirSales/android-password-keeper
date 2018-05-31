package io.github.samirsamir.passwordkeeper.entity;

public enum RegistrationType {
    DEFAULT("DEFAULT"), APP_ACCESS("APP_ACCESS");

    private String type;

    RegistrationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static RegistrationType getUserType(String ut){
        if(ut.equals(APP_ACCESS.getType())){
            return APP_ACCESS;
        }

        return DEFAULT;
    }
}
