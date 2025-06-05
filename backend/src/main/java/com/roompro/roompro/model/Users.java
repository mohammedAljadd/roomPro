package com.roompro.roompro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long userId;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    @ManyToOne

    @JoinColumn(name = "role_id", nullable = false)
    private Role role;


    private LocalDateTime createdAt;

    private LocalDateTime lastLoginAt;

    private LocalDateTime updatedAt;


    @Column(nullable = false)
    private int loginCount = 0;

    @Column(nullable = false)
    private boolean isActive = true;




    @Override
    public String toString() {
        return "Users{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + (role != null ? role.getRoleId() : null) +  // Avoiding recursion by printing only roleId
                '}';
    }


}
