package com.cinebloom.authservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id; // same as Keycloak ID

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String bio;

    @Lob
    private byte[] profilePicture;
}