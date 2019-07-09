package receive;

import jssc.SerialPortException;

public interface Receive {

	public int ultrasonic_0();

	public int ultrasonic_1();

	public int ultrasonic_2();

	public int ultrasonic_3();

	public int ball_speed();

	public int ambient_light();

	public int game_start();

	public void game_start(int number_of_players) throws SerialPortException;

	public void start(String port_name);

	public void close();

	public void winner(int playernumber) throws SerialPortException;

}
