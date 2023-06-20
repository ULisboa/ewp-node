package pt.ulisboa.ewp.node.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CompressionUtils.class);

    private CompressionUtils() {
    }

    public static byte[] compress(byte[] data) throws IOException {
        return compress(data, CompressionAlgorithm.GZIP);
    }

    public static byte[] compress(byte[] data, CompressionAlgorithm algorithm) throws IOException {
        if (algorithm == null) {
            return data;
        }

        if (algorithm == CompressionAlgorithm.GZIP) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write("{gzip}".getBytes(StandardCharsets.UTF_8));

            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(data);
            gzipOutputStream.close();

            return byteArrayOutputStream.toByteArray();
        }

        return data;
    }

    public static String uncompress(byte[] compressedBytes) throws IOException {
        CompressionAlgorithm compressionAlgorithm = getAlgorithmFromCompressedData(compressedBytes);
        byte[] compressedBytesWithoutAlgorithm = getCompressedDataWithoutAlgorithmPrefix(compressedBytes);
        if (compressionAlgorithm == CompressionAlgorithm.GZIP) {
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressedBytesWithoutAlgorithm));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }

        return new String(compressedBytesWithoutAlgorithm, StandardCharsets.UTF_8);
    }

    private static CompressionAlgorithm getAlgorithmFromCompressedData(byte[] compressedBytes) {
        if (compressedBytes[0] != '{') {
            return CompressionAlgorithm.NONE;
        }

        int closeBracketPosition = 1;
        while (closeBracketPosition < compressedBytes.length && compressedBytes[closeBracketPosition] != '}') {
            closeBracketPosition++;
        }

        if (closeBracketPosition >= compressedBytes.length) {
            return CompressionAlgorithm.NONE;
        }

        String algorithm = new String(Arrays.copyOfRange(compressedBytes, 1, closeBracketPosition));
        if (algorithm.equals("gzip")) {
            return CompressionAlgorithm.GZIP;
        }

        LOG.warn("Unknown compression algorithm '" + algorithm + "', ignoring it...");
        return CompressionAlgorithm.NONE;
    }

    private static byte[] getCompressedDataWithoutAlgorithmPrefix(byte[] compressedBytes) {
        if (compressedBytes[0] != '{') {
            return compressedBytes;
        }

        int startPosition = 0;
        while (startPosition < compressedBytes.length && compressedBytes[startPosition] != '}') {
            startPosition++;
        }

        if (startPosition >= compressedBytes.length) {
            return compressedBytes;
        }

        return Arrays.copyOfRange(compressedBytes, startPosition + 1, compressedBytes.length);
    }

    public enum CompressionAlgorithm {
        GZIP,
        NONE
    }
}
