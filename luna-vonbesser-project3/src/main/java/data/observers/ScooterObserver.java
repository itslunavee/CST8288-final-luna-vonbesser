package data.observers;

import model.Scooter;

public interface ScooterObserver {
    void onScooterStateChanged(Scooter scooter);
}
