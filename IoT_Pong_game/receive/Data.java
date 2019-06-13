package receive;

public class Data {
	
	private int data;

	public synchronized int getData() {
		return data;
	}

	protected synchronized void setData(int data) {
		this.data = data;
	}
	
	public Data() {
		this.data=0;
	}
	
}
