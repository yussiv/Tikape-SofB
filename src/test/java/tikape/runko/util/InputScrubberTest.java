package tikape.runko.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jussi
 */
public class InputScrubberTest {
    
    public InputScrubberTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of clean method, of class InputScrubber.
     */
    @Test
    public void removesScripts() {
        System.out.println("clean");
        String string = "clean <script>function a(){alert('hehee'); a();} a();</script>stuff";
        InputScrubber scrubber = new InputScrubber();
        
        String result = scrubber.clean(string);
        
        assertEquals("clean stuff", result);
    }
    
    @Test
    public void removesTags() {
        System.out.println("clean");
        String string = "<h3>clean <span style='display:hidden'>stuff</span>";
        InputScrubber scrubber = new InputScrubber();
        
        String result = scrubber.clean(string);
        
        assertEquals("clean stuff", result);
    }
    
}
