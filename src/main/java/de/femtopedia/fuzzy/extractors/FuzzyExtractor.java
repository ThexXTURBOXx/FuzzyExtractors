package de.femtopedia.fuzzy.extractors;

import de.femtopedia.fuzzy.api.Corrector;
import de.femtopedia.fuzzy.api.Extractor;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.pqc.math.linearalgebra.GF2Vector;

/**
 * Provides Fuzzy Extractor functionality using the given {@link Corrector}.
 */
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
    public GF2Vector setOriginalResponse(GF2Vector w) {
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
     * {@inheritDoc}
     */
    @Override
    public GF2Vector strongExtract(GF2Vector message) {
        byte[] encoded = message.getEncoded();
        Mac hmac;
        try {
            hmac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(encoded, "HmacSHA256");
        try {
            hmac.init(secretKeySpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
        byte[] extracted = hmac.doFinal(encoded);
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
