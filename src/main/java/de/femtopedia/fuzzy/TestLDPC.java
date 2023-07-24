package de.femtopedia.fuzzy;

import de.femtopedia.fuzzy.codes.LDPCCorrector;
import de.femtopedia.fuzzy.extractors.FuzzyExtractor;
import de.femtopedia.ldpc.LDPC;
import de.femtopedia.ldpc.LDPCGenerator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.bouncycastle.pqc.legacy.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.legacy.math.linearalgebra.GF2Vector;

/**
 * Provides a simple example as test case for the LDPC Fuzzy Extractor.
 */
public final class TestLDPC {

    /**
     * Don't initialize me.
     */
    private TestLDPC() {
        throw new UnsupportedOperationException();
    }

    /**
     * Starting point of the program.
     *
     * @param args The starting arguments.
     * @throws IOException Some I/O error occurred.
     */
    public static void main(String[] args) throws IOException {
        String root = "demonstration/";
        Path avgOrigSetPath = Paths.get(root, "dataOrigSet.m");
        Path avgExtractRightPath = Paths.get(root, "dataExtractRight.m");
        Path avgExtractWrongPath = Paths.get(root, "dataExtractWrong.m");

        GF2Matrix g = LDPCGenerator.readBinaryMatrix(Paths.get(root, "G"));
        GF2Matrix h = LDPCGenerator.readNonStandardAList(Paths.get(root, "H"));
        LDPC ldpc = new LDPC(g, h, 0.1, 20, null);

        System.out.println("Matrix calculation finished.");

        try (BufferedWriter b3 = Files.newBufferedWriter(avgExtractWrongPath);
             BufferedWriter b2 = Files.newBufferedWriter(avgExtractRightPath);
             BufferedWriter b1 = Files.newBufferedWriter(avgOrigSetPath)) {
            b1.append("origSet = [");
            b2.append("extractRight = [");
            b3.append("extractWrong = [");

            for (int i = 0; i < 10000; i++) {
                File[] dataAll = new File("data").listFiles();
                File[] data = Arrays.stream(Objects.requireNonNull(dataAll))
                        .filter(f -> f.getName().endsWith(".key"))
                        .toArray(File[]::new);

                // Array containing "right" responses
                GF2Vector[] right = Arrays.stream(data)
                        .filter(f -> f.getName().startsWith("r"))
                        .map(f -> LDPCGenerator.readAsciiBinaryVector(
                                f.toPath()))
                        .toArray(GF2Vector[]::new);

                // Array containing "wrong" responses
                GF2Vector[] wrong = Arrays.stream(data)
                        .filter(f -> f.getName().startsWith("f"))
                        .map(f -> LDPCGenerator.readAsciiBinaryVector(
                                f.toPath()))
                        .toArray(GF2Vector[]::new);

                // Original response
                GF2Vector orig = right[0];
                FuzzyExtractor extractor = new FuzzyExtractor(
                        new LDPCCorrector(ldpc));
                extractor.generateHelperData(orig);
                GF2Vector origExtracted = extractor.extract(orig);

                System.out.println("Loaded data.");

                List<Long> avgOrigSet = new ArrayList<>();
                for (GF2Vector key : right) {
                    FuzzyExtractor temp = new FuzzyExtractor(
                            new LDPCCorrector(ldpc));
                    long start = System.currentTimeMillis();
                    GF2Vector vec = temp.generateHelperData(key);
                    avgOrigSet.add(System.currentTimeMillis() - start);
                    vec.getEncoded();
                }
                for (GF2Vector key : wrong) {
                    FuzzyExtractor temp = new FuzzyExtractor(
                            new LDPCCorrector(ldpc));
                    long start = System.currentTimeMillis();
                    GF2Vector vec = temp.generateHelperData(key);
                    avgOrigSet.add(System.currentTimeMillis() - start);
                    vec.getEncoded();
                }

                System.out.println("Measured \"Set Original Response\" time.");

                List<Long> avgExtractRight = new ArrayList<>();
                for (GF2Vector key : right) {
                    long start = System.currentTimeMillis();
                    GF2Vector e = extractor.extract(key);
                    avgExtractRight.add(System.currentTimeMillis() - start);
                    if (!e.equals(origExtracted)) {
                        throw new RuntimeException("Extractor failed test!");
                    }
                }

                System.out.println("Measured \"Extract Right\" time.");

                List<Long> avgExtractWrong = new ArrayList<>();
                for (GF2Vector key : wrong) {
                    long start = System.currentTimeMillis();
                    GF2Vector e = extractor.extract(key);
                    avgExtractWrong.add(System.currentTimeMillis() - start);
                    if (e.equals(origExtracted)) {
                        throw new RuntimeException("Extractor failed test!");
                    }
                }

                System.out.println("Measured \"Extract Wrong\" time.");

                System.out.println();
                System.out.println("======= Set Original Response =======");

                for (Long val : avgOrigSet) {
                    System.out.print(val + ",");
                    b1.append(String.valueOf(val)).append(",");
                }
                System.out.println();
                b1.flush();

                System.out.println();
                System.out.println("=========== Extract Right ===========");

                for (Long val : avgExtractRight) {
                    System.out.print(val + ",");
                    b2.append(String.valueOf(val)).append(",");
                }
                System.out.println();
                b2.flush();

                System.out.println();
                System.out.println("=========== Extract Wrong ===========");

                for (Long val : avgExtractWrong) {
                    System.out.print(val + ",");
                    b3.append(String.valueOf(val)).append(",");
                }
                System.out.println();
                b3.flush();
            }

            b1.append("];");
            b2.append("];");
            b3.append("];");
        }
    }

}
