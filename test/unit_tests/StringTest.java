package unit_tests;

import junit.framework.TestCase;

/**
 *
 * @author Jonathan Molina
 */
public class StringTest extends TestCase
{
    
    public StringTest(String testName)
    {
        super(testName);
    }

    
    public void testHello() {
        String s1 = "Windows blah blah";
        String s2 = "Max osx Windows";
        String s3 = "lol";
        String s4 = "";
        assertTrue(s1.startsWith("Windows"));
        assertFalse(s2.startsWith("Windows"));
        assertFalse(s3.startsWith("Windows"));
        assertFalse(s4.startsWith("Windows"));
    }

}
