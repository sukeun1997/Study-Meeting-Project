package com.studyforyou_retry.modules.tags;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Tag getNewTag(String tagTitle) {
        Tag tag = tagRepository.findByTitle(tagTitle);

        if (tag == null) {
            Tag build = Tag.builder().title(tagTitle).build();
            tag = tagRepository.save(build);
        }
        return tag;
    }
}
