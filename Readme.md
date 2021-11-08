
The NumberParser class contains a public method parse that takes in a dialledNumber and an userNumber and returns the dialledNumber with the international dialling code prepended.
The constructor of the class accepts the two map - countryCodes and nationalTrunkPrefixes

The test class test some of the use cases- with different number combination and some error conditions.


Assumptions:

1. Country code is uniquely identified from the phone number.
for example if there exists a user number with country code +222 (Mauritania), assumption is that there is no valid country code +2 or +22.
This is because first check is done with 2, then if no country is found, check is done with 22, and if no country is found, 
final check is done with 222  (max of 3 digits) to retrieve the country MR (Mauritania). Hence it will be ambiguous if there exists a country with code +2, +22 and +222. 

2. Either the dialled number or user number should contain the international country code. Otherwise there is no way of knowing the country.

3. A unchecked custom exception can be thrown by the parse method, but for brevity of time the unchecked IllegalArgumentException is thrown for some exceptions

4. No check for correct phone number size (for example 10 digit number for UK) is done. Focus is on finding the country code

