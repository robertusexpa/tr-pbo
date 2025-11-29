package siasat.model;
public class Mahasiswa {
    private String nim;
    private String nama;
    private String password; // optional
    // getters & setters
    public String getNim(){return nim;}
    public void setNim(String n){this.nim=n;}
    public String getNama(){return nama;}
    public void setNama(String n){this.nama=n;}
    public String getPassword(){return password;}
    public void setPassword(String p){this.password=p;}
}
