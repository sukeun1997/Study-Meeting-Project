package com.studyforyou_retry.modules.event;

import com.studyforyou_retry.infra.WithMockUser;
import com.studyforyou_retry.modules.account.*;
import com.studyforyou_retry.modules.study.Study;
import com.studyforyou_retry.modules.study.StudyFactory;
import com.studyforyou_retry.modules.study.StudyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
class EventControllerTest {

    private static final String STUDY_TEST_NEW_EVENT = "/study/test/new-event";
    private static final String STUDY_TEST_EVENTS = "/study/test/events/";
    @Autowired
    EventRepository eventRepository;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyFactory studyFactory;
    @Autowired
    AccountFactory accountFactory;

    @Autowired
    EventService eventService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    EventFactory eventFactory;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    MockMvc mockMvc;

    Account account;
    Study study;
    Event event;

    @BeforeEach
    private void init() {
        account = accountRepository.findByNickname("test");
        study = studyFactory.createStudy(account, "test");
        event = eventFactory.createEventFCFS(account, study, "test");
    }

    @AfterEach
    private void after() {
        eventRepository.deleteAll();
        studyRepository.deleteAll();
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 관리자 이외에 모임 생성 페이지 접근")
    void accessDeniedEvent() throws Exception {

        study.getManagers().clear();


        mockMvc.perform(get(STUDY_TEST_NEW_EVENT))
                .andExpect(view().name("error"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 생성 뷰")
    void createEventView() throws Exception {

        mockMvc.perform(get(STUDY_TEST_NEW_EVENT))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("eventForm"))
                .andExpect(status().isOk())
                .andExpect(view().name(EventController.EVENT_FORM));
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 생성 - 성공")
    void createEvent() throws Exception {

        mockMvc.perform(post(STUDY_TEST_NEW_EVENT)
                        .param("title", "test")
                        .param("limitOfEnrollments", "2")
                        .param("endEnrollmentDateTime", LocalDateTime.now().plusDays(2).toString())
                        .param("startDateTime", LocalDateTime.now().plusDays(1).toString())
                        .param("endDateTime", LocalDateTime.now().plusDays(3).toString())
                        .param("eventType", EventType.FCFS.toString())
                        .param("description", "test")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        assertTrue(eventRepository.existsByTitle("test"));

    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 생성 - 실패 ( 잘못된 입력 : limitOfEnrollments )")
    void createEvent_Fail_limitOfEnrollments() throws Exception {

        mockMvc.perform(post(STUDY_TEST_NEW_EVENT)
                        .param("title", "test")
                        .param("limitOfEnrollments", "1")
                        .param("endEnrollmentDateTime", LocalDateTime.now().plusDays(2).toString())
                        .param("startDateTime", LocalDateTime.now().plusDays(1).toString())
                        .param("endDateTime", LocalDateTime.now().plusDays(3).toString())
                        .param("eventType", EventType.FCFS.toString())
                        .param("description", "test")
                        .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name(EventController.EVENT_FORM));

        assertFalse(eventRepository.existsByTitle("test"));

    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 생성 - 실패 ( 잘못된 입력 : endEnrollmentDateTime )")
    void createEvent_Fail_endEnrollmentDateTime() throws Exception {

        mockMvc.perform(post(STUDY_TEST_NEW_EVENT)
                        .param("title", "test")
                        .param("limitOfEnrollments", "1")
                        .param("endEnrollmentDateTime", LocalDateTime.now().plusDays(1).toString())
                        .param("startDateTime", LocalDateTime.now().plusDays(1).toString())
                        .param("endDateTime", LocalDateTime.now().plusDays(3).toString())
                        .param("eventType", EventType.FCFS.toString())
                        .param("description", "test")
                        .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name(EventController.EVENT_FORM));

        assertFalse(eventRepository.existsByTitle("test"));

    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 생성 - 실패 ( 잘못된 입력 : startDateTime )")
    void createEvent_Fail_startDateTime() throws Exception {

        mockMvc.perform(post(STUDY_TEST_NEW_EVENT)
                        .param("title", "test")
                        .param("limitOfEnrollments", "1")
                        .param("endEnrollmentDateTime", LocalDateTime.now().plusDays(2).toString())
                        .param("startDateTime", LocalDateTime.now().minusHours(1).toString())
                        .param("endDateTime", LocalDateTime.now().plusDays(3).toString())
                        .param("eventType", EventType.FCFS.toString())
                        .param("description", "test")
                        .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name(EventController.EVENT_FORM));

        assertFalse(eventRepository.existsByTitle("test"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 생성 - 실패 ( 잘못된 입력 : endDateTime )")
    void createEvent_Fail_endDateTime() throws Exception {

        mockMvc.perform(post(STUDY_TEST_NEW_EVENT)
                        .param("title", "test")
                        .param("limitOfEnrollments", "1")
                        .param("endEnrollmentDateTime", LocalDateTime.now().plusDays(2).toString())
                        .param("startDateTime", LocalDateTime.now().plusDays(1).toString())
                        .param("endDateTime", LocalDateTime.now().plusDays(1).toString())
                        .param("eventType", EventType.FCFS.toString())
                        .param("description", "test")
                        .with(csrf()))
                .andExpect(model().hasErrors())
                .andDo(print())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name(EventController.EVENT_FORM));

        assertFalse(eventRepository.existsByTitle("test"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 상세 뷰")
    void eventDetailView() throws Exception {

        mockMvc.perform(get(STUDY_TEST_EVENTS + event.getId()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("event"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/view"));
    }


    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 목록 뷰 - 새 모임만 있을경우")
    void newEventListView() throws Exception {

        for (int i = 0; i < 5; i++) {
            eventFactory.createEventFCFS(account, study, UUID.randomUUID().toString());
        }

        assertTrue(eventRepository.count() == 6);

        mockMvc.perform(get("/study/test/events"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attribute("newEvents", eventRepository.findAll()))
                .andExpect(model().attribute("oldEvents", List.of()))
                .andExpect(status().isOk())
                .andExpect(view().name("study/events"))
                .andDo(print());

    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 목록 뷰 - 새 모임 , 지난 모임 같이 있는경우")
    void oldAndNewEventListView() throws Exception {

        List<Event> newEvent = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Event eventFCFS = eventFactory.createEventFCFS(account, study, UUID.randomUUID().toString());
            newEvent.add(eventFCFS);
        }
        event.setEndDateTime(LocalDateTime.now().minusHours(3));

        assertTrue(eventRepository.count() == 6);

        mockMvc.perform(get("/study/test/events"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attribute("newEvents", newEvent))
                .andExpect(model().attribute("oldEvents", List.of(event)))
                .andExpect(status().isOk())
                .andExpect(view().name("study/events"))
                .andDo(print());
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 [선착순] 모임 참가 신청 - 확정")
    void enrollEvent_Accept() throws Exception {

        mockMvc.perform(post(STUDY_TEST_EVENTS + event.getId() + "/enroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(STUDY_TEST_EVENTS + event.getId()));

        assertTrue(event.getEnrollments().size() == 1);
        assertTrue(event.isEnrollment(new UserAccount(account)));
        assertTrue(event.acceptedCount() == 1);
        assertTrue(!event.isAttended(new UserAccount(account)));
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 [선착순] 모임 참가 신청 - 대기중 ( 남은 인원 0 )")
    void enrollEvent_Wait() throws Exception {

        event.setLimitOfEnrollments(0);

        mockMvc.perform(post(STUDY_TEST_EVENTS + event.getId() + "/enroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(STUDY_TEST_EVENTS + event.getId()));

        assertTrue(event.getEnrollments().size() == 1);
        assertTrue(event.isEnrollment(new UserAccount(account)));
        assertTrue(event.acceptedCount() == 0);
        assertTrue(!event.isAttended(new UserAccount(account)));
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 [선착순] 모임 참가 신청 취소 - 변동사항 X")
    void disEnrollEvent() throws Exception {

        eventService.enrollEvent(account, event);

        assertTrue(event.getEnrollments().size() == 1);
        assertTrue(event.isEnrollment(new UserAccount(account)));
        assertTrue(event.acceptedCount() == 1);

        mockMvc.perform(post(STUDY_TEST_EVENTS + event.getId() + "/disenroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(STUDY_TEST_EVENTS + event.getId()));

        assertTrue(!enrollmentRepository.existsByEventAndAccount(event, account));
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 [선착순] 모임 참가 신청 취소 - 대기중 -> 확정 ")
    void disEnrollEvent_() throws Exception {

        eventService.enrollEvent(account, event);

        for (int i = 0; i < 4; i++) {
            Account test1 = accountFactory.createNewAccount("test" + i);
            eventService.enrollEvent(test1, event);
        }
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        assertTrue(event.acceptedCount() == 2);
        assertTrue(enrollment.isAccepted());
        assertTrue(event.getWaitingCount() == 3);

        mockMvc.perform(post(STUDY_TEST_EVENTS + event.getId() + "/disenroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(STUDY_TEST_EVENTS + event.getId()));

        assertTrue(!enrollmentRepository.existsByEventAndAccount(event, account));
        assertTrue(event.acceptedCount() == 2);
        assertTrue(event.getWaitingCount() == 2);
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 관리자에 의한 거부")
    void rejectEnroll() throws Exception {

        Account test1 = accountFactory.createNewAccount("test1");
        eventService.enrollEvent(test1, event);

        assertTrue(event.acceptedCount() == 1);

        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, test1);


        mockMvc.perform(get(STUDY_TEST_EVENTS + event.getId() + "/enrollments/" + enrollment.getId() + "/reject"))
                .andExpect(status().is3xxRedirection());

        assertTrue(event.acceptedCount() == 0);
        assertTrue(event.getWaitingCount() == 1);

    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 관리자에 의한 승인")
    void acceptEnroll() throws Exception {

        Account test1 = accountFactory.createNewAccount("test1");
        eventService.enrollEvent(test1, event);

        assertTrue(event.acceptedCount() == 1);

        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, test1);
        eventService.rejectEnroll(event, enrollment);

        assertTrue(event.acceptedCount() == 0);
        assertTrue(event.getWaitingCount() == 1);

        mockMvc.perform(get(getStatusEnrollUrl(enrollment) + "/accept"))
                .andExpect(status().is3xxRedirection());

        assertTrue(event.acceptedCount() == 1);
        assertTrue(event.getWaitingCount() == 0);

    }


    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 체크인")
    void checkinEnroll() throws Exception {

        Account test1 = accountFactory.createNewAccount("test1");
        eventService.enrollEvent(test1, event);

        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, test1);

        mockMvc.perform(get(getStatusEnrollUrl(enrollment)+ "/checkin"))
                .andExpect(status().is3xxRedirection());

        assertTrue(enrollment.isAttended());

    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 체크인 취소")
    void cancelCheckinEnroll() throws Exception {

        Account test1 = accountFactory.createNewAccount("test1");
        eventService.enrollEvent(test1, event);

        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, test1);
        eventService.checkinEnroll(enrollment);

        assertTrue(enrollment.isAttended());
        assertTrue(enrollment.isAccepted());

        mockMvc.perform(get(getStatusEnrollUrl(enrollment)+ "/cancel-checkin"))
                .andExpect(status().is3xxRedirection());

        assertTrue(!enrollment.isAttended());
        assertTrue(enrollment.isAccepted());
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 정보 수정 폼")
    void updateEvent() throws Exception {

        assertTrue(study.isManager(new UserAccount(account)));

        mockMvc.perform(get(STUDY_TEST_EVENTS + event.getId() + "/edit"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("eventForm"))
                .andExpect(view().name("event/updateform"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 정보 수정 - 성공")
    void updateEvent_Success() throws Exception {

        assertTrue(study.isManager(new UserAccount(account)));

        mockMvc.perform(post(STUDY_TEST_EVENTS + event.getId() + "/edit")
                        .param("title", event.getTitle()+"asd")
                        .param("limitOfEnrollments", String.valueOf(event.getLimitOfEnrollments()))
                        .param("startDateTime", event.getStartDateTime().toString())
                        .param("endEnrollmentDateTime", event.getEndEnrollmentDateTime().toString())
                        .param("endDateTime", event.getEndDateTime().toString())
                        .param("description", event.getDescription())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 정보 수정 - 성공 ( 인원 늘린 후 선착순 대기인원 자동 확정 )")
    void updateEvent_Success_limitOfEnrollments() throws Exception {

        assertTrue(study.isManager(new UserAccount(account)));

        for (int i = 0; i < 4; i++) {
            Account newAccount = accountFactory.createNewAccount("test" + i);
            eventService.enrollEvent(newAccount,event);
        }

        assertTrue(event.getWaitingCount() == 2);
        assertTrue(event.acceptedCount() == 2);

        mockMvc.perform(post(STUDY_TEST_EVENTS + event.getId() + "/edit")
                        .param("title", event.getTitle()+"asd")
                        .param("limitOfEnrollments", String.valueOf(event.getLimitOfEnrollments() + 2))
                        .param("startDateTime", event.getStartDateTime().toString())
                        .param("endEnrollmentDateTime", event.getEndEnrollmentDateTime().toString())
                        .param("endDateTime", event.getEndDateTime().toString())
                        .param("description", event.getDescription())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        assertTrue(event.getWaitingCount() == 0);
        assertTrue(event.acceptedCount() == 4);
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 정보 수정 - 실패 ( 기존 인원수 보다 줄임 )")
    void updateEvent_Fail_limitOfEnrollments() throws Exception {

        assertTrue(study.isManager(new UserAccount(account)));

        event.setLimitOfEnrollments(3);

        for (int i = 0; i < 3; i++) {
            Account newAccount = accountFactory.createNewAccount("test" + i);
            eventService.enrollEvent(newAccount,event);
        }
        assertTrue(event.getWaitingCount() == 0);

        mockMvc.perform(post(STUDY_TEST_EVENTS + event.getId() + "/edit")
                        .param("title", event.getTitle()+"asd")
                        .param("limitOfEnrollments", String.valueOf(event.getLimitOfEnrollments()-1))
                        .param("startDateTime", event.getStartDateTime().toString())
                        .param("endEnrollmentDateTime", event.getEndEnrollmentDateTime().toString())
                        .param("endDateTime", event.getEndDateTime().toString())
                        .param("description", event.getDescription())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name("event/updateform"));

        assertTrue(event.getWaitingCount() == 0);
        assertTrue(event.getLimitOfEnrollments() == 3);
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 모임 삭제")
    void deleteEvent() throws Exception {

        assertTrue(study.isManager(new UserAccount(account)));

        mockMvc.perform(delete(STUDY_TEST_EVENTS + event.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        assertTrue(eventRepository.findById(event.getId()).isEmpty());
    }

    private String getStatusEnrollUrl(Enrollment enrollment) {
        return STUDY_TEST_EVENTS + event.getId() + "/enrollments/" + enrollment.getId();
    }


}