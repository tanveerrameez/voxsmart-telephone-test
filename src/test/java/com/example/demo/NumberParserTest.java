package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NumberParserTest {

	private NumberParser parser;

	
	@BeforeEach
	void setUp() {
		Map<String, Integer> countryCodes=new HashMap<>();
		countryCodes.put("UK", 44);
		countryCodes.put("FR", 33);
		countryCodes.put("US", 1);
		countryCodes.put("HK", 852);
		countryCodes.put("BD", 880);
		
		Map<String, String> nationalTrunkPrefixes=new HashMap<>();
		nationalTrunkPrefixes.put("UK", "0");
		nationalTrunkPrefixes.put("FR", "0");
		nationalTrunkPrefixes.put("US", "1");
		nationalTrunkPrefixes.put("HK", "");
		nationalTrunkPrefixes.put("BD", "0");
		
		parser=new NumberParser(countryCodes,nationalTrunkPrefixes);
	}
	
	@Test
	void testUKtoUK() {
		String returnedNumber = parser.parse("07277822334", "+447866866886");
		assertEquals("+447277822334", returnedNumber);
	}

	@Test
	void testUStoUS1() {
		String returnedNumber = parser.parse("1312233244", "+1212233200");
		assertEquals("+1312233244", returnedNumber);

	}

	@Test
	void testUKtoUS() {
		String returnedNumber = parser.parse("+1312233244", "+447866866886");
		assertEquals("+1312233244", returnedNumber);

	}

	@Test
	void testUStoUS2() {
		String returnedNumber = parser.parse("1312233244", "+1212233200");
		assertEquals("+1312233244", returnedNumber);

	}
	
	
	@Test
	void testHongKongToHongkong() {
		String returnedNumber = parser.parse("23456789", "+85225218121");
		assertEquals("+85223456789", returnedNumber);

	}
	
	
	
	@Test
	void testFranceToFrance() {
		String returnedNumber = parser.parse("0149527154", "+33149527154");
		assertEquals("+33149527154", returnedNumber);

	}
	
	@Test
	void testBangladeshToBangladesh() {
		String returnedNumber = parser.parse("0345678904", "+880123456789");
		assertEquals("+880345678904", returnedNumber);

	}
	
	@Test
	void testUnknownCountryCodeShouldThrowException() {
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> parser.parse("0345678904", "+23123456789"));
		String expectedMessage = "Country could not be identified from country code in User number";
	    String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));

	}
	
	@Test
	void testUnknownNationalTrunkPrefixShouldThrowException() {
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> parser.parse("1149527154", "+33149527154"));
		
		String expectedMessage = "Dialled number does not start with national trunk prefix for country FR";
	    String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	
	}
	
	@Test
	void testNoCountryCodeProvidedShouldThrowException() {
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> parser.parse("0149527154", "049527154"));
		
		String expectedMessage = "Neither User number nor dialled number has the country code. So the country code could not be determined";
	    String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	
	}
	
	@Test
	void testUnknownDialledNumberCountryCodeShouldThrowException() {
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> parser.parse("+98949527154", "+33149527154"));
		
		String expectedMessage = "Country could not be identified from country code in Dialled number";
	    String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	
	}
	
	
	@ParameterizedTest
	@MethodSource("methodForEmptyOrNullNumberInput")
	void testEmptyOrNullNumbersShouldThrowException(String dialledNumber, String userNumber) {
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> parser.parse(dialledNumber, userNumber));
		
		String expectedMessage = "dialledNumber or userNumber is empty";
	    String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	
	}
	
	private static Stream<Arguments> methodForEmptyOrNullNumberInput() {
	    return Stream.of(Arguments.of(null, null),
	                     Arguments.of("", "+447475431323"),
	                     Arguments.of("  ", "+447475431323"),
	                     Arguments.of("+447475431323", null),
	                     Arguments.of("+447475431323", "")
	    );
	}

}
