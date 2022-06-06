package de.notartzt.service.impl;

import de.notartzt.domain.ShiftType;
import de.notartzt.repository.ShiftTypeRepository;
import de.notartzt.service.ShiftTypeService;
import de.notartzt.service.dto.ShiftTypeDTO;
import de.notartzt.service.mapper.ShiftTypeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ShiftType}.
 */
@Service
@Transactional
public class ShiftTypeServiceImpl implements ShiftTypeService {

    private final Logger log = LoggerFactory.getLogger(ShiftTypeServiceImpl.class);

    private final ShiftTypeRepository shiftTypeRepository;

    private final ShiftTypeMapper shiftTypeMapper;

    public ShiftTypeServiceImpl(ShiftTypeRepository shiftTypeRepository, ShiftTypeMapper shiftTypeMapper) {
        this.shiftTypeRepository = shiftTypeRepository;
        this.shiftTypeMapper = shiftTypeMapper;
    }

    @Override
    public ShiftTypeDTO save(ShiftTypeDTO shiftTypeDTO) {
        log.debug("Request to save ShiftType : {}", shiftTypeDTO);
        ShiftType shiftType = shiftTypeMapper.toEntity(shiftTypeDTO);
        shiftType = shiftTypeRepository.save(shiftType);
        return shiftTypeMapper.toDto(shiftType);
    }

    @Override
    public ShiftTypeDTO update(ShiftTypeDTO shiftTypeDTO) {
        log.debug("Request to save ShiftType : {}", shiftTypeDTO);
        ShiftType shiftType = shiftTypeMapper.toEntity(shiftTypeDTO);
        shiftType = shiftTypeRepository.save(shiftType);
        return shiftTypeMapper.toDto(shiftType);
    }

    @Override
    public Optional<ShiftTypeDTO> partialUpdate(ShiftTypeDTO shiftTypeDTO) {
        log.debug("Request to partially update ShiftType : {}", shiftTypeDTO);

        return shiftTypeRepository
            .findById(shiftTypeDTO.getId())
            .map(existingShiftType -> {
                shiftTypeMapper.partialUpdate(existingShiftType, shiftTypeDTO);

                return existingShiftType;
            })
            .map(shiftTypeRepository::save)
            .map(shiftTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShiftTypeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ShiftTypes");
        return shiftTypeRepository.findAll(pageable).map(shiftTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShiftTypeDTO> findOne(Long id) {
        log.debug("Request to get ShiftType : {}", id);
        return shiftTypeRepository.findById(id).map(shiftTypeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ShiftType : {}", id);
        shiftTypeRepository.deleteById(id);
    }
}
