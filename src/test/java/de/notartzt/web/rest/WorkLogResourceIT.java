package de.notartzt.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.notartzt.IntegrationTest;
import de.notartzt.domain.WorkLog;
import de.notartzt.repository.WorkLogRepository;
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

    private static final Integer DEFAULT_START_MINUTE = 0;
    private static final Integer UPDATED_START_MINUTE = 1;

    private static final Integer DEFAULT_END_HOUR = 0;
    private static final Integer UPDATED_END_HOUR = 1;

    private static final Integer DEFAULT_END_MINUTE = 0;
    private static final Integer UPDATED_END_MINUTE = 1;

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
