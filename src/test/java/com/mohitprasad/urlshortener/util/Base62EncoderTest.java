package com.mohitprasad.urlshortener.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class Base62EncoderTest {

    private Base62Encoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new Base62Encoder();
    }

    @Test
    void encode_zero_returnsZeroChar() {
        assertThat(encoder.encode(0)).isEqualTo("0");
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 62L, 100L, 1000L, 123456789L, Long.MAX_VALUE / 1000})
    void encodeDecodeRoundtrip(long id) {
        String encoded = encoder.encode(id);
        long decoded = encoder.decode(encoded);
        assertThat(decoded).isEqualTo(id);
    }

    @Test
    void encode_producesBase62Characters() {
        String encoded = encoder.encode(12345L);
        assertThat(encoded).matches("[0-9a-zA-Z]+");
    }

    @Test
    void encode_id1_returnsExpectedValue() {
        assertThat(encoder.encode(1L)).isEqualTo("1");
    }

    @Test
    void encode_id62_returnsExpectedValue() {
        assertThat(encoder.encode(62L)).isEqualTo("10");
    }

    @Test
    void encode_shortEnoughForShortCodes() {
        // IDs up to 56 billion should produce codes of 6 characters or fewer
        String code = encoder.encode(56_000_000_000L);
        assertThat(code.length()).isLessThanOrEqualTo(7);
    }
}
