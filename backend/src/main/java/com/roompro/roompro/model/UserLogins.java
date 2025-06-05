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
@Table(name = "user_logins")
public class UserLogins {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loginId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "login_timestamp", nullable = false)
    private LocalDateTime loginTimestamp;

}