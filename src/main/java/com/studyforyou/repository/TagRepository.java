package com.studyforyou.repository;

import com.studyforyou.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag,Long> {

    Tag findByTitle(String title);
}
