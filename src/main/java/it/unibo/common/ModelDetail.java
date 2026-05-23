package it.unibo.common;

public class ModelDetail {
    private Float accuracy;
    private Long avgTime;
    
    public ModelDetail(Float accuracy, Long avgTime) {
        this.accuracy = accuracy;
        this.avgTime = avgTime;
    }

    public Float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
    }

    public Long getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(Long avgTime) {
        this.avgTime = avgTime;
    }
}
