package com.studyforyou_retry.modules.event;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.CurrentAccount;
import com.studyforyou_retry.modules.study.Study;
import com.studyforyou_retry.modules.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class EventController {

    public static final String EVENT_FORM = "event/form";
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final StudyService studyService;

    @GetMapping("study/{path}/new-event")
    private String createEvent(@CurrentAccount Account account, @PathVariable String path, Model model) {

        Study study = studyService.getStudyWithManagers(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(new EventForm());

        return EVENT_FORM;
    }

    @PostMapping("study/{path}/new-event")
    private String createEvent(@CurrentAccount Account account, @Valid EventForm eventForm, BindingResult bindingResult,Model model, @PathVariable String path) {

        Study study = studyService.getStudyWithManagers(account, path);

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return EVENT_FORM;
        }
            Event event = eventService.createEvent(account, study, eventForm);
        return "redirect:/study/" + study.getEncodePath(path) + "/event/" + event.getId();
    }
}
