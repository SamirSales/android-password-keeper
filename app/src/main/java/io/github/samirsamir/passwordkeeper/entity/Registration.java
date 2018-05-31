package io.github.samirsamir.passwordkeeper.entity;

public class Registration {

    private long id;
    private String login;
    private String password;

    public Registration(){
    }

    public Registration(String login, String password, RegistrationType registrationType) {
        this.login = login;
        this.password = password;
        this.registrationType = registrationType;
    }

    private RegistrationType registrationType;

    public RegistrationType getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(RegistrationType registrationType) {
        this.registrationType = registrationType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
