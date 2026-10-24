package com.eduhi.bookstore.models;

import jakarta.persistence.*;

import java.io.Serializable;
//import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name= "TB_USERS")
public class UserModel implements Serializable {
    private static final long serialVersionUUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private UUID userID;

    @Column(nullable = false, unique = true)
    private String Name;

    @Column(nullable = false, unique = true)
    private String email;

//    @Column(updatable = false, nullable = false)
//    private Instant created;
//
//    @Column(nullable = false)
//    private Instant updated;

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
//
//    public Instant getCreated() {
//        return created;
//    }
//
//    public void setCreated(Instant created) {
//        this.created = created;
//    }
//
//    public Instant getUpdated() {
//        return updated;
//    }
//
//    public void setUpdated(Instant updated) {
//        this.updated = updated;
//    }
}
