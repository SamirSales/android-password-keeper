package io.github.samirsamir.passwordkeeper.entity;

public class Registration {

    private long id;
    private String site;
    private String login;
    private String password;

    public Registration(String site, String login, String password, RegistrationType registrationType) {
        this.site = site;
        this.login = login;
        this.password = password;
        this.registrationType = registrationType;
    }

    public Registration(){
    }

    private RegistrationType registrationType;

    public RegistrationType getRegistrationType() {
        return registrationType;
    }

    public Registration getCopy(){
        Registration registration = new Registration();
        registration.setId(id);
        registration.setSite(site);
        registration.setPassword(password);
        registration.setRegistrationType(registrationType);
        return registration;
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

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
