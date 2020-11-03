package com.dbdemo2;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.dbdemo2.AddressBookService.IOService;

public class AddressBookTest {
	@Test
	public void givenEmpPayrollDataInDB_ShouldMatchEmpCount() {
		AddressBookService service = new AddressBookService();
		List<AddressBookData> addList = service.readAddressBookData(IOService.DB_IO);
		Assert.assertEquals(5, addList.size());
	}

	@Test
	public void givenNewCity_WhenUpdated_shouldMatchWithDB() {
		AddressBookService service = new AddressBookService();
		service.readAddressBookData(IOService.DB_IO);
		service.updateContactsCity("abc", "delhi");
		boolean result = service.checkAddressBookDataInSyncWithDB("abc", "delhi");
		Assert.assertTrue(result);
	}

	@Test
	public void givenContactsData_WhenCountByCity_ShouldReturnProperValue() {
		AddressBookService service = new AddressBookService();
		service.readAddressBookData(IOService.DB_IO);
		Map<String, Integer> countContactsByCity = service.readCountContactsByCity(IOService.DB_IO);
		Assert.assertTrue(countContactsByCity.get("amritsar").equals(1));
	}

	@Test
	public void givenContactsData_WhenCountByState_ShouldReturnProperValue() {
		AddressBookService service = new AddressBookService();
		service.readAddressBookData(IOService.DB_IO);
		Map<String, Integer> countContactsByState = service.readCountContactsByState(IOService.DB_IO);
		Assert.assertTrue(countContactsByState.get("punjab").equals(4));
	}

	@Test
	public void givenNewContact_WhenAdded_ShouldSyncWithDB() {
		AddressBookService service = new AddressBookService();
		service.readAddressBookData(IOService.DB_IO);
		service.addContact(10, "zzz", "yyy", "temp", "temp", "temp", "12345", "9888888888", "abc@gmail.com");
		boolean result = service.checkAddressBookDataInSyncWithDB("zzz", "temp");
		Assert.assertTrue(result);
	}
}