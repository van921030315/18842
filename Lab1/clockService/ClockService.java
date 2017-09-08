package clockService;

import messagePasser.TimeStamp;

public abstract interface ClockService {
	// Interface for ClockService of type Logical and Vector

    public abstract TimeStamp getTimeStamp();
    public abstract void updateClockTime(TimeStamp ts);
    public abstract String getClockType();
    public abstract TimeStamp getTime();
}
