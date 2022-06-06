package de.notartzt.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.notartzt.IntegrationTest;
import de.notartzt.domain.Shift;
import de.notartzt.domain.WorkLog;
import de.notartzt.repository.WorkLogRepository;
import de.notartzt.service.criteria.WorkLogCriteria;
import de.notartzt.service.dto.WorkLogDTO;
import de.notartzt.service.mapper.WorkLogMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WorkLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WorkLogResourceIT {

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final Integer DEFAULT_START_HOUR = 0;
    private static final Integer UPDATED_START_HOUR = 1;
    private static final Integer SMALLER_START_HOUR = 0 - 1;

    private static final Integer DEFAULT_START_MINUTE = 0;
    private static final Integer UPDATED_START_MINUTE = 1;
    private static final Integer SMALLER_START_MINUTE = 0 - 1;

    private static final Integer DEFAULT_END_HOUR = 0;
    private static final Integer UPDATED_END_HOUR = 1;
    private static final Integer SMALLER_END_HOUR = 0 - 1;

    private static final Integer DEFAULT_END_MINUTE = 0;
    private static final Integer UPDATED_END_MINUTE = 1;
    private static final Integer SMALLER_END_MINUTE = 0 - 1;

    private static final Boolean DEFAULT_OPTIONAL = false;
    private static final Boolean UPDATED_OPTIONAL = true;

    private static final String ENTITY_API_URL = "/api/work-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WorkLogRepository workLogRepository;

    @Autowired
    private WorkLogMapper workLogMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWorkLogMockMvc;

    private WorkLog workLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkLog createEntity(EntityManager em) {
        WorkLog workLog = new WorkLog()
            .note(DEFAULT_NOTE)
            .startHour(DEFAULT_START_HOUR)
            .startMinute(DEFAULT_START_MINUTE)
            .endHour(DEFAULT_END_HOUR)
            .endMinute(DEFAULT_END_MINUTE)
            .optional(DEFAULT_OPTIONAL);
        return workLog;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkLog createUpdatedEntity(EntityManager em) {
        WorkLog workLog = new WorkLog()
            .note(UPDATED_NOTE)
            .startHour(UPDATED_START_HOUR)
            .startMinute(UPDATED_START_MINUTE)
            .endHour(UPDATED_END_HOUR)
            .endMinute(UPDATED_END_MINUTE)
            .optional(UPDATED_OPTIONAL);
        return workLog;
    }

    @BeforeEach
    public void initTest() {
        workLog = createEntity(em);
    }

    @Test
    @Transactional
    void createWorkLog() throws Exception {
        int databaseSizeBeforeCreate = workLogRepository.findAll().size();
        // Create the WorkLog
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);
        restWorkLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLogDTO)))
            .andExpect(status().isCreated());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeCreate + 1);
        WorkLog testWorkLog = workLogList.get(workLogList.size() - 1);
        assertThat(testWorkLog.getNote()).isEqualTo(DEFAULT_NOTE);
        assertThat(testWorkLog.getStartHour()).isEqualTo(DEFAULT_START_HOUR);
        assertThat(testWorkLog.getStartMinute()).isEqualTo(DEFAULT_START_MINUTE);
        assertThat(testWorkLog.getEndHour()).isEqualTo(DEFAULT_END_HOUR);
        assertThat(testWorkLog.getEndMinute()).isEqualTo(DEFAULT_END_MINUTE);
        assertThat(testWorkLog.getOptional()).isEqualTo(DEFAULT_OPTIONAL);
    }

    @Test
    @Transactional
    void createWorkLogWithExistingId() throws Exception {
        // Create the WorkLog with an existing ID
        workLog.setId(1L);
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        int databaseSizeBeforeCreate = workLogRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLogDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStartHourIsRequired() throws Exception {
        int databaseSizeBeforeTest = workLogRepository.findAll().size();
        // set the field null
        workLog.setStartHour(null);

        // Create the WorkLog, which fails.
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        restWorkLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLogDTO)))
            .andExpect(status().isBadRequest());

        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartMinuteIsRequired() throws Exception {
        int databaseSizeBeforeTest = workLogRepository.findAll().size();
        // set the field null
        workLog.setStartMinute(null);

        // Create the WorkLog, which fails.
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        restWorkLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLogDTO)))
            .andExpect(status().isBadRequest());

        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndHourIsRequired() throws Exception {
        int databaseSizeBeforeTest = workLogRepository.findAll().size();
        // set the field null
        workLog.setEndHour(null);

        // Create the WorkLog, which fails.
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        restWorkLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLogDTO)))
            .andExpect(status().isBadRequest());

        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndMinuteIsRequired() throws Exception {
        int databaseSizeBeforeTest = workLogRepository.findAll().size();
        // set the field null
        workLog.setEndMinute(null);

        // Create the WorkLog, which fails.
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        restWorkLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLogDTO)))
            .andExpect(status().isBadRequest());

        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWorkLogs() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList
        restWorkLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].startHour").value(hasItem(DEFAULT_START_HOUR)))
            .andExpect(jsonPath("$.[*].startMinute").value(hasItem(DEFAULT_START_MINUTE)))
            .andExpect(jsonPath("$.[*].endHour").value(hasItem(DEFAULT_END_HOUR)))
            .andExpect(jsonPath("$.[*].endMinute").value(hasItem(DEFAULT_END_MINUTE)))
            .andExpect(jsonPath("$.[*].optional").value(hasItem(DEFAULT_OPTIONAL.booleanValue())));
    }

    @Test
    @Transactional
    void getWorkLog() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get the workLog
        restWorkLogMockMvc
            .perform(get(ENTITY_API_URL_ID, workLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(workLog.getId().intValue()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.startHour").value(DEFAULT_START_HOUR))
            .andExpect(jsonPath("$.startMinute").value(DEFAULT_START_MINUTE))
            .andExpect(jsonPath("$.endHour").value(DEFAULT_END_HOUR))
            .andExpect(jsonPath("$.endMinute").value(DEFAULT_END_MINUTE))
            .andExpect(jsonPath("$.optional").value(DEFAULT_OPTIONAL.booleanValue()));
    }

    @Test
    @Transactional
    void getWorkLogsByIdFiltering() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        Long id = workLog.getId();

        defaultWorkLogShouldBeFound("id.equals=" + id);
        defaultWorkLogShouldNotBeFound("id.notEquals=" + id);

        defaultWorkLogShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultWorkLogShouldNotBeFound("id.greaterThan=" + id);

        defaultWorkLogShouldBeFound("id.lessThanOrEqual=" + id);
        defaultWorkLogShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWorkLogsByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where note equals to DEFAULT_NOTE
        defaultWorkLogShouldBeFound("note.equals=" + DEFAULT_NOTE);

        // Get all the workLogList where note equals to UPDATED_NOTE
        defaultWorkLogShouldNotBeFound("note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByNoteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where note not equals to DEFAULT_NOTE
        defaultWorkLogShouldNotBeFound("note.notEquals=" + DEFAULT_NOTE);

        // Get all the workLogList where note not equals to UPDATED_NOTE
        defaultWorkLogShouldBeFound("note.notEquals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where note in DEFAULT_NOTE or UPDATED_NOTE
        defaultWorkLogShouldBeFound("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE);

        // Get all the workLogList where note equals to UPDATED_NOTE
        defaultWorkLogShouldNotBeFound("note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where note is not null
        defaultWorkLogShouldBeFound("note.specified=true");

        // Get all the workLogList where note is null
        defaultWorkLogShouldNotBeFound("note.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkLogsByNoteContainsSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where note contains DEFAULT_NOTE
        defaultWorkLogShouldBeFound("note.contains=" + DEFAULT_NOTE);

        // Get all the workLogList where note contains UPDATED_NOTE
        defaultWorkLogShouldNotBeFound("note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where note does not contain DEFAULT_NOTE
        defaultWorkLogShouldNotBeFound("note.doesNotContain=" + DEFAULT_NOTE);

        // Get all the workLogList where note does not contain UPDATED_NOTE
        defaultWorkLogShouldBeFound("note.doesNotContain=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByStartHourIsEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where startHour equals to DEFAULT_START_HOUR
        defaultWorkLogShouldBeFound("startHour.equals=" + DEFAULT_START_HOUR);

        // Get all the workLogList where startHour equals to UPDATED_START_HOUR
        defaultWorkLogShouldNotBeFound("startHour.equals=" + UPDATED_START_HOUR);
    }

    @Test
    @Transactional
    void getAllWorkLogsByStartHourIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where startHour not equals to DEFAULT_START_HOUR
        defaultWorkLogShouldNotBeFound("startHour.notEquals=" + DEFAULT_START_HOUR);

        // Get all the workLogList where startHour not equals to UPDATED_START_HOUR
        defaultWorkLogShouldBeFound("startHour.notEquals=" + UPDATED_START_HOUR);
    }

    @Test
    @Transactional
    void getAllWorkLogsByStartHourIsInShouldWork() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where startHour in DEFAULT_START_HOUR or UPDATED_START_HOUR
        defaultWorkLogShouldBeFound("startHour.in=" + DEFAULT_START_HOUR + "," + UPDATED_START_HOUR);

        // Get all the workLogList where startHour equals to UPDATED_START_HOUR
        defaultWorkLogShouldNotBeFound("startHour.in=" + UPDATED_START_HOUR);
    }

    @Test
    @Transactional
    void getAllWorkLogsByStartHourIsNullOrNotNull() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where startHour is not null
        defaultWorkLogShouldBeFound("startHour.specified=true");

        // Get all the workLogList where startHour is null
        defaultWorkLogShouldNotBeFound("startHour.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkLogsByStartHourIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where startHour is greater than or equal to DEFAULT_START_HOUR
        defaultWorkLogShouldBeFound("startHour.greaterThanOrEqual=" + DEFAULT_START_HOUR);

        // Get all the workLogList where startHour is greater than or equal to (DEFAULT_START_HOUR + 1)
        defaultWorkLogShouldNotBeFound("startHour.greaterThanOrEqual=" + (DEFAULT_START_HOUR + 1));
    }

    @Test
    @Transactional
    void getAllWorkLogsByStartHourIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where startHour is less than or equal to DEFAULT_START_HOUR
        defaultWorkLogShouldBeFound("startHour.lessThanOrEqual=" + DEFAULT_START_HOUR);

        // Get all the workLogList where startHour is less than or equal to SMALLER_START_HOUR
        defaultWorkLogShouldNotBeFound("startHour.lessThanOrEqual=" + SMALLER_START_HOUR);
    }

    @Test
    @Transactional
    void getAllWorkLogsByStartHourIsLessThanSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where startHour is less than DEFAULT_START_HOUR
        defaultWorkLogShouldNotBeFound("startHour.lessThan=" + DEFAULT_START_HOUR);

        // Get all the workLogList where startHour is less than (DEFAULT_START_HOUR + 1)
        defaultWorkLogShouldBeFound("startHour.lessThan=" + (DEFAULT_START_HOUR + 1));
    }

    @Test
    @Transactional
    void getAllWorkLogsByStartHourIsGreaterThanSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where startHour is greater than DEFAULT_START_HOUR
        defaultWorkLogShouldNotBeFound("startHour.greaterThan=" + DEFAULT_START_HOUR);

        // Get all the workLogList where startHour is greater than SMALLER_START_HOUR
        defaultWorkLogShouldBeFound("startHour.greaterThan=" + SMALLER_START_HOUR);
    }

    @Test
    @Transactional
    void getAllWorkLogsByStartMinuteIsEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where startMinute equals to DEFAULT_START_MINUTE
        defaultWorkLogShouldBeFound("startMinute.equals=" + DEFAULT_START_MINUTE);

        // Get all the workLogList where startMinute equals to UPDATED_START_MINUTE
        defaultWorkLogShouldNotBeFound("startMinute.equals=" + UPDATED_START_MINUTE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByStartMinuteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where startMinute not equals to DEFAULT_START_MINUTE
        defaultWorkLogShouldNotBeFound("startMinute.notEquals=" + DEFAULT_START_MINUTE);

        // Get all the workLogList where startMinute not equals to UPDATED_START_MINUTE
        defaultWorkLogShouldBeFound("startMinute.notEquals=" + UPDATED_START_MINUTE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByStartMinuteIsInShouldWork() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where startMinute in DEFAULT_START_MINUTE or UPDATED_START_MINUTE
        defaultWorkLogShouldBeFound("startMinute.in=" + DEFAULT_START_MINUTE + "," + UPDATED_START_MINUTE);

        // Get all the workLogList where startMinute equals to UPDATED_START_MINUTE
        defaultWorkLogShouldNotBeFound("startMinute.in=" + UPDATED_START_MINUTE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByStartMinuteIsNullOrNotNull() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where startMinute is not null
        defaultWorkLogShouldBeFound("startMinute.specified=true");

        // Get all the workLogList where startMinute is null
        defaultWorkLogShouldNotBeFound("startMinute.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkLogsByStartMinuteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where startMinute is greater than or equal to DEFAULT_START_MINUTE
        defaultWorkLogShouldBeFound("startMinute.greaterThanOrEqual=" + DEFAULT_START_MINUTE);

        // Get all the workLogList where startMinute is greater than or equal to (DEFAULT_START_MINUTE + 1)
        defaultWorkLogShouldNotBeFound("startMinute.greaterThanOrEqual=" + (DEFAULT_START_MINUTE + 1));
    }

    @Test
    @Transactional
    void getAllWorkLogsByStartMinuteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where startMinute is less than or equal to DEFAULT_START_MINUTE
        defaultWorkLogShouldBeFound("startMinute.lessThanOrEqual=" + DEFAULT_START_MINUTE);

        // Get all the workLogList where startMinute is less than or equal to SMALLER_START_MINUTE
        defaultWorkLogShouldNotBeFound("startMinute.lessThanOrEqual=" + SMALLER_START_MINUTE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByStartMinuteIsLessThanSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where startMinute is less than DEFAULT_START_MINUTE
        defaultWorkLogShouldNotBeFound("startMinute.lessThan=" + DEFAULT_START_MINUTE);

        // Get all the workLogList where startMinute is less than (DEFAULT_START_MINUTE + 1)
        defaultWorkLogShouldBeFound("startMinute.lessThan=" + (DEFAULT_START_MINUTE + 1));
    }

    @Test
    @Transactional
    void getAllWorkLogsByStartMinuteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where startMinute is greater than DEFAULT_START_MINUTE
        defaultWorkLogShouldNotBeFound("startMinute.greaterThan=" + DEFAULT_START_MINUTE);

        // Get all the workLogList where startMinute is greater than SMALLER_START_MINUTE
        defaultWorkLogShouldBeFound("startMinute.greaterThan=" + SMALLER_START_MINUTE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByEndHourIsEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where endHour equals to DEFAULT_END_HOUR
        defaultWorkLogShouldBeFound("endHour.equals=" + DEFAULT_END_HOUR);

        // Get all the workLogList where endHour equals to UPDATED_END_HOUR
        defaultWorkLogShouldNotBeFound("endHour.equals=" + UPDATED_END_HOUR);
    }

    @Test
    @Transactional
    void getAllWorkLogsByEndHourIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where endHour not equals to DEFAULT_END_HOUR
        defaultWorkLogShouldNotBeFound("endHour.notEquals=" + DEFAULT_END_HOUR);

        // Get all the workLogList where endHour not equals to UPDATED_END_HOUR
        defaultWorkLogShouldBeFound("endHour.notEquals=" + UPDATED_END_HOUR);
    }

    @Test
    @Transactional
    void getAllWorkLogsByEndHourIsInShouldWork() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where endHour in DEFAULT_END_HOUR or UPDATED_END_HOUR
        defaultWorkLogShouldBeFound("endHour.in=" + DEFAULT_END_HOUR + "," + UPDATED_END_HOUR);

        // Get all the workLogList where endHour equals to UPDATED_END_HOUR
        defaultWorkLogShouldNotBeFound("endHour.in=" + UPDATED_END_HOUR);
    }

    @Test
    @Transactional
    void getAllWorkLogsByEndHourIsNullOrNotNull() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where endHour is not null
        defaultWorkLogShouldBeFound("endHour.specified=true");

        // Get all the workLogList where endHour is null
        defaultWorkLogShouldNotBeFound("endHour.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkLogsByEndHourIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where endHour is greater than or equal to DEFAULT_END_HOUR
        defaultWorkLogShouldBeFound("endHour.greaterThanOrEqual=" + DEFAULT_END_HOUR);

        // Get all the workLogList where endHour is greater than or equal to (DEFAULT_END_HOUR + 1)
        defaultWorkLogShouldNotBeFound("endHour.greaterThanOrEqual=" + (DEFAULT_END_HOUR + 1));
    }

    @Test
    @Transactional
    void getAllWorkLogsByEndHourIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where endHour is less than or equal to DEFAULT_END_HOUR
        defaultWorkLogShouldBeFound("endHour.lessThanOrEqual=" + DEFAULT_END_HOUR);

        // Get all the workLogList where endHour is less than or equal to SMALLER_END_HOUR
        defaultWorkLogShouldNotBeFound("endHour.lessThanOrEqual=" + SMALLER_END_HOUR);
    }

    @Test
    @Transactional
    void getAllWorkLogsByEndHourIsLessThanSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where endHour is less than DEFAULT_END_HOUR
        defaultWorkLogShouldNotBeFound("endHour.lessThan=" + DEFAULT_END_HOUR);

        // Get all the workLogList where endHour is less than (DEFAULT_END_HOUR + 1)
        defaultWorkLogShouldBeFound("endHour.lessThan=" + (DEFAULT_END_HOUR + 1));
    }

    @Test
    @Transactional
    void getAllWorkLogsByEndHourIsGreaterThanSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where endHour is greater than DEFAULT_END_HOUR
        defaultWorkLogShouldNotBeFound("endHour.greaterThan=" + DEFAULT_END_HOUR);

        // Get all the workLogList where endHour is greater than SMALLER_END_HOUR
        defaultWorkLogShouldBeFound("endHour.greaterThan=" + SMALLER_END_HOUR);
    }

    @Test
    @Transactional
    void getAllWorkLogsByEndMinuteIsEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where endMinute equals to DEFAULT_END_MINUTE
        defaultWorkLogShouldBeFound("endMinute.equals=" + DEFAULT_END_MINUTE);

        // Get all the workLogList where endMinute equals to UPDATED_END_MINUTE
        defaultWorkLogShouldNotBeFound("endMinute.equals=" + UPDATED_END_MINUTE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByEndMinuteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where endMinute not equals to DEFAULT_END_MINUTE
        defaultWorkLogShouldNotBeFound("endMinute.notEquals=" + DEFAULT_END_MINUTE);

        // Get all the workLogList where endMinute not equals to UPDATED_END_MINUTE
        defaultWorkLogShouldBeFound("endMinute.notEquals=" + UPDATED_END_MINUTE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByEndMinuteIsInShouldWork() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where endMinute in DEFAULT_END_MINUTE or UPDATED_END_MINUTE
        defaultWorkLogShouldBeFound("endMinute.in=" + DEFAULT_END_MINUTE + "," + UPDATED_END_MINUTE);

        // Get all the workLogList where endMinute equals to UPDATED_END_MINUTE
        defaultWorkLogShouldNotBeFound("endMinute.in=" + UPDATED_END_MINUTE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByEndMinuteIsNullOrNotNull() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where endMinute is not null
        defaultWorkLogShouldBeFound("endMinute.specified=true");

        // Get all the workLogList where endMinute is null
        defaultWorkLogShouldNotBeFound("endMinute.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkLogsByEndMinuteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where endMinute is greater than or equal to DEFAULT_END_MINUTE
        defaultWorkLogShouldBeFound("endMinute.greaterThanOrEqual=" + DEFAULT_END_MINUTE);

        // Get all the workLogList where endMinute is greater than or equal to (DEFAULT_END_MINUTE + 1)
        defaultWorkLogShouldNotBeFound("endMinute.greaterThanOrEqual=" + (DEFAULT_END_MINUTE + 1));
    }

    @Test
    @Transactional
    void getAllWorkLogsByEndMinuteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where endMinute is less than or equal to DEFAULT_END_MINUTE
        defaultWorkLogShouldBeFound("endMinute.lessThanOrEqual=" + DEFAULT_END_MINUTE);

        // Get all the workLogList where endMinute is less than or equal to SMALLER_END_MINUTE
        defaultWorkLogShouldNotBeFound("endMinute.lessThanOrEqual=" + SMALLER_END_MINUTE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByEndMinuteIsLessThanSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where endMinute is less than DEFAULT_END_MINUTE
        defaultWorkLogShouldNotBeFound("endMinute.lessThan=" + DEFAULT_END_MINUTE);

        // Get all the workLogList where endMinute is less than (DEFAULT_END_MINUTE + 1)
        defaultWorkLogShouldBeFound("endMinute.lessThan=" + (DEFAULT_END_MINUTE + 1));
    }

    @Test
    @Transactional
    void getAllWorkLogsByEndMinuteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where endMinute is greater than DEFAULT_END_MINUTE
        defaultWorkLogShouldNotBeFound("endMinute.greaterThan=" + DEFAULT_END_MINUTE);

        // Get all the workLogList where endMinute is greater than SMALLER_END_MINUTE
        defaultWorkLogShouldBeFound("endMinute.greaterThan=" + SMALLER_END_MINUTE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByOptionalIsEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where optional equals to DEFAULT_OPTIONAL
        defaultWorkLogShouldBeFound("optional.equals=" + DEFAULT_OPTIONAL);

        // Get all the workLogList where optional equals to UPDATED_OPTIONAL
        defaultWorkLogShouldNotBeFound("optional.equals=" + UPDATED_OPTIONAL);
    }

    @Test
    @Transactional
    void getAllWorkLogsByOptionalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where optional not equals to DEFAULT_OPTIONAL
        defaultWorkLogShouldNotBeFound("optional.notEquals=" + DEFAULT_OPTIONAL);

        // Get all the workLogList where optional not equals to UPDATED_OPTIONAL
        defaultWorkLogShouldBeFound("optional.notEquals=" + UPDATED_OPTIONAL);
    }

    @Test
    @Transactional
    void getAllWorkLogsByOptionalIsInShouldWork() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where optional in DEFAULT_OPTIONAL or UPDATED_OPTIONAL
        defaultWorkLogShouldBeFound("optional.in=" + DEFAULT_OPTIONAL + "," + UPDATED_OPTIONAL);

        // Get all the workLogList where optional equals to UPDATED_OPTIONAL
        defaultWorkLogShouldNotBeFound("optional.in=" + UPDATED_OPTIONAL);
    }

    @Test
    @Transactional
    void getAllWorkLogsByOptionalIsNullOrNotNull() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where optional is not null
        defaultWorkLogShouldBeFound("optional.specified=true");

        // Get all the workLogList where optional is null
        defaultWorkLogShouldNotBeFound("optional.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkLogsByShiftIsEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);
        Shift shift;
        if (TestUtil.findAll(em, Shift.class).isEmpty()) {
            shift = ShiftResourceIT.createEntity(em);
            em.persist(shift);
            em.flush();
        } else {
            shift = TestUtil.findAll(em, Shift.class).get(0);
        }
        em.persist(shift);
        em.flush();
        workLog.setShift(shift);
        workLogRepository.saveAndFlush(workLog);
        Long shiftId = shift.getId();

        // Get all the workLogList where shift equals to shiftId
        defaultWorkLogShouldBeFound("shiftId.equals=" + shiftId);

        // Get all the workLogList where shift equals to (shiftId + 1)
        defaultWorkLogShouldNotBeFound("shiftId.equals=" + (shiftId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWorkLogShouldBeFound(String filter) throws Exception {
        restWorkLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].startHour").value(hasItem(DEFAULT_START_HOUR)))
            .andExpect(jsonPath("$.[*].startMinute").value(hasItem(DEFAULT_START_MINUTE)))
            .andExpect(jsonPath("$.[*].endHour").value(hasItem(DEFAULT_END_HOUR)))
            .andExpect(jsonPath("$.[*].endMinute").value(hasItem(DEFAULT_END_MINUTE)))
            .andExpect(jsonPath("$.[*].optional").value(hasItem(DEFAULT_OPTIONAL.booleanValue())));

        // Check, that the count call also returns 1
        restWorkLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWorkLogShouldNotBeFound(String filter) throws Exception {
        restWorkLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWorkLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWorkLog() throws Exception {
        // Get the workLog
        restWorkLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewWorkLog() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();

        // Update the workLog
        WorkLog updatedWorkLog = workLogRepository.findById(workLog.getId()).get();
        // Disconnect from session so that the updates on updatedWorkLog are not directly saved in db
        em.detach(updatedWorkLog);
        updatedWorkLog
            .note(UPDATED_NOTE)
            .startHour(UPDATED_START_HOUR)
            .startMinute(UPDATED_START_MINUTE)
            .endHour(UPDATED_END_HOUR)
            .endMinute(UPDATED_END_MINUTE)
            .optional(UPDATED_OPTIONAL);
        WorkLogDTO workLogDTO = workLogMapper.toDto(updatedWorkLog);

        restWorkLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workLogDTO))
            )
            .andExpect(status().isOk());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
        WorkLog testWorkLog = workLogList.get(workLogList.size() - 1);
        assertThat(testWorkLog.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testWorkLog.getStartHour()).isEqualTo(UPDATED_START_HOUR);
        assertThat(testWorkLog.getStartMinute()).isEqualTo(UPDATED_START_MINUTE);
        assertThat(testWorkLog.getEndHour()).isEqualTo(UPDATED_END_HOUR);
        assertThat(testWorkLog.getEndMinute()).isEqualTo(UPDATED_END_MINUTE);
        assertThat(testWorkLog.getOptional()).isEqualTo(UPDATED_OPTIONAL);
    }

    @Test
    @Transactional
    void putNonExistingWorkLog() throws Exception {
        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();
        workLog.setId(count.incrementAndGet());

        // Create the WorkLog
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWorkLog() throws Exception {
        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();
        workLog.setId(count.incrementAndGet());

        // Create the WorkLog
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWorkLog() throws Exception {
        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();
        workLog.setId(count.incrementAndGet());

        // Create the WorkLog
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWorkLogWithPatch() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();

        // Update the workLog using partial update
        WorkLog partialUpdatedWorkLog = new WorkLog();
        partialUpdatedWorkLog.setId(workLog.getId());

        partialUpdatedWorkLog.note(UPDATED_NOTE).startHour(UPDATED_START_HOUR).startMinute(UPDATED_START_MINUTE).optional(UPDATED_OPTIONAL);

        restWorkLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWorkLog))
            )
            .andExpect(status().isOk());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
        WorkLog testWorkLog = workLogList.get(workLogList.size() - 1);
        assertThat(testWorkLog.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testWorkLog.getStartHour()).isEqualTo(UPDATED_START_HOUR);
        assertThat(testWorkLog.getStartMinute()).isEqualTo(UPDATED_START_MINUTE);
        assertThat(testWorkLog.getEndHour()).isEqualTo(DEFAULT_END_HOUR);
        assertThat(testWorkLog.getEndMinute()).isEqualTo(DEFAULT_END_MINUTE);
        assertThat(testWorkLog.getOptional()).isEqualTo(UPDATED_OPTIONAL);
    }

    @Test
    @Transactional
    void fullUpdateWorkLogWithPatch() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();

        // Update the workLog using partial update
        WorkLog partialUpdatedWorkLog = new WorkLog();
        partialUpdatedWorkLog.setId(workLog.getId());

        partialUpdatedWorkLog
            .note(UPDATED_NOTE)
            .startHour(UPDATED_START_HOUR)
            .startMinute(UPDATED_START_MINUTE)
            .endHour(UPDATED_END_HOUR)
            .endMinute(UPDATED_END_MINUTE)
            .optional(UPDATED_OPTIONAL);

        restWorkLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWorkLog))
            )
            .andExpect(status().isOk());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
        WorkLog testWorkLog = workLogList.get(workLogList.size() - 1);
        assertThat(testWorkLog.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testWorkLog.getStartHour()).isEqualTo(UPDATED_START_HOUR);
        assertThat(testWorkLog.getStartMinute()).isEqualTo(UPDATED_START_MINUTE);
        assertThat(testWorkLog.getEndHour()).isEqualTo(UPDATED_END_HOUR);
        assertThat(testWorkLog.getEndMinute()).isEqualTo(UPDATED_END_MINUTE);
        assertThat(testWorkLog.getOptional()).isEqualTo(UPDATED_OPTIONAL);
    }

    @Test
    @Transactional
    void patchNonExistingWorkLog() throws Exception {
        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();
        workLog.setId(count.incrementAndGet());

        // Create the WorkLog
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, workLogDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWorkLog() throws Exception {
        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();
        workLog.setId(count.incrementAndGet());

        // Create the WorkLog
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWorkLog() throws Exception {
        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();
        workLog.setId(count.incrementAndGet());

        // Create the WorkLog
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(workLogDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWorkLog() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        int databaseSizeBeforeDelete = workLogRepository.findAll().size();

        // Delete the workLog
        restWorkLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, workLog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
