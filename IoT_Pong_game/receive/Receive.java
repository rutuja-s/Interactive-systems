package receive;

public interface Receive {
	
	public int ultrasonic_0();
	
	public int ultrasonic_1();
	
	public int ultrasonic_2();
	
	public int ultrasonic_3();
	
	public int ball_speed();
	
	public int ambient_light();
	
	public void start(String port_name);
	
	public void close();

}
