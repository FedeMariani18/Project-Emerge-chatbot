package it.unibo.validation;

public class MainTester {
    public static void main(String[] args) {
        final String REPORT_PATH = "report/output_benchmark"; 
        final String JSON_PATH = REPORT_PATH + ".json";
        final String TEXT_PATH = REPORT_PATH + ".md";
        
        ModelTester modelTester = new ModelTester();
        TestReportWriter writer = new TestReportWriter();
        writer.cleanFiles(JSON_PATH, TEXT_PATH);
        
        modelTester.test();

        writer.writeAsJson(modelTester.getTestResult(), JSON_PATH);
        writer.writeAsReadableText(modelTester.getTestResult(), TEXT_PATH);
    }
}
