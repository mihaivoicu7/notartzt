package de.notartzt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.notartzt.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShiftTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Shift.class);
        Shift shift1 = new Shift();
        shift1.setId(1L);
        Shift shift2 = new Shift();
        shift2.setId(shift1.getId());
        assertThat(shift1).isEqualTo(shift2);
        shift2.setId(2L);
        assertThat(shift1).isNotEqualTo(shift2);
        shift1.setId(null);
        assertThat(shift1).isNotEqualTo(shift2);
    }
}
