package de.femtopedia.fuzzy.api;

import org.bouncycastle.pqc.math.linearalgebra.GF2Vector;

/**
 * Provides methods to extract errors from a noisy binary message.
 */
public interface Extractor {

    /**
     * Calculates helper data for the given response {@link GF2Vector}.
     * May only be called once, otherwise does nothing.
     *
     * @param message The response to generate helper data from.
     * @return The generated key or {@code null} if this method has already
     *         been called before.
     */
    GF2Vector setOriginalResponse(GF2Vector message);

    /**
     * Applies the Fuzzy Extractor to the given response {@link GF2Vector}
     * and return the denoised {@link GF2Vector}.
     *
     * @param message The response to denoise.
     * @return The denoised response.
     */
    GF2Vector extract(GF2Vector message);

    /**
     * Applies a Strong Extractor to the given response {@link GF2Vector}
     * and return the resulting {@link GF2Vector}.
     *
     * @param message The message to apply the Strong Extractor to.
     * @return The resulting response.
     */
    GF2Vector strongExtract(GF2Vector message);

}
