package io.github.samirsamir.passwordkeeper.util;

public class InjectionDefenceSQL {

    public String filter(String text){
        return text.replace("'", "''");
    }

    public String reclaim(String text){
        return text.replace("''", "'");
    }
}
