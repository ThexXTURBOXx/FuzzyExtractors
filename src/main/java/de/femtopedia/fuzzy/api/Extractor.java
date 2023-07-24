package de.femtopedia.fuzzy.api;

import org.bouncycastle.pqc.legacy.math.linearalgebra.GF2Vector;

/**
 * Provides methods to extract errors from a noisy binary message.
 *
 * @param <HelperData> The type for the helper data.
 */
public interface Extractor<HelperData> {

    /**
     * Calculates helper data for the given response {@link GF2Vector}.
     * May only be called once, otherwise does nothing.
     * Corresponds to the <code>Gen</code> function.
     * This method now delegates to {@link #generateHelperData(GF2Vector)}.
     *
     * @param message The response to generate helper data from.
     * @return The generated key or {@code null} if this method has already
     *         been called before.
     * @deprecated Use {@link #generateHelperData(GF2Vector)} instead.
     */
    @Deprecated
    default GF2Vector setOriginalResponse(GF2Vector message) {
        return generateHelperData(message);
    }

    /**
     * Calculates helper data for the given response {@link GF2Vector}.
     * May only be called once, otherwise does nothing.
     * Corresponds to the <code>Gen</code> function.
     *
     * @param message The response to generate helper data from.
     * @return The generated key or {@code null} if this method has already
     *         been called before.
     */
    GF2Vector generateHelperData(GF2Vector message);

    /**
     * Applies the Fuzzy Extractor to the given response {@link GF2Vector}
     * and return the denoised {@link GF2Vector}.
     * Corresponds to the <code>Rep</code> function.
     *
     * @param message The response to denoise.
     * @return The denoised response.
     */
    GF2Vector extract(GF2Vector message);

    /**
     * Returns the generated helper data.
     *
     * @return The generated and used helper data.
     */
    HelperData getHelperData();

}
