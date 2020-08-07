package com.telekom.timon.leanix.performance;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static com.telekom.timon.leanix.leanixapi.LeanixPototypeConstants.GENERATTED_PERFORMANCE_RESULT;

public class PerformanceTester {

    private FileWriter writer = null;

    public PerformanceTester() {
        try {
            if (writer == null) {
                writer = new FileWriter(GENERATTED_PERFORMANCE_RESULT, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void writePerformanceDataIntoFile(String methodName, long duration) {
        try {
            writer.write(methodName + ": " + duration);
            writer.write("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closePerformanceWriter(){
        try {
            if (writer != null){
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executePerformanceTest(final Instant start, String methodName) {
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();

        writePerformanceDataIntoFile(methodName, timeElapsed);
    }

}
