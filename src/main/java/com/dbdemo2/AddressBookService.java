package com.dbdemo2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressBookService {
	public enum IOService {
		DB_IO, REST_IO
	}

	private static List<AddressBookData> addList;
	private AddressBookDBService addressBookDBService;
	AddressBookData ad = null;

	public AddressBookService() {
		addressBookDBService = AddressBookDBService.getInstance();
	}

	public static long countEntries() {
		return addList.size();
	}

	public AddressBookService(List<AddressBookData> addList) {
		this.addList = new ArrayList<>(addList);
		
	}

	public List<AddressBookData> readAddressBookData(IOService dbIo) {
		if (dbIo.equals(IOService.DB_IO)) {
			this.addList = new AddressBookDBService().readData();
		}
		return this.addList;
	}

	AddressBookData getAddressBookData(String name) {
		System.out.println("xxxx"+addList);
		for (AddressBookData data : addList) {
			if (data.first_name.equalsIgnoreCase(name)) {
				return data;
			}
		}
		return null;
	}

	public void updateContactsCity(String firstname, String city) {
		int result = addressBookDBService.updateAddressBookDataUsingPreparedStatement(firstname, city);
		if (result == 0)
			return;
		AddressBookData addBookData = this.getAddressBookData(firstname);
		if (addBookData != null)
			addBookData.city = city;
	}

	public boolean checkAddressBookDataInSyncWithDB(String fname, String city) {
		for (AddressBookData data : addList) {
			if (data.first_name.equals(fname)) {
				if (data.city.equals(city)) {
					return true;
				}
			}
		}
		return false;
	}

	public Map<String, Integer> readCountContactsByCity(IOService ioService) {
		if (ioService.equals(IOService.DB_IO)) {
			return addressBookDBService.getCountByCity();
		}
		return null;
	}

	public Map<String, Integer> readCountContactsByState(IOService ioService) {
		if (ioService.equals(IOService.DB_IO)) {
			return addressBookDBService.getCountByState();
		}
		return null;
	}

	public void addContact(int id, String firstName, String lastName, String address, String city, String state,
			String zipcode, String phone, String email) {
		addList.add(
				addressBookDBService.addContact(id, firstName, lastName, address, city, state, zipcode, phone, email));
	}

	public void addContactsWithThreads(List<AddressBookData> addBookList) {
		Map<Integer, Boolean> contactAdditionStatus = new HashMap<Integer, Boolean>();
		addBookList.forEach(ad -> {
			Runnable task = () -> {
				contactAdditionStatus.put(addressBookDBService.hashCode(), false);

				this.addContact(ad.id, ad.first_name, ad.last_name, ad.address, ad.city, ad.state, ad.zip, ad.phone_no,
						ad.email);
				contactAdditionStatus.put(addressBookDBService.hashCode(), true);

			};
			Thread thread = new Thread(task, ad.first_name);
			thread.start();
		});
		while (contactAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	public void addPerson(AddressBookData personData, IOService ioService) {

		addList.add(personData);
	}
	
	public void updatePersonCity(String name, String city, IOService ioService) {
		AddressBookData personData = this.getAddressBookData(name);
		if (personData != null)
			personData.city = city;
		System.out.println("xxxxxxxx"+personData);
	}
	
	public void deletePersonData(String name, IOService ioService) {
		if (ioService.equals(IOService.REST_IO)) {
			AddressBookData personData = this.getAddressBookData(name);
			addList.remove(personData);
		}
	}

}
