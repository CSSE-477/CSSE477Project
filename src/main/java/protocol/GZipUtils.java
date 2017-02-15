package protocol;

import utils.SwsLogger;

import java.io.*;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Tayler How on 2/15/2017.
 */
public class GZipUtils {

    /**
     * Takes a gzipped body and decompresses it into a char[]
     *
     * @param str
     * @return the decompressed string as a char[]
     * @throws Exception
     */
    public static char[] decompressString(String str) throws Exception {
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes()));
        BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));

        String outStr = "";
        String line;
        while ((line = br.readLine()) != null) {
            outStr += line + "\n";
        }

        br.close();
        gis.close();
        return outStr.toCharArray();
    }


    /**
     * Compress the body to gzip format
     * @param str the body as a string
     * @param header map of headers
     *     * @return the body as a gzip byte[]
     */
    public static byte[] compressBody(String str, Map<String, String> header) {
        // check to see if header map exists, if not return null since body cannot be processed
        if (header == null) {
            return null;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(str.getBytes());
            gzip.close();
            bos.close();
        } catch (IOException e) {
            SwsLogger.errorLogger.error("Error writing body as gzip.", e);
        }

        byte[] compressed = bos.toByteArray();
        // update headers that are relevant
        header.put(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH), compressed.length + "");
        return compressed;
    }
}
