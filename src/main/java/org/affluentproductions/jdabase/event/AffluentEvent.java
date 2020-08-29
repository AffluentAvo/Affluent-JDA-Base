package org.affluentproductions.jdabase.event;

import org.affluentproductions.jdabase.JDABase;

public abstract class AffluentEvent {

    private JDABase jdaBase;

    public AffluentEvent(JDABase jdaBase) {
        this.jdaBase = jdaBase;
    }

    public void setJDABase(JDABase jdaBase) {
        this.jdaBase = jdaBase;
    }

    /**
     * @return current JDA Base
     */
    public JDABase getJDABase() {
        return jdaBase;
    }
}
