package com.studyforyou_retry.modules.event;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.CurrentAccount;
import com.studyforyou_retry.modules.study.Study;
import com.studyforyou_retry.modules.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("study/{path}/")
public class EventController {

    public static final String EVENT_FORM = "event/form";
    private static final String EVENTS_EVENT_ID = "events/{eventId}/";
    private static final String ENROLLMENTS_ENROLL_ID = "enrollments/{enrollId}/";
    private final EventService eventService;
    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final EventFormValidator eventFormValidator;
    private final EventRepository eventRepository;

    @InitBinder("eventForm")
    private void eventFormValid(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventFormValidator);
    }

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

    @GetMapping("events")
    private String eventListView(@CurrentAccount Account account, @PathVariable String path, Model model) {


        Study study = studyService.getStudyAll(path);
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();

        eventRepository.findEventByStudyOrderByCreatedDateTime(study).stream().forEach(event -> {
            if (event.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldEvents.add(event);
            } else {
                newEvents.add(event);
            }
        });

        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);

        return "study/events";
    }


    @GetMapping("events/{eventId}")
    private String eventView(@CurrentAccount Account account, Model model, @PathVariable String path, @PathVariable("eventId") Event event) {

        Study study = studyService.getStudyWithManagers(path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(event);
        return "event/view";
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
    private String rejectEnroll(@CurrentAccount Account account, @PathVariable("eventId") Event event, @PathVariable String path, @PathVariable("enrollId") Enrollment enrollment
    ) {
        Study study = studyService.getStudyWithManagersByManagers(account, path);
        eventService.rejectEnroll(event, enrollment);

        return redirectEventView(event.getId(), study.getPath());
    }


    @GetMapping(EVENTS_EVENT_ID + ENROLLMENTS_ENROLL_ID + "accept")
    private String acceptEnroll(@CurrentAccount Account account, @PathVariable("eventId") Event event, @PathVariable String path, @PathVariable("enrollId") Enrollment enrollment
    ) {
        Study study = studyService.getStudyWithManagersByManagers(account, path);
        eventService.acceptEnroll(event, enrollment);

        return redirectEventView(event.getId(), study.getPath());
    }

    @GetMapping(EVENTS_EVENT_ID + ENROLLMENTS_ENROLL_ID + "checkin")
    private String checkinEnroll(@CurrentAccount Account account, @PathVariable("eventId") Event event, @PathVariable String path, @PathVariable("enrollId") Enrollment enrollment) {

        Study study = studyService.getStudyWithManagersByManagers(account, path);
        eventService.checkinEnroll(enrollment);

        return redirectEventView(event.getId(), study.getPath());
    }

    @GetMapping(EVENTS_EVENT_ID + ENROLLMENTS_ENROLL_ID + "cancel-checkin")
    private String cancelCheckinEnroll(@CurrentAccount Account account, @PathVariable("eventId") Event event, @PathVariable String path, @PathVariable("enrollId") Enrollment enrollment) {

        Study study = studyService.getStudyWithManagersByManagers(account, path);
        eventService.cancelCheckinEnroll(enrollment);

        return redirectEventView(event.getId(), study.getPath());
    }

    @GetMapping(EVENTS_EVENT_ID + "edit")
    private String updateEvent(@CurrentAccount Account account, @PathVariable("eventId") Event event, @PathVariable String path, Model model) {
        Study study = studyService.getStudyWithManagersByManagers(account, path);

        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(event);
        model.addAttribute(modelMapper.map(event, EventForm.class));
        return "event/updateform";
    }

    @PostMapping(EVENTS_EVENT_ID + "edit")
    private String updateEvent(@CurrentAccount Account account, @Valid EventForm eventForm, BindingResult bindingResult,
                               @PathVariable("eventId") Event event, @PathVariable String path, Model model) {

        Study study = studyService.getStudyWithManagersByManagers(account, path);

        if (bindingResult.hasErrors()) {
            return getUpdateErrorView(account, event, model, study);
        }

        if (eventForm.getLimitOfEnrollments() < event.acceptedCount()) {
            bindingResult.rejectValue("limitOfEnrollments", "wrong value", "현재 참석 모임 인원보다 적을 수 없습니다.");
            return getUpdateErrorView(account, event, model, study);
        }

        eventService.updateEvent(eventForm, event);
        return redirectEventView(event.getId(), study.getPath());
    }

    @DeleteMapping("events/{eventId}")
    private String deleteEvent(@CurrentAccount Account account, @PathVariable("eventId") Event event, @PathVariable String path) {

        Study study = studyService.getStudyWithManagersByManagers(account, path);
        eventService.deleteEvent(event);

        return "redirect:/study/" + study.getEncodePath(path) + "/events";
    }

    private String getUpdateErrorView(@CurrentAccount Account account, @PathVariable("eventId") Event event, Model model, Study study) {
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(event);
        return "event/updateform";
    }

    private String redirectEventView(Long id, String path) {
        return "redirect:/study/" + path + "/events/" + id;
    }

}
