package de.notartzt.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.notartzt.IntegrationTest;
import de.notartzt.domain.Shift;
import de.notartzt.domain.ShiftType;
import de.notartzt.domain.WorkLog;
import de.notartzt.domain.enumeration.ShiftStatus;
import de.notartzt.repository.ShiftRepository;
import de.notartzt.service.criteria.ShiftCriteria;
import de.notartzt.service.dto.ShiftDTO;
import de.notartzt.service.mapper.ShiftMapper;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link ShiftResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShiftResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE = LocalDate.ofEpochDay(-1L);

    private static final ShiftStatus DEFAULT_STATUS = ShiftStatus.OPEN;
    private static final ShiftStatus UPDATED_STATUS = ShiftStatus.CLOSED;

    private static final String ENTITY_API_URL = "/api/shifts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ShiftMapper shiftMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShiftMockMvc;

    private Shift shift;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shift createEntity(EntityManager em) {
        Shift shift = new Shift().date(DEFAULT_DATE).status(DEFAULT_STATUS);
        return shift;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shift createUpdatedEntity(EntityManager em) {
        Shift shift = new Shift().date(UPDATED_DATE).status(UPDATED_STATUS);
        return shift;
    }

    @BeforeEach
    public void initTest() {
        shift = createEntity(em);
    }

    @Test
    @Transactional
    void createShift() throws Exception {
        int databaseSizeBeforeCreate = shiftRepository.findAll().size();
        // Create the Shift
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);
        restShiftMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shiftDTO)))
            .andExpect(status().isCreated());

        // Validate the Shift in the database
        List<Shift> shiftList = shiftRepository.findAll();
        assertThat(shiftList).hasSize(databaseSizeBeforeCreate + 1);
        Shift testShift = shiftList.get(shiftList.size() - 1);
        assertThat(testShift.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testShift.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createShiftWithExistingId() throws Exception {
        // Create the Shift with an existing ID
        shift.setId(1L);
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        int databaseSizeBeforeCreate = shiftRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShiftMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shiftDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Shift in the database
        List<Shift> shiftList = shiftRepository.findAll();
        assertThat(shiftList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = shiftRepository.findAll().size();
        // set the field null
        shift.setDate(null);

        // Create the Shift, which fails.
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        restShiftMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shiftDTO)))
            .andExpect(status().isBadRequest());

        List<Shift> shiftList = shiftRepository.findAll();
        assertThat(shiftList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllShifts() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        // Get all the shiftList
        restShiftMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shift.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getShift() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        // Get the shift
        restShiftMockMvc
            .perform(get(ENTITY_API_URL_ID, shift.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shift.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getShiftsByIdFiltering() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        Long id = shift.getId();

        defaultShiftShouldBeFound("id.equals=" + id);
        defaultShiftShouldNotBeFound("id.notEquals=" + id);

        defaultShiftShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultShiftShouldNotBeFound("id.greaterThan=" + id);

        defaultShiftShouldBeFound("id.lessThanOrEqual=" + id);
        defaultShiftShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllShiftsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        // Get all the shiftList where date equals to DEFAULT_DATE
        defaultShiftShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the shiftList where date equals to UPDATED_DATE
        defaultShiftShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllShiftsByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        // Get all the shiftList where date not equals to DEFAULT_DATE
        defaultShiftShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the shiftList where date not equals to UPDATED_DATE
        defaultShiftShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllShiftsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        // Get all the shiftList where date in DEFAULT_DATE or UPDATED_DATE
        defaultShiftShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the shiftList where date equals to UPDATED_DATE
        defaultShiftShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllShiftsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        // Get all the shiftList where date is not null
        defaultShiftShouldBeFound("date.specified=true");

        // Get all the shiftList where date is null
        defaultShiftShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllShiftsByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        // Get all the shiftList where date is greater than or equal to DEFAULT_DATE
        defaultShiftShouldBeFound("date.greaterThanOrEqual=" + DEFAULT_DATE);

        // Get all the shiftList where date is greater than or equal to UPDATED_DATE
        defaultShiftShouldNotBeFound("date.greaterThanOrEqual=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllShiftsByDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        // Get all the shiftList where date is less than or equal to DEFAULT_DATE
        defaultShiftShouldBeFound("date.lessThanOrEqual=" + DEFAULT_DATE);

        // Get all the shiftList where date is less than or equal to SMALLER_DATE
        defaultShiftShouldNotBeFound("date.lessThanOrEqual=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllShiftsByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        // Get all the shiftList where date is less than DEFAULT_DATE
        defaultShiftShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the shiftList where date is less than UPDATED_DATE
        defaultShiftShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllShiftsByDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        // Get all the shiftList where date is greater than DEFAULT_DATE
        defaultShiftShouldNotBeFound("date.greaterThan=" + DEFAULT_DATE);

        // Get all the shiftList where date is greater than SMALLER_DATE
        defaultShiftShouldBeFound("date.greaterThan=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllShiftsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        // Get all the shiftList where status equals to DEFAULT_STATUS
        defaultShiftShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the shiftList where status equals to UPDATED_STATUS
        defaultShiftShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllShiftsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        // Get all the shiftList where status not equals to DEFAULT_STATUS
        defaultShiftShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the shiftList where status not equals to UPDATED_STATUS
        defaultShiftShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllShiftsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        // Get all the shiftList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultShiftShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the shiftList where status equals to UPDATED_STATUS
        defaultShiftShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllShiftsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        // Get all the shiftList where status is not null
        defaultShiftShouldBeFound("status.specified=true");

        // Get all the shiftList where status is null
        defaultShiftShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllShiftsByWorkLogIsEqualToSomething() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);
        WorkLog workLog;
        if (TestUtil.findAll(em, WorkLog.class).isEmpty()) {
            workLog = WorkLogResourceIT.createEntity(em);
            em.persist(workLog);
            em.flush();
        } else {
            workLog = TestUtil.findAll(em, WorkLog.class).get(0);
        }
        em.persist(workLog);
        em.flush();
        shift.addWorkLog(workLog);
        shiftRepository.saveAndFlush(shift);
        Long workLogId = workLog.getId();

        // Get all the shiftList where workLog equals to workLogId
        defaultShiftShouldBeFound("workLogId.equals=" + workLogId);

        // Get all the shiftList where workLog equals to (workLogId + 1)
        defaultShiftShouldNotBeFound("workLogId.equals=" + (workLogId + 1));
    }

    @Test
    @Transactional
    void getAllShiftsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);
        ShiftType type;
        if (TestUtil.findAll(em, ShiftType.class).isEmpty()) {
            type = ShiftTypeResourceIT.createEntity(em);
            em.persist(type);
            em.flush();
        } else {
            type = TestUtil.findAll(em, ShiftType.class).get(0);
        }
        em.persist(type);
        em.flush();
        shift.setType(type);
        shiftRepository.saveAndFlush(shift);
        Long typeId = type.getId();

        // Get all the shiftList where type equals to typeId
        defaultShiftShouldBeFound("typeId.equals=" + typeId);

        // Get all the shiftList where type equals to (typeId + 1)
        defaultShiftShouldNotBeFound("typeId.equals=" + (typeId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultShiftShouldBeFound(String filter) throws Exception {
        restShiftMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shift.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restShiftMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultShiftShouldNotBeFound(String filter) throws Exception {
        restShiftMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restShiftMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingShift() throws Exception {
        // Get the shift
        restShiftMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewShift() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        int databaseSizeBeforeUpdate = shiftRepository.findAll().size();

        // Update the shift
        Shift updatedShift = shiftRepository.findById(shift.getId()).get();
        // Disconnect from session so that the updates on updatedShift are not directly saved in db
        em.detach(updatedShift);
        updatedShift.date(UPDATED_DATE).status(UPDATED_STATUS);
        ShiftDTO shiftDTO = shiftMapper.toDto(updatedShift);

        restShiftMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shiftDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shiftDTO))
            )
            .andExpect(status().isOk());

        // Validate the Shift in the database
        List<Shift> shiftList = shiftRepository.findAll();
        assertThat(shiftList).hasSize(databaseSizeBeforeUpdate);
        Shift testShift = shiftList.get(shiftList.size() - 1);
        assertThat(testShift.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testShift.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingShift() throws Exception {
        int databaseSizeBeforeUpdate = shiftRepository.findAll().size();
        shift.setId(count.incrementAndGet());

        // Create the Shift
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShiftMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shiftDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shiftDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shift in the database
        List<Shift> shiftList = shiftRepository.findAll();
        assertThat(shiftList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShift() throws Exception {
        int databaseSizeBeforeUpdate = shiftRepository.findAll().size();
        shift.setId(count.incrementAndGet());

        // Create the Shift
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShiftMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shiftDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shift in the database
        List<Shift> shiftList = shiftRepository.findAll();
        assertThat(shiftList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShift() throws Exception {
        int databaseSizeBeforeUpdate = shiftRepository.findAll().size();
        shift.setId(count.incrementAndGet());

        // Create the Shift
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShiftMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shiftDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Shift in the database
        List<Shift> shiftList = shiftRepository.findAll();
        assertThat(shiftList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShiftWithPatch() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        int databaseSizeBeforeUpdate = shiftRepository.findAll().size();

        // Update the shift using partial update
        Shift partialUpdatedShift = new Shift();
        partialUpdatedShift.setId(shift.getId());

        partialUpdatedShift.date(UPDATED_DATE).status(UPDATED_STATUS);

        restShiftMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShift.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShift))
            )
            .andExpect(status().isOk());

        // Validate the Shift in the database
        List<Shift> shiftList = shiftRepository.findAll();
        assertThat(shiftList).hasSize(databaseSizeBeforeUpdate);
        Shift testShift = shiftList.get(shiftList.size() - 1);
        assertThat(testShift.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testShift.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateShiftWithPatch() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        int databaseSizeBeforeUpdate = shiftRepository.findAll().size();

        // Update the shift using partial update
        Shift partialUpdatedShift = new Shift();
        partialUpdatedShift.setId(shift.getId());

        partialUpdatedShift.date(UPDATED_DATE).status(UPDATED_STATUS);

        restShiftMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShift.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShift))
            )
            .andExpect(status().isOk());

        // Validate the Shift in the database
        List<Shift> shiftList = shiftRepository.findAll();
        assertThat(shiftList).hasSize(databaseSizeBeforeUpdate);
        Shift testShift = shiftList.get(shiftList.size() - 1);
        assertThat(testShift.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testShift.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingShift() throws Exception {
        int databaseSizeBeforeUpdate = shiftRepository.findAll().size();
        shift.setId(count.incrementAndGet());

        // Create the Shift
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShiftMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shiftDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shiftDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shift in the database
        List<Shift> shiftList = shiftRepository.findAll();
        assertThat(shiftList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShift() throws Exception {
        int databaseSizeBeforeUpdate = shiftRepository.findAll().size();
        shift.setId(count.incrementAndGet());

        // Create the Shift
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShiftMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shiftDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shift in the database
        List<Shift> shiftList = shiftRepository.findAll();
        assertThat(shiftList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShift() throws Exception {
        int databaseSizeBeforeUpdate = shiftRepository.findAll().size();
        shift.setId(count.incrementAndGet());

        // Create the Shift
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShiftMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(shiftDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Shift in the database
        List<Shift> shiftList = shiftRepository.findAll();
        assertThat(shiftList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShift() throws Exception {
        // Initialize the database
        shiftRepository.saveAndFlush(shift);

        int databaseSizeBeforeDelete = shiftRepository.findAll().size();

        // Delete the shift
        restShiftMockMvc
            .perform(delete(ENTITY_API_URL_ID, shift.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Shift> shiftList = shiftRepository.findAll();
        assertThat(shiftList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
