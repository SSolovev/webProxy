package srg.app;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {


        String chunk="12345\r\n0\r\n\r\n";
        String chunk2="\r\n0\r\n\r\n";
        System.out.println(chunk.lastIndexOf("\r\n0\r\n\r\n"));
        System.out.println(chunk.indexOf("\r\n0\r\n\r\n"));
        System.out.println(chunk.length());
        System.out.println(chunk.substring(chunk.length()-7).equals("\r\n0\r\n\r\n"));
        System.out.println(chunk2.substring(chunk2.length()-7).equals("\r\n0\r\n\r\n"));

        assertTrue( true );
    }
}
