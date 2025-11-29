package siasat.model;

public class Dosen {
    private String nidn;
    private String nama;
    private String email;
    private String password; // optional, bisa null jika DB tidak punya kolom

    public Dosen() {}

    public Dosen(String nidn, String nama) {
        this.nidn = nidn;
        this.nama = nama;
    }

    public String getNidn() {
        return nidn;
    }

    public void setNidn(String nidn) {
        this.nidn = nidn;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Dosen{" +
                "nidn='" + nidn + '\'' +
                ", nama='" + nama + '\'' +
                '}';
    }
}
