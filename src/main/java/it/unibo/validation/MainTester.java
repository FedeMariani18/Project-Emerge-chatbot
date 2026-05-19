package it.unibo.validation;

public class MainTester {
    public static void main(String[] args) {
        final String REPORT_PATH = "report/output_benchmark"; 
        
        ModelTester modelTester = new ModelTester();
        TestReportWriter writer = new TestReportWriter();
        
        modelTester.test();

        writer.writeAsJson(modelTester.getTestResult(), REPORT_PATH + ".json");
        writer.writeAsReadableText(modelTester.getTestResult(), REPORT_PATH + ".md");
    }
}
