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
    public List<Sequence>   listAllSequences(String order);
    public Sequence         getSequenceById(long id);
    public List<Step>       getSequenceSteps(long sId);
    public Trigger          getSequenceTrigger(long sId);
    public Sequence         saveSequence(Sequence s);
    public void             deleteSequence(Sequence s);
    public void             updateSequence(long id, Sequence newS);
    public void             changeSequenceOrder(Sequence s, int order);
    public void             setCollapsed(Sequence s, boolean collapsed);

    // Steps
    public List<Step>   listAllSteps();
    public Step         createStep(Step step);
    public void         completeStep(Step step, boolean complete);

    // Triggers
    public List<Trigger>    listAllTriggers();
    public Trigger          createTrigger(Trigger t);
    public void             deleteTrigger(Trigger t);


}
