package com.studyforyou_retry.infra.config;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Table(name = "persistent_logins")
@Entity
@Getter @Setter
public class PersistentLogins {

    @Id
    @Column(length = 64)
    private String series;

    @NotNull
    @Column(length = 64)
    private String username;

    @NotNull
    @Column(length = 64)
    private String token;

    @NotNull
    @Column(name = "last_used")
    private LocalDateTime lastUsed;
}
