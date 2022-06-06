package de.notartzt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.notartzt.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShiftTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShiftType.class);
        ShiftType shiftType1 = new ShiftType();
        shiftType1.setId(1L);
        ShiftType shiftType2 = new ShiftType();
        shiftType2.setId(shiftType1.getId());
        assertThat(shiftType1).isEqualTo(shiftType2);
        shiftType2.setId(2L);
        assertThat(shiftType1).isNotEqualTo(shiftType2);
        shiftType1.setId(null);
        assertThat(shiftType1).isNotEqualTo(shiftType2);
    }
}
