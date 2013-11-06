import java.io.IOException;

public class AprioriMultiThreaded extends Thread
{
	public AprioriMultiThreaded(String string)
	{
		this.setName(string);
	}

	public void run()
	{
		try {
			new Apriori(Integer.parseInt(this.getName()));
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws InterruptedException
	{
		long start = System.currentTimeMillis();
		AprioriMultiThreaded t1 = new AprioriMultiThreaded("0");
		AprioriMultiThreaded t2 = new AprioriMultiThreaded("1");
		AprioriMultiThreaded t3 = new AprioriMultiThreaded("2");
		AprioriMultiThreaded t4 = new AprioriMultiThreaded("3");
		AprioriMultiThreaded t5 = new AprioriMultiThreaded("4");
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		
		t1.join();
		t2.join();
		t3.join();
		t4.join();
		t5.join();
		long end = System.currentTimeMillis();
		
		System.out.println();
		System.out.println("Time taken : " + (end - start));
		
		System.exit(0);
	}
}