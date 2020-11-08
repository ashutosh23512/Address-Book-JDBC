package com.dbdemo2;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dbdemo2.AddressBookService.IOService;
import com.google.gson.Gson;

import io.restassured.*;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AddressBookTest {
	@Test
	public void givenEmpPayrollDataInDB_ShouldMatchEmpCount() {
		AddressBookService service = new AddressBookService();
		List<AddressBookData> addList = service.readAddressBookData(IOService.DB_IO);
		Assert.assertEquals(8, addList.size());
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
	
	
	
	@Test 
    public void given3Contacts_WhenAdded_ShouldMatchContactsCount() {
    	AddressBookData[] addBookData = {
    			new AddressBookData(11,"qq", "ww", "ee","rr", "tt", "12345", "988888888", "abc@gmail.com"),
    			new AddressBookData(12,"aa", "ss", "dd","ff", "gg", "12345", "9000000000", "abc@gmail.com"),
    			new AddressBookData(13,"zz", "xx", "cc","vv", "bb", "12345", "9111111111", "abc@gmail.com"),
    	};
    	AddressBookService addBookService = new AddressBookService();
    	addBookService.readAddressBookData(IOService.DB_IO);
    	Instant threadStart = Instant.now();
    	addBookService.addContactsWithThreads(Arrays.asList(addBookData));
    	Instant threadEnd = Instant.now();
    	System.out.println("Duration with thread : " + Duration.between(threadStart, threadEnd));
    	List<AddressBookData> addressBookData = addBookService.readAddressBookData(IOService.DB_IO);
    	System.out.println(addressBookData.size());
    	Assert.assertEquals(8, addressBookData.size());
    }

	@Before
	public void Setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	@Test
	public void givenEmployeeInJSONServer_whenRetrieved_ShouldMatchTheCount() {
		AddressBookData[] arrayOfcontacts = getaddbook();
		AddressBookService addressBookService = new AddressBookService(Arrays.asList(arrayOfcontacts));
		long entries = AddressBookService.countEntries();
		Assert.assertEquals(8, entries);
	}

	private AddressBookData[] getaddbook() {
		Response response = RestAssured.get("/addbook");
		System.out.println("Address Book Contacts IN JSONServer:\n" + response.asString());
		AddressBookData[] arrayOfcontacts = new Gson().fromJson(response.asString(), AddressBookData[].class);
		return arrayOfcontacts;
	}
	@Test
	public void givenNewPersons_WhenAdded_ShouldMatch201ResponseAndCount() {
		AddressBookService service;
		AddressBookData[] ArrayOfEmps = getaddbook();
		service = new AddressBookService(Arrays.asList(ArrayOfEmps));
		AddressBookData[] arrayPersonData = {
				new AddressBookData(4, "stu", "vwx", "125 Street", "city4", "state4", "19013121", "9999999996",
						"stu@gmail.com"),
				new AddressBookData(5, "yzq", "abc", "123 Street", "city5", "state5", "1310021", "9999999994",
						"yzq@gmail.com") };
		for (AddressBookData personData : Arrays.asList(arrayPersonData)) {
			Response response = addPersonToJsonServer(personData);
			int statusCode = response.getStatusCode();
			Assert.assertEquals(201, statusCode);
			personData = new Gson().fromJson(response.asString(), AddressBookData.class);
			service.addPerson(personData, IOService.REST_IO);
		}
		long entries = service.countEntries();
		Assert.assertEquals(10, entries);
	}
	
	private Response addPersonToJsonServer(AddressBookData employeePayrollData) {
		String empJson = new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		return request.post("/addbook");
	}
	
	@Test
	public void givenNewCityForPerson_WhenUpdated_ShouldMatch200ResponseAndCount() {
		AddressBookService service;
		AddressBookData[] ArrayOfEmps = getaddbook();
		service = new AddressBookService(Arrays.asList(ArrayOfEmps));
		service.updatePersonCity("stu", "newCity1", IOService.REST_IO);
		AddressBookData personData = service.getAddressBookData("stu");
		String personJson = new Gson().toJson(personData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(personJson);
		Response response = request.put("/addbook/" + personData.id);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(200, statusCode);
	}
	
	@Test
	public void givenEmployeeName_WhenDeleted_ShouldMatch200ResponseAndCount() {
		AddressBookService service;
		AddressBookData[] ArrayOfEmps = getaddbook();
		service = new AddressBookService(Arrays.asList(ArrayOfEmps));
		AddressBookData personData = service.getAddressBookData("stu");
		String personJson = new Gson().toJson(personData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		Response response = request.delete("/addbook/" + personData.id);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(200, statusCode);
		service.deletePersonData(personData.first_name, IOService.REST_IO);
		long entries = service.countEntries();
		Assert.assertEquals(3, entries);
	}
}