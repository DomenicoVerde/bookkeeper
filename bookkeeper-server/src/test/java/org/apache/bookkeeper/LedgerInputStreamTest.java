package org.apache.bookkeeper;

import java.io.IOException;
import org.apache.bookkeeper.client.BKException;
import org.apache.bookkeeper.client.BookKeeper;
import org.apache.bookkeeper.client.LedgerHandle;
import org.apache.bookkeeper.streaming.LedgerInputStream;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class LedgerInputStreamTest {
	
	private static LedgerInputStream ledger;
	private static BookKeeper bkClient;
	private static LedgerHandle ledgerHandle;
	
	@BeforeClass
	public static void init() {
		//Initializing Client and creating a new ledger
		try {
			bkClient = new BookKeeper("127.0.0.1:2181");
			byte[] password = "password".getBytes();
			ledgerHandle = bkClient.createLedger(BookKeeper.DigestType.MAC, password);
			ledgerHandle.addEntry("This is a test phrase".getBytes());
			ledger = new LedgerInputStream(ledgerHandle);
		} catch (BKException | IOException | InterruptedException e) {
			e.printStackTrace();
			return;
		}
	}
	
	@AfterClass
	public static void closeResources() {
		try {
			bkClient.close();
			ledger.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Category Partition Tests (CP)
	@Test
	public void test1CP() {
		boolean passed = false;
		try {
			ledger.read(null, -1, -1);
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			passed = true;
		} catch (IOException e) {
			Assert.fail();
		}
		
		Assert.assertTrue(passed);
	}
	
	@Test
	public void test2CP() {
		byte[] b = new byte[0];
		boolean passed = false;
		try {
			ledger.read(b, 1, 0);
		} catch (IndexOutOfBoundsException  e) {
			passed = true;
		} catch (Exception e) {
			Assert.fail();
		}
		
		Assert.assertTrue(passed);
	}

	@Test
	public void test3CP() {
		byte[] b = new byte[2];
		int result = 0;
		try {
			result = ledger.read(b, 1, 1);
		} catch (Exception e) {
			Assert.fail();
		}
		
		Assert.assertEquals(result,1);
	}
	
	@Test
	public void test4CP() {
		byte[] b = new byte[3];
		boolean passed = false;
		try {
			ledger.read(b, 0, 4);
		} catch (IndexOutOfBoundsException e) {
			passed = true;
		} catch (Exception e) {
			Assert.fail();
		}
		
		Assert.assertTrue(passed);
	}
	
	@Test
	public void test5CP() {
		byte[] b = new byte[4];
		int result = 0;
		try {
			result = ledger.read(b, 1, 2);
		} catch (Exception e) {
			Assert.fail();
		}
		
		Assert.assertEquals(result,2);
	}

	
	//Other Tests to reach Adequacy Criteria	
	@Test
	public void test1A() {
		LedgerInputStream ledger2 = null;
		try {
			ledger2 = new LedgerInputStream(ledgerHandle, 1000);
			// read() method reads 1 byte at a time. In the ledger there
			// are 21 chars, so it should be called 21 times before reach EOS
			int result = 0;
			int counter = 0;
			while (result != -1) {
				result = ledger2.read();
				counter++;
			}
			Assert.assertEquals(counter, 22);
			
			// the buffer can store all the ledger data. So the second time
			// it fails
			result = 0;
			counter = 0;
			while (result != -1) {
				result = ledger2.read(new byte[1000]);
				counter++;
			}
			Assert.assertEquals(counter, 2);
			
			result = 0;
			counter = 0;
			while (result != -1) {
				result = ledger2.read(new byte[1000], 0, 1000);
				counter++;
			}
			Assert.assertEquals(counter, 2);
			
		} catch (Exception e) {
			Assert.fail();
		} finally {
			ledger2.close();
		}
	}
	
}
