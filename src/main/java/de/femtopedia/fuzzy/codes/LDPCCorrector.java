package de.femtopedia.fuzzy.codes;

import de.femtopedia.fuzzy.api.Corrector;
import de.femtopedia.ldpc.LDPC;
import lombok.AllArgsConstructor;
import org.bouncycastle.pqc.math.linearalgebra.GF2Vector;

/**
 * The {@link Corrector} implementation for {@link LDPC} codes.
 */
@AllArgsConstructor
public class LDPCCorrector implements Corrector {

    /**
     * The underlying {@link LDPC} instance.
     */
    private final LDPC ldpc;

    /**
     * {@inheritDoc}
     */
    @Override
    public GF2Vector decode(GF2Vector toDecode) {
        return ldpc.decode(toDecode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GF2Vector encode(GF2Vector toEncode) {
        return ldpc.encode(toEncode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getParityBits() {
        return ldpc.getParityBits();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMessageBits() {
        return ldpc.getMessageBits();
    }

}
