package com.example.demo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NumberParser {
	
	private static final String COUNTRY_CODE_START = "+";

	private final Map<String, Integer> countryCodes;
	private final Map<String, String> nationalTrunkPrefixes;

	

	public NumberParser(Map<String, Integer> countryCodes, Map<String, String> nationalTrunkPrefixes) {

		this.countryCodes = countryCodes;
		this.nationalTrunkPrefixes = nationalTrunkPrefixes;

	}

	public String parse(String dialledNumber, String userNumber) {

		if (null == dialledNumber || dialledNumber.trim().length() == 0 || null == userNumber
				|| userNumber.trim().length() == 0) {
			throw new IllegalArgumentException("dialledNumber or userNumber is empty");
		}

		if (dialledNumber.startsWith(COUNTRY_CODE_START)) {
			
			//invoke getCountry to verify if the country code in dialedNumber is a valid country code
			getCountry(dialledNumber, "Dialled number");
			return dialledNumber; // dialled number already starts with country code, so return it unchanged

		}

		if (userNumber.startsWith(COUNTRY_CODE_START)) {// user number has country code preceded by +

			String selectedCountry = getCountry(userNumber, "User number");

			// Country has now been identified from the user number at this stage

			return replaceNationalTrunkPrefixWithCountryCode(dialledNumber, selectedCountry, countryCodes.get(selectedCountry));

		} else {
			throw new IllegalArgumentException(
					"Neither User number nor dialled number has the country code. So the country code could not be determined");
		}

	}

	
	/**
	 * Obtain the country for the country code extracted from the number
	 * @param number phone number from which the country code is to be extracted
	 * @return country (2 letter country)
	 */
	private String getCountry(String number, String numberType) {
		String selectedCountry = null;

		// the 0th index has '+', so country code starts at index 1. Assuming a country
		// code can be maximum of 3 digits
		int startOfCountryCodeIndex = 1, maxCountryCodeSize = 3;

		// Loop through the number, increasing the country code size in each loop,
		// comparing the potential country code to the codes in the countryCodes map
		// until a match is found
		for (int i = startOfCountryCodeIndex; i <= maxCountryCodeSize; i++) {
			String potentialCountryCode = number.substring(startOfCountryCodeIndex, i + 1);
			Stream<String> keys = keys(countryCodes, Integer.parseInt(potentialCountryCode));
			List<String> countryCodeList = keys.collect(Collectors.toList());

			// This should not occur unless the map is faulty, nevertheless its possible
			if (countryCodeList.size() > 1)
				throw new IllegalArgumentException(String.format(
						"More than one country found for the country code %s. CountryCode map has multiple country for same country code !!",
						potentialCountryCode));
			else if (countryCodeList.size() == 1) { // unique country code found as expected
				selectedCountry = countryCodeList.get(0);
				// country found, so break out of the loop
				break;
			}
			// else no country found, meaning the 'for' loop should continue increasing the
			// length of the country code by one digit upto max maxCountryCodeSize

		}

		if (selectedCountry == null) {
			throw new IllegalArgumentException(
					String.format("Country could not be identified from country code in %s", numberType));
		}
		
		return selectedCountry;
	}

	/**
	 * Replaces the national trunk prefix in dialled number with country code
	 * 
	 * @param dialledNumber
	 * @param selectedCountry
	 * @param selectedCountryCode
	 * @return
	 */
	private String replaceNationalTrunkPrefixWithCountryCode(String dialledNumber, String selectedCountry,
			Integer selectedCountryCode) {
		String nationalTrunkPrefix = nationalTrunkPrefixes.get(selectedCountry);
		if (nationalTrunkPrefix == null) {
			throw new IllegalArgumentException(String.format(
					"National trunk prefix not found for country %s in national trunk prefix map", selectedCountry));
		} else if (!dialledNumber.startsWith(nationalTrunkPrefix)) {
			throw new IllegalArgumentException(String.format(
					"Dialled number does not start with national trunk prefix for country %s", selectedCountry));
		}

		// National Trunk Prefix is valid and the dialled number contains the prefix, at
		// this stage

		return dialledNumber.replaceFirst(nationalTrunkPrefix, COUNTRY_CODE_START + selectedCountryCode);
	}

	/**
	 * Get the keys for the value form the Map
	 * 
	 * @param map   The map in which the value is to be searched
	 * @param value The value to be searched in the map
	 * @return the Stream of keys containing the value
	 */
	public <K, V> Stream<K> keys(Map<K, V> map, V value) {
		return map.entrySet().stream().filter(entry -> value.equals(entry.getValue())).map(Map.Entry::getKey);
	}
}