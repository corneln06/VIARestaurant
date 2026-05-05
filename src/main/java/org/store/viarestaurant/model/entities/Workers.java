package org.store.viarestaurant.model.entities;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.store.viarestaurant.model.enums.WorkerRole;

public abstract class Workers {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    //tesst
    private String password;

//    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Workers(String fn, String ln, String email, String rawPassword)
    {
        this.firstName = fn;
        this.lastName = ln;
        this.email = normlizeEmail(email);
        this.passwordHash = hashPassword(rawPassword);
        //test
        this.password = rawPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String normlizeEmail(String email)
    {
        return email == null ? null : email.trim().toLowerCase();
    }
    public String hashPassword(String rawPass)
    {
      //tets
//        return encoder.encode(rawPass);

      return rawPass;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = normlizeEmail(email);
    }

    public void setPasswordHash(String rawPass) {
//        this.passwordHash = encoder.encode(rawPass);
    }

    public boolean verifyPassword(String rawPass)
    {
        return password.equals(rawPass);
    }

    public abstract WorkerRole getRole();

    public abstract void setRole(WorkerRole role);
}

