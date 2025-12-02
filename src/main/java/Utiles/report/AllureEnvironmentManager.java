package Utiles.report;

import Utiles.dataReader.PropertyReader;
import Utiles.logs.LogsManager;
import com.google.common.collect.ImmutableMap;

import java.io.File;

import static com.github.automatedowl.tools.AllureEnvironmentWriter.allureEnvironmentWriter;

public class AllureEnvironmentManager {
    public static void setEnvironmentVariables() {
        allureEnvironmentWriter(
                ImmutableMap.<String, String>builder()
                        .put("OS", PropertyReader.getProperty("os.name"))
                        .put("JDK version", PropertyReader.getProperty("java.runtime.version"))
                        .put("Base_url",PropertyReader.getProperty("base_url"))
                        .build(), (AllureConstants.RESULTS_FOLDER) +File.separator);

        LogsManager.info("Allure environment variables set.");
        AllureBinaryManager.downloadAndExtract();
    }

}
