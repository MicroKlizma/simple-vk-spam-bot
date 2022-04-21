package com.vkbot.observer;

public interface Observable {
    public void addObserver(Observer observer);

    public void notifyObserver();
}
