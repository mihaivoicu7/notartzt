package de.notartzt.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import de.notartzt.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShiftTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShiftTypeDTO.class);
        ShiftTypeDTO shiftTypeDTO1 = new ShiftTypeDTO();
        shiftTypeDTO1.setId(1L);
        ShiftTypeDTO shiftTypeDTO2 = new ShiftTypeDTO();
        assertThat(shiftTypeDTO1).isNotEqualTo(shiftTypeDTO2);
        shiftTypeDTO2.setId(shiftTypeDTO1.getId());
        assertThat(shiftTypeDTO1).isEqualTo(shiftTypeDTO2);
        shiftTypeDTO2.setId(2L);
        assertThat(shiftTypeDTO1).isNotEqualTo(shiftTypeDTO2);
        shiftTypeDTO1.setId(null);
        assertThat(shiftTypeDTO1).isNotEqualTo(shiftTypeDTO2);
    }
}
