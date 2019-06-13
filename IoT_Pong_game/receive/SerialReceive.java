package receive;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialReceive implements Receive {

	private static SerialPort serialPort;
	private static Data[] ultrasonic;
	private static Data light;
	private static Data ball_speed;

	@Override
	public int ultrasonic_0() {
		
		return ultrasonic[0].getData();
	}

	@Override
	public int ultrasonic_1() {
		
		return ultrasonic[1].getData();
	}

	@Override
	public int ultrasonic_2() {
		
		return ultrasonic[2].getData();
	}

	@Override
	public int ultrasonic_3() {
		
		return ultrasonic[3].getData();
	}

	@Override
	public int ball_speed() {
		
		return ball_speed.getData();
	}

	@Override
	public int ambient_light() {
		
		return light.getData();
	}


	@Override
	public void start(String port_name) {
		
		ultrasonic = new Data[4];
		ultrasonic[0] = new Data();
		ultrasonic[1] = new Data();
		ultrasonic[2] = new Data();
		ultrasonic[3] = new Data();
		light = new Data();
		ball_speed = new Data();

		serialPort = new SerialPort("/dev/ttyACM0");
		try {
			serialPort.openPort();

			serialPort.setParams(SerialPort.BAUDRATE_9600,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | 
					SerialPort.FLOWCONTROL_RTSCTS_OUT);

			serialPort.addEventListener(new SerialPortReader(), SerialPort.MASK_RXCHAR);
		}catch (SerialPortException e) {
			e.printStackTrace();
		}

	}

	static class SerialPortReader implements SerialPortEventListener{

		@Override
		public void serialEvent(SerialPortEvent event) {
			if(event.isRXCHAR() && event.getEventValue() > 0) {
				try {
					//System.out.println("event value" + event.getEventValue() );
					TimeUnit.MILLISECONDS.sleep(10);
					String receivedData = serialPort.readString();
					parse(receivedData);
					System.out.println("Received response: " + receivedData);
				}
				catch (SerialPortException ex) {
					System.out.println("Error in receiving string from COM-port: " + ex);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void close() {
		
		try {
			serialPort.closePort();
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static void parse(String data) throws IOException {
		System.out.println("data" + data);
		String[] sensor_value = data.split(",");
		switch(sensor_value[0]) {
		case "u0" : ultrasonic[0].setData(Integer.parseInt(sensor_value[1]));
			break;
		case "u1" : ultrasonic[1].setData(Integer.parseInt(sensor_value[1]));
			break;
		case "u2" : ultrasonic[2].setData(Integer.parseInt(sensor_value[1]));
			break;
		case "u3" : ultrasonic[3].setData(Integer.parseInt(sensor_value[1]));
			break;
		case "l" :  light.setData(Integer.parseInt(sensor_value[1]));
			break;
		case "p" : ball_speed.setData(Integer.parseInt(sensor_value[1]));
			break;
		default : throw new IOException("Illegal data received");
			
		}
		
	}

}
