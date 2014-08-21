package com.inspiredo.latch;

import java.util.List;

/**
 * Interface for interacting with persisted data
 */
public interface DataSource {

    // Opening and closing
    public void     open();
    public void     close();

    // Sequences
    public List<Sequence>   listAllSequences();
    public Sequence         getSequenceById(long id);
    public List<Step>       getSequenceSteps(long sId);
    public Trigger          getSequenceTrigger(long sId);
    public void             saveSequence(Sequence s);
    public void             deleteSequence(Sequence s);
    public void             updateSequence(long id, Sequence newS);
    public void             changeSequenceOrder(Sequence s, int order);
    public void             setCollapsed(Sequence s, boolean collapsed);

    // Steps
    public List<Step>   listAllSteps();
    public void         createStep(Step step);
    public void         completeStep(Step step, boolean complete);
    public void         deleteStep(Step step);

    // Triggers
    public List<Trigger>    listAllTriggers();
    public void             createTrigger(Trigger t);
    public void             deleteTrigger(Trigger t);
    public void             updateTrigger(long id, Trigger newT);


}
