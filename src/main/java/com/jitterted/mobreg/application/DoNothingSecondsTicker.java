package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.SecondsTicker;

public class DoNothingSecondsTicker implements SecondsTicker {
    @Override
    public void start() {}

    @Override
    public void stop() {}
}
