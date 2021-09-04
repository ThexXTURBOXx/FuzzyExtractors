package de.femtopedia.fuzzy.extractors;

import de.femtopedia.fuzzy.api.Corrector;
import de.femtopedia.fuzzy.api.Extractor;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.pqc.math.linearalgebra.GF2Vector;

/**
 * Provides Fuzzy Extractor functionality using the given {@link Corrector}.
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class FuzzyExtractor implements Extractor<FuzzyExtractor.HelperData> {

    /**
     * The underlying {@link Corrector} instance.
     */
    private final Corrector corrector;

    /**
     * The secret helper data.
     */
    @Getter
    private HelperData helperData;

    /**
     * {@inheritDoc}
     */
    @Override
    public GF2Vector generateHelperData(GF2Vector w) {
        if (helperData != null) {
            return null;
        }

        int respCols = w.getLength();
        SecureRandom rnd = new SecureRandom();

        GF2Vector x = new GF2Vector(respCols, rnd);

        // SS
        GF2Vector k = new GF2Vector(respCols - corrector.getParityBits(), rnd);
        GF2Vector r = corrector.encode(k);
        GF2Vector s = (GF2Vector) r.add(w);

        helperData = new HelperData(s, x);

        // Ext
        // Strong Extract to receive R
        return strongExtract((GF2Vector) w.add(x));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GF2Vector extract(GF2Vector wd) {
        // Rec
        GF2Vector rd = (GF2Vector) wd.add(helperData.s);
        GF2Vector k = corrector.decode(rd)
                .extractLeftVector(corrector.getMessageBits());
        GF2Vector r = corrector.encode(k);
        GF2Vector w = (GF2Vector) helperData.s.add(r);

        // Ext
        // Strong Extract to receive R
        return strongExtract((GF2Vector) helperData.x.add(w));
    }

    /**
     * Applies a Strong Extractor to the given response {@link GF2Vector}
     * and return the resulting {@link GF2Vector}.
     *
     * @param message The message to apply the Strong Extractor to.
     * @return The resulting response.
     */
    private GF2Vector strongExtract(GF2Vector message) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] extracted = digest.digest(message.getEncoded());
        return GF2Vector.OS2VP(256, extracted);
    }

    /**
     * The helper data for the Fuzzy Extractor.
     */
    @Getter
    @AllArgsConstructor
    public static class HelperData {
        /**
         * The first part of the helper data.
         */
        private final GF2Vector s;

        /**
         * The second part of the helper data.
         */
        private final GF2Vector x;
    }

}
