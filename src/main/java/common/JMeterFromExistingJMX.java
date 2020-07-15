package common;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.report.config.ReportGeneratorConfiguration;
import org.apache.jmeter.report.dashboard.ExportException;
import org.apache.jmeter.report.processor.SampleContext;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;

import java.io.*;
import java.util.Properties;
import org.apache.jorphan.collections.HashTree;
import org.apache.jmeter.report.config.ConfigurationException;
import org.apache.jmeter.report.dashboard.HtmlTemplateExporter;

public class JMeterFromExistingJMX {
    public static void main(String[] argv) throws Exception {
        // JMeter Engine
        StandardJMeterEngine jmeter = new StandardJMeterEngine();
        String jmeterHome = "D:\\Software\\apache-jmeter-3.3";
        String slash = System.getProperty("file.separator");
        // Initialize Properties, logging, locale, etc.
        JMeterUtils.loadJMeterProperties("jmeter.properties");
        JMeterUtils.setJMeterHome(jmeterHome);
        JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
        JMeterUtils.initLocale();
        //JMeterUtils.setProperty("saveservice_properties", "/saveservice.properties");

        // Initialize JMeter SaveService
        SaveService.loadProperties();

        // Load existing .jmx Test Plan
        File in = new File(jmeterHome+"\\extras\\Test.jmx");
       //FileInputStream in = new FileInputStream("D://Software/apache-jmeter-3.3/extras/Test.jmx");
        HashTree testPlanTree = SaveService.loadTree(in);
        //in.close();

        //add Summarizer output to get test progress in stdout like:
        // summary =      2 in   1.3s =    1.5/s Avg:   631 Min:   290 Max:   973 Err:     0 (0.00%)
        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }

        // Store execution results into a .jtl file
        String logFile = jmeterHome + slash + "example.jtl";
        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(logFile);
        testPlanTree.add(testPlanTree.getArray()[0], logger);

        // Run JMeter Test
        jmeter.configure(testPlanTree);
        jmeter.runTest();
        //exportResultsToHtml();

        System.out.println("Test completed. See " + jmeterHome + slash + "example.jtl file for results");
        System.exit(0);

    }

    private static void exportResultsToHtml() throws ConfigurationException, ExportException, IOException {

        HtmlTemplateExporter htmlTemplateExporter = new HtmlTemplateExporter();

        SampleContext sampleContext = new SampleContext();
        File file = new File("report.txt");
        Properties reportGenerationProperties = new Properties();
        FileReader fileReader = new FileReader(new File("reportgenerator.properties"));
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        reportGenerationProperties.load(bufferedReader);
        ReportGeneratorConfiguration reportGeneratorConfiguration =  ReportGeneratorConfiguration.loadFromProperties(reportGenerationProperties);


        htmlTemplateExporter.export(sampleContext, file, reportGeneratorConfiguration);
    }
}
