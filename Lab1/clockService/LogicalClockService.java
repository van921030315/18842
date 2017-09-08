package clockService;

import java.util.concurrent.atomic.AtomicInteger;

import messagePasser.TimeStamp;

public class LogicalClockService implements ClockService {
	AtomicInteger time;
	
	public LogicalClockService(){
		this.time = new AtomicInteger(0);
	}
	
	public TimeStamp getTimeStamp(){
		return new TimeStamp(time.incrementAndGet());
	}
	
	public TimeStamp getTime(){
		return new TimeStamp(time.get());
	}
	
    public void updateClockTime(TimeStamp ts){
    	int value = ((Integer)ts.get_time()).intValue();
    	time.getAndUpdate(x -> x < value ? value : x);
    }

	@Override
	public String getClockType() {
		return "logical";
	}
}
