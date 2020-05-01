package com.jstarcraft.core.common.selection;

public abstract class AbstractSelector<T> implements QuerySelector<T> {

    protected String query;

    public AbstractSelector(String query) {
        this.query = query;
    }

    @Override
    public String getQuery() {
        return query;
    }

}
