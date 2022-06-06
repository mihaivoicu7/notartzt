package de.notartzt.service.impl;

import de.notartzt.domain.WorkLog;
import de.notartzt.repository.WorkLogRepository;
import de.notartzt.service.WorkLogService;
import de.notartzt.service.dto.WorkLogDTO;
import de.notartzt.service.mapper.WorkLogMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link WorkLog}.
 */
@Service
@Transactional
public class WorkLogServiceImpl implements WorkLogService {

    private final Logger log = LoggerFactory.getLogger(WorkLogServiceImpl.class);

    private final WorkLogRepository workLogRepository;

    private final WorkLogMapper workLogMapper;

    public WorkLogServiceImpl(WorkLogRepository workLogRepository, WorkLogMapper workLogMapper) {
        this.workLogRepository = workLogRepository;
        this.workLogMapper = workLogMapper;
    }

    @Override
    public WorkLogDTO save(WorkLogDTO workLogDTO) {
        log.debug("Request to save WorkLog : {}", workLogDTO);
        WorkLog workLog = workLogMapper.toEntity(workLogDTO);
        workLog = workLogRepository.save(workLog);
        return workLogMapper.toDto(workLog);
    }

    @Override
    public WorkLogDTO update(WorkLogDTO workLogDTO) {
        log.debug("Request to save WorkLog : {}", workLogDTO);
        WorkLog workLog = workLogMapper.toEntity(workLogDTO);
        workLog = workLogRepository.save(workLog);
        return workLogMapper.toDto(workLog);
    }

    @Override
    public Optional<WorkLogDTO> partialUpdate(WorkLogDTO workLogDTO) {
        log.debug("Request to partially update WorkLog : {}", workLogDTO);

        return workLogRepository
            .findById(workLogDTO.getId())
            .map(existingWorkLog -> {
                workLogMapper.partialUpdate(existingWorkLog, workLogDTO);

                return existingWorkLog;
            })
            .map(workLogRepository::save)
            .map(workLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkLogDTO> findAll(Pageable pageable) {
        log.debug("Request to get all WorkLogs");
        return workLogRepository.findAll(pageable).map(workLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WorkLogDTO> findOne(Long id) {
        log.debug("Request to get WorkLog : {}", id);
        return workLogRepository.findById(id).map(workLogMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete WorkLog : {}", id);
        workLogRepository.deleteById(id);
    }
}
