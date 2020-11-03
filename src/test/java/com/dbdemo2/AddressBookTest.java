package com.dbdemo2;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.dbdemo2.AddressBookService.IOService;

public class AddressBookTest {
	@Test
	public void givenEmpPayrollDataInDB_ShouldMatchEmpCount() {
		AddressBookService service = new AddressBookService();
		List<AddressBookData> addList = service.readAddressBookData(IOService.DB_IO);
		Assert.assertEquals(4, addList.size());
	}

	@Test 
    public void givenNewCity_WhenUpdated_shouldMatchWithDB() {
    	AddressBookService service = new AddressBookService();
    	service.readAddressBookData(IOService.DB_IO);
    	service.updateContactsCity("abc", "delhi");
    	boolean result = service.checkAddressBookDataInSyncWithDB("abc","delhi");
		Assert.assertTrue(result);
    }
}