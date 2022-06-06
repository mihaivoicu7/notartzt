package de.notartzt.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.notartzt.IntegrationTest;
import de.notartzt.domain.ShiftType;
import de.notartzt.repository.ShiftTypeRepository;
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

    private static final Integer DEFAULT_START_MINUTE = 0;
    private static final Integer UPDATED_START_MINUTE = 1;

    private static final Integer DEFAULT_END_HOUR = 0;
    private static final Integer UPDATED_END_HOUR = 1;

    private static final Integer DEFAULT_END_MINUTE = 0;
    private static final Integer UPDATED_END_MINUTE = 1;

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
