package de.notartzt.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import de.notartzt.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShiftDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShiftDTO.class);
        ShiftDTO shiftDTO1 = new ShiftDTO();
        shiftDTO1.setId(1L);
        ShiftDTO shiftDTO2 = new ShiftDTO();
        assertThat(shiftDTO1).isNotEqualTo(shiftDTO2);
        shiftDTO2.setId(shiftDTO1.getId());
        assertThat(shiftDTO1).isEqualTo(shiftDTO2);
        shiftDTO2.setId(2L);
        assertThat(shiftDTO1).isNotEqualTo(shiftDTO2);
        shiftDTO1.setId(null);
        assertThat(shiftDTO1).isNotEqualTo(shiftDTO2);
    }
}
