package receive;

import java.util.concurrent.TimeUnit;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialReceive implements Receive {

	private static SerialPort serialPort;
	
	@Override
	public int ultrasonic_1() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int ultrasonic_2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int ultrasonic_3() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int ultrasonic_4() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int ball_speed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int ambient_light() {
		// TODO Auto-generated method stub
		return 0;
	}
	

	@Override
	public void start(String port_name) {
		// TODO Auto-generated method stub

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
	                System.out.println("Received response: " + receivedData);
	            }
	            catch (SerialPortException ex) {
	                System.out.println("Error in receiving string from COM-port: " + ex);
	            } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		try {
			serialPort.closePort();
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
