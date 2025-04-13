package org.acme;

public interface ApplicationController {
    void start();
    void stop();
    boolean isStarted();
    Position getPosition() ;
}
