package de.notartzt.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShiftMapperTest {

    private ShiftMapper shiftMapper;

    @BeforeEach
    public void setUp() {
        shiftMapper = new ShiftMapperImpl();
    }
}
