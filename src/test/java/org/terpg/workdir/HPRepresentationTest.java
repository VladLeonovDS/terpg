package org.terpg.workdir;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HPRepresentationTest {
    @Test
    void convertsBase() {
        HPRepresentation hp = new HPRepresentation(31, 16, false, false, false);
        assertEquals("1F", hp.shownHp());
        HPRepresentation decimal = hp.convertedToBase(10);
        assertEquals("31", decimal.shownHp());
        assertEquals(hp.trueHp(), decimal.trueHp());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ff", "FF", "0FF"})
    void parsesHexValues(String value) {
        HPRepresentation hp = new HPRepresentation(255, 16, false, false, false);
        Optional<Long> parsed = hp.parseShownToTrue(value);
        assertTrue(parsed.isPresent());
        assertEquals(255L, parsed.get());
    }

    @Test
    void sanitizesGlitchyHp() {
        HPRepresentation hp = new HPRepresentation(-10, 10, false, true, true);
        HPRepresentation sanitized = hp.sanitize();
        assertFalse(sanitized.hasGlitch());
        assertFalse(sanitized.isAllowFloat());
        assertEquals(10, sanitized.trueHp());
    }
}
