import java.lang.Runnable;

public class Timer implements Runnable{
	
	private Boolean active = false;
	private MainWindow listener;

	public Timer(MainWindow listener) {
		this.listener = listener;
	}

	public void run() {
		active = true;
		while (active) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				active = false;
			}

			listener.seconds++;
			listener.drawSeconds();
			//System.out.println(listener.seconds);
		}
	}
}