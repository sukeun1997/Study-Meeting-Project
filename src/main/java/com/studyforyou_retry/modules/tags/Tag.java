package com.studyforyou_retry.modules.tags;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(of = "id") @Builder
public class Tag {

    @Id @GeneratedValue
    private Long id;

    private String title;
}
