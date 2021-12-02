package com.studyforyou.event;

import com.studyforyou.account.CurrentAccount;
import com.studyforyou.domain.Account;
import com.studyforyou.domain.Enrollment;
import com.studyforyou.domain.Event;
import com.studyforyou.domain.Study;
import com.studyforyou.dto.EventForm;
import com.studyforyou.repository.EventRepository;
import com.studyforyou.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}")
public class EventController {

    private final StudyService studyService;
    private final EventFormValidator eventFormValidator;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventRepository eventRepository;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventFormValidator);
    }

    @GetMapping("/new-event")
    public String createEventView(@CurrentAccount Account account, Model model, @PathVariable String path) {

        Study study = studyService.getStudyWithManagers(account, path);

        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(new EventForm());

        return "/event/form";
    }

    @PostMapping("/new-event")
    public String createEvent(@CurrentAccount Account account, Model model, @PathVariable String path,
                              @Valid EventForm eventForm, BindingResult bindingResult) {

        Study study = studyService.getStudyWithManagers(account, path);

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "event/form";
        }

        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), account, study);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}")
    public String eventView(@CurrentAccount Account account, @PathVariable Long eventId, @PathVariable String path, Model model) {

        Study study = studyService.getEventStudy(path); // TODO TAG,ZONE,MEMBERS 불필요
        Event event = eventService.getEvent(eventId);

        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(event);

        return "event/view";
    }

    @GetMapping("/events")
    public String eventList(@CurrentAccount Account account, Model model, @PathVariable String path) {
        Study study = studyService.getStudy(path);

        model.addAttribute(account);
        model.addAttribute(study);

        Set<Event> allEvents = eventRepository.findByStudyOrderByStartDateTime(study);
        Set<Event> newEvents = allEvents.stream().filter(event -> event.getEndDateTime().isAfter(LocalDateTime.now())).collect(Collectors.toSet());
        Set<Event> oldEvents = allEvents.stream().filter(event -> event.getEndDateTime().isBefore(LocalDateTime.now())).collect(Collectors.toSet());
        //TODO newEvents, oldEvents Foreach 를 사용하여 조건에 따라 리스트에 추가하는식으로 리팩토링

        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);

        return "study/events";
    }

    @GetMapping("/events/{eventId}/edit")
    public String eventEdit(@CurrentAccount Account account, Model model, @PathVariable("eventId") Event event, @PathVariable String path) {

        Study study = studyService.getStudyWithManagers(account, path);

        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(event);
        model.addAttribute(modelMapper.map(event, EventForm.class));

        return "event/updateform";
    }

    @PostMapping("/events/{eventId}/edit")
    public String eventEdit(@CurrentAccount Account account, Model model, @PathVariable String path,
                            @Valid EventForm eventForm, BindingResult bindingResult, @PathVariable("eventId") Event event) {

        Study study = studyService.getStudyWithManagers(account, path);
        eventFormValidator.isValidEnrollmentSize(eventForm, event, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            model.addAttribute(event);
            return "event/updateform";
        }
        eventForm.setEventType(event.getEventType());
        eventService.updateForm(event, eventForm);


        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }

    @DeleteMapping("/events/{eventId}")
    public String eventDelete(@CurrentAccount Account account, @PathVariable("eventId") Event event, @PathVariable String path) {

        Study study = studyService.getStudyWithManagers(account, path);
        eventService.deleteEvent(event);
        return "redirect:/study/" + study.getEncodedPath() + "/events";

    }

    @PostMapping("/events/{eventId}/enroll")
    public String eventEnroll(@CurrentAccount Account account, @PathVariable("eventId") Event event, @PathVariable String path) {

        Study study = studyService.getOnlyStudyByPath(path);
        eventService.enrollEvent(account, event);


        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }

    @PostMapping("/events/{eventId}/disenroll")
    public String eventDisEnroll(@CurrentAccount Account account, @PathVariable("eventId") Event event, @PathVariable String path) {

        Study study = studyService.getOnlyStudyByPath(path);
        eventService.disenrollEvent(account, event);

        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/accept")
    public String acceptEnrollment(@CurrentAccount Account account ,@PathVariable String path, @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {

        Study study = studyService.getStudyWithManagers(account, path);
        eventService.acceptEnrollment(event,enrollment);

        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/reject")
    public String rejectEnrollment(@CurrentAccount Account account ,@PathVariable String path, @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {

        Study study = studyService.getStudyWithManagers(account, path);
        eventService.rejectEnrollment(event,enrollment);

        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/checkin")
    public String checkinEnrollment(@CurrentAccount Account account ,@PathVariable String path, @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {

        Study study = studyService.getStudyWithManagers(account, path);
        eventService.checkinEnrollment(event,enrollment);

        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }
    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/cancel-checkin")
    public String checkoutEnrollment(@CurrentAccount Account account ,@PathVariable String path, @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {

        Study study = studyService.getStudyWithManagers(account, path);
        eventService.checkoutEnrollment(event,enrollment);

        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }
}
