package org.apache.bookkeeper;

import java.io.IOException;
import org.apache.bookkeeper.client.BKException;
import org.apache.bookkeeper.client.BookKeeper;
import org.apache.bookkeeper.client.BookKeeper.DigestType;
import org.apache.bookkeeper.client.LedgerHandle;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class BookkeeperTest {

	private static BookKeeper bkClient;
	
	@BeforeClass
	public static void init() {
		//Initializes a new BookKeeper Client
		try {	
			bkClient = new BookKeeper("127.0.0.1:2181");
		} catch (BKException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void closeResources() {
		try {
			bkClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Category Partition Tests (CP)
	@Test
	public void test1CP() {
		boolean passed = false;
		try {
			bkClient.createLedger(-1, -2, -3, DigestType.DUMMY, null);
		} catch (Exception e) {
			passed = true;
		}
		
		Assert.assertTrue(passed);
	}
	
	@Test
	public void test2CP() {
		boolean passed = false;
		byte[] password = "".getBytes();
		try {
			bkClient.createLedger(0, 1, 2, DigestType.CRC32C, password);
		} catch (Exception e) {
			passed = true;
		}
		
		Assert.assertTrue(passed);
	}
	
	@Test
	public void test3CP() {
		byte[] password = "password".getBytes();
		LedgerHandle ledger = null;
		try {
			ledger = bkClient.createLedger(1, 1, 1, DigestType.CRC32, password);
		} catch (Exception e) {
			Assert.fail();
		}
		
		Assert.assertNotNull(ledger);
	}
	
	@Test
	public void test4CP() {
		byte[] password = "".getBytes();
		LedgerHandle ledger = null;
		try {
			ledger = bkClient.createLedger(1, 1, 1, DigestType.MAC, password);
		} catch (Exception e) { 
			Assert.fail();
		}
		
		Assert.assertNotNull(ledger);
	}
	
	@Test
	public void test5CP() {
		boolean passed = false;
		byte[] password = "".getBytes();
		try {
			bkClient.createLedger(1, 1, 0, null, password);
		} catch (Exception e) {
			passed = true;
		}
		
		Assert.assertTrue(passed);
	}
}
