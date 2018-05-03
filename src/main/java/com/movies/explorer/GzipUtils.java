package com.movies.explorer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class GzipUtils {

    public static String gzip(String filePath) {
        String outFile = filePath.replace(".gz", "");
        byte[] buffer = new byte[1024];
        try (GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(filePath));
                FileOutputStream out = new FileOutputStream(outFile)) {
            int chunkLength;
            while ((chunkLength = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, chunkLength);
            }
            return outFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
