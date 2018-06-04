package io.github.samirsamir.passwordkeeper.util;

public class TreatmentInjectionSQL {

    public String filter(String text){
        if(text == null || text.isEmpty()) {
            return "";
        }

        return text.replace("'", "''").trim();
    }

    public String reclaim(String text){
        if(text == null || text.isEmpty()) {
            return "";
        }

        return text.replace("''", "'").trim();
    }
}
