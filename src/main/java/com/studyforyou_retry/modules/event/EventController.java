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
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("study/{path}/")
public class EventController {

    public static final String EVENT_FORM = "event/form";
    private static final String EVENTS_EVENT_ID = "events/{eventId}/";
    private static final String ENROLLMENTS_ENROLL_ID = "enrollments/{enrollId}/";
    private final EventService eventService;
    private final StudyService studyService;

    @GetMapping("new-event")
    private String createEvent(@CurrentAccount Account account, @PathVariable String path, Model model) {

        Study study = studyService.getStudyWithManagersByManagers(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(new EventForm());

        return EVENT_FORM;
    }

    @PostMapping("new-event")
    private String createEvent(@CurrentAccount Account account, @Valid EventForm eventForm, BindingResult bindingResult, Model model, @PathVariable String path) {

        Study study = studyService.getStudyWithManagersByManagers(account, path);

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return EVENT_FORM;
        }
        Event event = eventService.createEvent(account, study, eventForm);
        return redirectEventView(event.getId(), study.getEncodePath(path));
    }


    @GetMapping("events/{eventId}")
    private String eventView(@CurrentAccount Account account, Model model, @PathVariable String path, @PathVariable("eventId") Event event) {

        Study study = studyService.getStudyWithManagers(path);
        return getEventView(account, event, model, study);
    }

    @PostMapping(EVENTS_EVENT_ID + "enroll")
    private String enrollEvent(@CurrentAccount Account account, @PathVariable("eventId") Event event, @PathVariable String path) {

        Study study = studyService.getOnlyStudyByPath(path);
        eventService.enrollEvent(account, event);

        return redirectEventView(event.getId(), study.getEncodePath(path));
    }

    @PostMapping(EVENTS_EVENT_ID + "disenroll")
    private String disenrollEvent(@CurrentAccount Account account, @PathVariable("eventId") Event event, @PathVariable String path) {

        Study study = studyService.getOnlyStudyByPath(path);
        eventService.disenrollEvent(account, event);

        return redirectEventView(event.getId(), study.getEncodePath(path));
    }

    @GetMapping(EVENTS_EVENT_ID + ENROLLMENTS_ENROLL_ID + "reject")
    private String rejectEnroll(@CurrentAccount Account account, @PathVariable("eventId") Event event, @PathVariable String path, @PathVariable("enrollId") Enrollment enrollment, Model model
    ) {
        Study study = studyService.getStudyWithManagersByManagers(account, path);
        eventService.rejectEnroll(event, enrollment);

        return getEventView(account, event, model, study);
    }


    @GetMapping(EVENTS_EVENT_ID + ENROLLMENTS_ENROLL_ID + "accept")
    private String acceptEnroll(@CurrentAccount Account account, @PathVariable("eventId") Event event, @PathVariable String path, @PathVariable("enrollId") Enrollment enrollment, Model model
    ) {
        Study study = studyService.getStudyWithManagersByManagers(account, path);
        eventService.acceptEnroll(event, enrollment);

        return getEventView(account, event, model, study);
    }

    @GetMapping(EVENTS_EVENT_ID + ENROLLMENTS_ENROLL_ID + "checkin")
    private String checkinEnroll(@CurrentAccount Account account, @PathVariable("eventId") Event event, @PathVariable String path, @PathVariable("enrollId") Enrollment enrollment, Model model) {

        Study study = studyService.getStudyWithManagersByManagers(account, path);
        eventService.checkinEnroll(enrollment);

        return getEventView(account, event, model, study);
    }
    @GetMapping(EVENTS_EVENT_ID + ENROLLMENTS_ENROLL_ID + "cancel-checkin")
    private String cancelCheckinEnroll(@CurrentAccount Account account, @PathVariable("eventId") Event event, @PathVariable String path, @PathVariable("enrollId") Enrollment enrollment, Model model) {

        Study study = studyService.getStudyWithManagersByManagers(account, path);
        eventService.cancelCheckinEnroll(enrollment);

        return getEventView(account, event, model, study);
    }

    private String getEventView(@CurrentAccount Account account, @PathVariable("eventId") Event event, Model model, Study study) {
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(event);
        return "event/view";
    }

    private String redirectEventView(Long id, String path) {
        return "redirect:/study/" + path + "/events/" + id;
    }

}
