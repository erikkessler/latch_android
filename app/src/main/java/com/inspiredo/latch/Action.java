package com.inspiredo.latch;

/**
 * Object representing an Action
 */
public class Action {

    // Name of the Action
    private String mName;

    // Count
    private int mCount;

    // Value
    private float mValue;

    public Action(String name, int count, float value) {
        mName = name;
        mCount = count;
        mValue = value;
    }

    public Action(String name, float value) {
        this(name, 0, value);
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setCount(int count) {
        mCount = count;
    }

    public int getCount() {
        return mCount;
    }

    public int incrementCount() {
        return ++mCount;
    }

    public int decrementCount() {
        return --mCount;
    }

    public void setValue(float value) {
        mValue = value;
    }

    public float getValue() {
        return mValue;
    }

    public float getPayout() {
        return mValue * mCount;
    }
}
