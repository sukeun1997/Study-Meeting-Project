package com.studyforyou.tag;

import com.studyforyou.domain.Tag;
import com.studyforyou.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;


    @Transactional(readOnly = true)
    public Set<String> findByAllTags() {
        return tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toSet());
    }

    public Tag getTag(String tagTitle) {

        Tag tag = tagRepository.findByTitle(tagTitle);

        if (tag == null) {
            tag = tagRepository.save(Tag.builder().title(tagTitle).build());
        }
        return tag;
    }
}
