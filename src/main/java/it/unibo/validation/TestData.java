package it.unibo.validation;

import java.util.LinkedList;
import java.util.List;

import it.unibo.common.Pair;

public class TestData {
    private List<Pair<Float, Long>> questionsResult;
    private Float totalAccuracy;
    private Long avgResponseTime;

    public TestData(){
        questionsResult = new LinkedList();
        totalAccuracy = 0f;
        avgResponseTime = 0l;
    }

    public List<Pair<Float, Long>> getQuestionsResult() {
        return List.copyOf(questionsResult);
    }

    public void addQuestionResult(Pair<Float, Long> result) {
        this.questionsResult.add(result);
    }

    public Float getTotalAccuracy() {
        return totalAccuracy;
    }

    public void setTotalAccuracy(Float totalAccuracy) {
        this.totalAccuracy = totalAccuracy;
    }

    public Long getAvgResponseTime() {
        return avgResponseTime;
    }

    public void setAvgResponseTime(Long avgResponseTime) {
        this.avgResponseTime = avgResponseTime;
    }


}
