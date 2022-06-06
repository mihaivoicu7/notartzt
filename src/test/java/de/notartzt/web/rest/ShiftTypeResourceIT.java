package de.notartzt.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.notartzt.IntegrationTest;
import de.notartzt.domain.Location;
import de.notartzt.domain.Shift;
import de.notartzt.domain.ShiftType;
import de.notartzt.repository.ShiftTypeRepository;
import de.notartzt.service.criteria.ShiftTypeCriteria;
import de.notartzt.service.dto.ShiftTypeDTO;
import de.notartzt.service.mapper.ShiftTypeMapper;
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
 * Integration tests for the {@link ShiftTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShiftTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/shift-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ShiftTypeRepository shiftTypeRepository;

    @Autowired
    private ShiftTypeMapper shiftTypeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShiftTypeMockMvc;

    private ShiftType shiftType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShiftType createEntity(EntityManager em) {
        ShiftType shiftType = new ShiftType()
            .name(DEFAULT_NAME)
            .startHour(DEFAULT_START_HOUR)
            .startMinute(DEFAULT_START_MINUTE)
            .endHour(DEFAULT_END_HOUR)
            .endMinute(DEFAULT_END_MINUTE);
        return shiftType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShiftType createUpdatedEntity(EntityManager em) {
        ShiftType shiftType = new ShiftType()
            .name(UPDATED_NAME)
            .startHour(UPDATED_START_HOUR)
            .startMinute(UPDATED_START_MINUTE)
            .endHour(UPDATED_END_HOUR)
            .endMinute(UPDATED_END_MINUTE);
        return shiftType;
    }

    @BeforeEach
    public void initTest() {
        shiftType = createEntity(em);
    }

    @Test
    @Transactional
    void createShiftType() throws Exception {
        int databaseSizeBeforeCreate = shiftTypeRepository.findAll().size();
        // Create the ShiftType
        ShiftTypeDTO shiftTypeDTO = shiftTypeMapper.toDto(shiftType);
        restShiftTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shiftTypeDTO)))
            .andExpect(status().isCreated());

        // Validate the ShiftType in the database
        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeCreate + 1);
        ShiftType testShiftType = shiftTypeList.get(shiftTypeList.size() - 1);
        assertThat(testShiftType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testShiftType.getStartHour()).isEqualTo(DEFAULT_START_HOUR);
        assertThat(testShiftType.getStartMinute()).isEqualTo(DEFAULT_START_MINUTE);
        assertThat(testShiftType.getEndHour()).isEqualTo(DEFAULT_END_HOUR);
        assertThat(testShiftType.getEndMinute()).isEqualTo(DEFAULT_END_MINUTE);
    }

    @Test
    @Transactional
    void createShiftTypeWithExistingId() throws Exception {
        // Create the ShiftType with an existing ID
        shiftType.setId(1L);
        ShiftTypeDTO shiftTypeDTO = shiftTypeMapper.toDto(shiftType);

        int databaseSizeBeforeCreate = shiftTypeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShiftTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shiftTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ShiftType in the database
        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = shiftTypeRepository.findAll().size();
        // set the field null
        shiftType.setName(null);

        // Create the ShiftType, which fails.
        ShiftTypeDTO shiftTypeDTO = shiftTypeMapper.toDto(shiftType);

        restShiftTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shiftTypeDTO)))
            .andExpect(status().isBadRequest());

        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartHourIsRequired() throws Exception {
        int databaseSizeBeforeTest = shiftTypeRepository.findAll().size();
        // set the field null
        shiftType.setStartHour(null);

        // Create the ShiftType, which fails.
        ShiftTypeDTO shiftTypeDTO = shiftTypeMapper.toDto(shiftType);

        restShiftTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shiftTypeDTO)))
            .andExpect(status().isBadRequest());

        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartMinuteIsRequired() throws Exception {
        int databaseSizeBeforeTest = shiftTypeRepository.findAll().size();
        // set the field null
        shiftType.setStartMinute(null);

        // Create the ShiftType, which fails.
        ShiftTypeDTO shiftTypeDTO = shiftTypeMapper.toDto(shiftType);

        restShiftTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shiftTypeDTO)))
            .andExpect(status().isBadRequest());

        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndHourIsRequired() throws Exception {
        int databaseSizeBeforeTest = shiftTypeRepository.findAll().size();
        // set the field null
        shiftType.setEndHour(null);

        // Create the ShiftType, which fails.
        ShiftTypeDTO shiftTypeDTO = shiftTypeMapper.toDto(shiftType);

        restShiftTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shiftTypeDTO)))
            .andExpect(status().isBadRequest());

        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndMinuteIsRequired() throws Exception {
        int databaseSizeBeforeTest = shiftTypeRepository.findAll().size();
        // set the field null
        shiftType.setEndMinute(null);

        // Create the ShiftType, which fails.
        ShiftTypeDTO shiftTypeDTO = shiftTypeMapper.toDto(shiftType);

        restShiftTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shiftTypeDTO)))
            .andExpect(status().isBadRequest());

        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllShiftTypes() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList
        restShiftTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shiftType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].startHour").value(hasItem(DEFAULT_START_HOUR)))
            .andExpect(jsonPath("$.[*].startMinute").value(hasItem(DEFAULT_START_MINUTE)))
            .andExpect(jsonPath("$.[*].endHour").value(hasItem(DEFAULT_END_HOUR)))
            .andExpect(jsonPath("$.[*].endMinute").value(hasItem(DEFAULT_END_MINUTE)));
    }

    @Test
    @Transactional
    void getShiftType() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get the shiftType
        restShiftTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, shiftType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shiftType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.startHour").value(DEFAULT_START_HOUR))
            .andExpect(jsonPath("$.startMinute").value(DEFAULT_START_MINUTE))
            .andExpect(jsonPath("$.endHour").value(DEFAULT_END_HOUR))
            .andExpect(jsonPath("$.endMinute").value(DEFAULT_END_MINUTE));
    }

    @Test
    @Transactional
    void getShiftTypesByIdFiltering() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        Long id = shiftType.getId();

        defaultShiftTypeShouldBeFound("id.equals=" + id);
        defaultShiftTypeShouldNotBeFound("id.notEquals=" + id);

        defaultShiftTypeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultShiftTypeShouldNotBeFound("id.greaterThan=" + id);

        defaultShiftTypeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultShiftTypeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllShiftTypesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where name equals to DEFAULT_NAME
        defaultShiftTypeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the shiftTypeList where name equals to UPDATED_NAME
        defaultShiftTypeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllShiftTypesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where name not equals to DEFAULT_NAME
        defaultShiftTypeShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the shiftTypeList where name not equals to UPDATED_NAME
        defaultShiftTypeShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllShiftTypesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultShiftTypeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the shiftTypeList where name equals to UPDATED_NAME
        defaultShiftTypeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllShiftTypesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where name is not null
        defaultShiftTypeShouldBeFound("name.specified=true");

        // Get all the shiftTypeList where name is null
        defaultShiftTypeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllShiftTypesByNameContainsSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where name contains DEFAULT_NAME
        defaultShiftTypeShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the shiftTypeList where name contains UPDATED_NAME
        defaultShiftTypeShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllShiftTypesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where name does not contain DEFAULT_NAME
        defaultShiftTypeShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the shiftTypeList where name does not contain UPDATED_NAME
        defaultShiftTypeShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllShiftTypesByStartHourIsEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where startHour equals to DEFAULT_START_HOUR
        defaultShiftTypeShouldBeFound("startHour.equals=" + DEFAULT_START_HOUR);

        // Get all the shiftTypeList where startHour equals to UPDATED_START_HOUR
        defaultShiftTypeShouldNotBeFound("startHour.equals=" + UPDATED_START_HOUR);
    }

    @Test
    @Transactional
    void getAllShiftTypesByStartHourIsNotEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where startHour not equals to DEFAULT_START_HOUR
        defaultShiftTypeShouldNotBeFound("startHour.notEquals=" + DEFAULT_START_HOUR);

        // Get all the shiftTypeList where startHour not equals to UPDATED_START_HOUR
        defaultShiftTypeShouldBeFound("startHour.notEquals=" + UPDATED_START_HOUR);
    }

    @Test
    @Transactional
    void getAllShiftTypesByStartHourIsInShouldWork() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where startHour in DEFAULT_START_HOUR or UPDATED_START_HOUR
        defaultShiftTypeShouldBeFound("startHour.in=" + DEFAULT_START_HOUR + "," + UPDATED_START_HOUR);

        // Get all the shiftTypeList where startHour equals to UPDATED_START_HOUR
        defaultShiftTypeShouldNotBeFound("startHour.in=" + UPDATED_START_HOUR);
    }

    @Test
    @Transactional
    void getAllShiftTypesByStartHourIsNullOrNotNull() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where startHour is not null
        defaultShiftTypeShouldBeFound("startHour.specified=true");

        // Get all the shiftTypeList where startHour is null
        defaultShiftTypeShouldNotBeFound("startHour.specified=false");
    }

    @Test
    @Transactional
    void getAllShiftTypesByStartHourIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where startHour is greater than or equal to DEFAULT_START_HOUR
        defaultShiftTypeShouldBeFound("startHour.greaterThanOrEqual=" + DEFAULT_START_HOUR);

        // Get all the shiftTypeList where startHour is greater than or equal to (DEFAULT_START_HOUR + 1)
        defaultShiftTypeShouldNotBeFound("startHour.greaterThanOrEqual=" + (DEFAULT_START_HOUR + 1));
    }

    @Test
    @Transactional
    void getAllShiftTypesByStartHourIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where startHour is less than or equal to DEFAULT_START_HOUR
        defaultShiftTypeShouldBeFound("startHour.lessThanOrEqual=" + DEFAULT_START_HOUR);

        // Get all the shiftTypeList where startHour is less than or equal to SMALLER_START_HOUR
        defaultShiftTypeShouldNotBeFound("startHour.lessThanOrEqual=" + SMALLER_START_HOUR);
    }

    @Test
    @Transactional
    void getAllShiftTypesByStartHourIsLessThanSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where startHour is less than DEFAULT_START_HOUR
        defaultShiftTypeShouldNotBeFound("startHour.lessThan=" + DEFAULT_START_HOUR);

        // Get all the shiftTypeList where startHour is less than (DEFAULT_START_HOUR + 1)
        defaultShiftTypeShouldBeFound("startHour.lessThan=" + (DEFAULT_START_HOUR + 1));
    }

    @Test
    @Transactional
    void getAllShiftTypesByStartHourIsGreaterThanSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where startHour is greater than DEFAULT_START_HOUR
        defaultShiftTypeShouldNotBeFound("startHour.greaterThan=" + DEFAULT_START_HOUR);

        // Get all the shiftTypeList where startHour is greater than SMALLER_START_HOUR
        defaultShiftTypeShouldBeFound("startHour.greaterThan=" + SMALLER_START_HOUR);
    }

    @Test
    @Transactional
    void getAllShiftTypesByStartMinuteIsEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where startMinute equals to DEFAULT_START_MINUTE
        defaultShiftTypeShouldBeFound("startMinute.equals=" + DEFAULT_START_MINUTE);

        // Get all the shiftTypeList where startMinute equals to UPDATED_START_MINUTE
        defaultShiftTypeShouldNotBeFound("startMinute.equals=" + UPDATED_START_MINUTE);
    }

    @Test
    @Transactional
    void getAllShiftTypesByStartMinuteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where startMinute not equals to DEFAULT_START_MINUTE
        defaultShiftTypeShouldNotBeFound("startMinute.notEquals=" + DEFAULT_START_MINUTE);

        // Get all the shiftTypeList where startMinute not equals to UPDATED_START_MINUTE
        defaultShiftTypeShouldBeFound("startMinute.notEquals=" + UPDATED_START_MINUTE);
    }

    @Test
    @Transactional
    void getAllShiftTypesByStartMinuteIsInShouldWork() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where startMinute in DEFAULT_START_MINUTE or UPDATED_START_MINUTE
        defaultShiftTypeShouldBeFound("startMinute.in=" + DEFAULT_START_MINUTE + "," + UPDATED_START_MINUTE);

        // Get all the shiftTypeList where startMinute equals to UPDATED_START_MINUTE
        defaultShiftTypeShouldNotBeFound("startMinute.in=" + UPDATED_START_MINUTE);
    }

    @Test
    @Transactional
    void getAllShiftTypesByStartMinuteIsNullOrNotNull() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where startMinute is not null
        defaultShiftTypeShouldBeFound("startMinute.specified=true");

        // Get all the shiftTypeList where startMinute is null
        defaultShiftTypeShouldNotBeFound("startMinute.specified=false");
    }

    @Test
    @Transactional
    void getAllShiftTypesByStartMinuteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where startMinute is greater than or equal to DEFAULT_START_MINUTE
        defaultShiftTypeShouldBeFound("startMinute.greaterThanOrEqual=" + DEFAULT_START_MINUTE);

        // Get all the shiftTypeList where startMinute is greater than or equal to (DEFAULT_START_MINUTE + 1)
        defaultShiftTypeShouldNotBeFound("startMinute.greaterThanOrEqual=" + (DEFAULT_START_MINUTE + 1));
    }

    @Test
    @Transactional
    void getAllShiftTypesByStartMinuteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where startMinute is less than or equal to DEFAULT_START_MINUTE
        defaultShiftTypeShouldBeFound("startMinute.lessThanOrEqual=" + DEFAULT_START_MINUTE);

        // Get all the shiftTypeList where startMinute is less than or equal to SMALLER_START_MINUTE
        defaultShiftTypeShouldNotBeFound("startMinute.lessThanOrEqual=" + SMALLER_START_MINUTE);
    }

    @Test
    @Transactional
    void getAllShiftTypesByStartMinuteIsLessThanSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where startMinute is less than DEFAULT_START_MINUTE
        defaultShiftTypeShouldNotBeFound("startMinute.lessThan=" + DEFAULT_START_MINUTE);

        // Get all the shiftTypeList where startMinute is less than (DEFAULT_START_MINUTE + 1)
        defaultShiftTypeShouldBeFound("startMinute.lessThan=" + (DEFAULT_START_MINUTE + 1));
    }

    @Test
    @Transactional
    void getAllShiftTypesByStartMinuteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where startMinute is greater than DEFAULT_START_MINUTE
        defaultShiftTypeShouldNotBeFound("startMinute.greaterThan=" + DEFAULT_START_MINUTE);

        // Get all the shiftTypeList where startMinute is greater than SMALLER_START_MINUTE
        defaultShiftTypeShouldBeFound("startMinute.greaterThan=" + SMALLER_START_MINUTE);
    }

    @Test
    @Transactional
    void getAllShiftTypesByEndHourIsEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where endHour equals to DEFAULT_END_HOUR
        defaultShiftTypeShouldBeFound("endHour.equals=" + DEFAULT_END_HOUR);

        // Get all the shiftTypeList where endHour equals to UPDATED_END_HOUR
        defaultShiftTypeShouldNotBeFound("endHour.equals=" + UPDATED_END_HOUR);
    }

    @Test
    @Transactional
    void getAllShiftTypesByEndHourIsNotEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where endHour not equals to DEFAULT_END_HOUR
        defaultShiftTypeShouldNotBeFound("endHour.notEquals=" + DEFAULT_END_HOUR);

        // Get all the shiftTypeList where endHour not equals to UPDATED_END_HOUR
        defaultShiftTypeShouldBeFound("endHour.notEquals=" + UPDATED_END_HOUR);
    }

    @Test
    @Transactional
    void getAllShiftTypesByEndHourIsInShouldWork() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where endHour in DEFAULT_END_HOUR or UPDATED_END_HOUR
        defaultShiftTypeShouldBeFound("endHour.in=" + DEFAULT_END_HOUR + "," + UPDATED_END_HOUR);

        // Get all the shiftTypeList where endHour equals to UPDATED_END_HOUR
        defaultShiftTypeShouldNotBeFound("endHour.in=" + UPDATED_END_HOUR);
    }

    @Test
    @Transactional
    void getAllShiftTypesByEndHourIsNullOrNotNull() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where endHour is not null
        defaultShiftTypeShouldBeFound("endHour.specified=true");

        // Get all the shiftTypeList where endHour is null
        defaultShiftTypeShouldNotBeFound("endHour.specified=false");
    }

    @Test
    @Transactional
    void getAllShiftTypesByEndHourIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where endHour is greater than or equal to DEFAULT_END_HOUR
        defaultShiftTypeShouldBeFound("endHour.greaterThanOrEqual=" + DEFAULT_END_HOUR);

        // Get all the shiftTypeList where endHour is greater than or equal to (DEFAULT_END_HOUR + 1)
        defaultShiftTypeShouldNotBeFound("endHour.greaterThanOrEqual=" + (DEFAULT_END_HOUR + 1));
    }

    @Test
    @Transactional
    void getAllShiftTypesByEndHourIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where endHour is less than or equal to DEFAULT_END_HOUR
        defaultShiftTypeShouldBeFound("endHour.lessThanOrEqual=" + DEFAULT_END_HOUR);

        // Get all the shiftTypeList where endHour is less than or equal to SMALLER_END_HOUR
        defaultShiftTypeShouldNotBeFound("endHour.lessThanOrEqual=" + SMALLER_END_HOUR);
    }

    @Test
    @Transactional
    void getAllShiftTypesByEndHourIsLessThanSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where endHour is less than DEFAULT_END_HOUR
        defaultShiftTypeShouldNotBeFound("endHour.lessThan=" + DEFAULT_END_HOUR);

        // Get all the shiftTypeList where endHour is less than (DEFAULT_END_HOUR + 1)
        defaultShiftTypeShouldBeFound("endHour.lessThan=" + (DEFAULT_END_HOUR + 1));
    }

    @Test
    @Transactional
    void getAllShiftTypesByEndHourIsGreaterThanSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where endHour is greater than DEFAULT_END_HOUR
        defaultShiftTypeShouldNotBeFound("endHour.greaterThan=" + DEFAULT_END_HOUR);

        // Get all the shiftTypeList where endHour is greater than SMALLER_END_HOUR
        defaultShiftTypeShouldBeFound("endHour.greaterThan=" + SMALLER_END_HOUR);
    }

    @Test
    @Transactional
    void getAllShiftTypesByEndMinuteIsEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where endMinute equals to DEFAULT_END_MINUTE
        defaultShiftTypeShouldBeFound("endMinute.equals=" + DEFAULT_END_MINUTE);

        // Get all the shiftTypeList where endMinute equals to UPDATED_END_MINUTE
        defaultShiftTypeShouldNotBeFound("endMinute.equals=" + UPDATED_END_MINUTE);
    }

    @Test
    @Transactional
    void getAllShiftTypesByEndMinuteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where endMinute not equals to DEFAULT_END_MINUTE
        defaultShiftTypeShouldNotBeFound("endMinute.notEquals=" + DEFAULT_END_MINUTE);

        // Get all the shiftTypeList where endMinute not equals to UPDATED_END_MINUTE
        defaultShiftTypeShouldBeFound("endMinute.notEquals=" + UPDATED_END_MINUTE);
    }

    @Test
    @Transactional
    void getAllShiftTypesByEndMinuteIsInShouldWork() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where endMinute in DEFAULT_END_MINUTE or UPDATED_END_MINUTE
        defaultShiftTypeShouldBeFound("endMinute.in=" + DEFAULT_END_MINUTE + "," + UPDATED_END_MINUTE);

        // Get all the shiftTypeList where endMinute equals to UPDATED_END_MINUTE
        defaultShiftTypeShouldNotBeFound("endMinute.in=" + UPDATED_END_MINUTE);
    }

    @Test
    @Transactional
    void getAllShiftTypesByEndMinuteIsNullOrNotNull() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where endMinute is not null
        defaultShiftTypeShouldBeFound("endMinute.specified=true");

        // Get all the shiftTypeList where endMinute is null
        defaultShiftTypeShouldNotBeFound("endMinute.specified=false");
    }

    @Test
    @Transactional
    void getAllShiftTypesByEndMinuteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where endMinute is greater than or equal to DEFAULT_END_MINUTE
        defaultShiftTypeShouldBeFound("endMinute.greaterThanOrEqual=" + DEFAULT_END_MINUTE);

        // Get all the shiftTypeList where endMinute is greater than or equal to (DEFAULT_END_MINUTE + 1)
        defaultShiftTypeShouldNotBeFound("endMinute.greaterThanOrEqual=" + (DEFAULT_END_MINUTE + 1));
    }

    @Test
    @Transactional
    void getAllShiftTypesByEndMinuteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where endMinute is less than or equal to DEFAULT_END_MINUTE
        defaultShiftTypeShouldBeFound("endMinute.lessThanOrEqual=" + DEFAULT_END_MINUTE);

        // Get all the shiftTypeList where endMinute is less than or equal to SMALLER_END_MINUTE
        defaultShiftTypeShouldNotBeFound("endMinute.lessThanOrEqual=" + SMALLER_END_MINUTE);
    }

    @Test
    @Transactional
    void getAllShiftTypesByEndMinuteIsLessThanSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where endMinute is less than DEFAULT_END_MINUTE
        defaultShiftTypeShouldNotBeFound("endMinute.lessThan=" + DEFAULT_END_MINUTE);

        // Get all the shiftTypeList where endMinute is less than (DEFAULT_END_MINUTE + 1)
        defaultShiftTypeShouldBeFound("endMinute.lessThan=" + (DEFAULT_END_MINUTE + 1));
    }

    @Test
    @Transactional
    void getAllShiftTypesByEndMinuteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        // Get all the shiftTypeList where endMinute is greater than DEFAULT_END_MINUTE
        defaultShiftTypeShouldNotBeFound("endMinute.greaterThan=" + DEFAULT_END_MINUTE);

        // Get all the shiftTypeList where endMinute is greater than SMALLER_END_MINUTE
        defaultShiftTypeShouldBeFound("endMinute.greaterThan=" + SMALLER_END_MINUTE);
    }

    @Test
    @Transactional
    void getAllShiftTypesByShiftIsEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);
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
        shiftType.addShift(shift);
        shiftTypeRepository.saveAndFlush(shiftType);
        Long shiftId = shift.getId();

        // Get all the shiftTypeList where shift equals to shiftId
        defaultShiftTypeShouldBeFound("shiftId.equals=" + shiftId);

        // Get all the shiftTypeList where shift equals to (shiftId + 1)
        defaultShiftTypeShouldNotBeFound("shiftId.equals=" + (shiftId + 1));
    }

    @Test
    @Transactional
    void getAllShiftTypesByLocationIsEqualToSomething() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            location = LocationResourceIT.createEntity(em);
            em.persist(location);
            em.flush();
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        em.persist(location);
        em.flush();
        shiftType.setLocation(location);
        shiftTypeRepository.saveAndFlush(shiftType);
        Long locationId = location.getId();

        // Get all the shiftTypeList where location equals to locationId
        defaultShiftTypeShouldBeFound("locationId.equals=" + locationId);

        // Get all the shiftTypeList where location equals to (locationId + 1)
        defaultShiftTypeShouldNotBeFound("locationId.equals=" + (locationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultShiftTypeShouldBeFound(String filter) throws Exception {
        restShiftTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shiftType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].startHour").value(hasItem(DEFAULT_START_HOUR)))
            .andExpect(jsonPath("$.[*].startMinute").value(hasItem(DEFAULT_START_MINUTE)))
            .andExpect(jsonPath("$.[*].endHour").value(hasItem(DEFAULT_END_HOUR)))
            .andExpect(jsonPath("$.[*].endMinute").value(hasItem(DEFAULT_END_MINUTE)));

        // Check, that the count call also returns 1
        restShiftTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultShiftTypeShouldNotBeFound(String filter) throws Exception {
        restShiftTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restShiftTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingShiftType() throws Exception {
        // Get the shiftType
        restShiftTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewShiftType() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        int databaseSizeBeforeUpdate = shiftTypeRepository.findAll().size();

        // Update the shiftType
        ShiftType updatedShiftType = shiftTypeRepository.findById(shiftType.getId()).get();
        // Disconnect from session so that the updates on updatedShiftType are not directly saved in db
        em.detach(updatedShiftType);
        updatedShiftType
            .name(UPDATED_NAME)
            .startHour(UPDATED_START_HOUR)
            .startMinute(UPDATED_START_MINUTE)
            .endHour(UPDATED_END_HOUR)
            .endMinute(UPDATED_END_MINUTE);
        ShiftTypeDTO shiftTypeDTO = shiftTypeMapper.toDto(updatedShiftType);

        restShiftTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shiftTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shiftTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the ShiftType in the database
        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeUpdate);
        ShiftType testShiftType = shiftTypeList.get(shiftTypeList.size() - 1);
        assertThat(testShiftType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testShiftType.getStartHour()).isEqualTo(UPDATED_START_HOUR);
        assertThat(testShiftType.getStartMinute()).isEqualTo(UPDATED_START_MINUTE);
        assertThat(testShiftType.getEndHour()).isEqualTo(UPDATED_END_HOUR);
        assertThat(testShiftType.getEndMinute()).isEqualTo(UPDATED_END_MINUTE);
    }

    @Test
    @Transactional
    void putNonExistingShiftType() throws Exception {
        int databaseSizeBeforeUpdate = shiftTypeRepository.findAll().size();
        shiftType.setId(count.incrementAndGet());

        // Create the ShiftType
        ShiftTypeDTO shiftTypeDTO = shiftTypeMapper.toDto(shiftType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShiftTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shiftTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shiftTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShiftType in the database
        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShiftType() throws Exception {
        int databaseSizeBeforeUpdate = shiftTypeRepository.findAll().size();
        shiftType.setId(count.incrementAndGet());

        // Create the ShiftType
        ShiftTypeDTO shiftTypeDTO = shiftTypeMapper.toDto(shiftType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShiftTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shiftTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShiftType in the database
        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShiftType() throws Exception {
        int databaseSizeBeforeUpdate = shiftTypeRepository.findAll().size();
        shiftType.setId(count.incrementAndGet());

        // Create the ShiftType
        ShiftTypeDTO shiftTypeDTO = shiftTypeMapper.toDto(shiftType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShiftTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shiftTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShiftType in the database
        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShiftTypeWithPatch() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        int databaseSizeBeforeUpdate = shiftTypeRepository.findAll().size();

        // Update the shiftType using partial update
        ShiftType partialUpdatedShiftType = new ShiftType();
        partialUpdatedShiftType.setId(shiftType.getId());

        partialUpdatedShiftType.startHour(UPDATED_START_HOUR);

        restShiftTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShiftType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShiftType))
            )
            .andExpect(status().isOk());

        // Validate the ShiftType in the database
        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeUpdate);
        ShiftType testShiftType = shiftTypeList.get(shiftTypeList.size() - 1);
        assertThat(testShiftType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testShiftType.getStartHour()).isEqualTo(UPDATED_START_HOUR);
        assertThat(testShiftType.getStartMinute()).isEqualTo(DEFAULT_START_MINUTE);
        assertThat(testShiftType.getEndHour()).isEqualTo(DEFAULT_END_HOUR);
        assertThat(testShiftType.getEndMinute()).isEqualTo(DEFAULT_END_MINUTE);
    }

    @Test
    @Transactional
    void fullUpdateShiftTypeWithPatch() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        int databaseSizeBeforeUpdate = shiftTypeRepository.findAll().size();

        // Update the shiftType using partial update
        ShiftType partialUpdatedShiftType = new ShiftType();
        partialUpdatedShiftType.setId(shiftType.getId());

        partialUpdatedShiftType
            .name(UPDATED_NAME)
            .startHour(UPDATED_START_HOUR)
            .startMinute(UPDATED_START_MINUTE)
            .endHour(UPDATED_END_HOUR)
            .endMinute(UPDATED_END_MINUTE);

        restShiftTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShiftType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShiftType))
            )
            .andExpect(status().isOk());

        // Validate the ShiftType in the database
        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeUpdate);
        ShiftType testShiftType = shiftTypeList.get(shiftTypeList.size() - 1);
        assertThat(testShiftType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testShiftType.getStartHour()).isEqualTo(UPDATED_START_HOUR);
        assertThat(testShiftType.getStartMinute()).isEqualTo(UPDATED_START_MINUTE);
        assertThat(testShiftType.getEndHour()).isEqualTo(UPDATED_END_HOUR);
        assertThat(testShiftType.getEndMinute()).isEqualTo(UPDATED_END_MINUTE);
    }

    @Test
    @Transactional
    void patchNonExistingShiftType() throws Exception {
        int databaseSizeBeforeUpdate = shiftTypeRepository.findAll().size();
        shiftType.setId(count.incrementAndGet());

        // Create the ShiftType
        ShiftTypeDTO shiftTypeDTO = shiftTypeMapper.toDto(shiftType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShiftTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shiftTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shiftTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShiftType in the database
        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShiftType() throws Exception {
        int databaseSizeBeforeUpdate = shiftTypeRepository.findAll().size();
        shiftType.setId(count.incrementAndGet());

        // Create the ShiftType
        ShiftTypeDTO shiftTypeDTO = shiftTypeMapper.toDto(shiftType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShiftTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shiftTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShiftType in the database
        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShiftType() throws Exception {
        int databaseSizeBeforeUpdate = shiftTypeRepository.findAll().size();
        shiftType.setId(count.incrementAndGet());

        // Create the ShiftType
        ShiftTypeDTO shiftTypeDTO = shiftTypeMapper.toDto(shiftType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShiftTypeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(shiftTypeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShiftType in the database
        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShiftType() throws Exception {
        // Initialize the database
        shiftTypeRepository.saveAndFlush(shiftType);

        int databaseSizeBeforeDelete = shiftTypeRepository.findAll().size();

        // Delete the shiftType
        restShiftTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, shiftType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ShiftType> shiftTypeList = shiftTypeRepository.findAll();
        assertThat(shiftTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
