package de.femtopedia.fuzzy.api;

import org.bouncycastle.pqc.math.linearalgebra.GF2Vector;

/**
 * Provides methods to correct errors in binary messages.
 */
public interface Corrector {

    /**
     * Tries to decode the given binary message.
     *
     * @param message The message to decode as binary vector.
     * @return The possibly decoded message as binary vector.
     */
    GF2Vector decode(GF2Vector message);

    /**
     * Encodes the given binary message.
     *
     * @param message The message to encode as binary vector.
     * @return The encoded message as binary vector.
     */
    GF2Vector encode(GF2Vector message);

    /**
     * Returns the number of parity bits in an encoded message.
     *
     * @return The number of parity bits in an encoded message.
     */
    int getParityBits();

    /**
     * Returns the number of message bits in an encoded message.
     *
     * @return The number of message bits in an encoded message.
     */
    int getMessageBits();

}
