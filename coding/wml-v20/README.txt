Here is a short description how to integrate this module into the 52N SOS 4.0.

1. Check out the 52N SOS 4.0 project.
2. Check out the WaterML 2.0 module as a separate project in the same folder as the 52N SOS project.
3. Build the 52N SOS 4.0 with WaterML 2.0 as responseFormat for observations with the additional profile 'wml' like this:
	'mvn clean install -Pwml'